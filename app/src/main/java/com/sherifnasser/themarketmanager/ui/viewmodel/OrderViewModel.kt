package com.sherifnasser.themarketmanager.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.sherifnasser.themarketmanager.database.model.*
import com.sherifnasser.themarketmanager.repository.OrderRepository
import com.sherifnasser.themarketmanager.util.startOfDay
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date
import kotlin.collections.ArrayList

class OrderViewModel
@ViewModelInject
constructor(
    private val orderRepository:OrderRepository,
    private val ioDispatcher:CoroutineDispatcher,
    private val _orderInfo:MutableLiveData<Order>,
    private val _orderInfoOldSoldProducts:MutableLiveData<List<SoldProduct>>,
    /*
    pagedListConfig: The configuration of the paged list to achieve paging when displaying orders.
    */
    pagedListConfig:PagedList.Config
):ViewModel(){

    private val _allOrders by lazy{orderRepository.allOrders.toLiveData(pagedListConfig)}

    private suspend fun getOrder(order:Order):Order{
        /*
        1- get order products
        2- get sold qty for every sold product
        3- add the sold products to the order
        */

        val fullOrder=order.copy()
        val orderId=fullOrder.orderId
        val soldProducts=ArrayList<SoldProduct>()

        // 1- get order products
        val orderWithProducts=orderRepository.getOrderWithProducts(orderId)
        // 2- get sold qty for every sold product
        orderWithProducts.products.forEach{product->
            with(product){
                val soldQuantity=orderRepository.getQuantityOfSoldProduct(orderId,productId)
                soldProducts.add(
                    SoldProduct(productId,name,price,availableQuantity,soldQuantity)
                )
            }
        }
        // 3- add the sold products to the order
        fullOrder.soldProducts=soldProducts

        return fullOrder
    }

    fun insert(order:Order,onDone:()->Unit={}){
        /*
        1- insert order in database
        2- decrease available qty of sold products
        3- update products in database
        4- insert orderProductCrossRefs in database
        5- update order day
        6- invoke onDone
        */

        viewModelScope.launch(ioDispatcher){
            // 1- insert order in database
            orderRepository.insertOrder(order)

            // 2- decrease available qty of sold products in database
            val orderProductCrossRefs=arrayListOf<OrderProductCrossRef>()
            order.soldProducts!!.forEach{soldProduct->
                with(soldProduct){
                    availableQuantity-=soldQuantity
                    orderProductCrossRefs.add(
                        orderProductCrossRefOf(order,this)
                    )
                }
            }

            // 3- update products in database
            updateProducts(order.soldProducts!!)

            // 4- insert orderProductCrossRefs in database
            orderRepository.insertOrderProductCrossRefs(orderProductCrossRefs)

            //5- update order date
            val startOfDay=order.date.startOfDay
            var isNewOrdersDay=false
            val ordersDay=
                getOrdersDay(startOfDay)?:
                OrdersDay(startOfDay).also{
                    isNewOrdersDay=true
                }
            ordersDay.ordersDoneCount++
            ordersDay.revenue+=order.total
            if(isNewOrdersDay)orderRepository.insertOrdersDay(ordersDay)
            else orderRepository.updateOrdersDay(ordersDay)

            // 6- invoke onDone
            withContext(Main){onDone.invoke()}
        }
    }

    fun update(order:Order,onDone:()->Unit={}){
        if(!areOrderInfoSoldProductsChanged){
            onDone()
            return
        }

        val oldSoldProducts=_orderInfoOldSoldProducts.value!!.sortedBy{it.productId}

        val newSoldProducts=order.soldProducts!!.sortedBy{it.productId}.toMutableList()
        val updatedSoldProducts=arrayListOf<SoldProduct>()
        val deletedSoldProducts=arrayListOf<SoldProduct>()

        val newSoldProductsCrossRefs=arrayListOf<OrderProductCrossRef>()
        val updatedSoldProductsCrossRefs=arrayListOf<OrderProductCrossRef>()
        val deletedSoldProductsCrossRefs=arrayListOf<OrderProductCrossRef>()

        var lastFoundIndex=0
        oldSoldProducts.forEach{oldSoldProduct->

            val indexInNewSoldProducts=newSoldProducts.binarySearchBy(
                key=oldSoldProduct.productId,
                fromIndex=lastFoundIndex

            ){it.productId}

            val orderProductCrossRef=orderProductCrossRefOf(order,oldSoldProduct)

            if(indexInNewSoldProducts<0){
                // as deleting the product
                oldSoldProduct.availableQuantity+=oldSoldProduct.soldQuantity

                deletedSoldProducts.add(oldSoldProduct)

                deletedSoldProductsCrossRefs.add(orderProductCrossRef)
            }
            else{
                lastFoundIndex=indexInNewSoldProducts
                if(oldSoldProduct.soldQuantity!=newSoldProducts[indexInNewSoldProducts].soldQuantity){
                    // as deleting the product
                    oldSoldProduct.availableQuantity+=oldSoldProduct.soldQuantity
                    // as inserting the product
                    oldSoldProduct.availableQuantity-=newSoldProducts[indexInNewSoldProducts].soldQuantity

                    orderProductCrossRef.soldQuantity=newSoldProducts[indexInNewSoldProducts].soldQuantity

                    updatedSoldProducts.add(oldSoldProduct)

                    updatedSoldProductsCrossRefs.add(orderProductCrossRef)
                }
                newSoldProducts.removeAt(indexInNewSoldProducts)
            }
        }

        newSoldProducts.forEach{newSoldProduct->
            // as inserting the product
            with(newSoldProduct){
                availableQuantity-=soldQuantity
                newSoldProductsCrossRefs.add(
                    orderProductCrossRefOf(order,this)
                )
            }
        }

        viewModelScope.launch(ioDispatcher){
            orderRepository.run{

                updateOrder(order)

                val soldProducts=newSoldProducts+updatedSoldProducts+deletedSoldProducts
                updateProducts(soldProducts)

                insertOrderProductCrossRefs(newSoldProductsCrossRefs)
                updateOrderProductCrossRefs(updatedSoldProductsCrossRefs)
                deleteOrderProductCrossRefs(deletedSoldProductsCrossRefs)

                val orderDay=getOrdersDay(order.date.startOfDay)!!
                val oldOrderTotal=_orderInfoOldSoldProducts.value!!.sumByDouble{it.price*it.soldQuantity}
                orderDay.revenue-=oldOrderTotal
                orderDay.revenue+=order.total
                orderRepository.updateOrdersDay(orderDay)

                withContext(Main){onDone.invoke()}
            }
        }
    }

    fun delete(order:Order,onDone:()->Unit={}){
        /*
        1- get full order info from database if order's soldProducts list is null
        2- delete order from database
        3- increase available qty of sold products in database
        4- update products in database
        5- delete orderProductCrossRefs in database
        6- update order day
        7- invoke onDone
        */
        viewModelScope.launch(ioDispatcher){

            // 1- get full order info from database if order's soldProducts list is null
            val orderToDelete=if(order.soldProducts==null)getOrder(order)else
                order.apply{soldProducts=_orderInfoOldSoldProducts.value}


            // 2- delete order from database
            orderRepository.deleteOrder(orderToDelete)

            // 3- increase available qty of sold products in database
            orderToDelete.soldProducts!!.forEach{ soldProduct->
                with(soldProduct){
                    availableQuantity+=soldQuantity
                }
            }

            // 4- update products in database
            updateProducts(orderToDelete.soldProducts!!)

            // 5- delete orderProductCrossRefs in database
            orderRepository.deleteAllOrderProductCrossRefsWhere(orderToDelete.orderId)

            // 6- update order day
            val orderDay=getOrdersDay(order.date.startOfDay)!!
            orderDay.ordersDoneCount--
            orderDay.revenue-=orderToDelete.soldProducts!!.sumByDouble{it.price*it.soldQuantity}
            orderRepository.updateOrdersDay(orderDay)

            // 7- invoke onDone
            withContext(Main){onDone.invoke()}
        }
    }

    fun setOrderInfo(order:Order,onDone:()->Unit){
        /*
        1- get the full order info from database
        2- set order info old sold products to be the sold products of the order
        3- update order info live data
        4- invoke onDone
         */

        viewModelScope.launch(ioDispatcher){
            // 1- get the full order info from database
            val orderFullInfo=getOrder(order)

            // 2- set order info old sold products to be the sold products of the order
            _orderInfoOldSoldProducts.postValue(orderFullInfo.soldProducts!!.toList())

            // 3- update order info live data
            orderInfo.postValue(orderFullInfo)

            // 4- invoke onDone
            withContext(Main){onDone.invoke()}
        }
    }

    fun setOrderInfoToNew(){
        var lastOrderId=0
        allOrders.value!!.also{
            if(it.isNotEmpty())
            // The list is in desc order already from the dao, so the first item is the last here.
                lastOrderId=it.first().orderId
        }
        val currentDate=Calendar.getInstance().time
        val order=Order(orderId=++lastOrderId,date=currentDate,total=0.0).apply{soldProducts=ArrayList()}
        orderInfo.value=order
        _orderInfoOldSoldProducts.value=emptyList()
    }

    val allOrders get()=_allOrders

    val orderInfo get()=_orderInfo

    val areOrderInfoSoldProductsChanged get()=_orderInfoOldSoldProducts.value!=orderInfo.value!!.soldProducts

    private suspend fun updateProducts(products:List<Product>)=orderRepository.updateProducts(products)

    private fun orderProductCrossRefOf(order:Order,soldProduct:SoldProduct)=
        OrderProductCrossRef(
            orderId=order.orderId,
            productId=soldProduct.productId,
            soldQuantity=soldProduct.soldQuantity
        )

    private suspend fun getOrdersDay(day:Date)=orderRepository.getOrdersDay(day)
}