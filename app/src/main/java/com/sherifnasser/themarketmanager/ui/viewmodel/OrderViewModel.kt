package com.sherifnasser.themarketmanager.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.sherifnasser.themarketmanager.database.model.Order
import com.sherifnasser.themarketmanager.database.model.OrderProductCrossRef
import com.sherifnasser.themarketmanager.database.model.Product
import com.sherifnasser.themarketmanager.database.model.SoldProduct
import com.sherifnasser.themarketmanager.repository.OrderRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class OrderViewModel
@ViewModelInject
constructor(
    private val orderRepository:OrderRepository,
    private val ioDispatcher:CoroutineDispatcher,
    private val _orderInfo:MutableLiveData<Order>,
    val _orderInfoOldSoldProducts:MutableLiveData<List<SoldProduct>>,
    /*
    pagedListConfig: The configuration of the paged list to achieve paging when displaying orders.
    */
    pagedListConfig:PagedList.Config
):ViewModel(){

    private val _allOrders by lazy{orderRepository.allOrders.toLiveData(pagedListConfig)}


    fun insert(order:Order){
        /*
        1- insert order in database
        2- decrease available qty of sold products
        3- update products in database
        4- insert orderProductCrossRefs in database
        */

        // 1- insert order in database
        viewModelScope.launch(ioDispatcher){
            val orderId=orderRepository.insertOrder(order).toInt()
            val orderProductCrossRefs=arrayListOf<OrderProductCrossRef>()

            // 2- decrease available qty of sold products in database
            order.soldProducts!!.forEach{soldProduct->
                with(soldProduct){
                    availableQuantity-=soldQuantity
                    orderProductCrossRefs.add(
                        OrderProductCrossRef(
                            orderId=orderId,
                            productId=productId,
                            soldQuantity=soldQuantity
                        )
                    )
                }
            }

            // 3- update products in database
            updateProducts(order.soldProducts!!)

            // 4- insert orderProductCrossRefs in database
            orderRepository.insertOrderProductCrossRef(orderProductCrossRefs)
        }
    }

    fun setOrderInfo(order:Order){
        /*
        1- get the full order info
        2- get sold qty for every sold product
        3- add the sold products to the order
        4- set order info old sold products to be the sold products of the order
        5- update order info live data
         */

        val orderId=order.orderId
        val soldProducts=ArrayList<SoldProduct>()

        viewModelScope.launch(ioDispatcher){
            // 1- get the full order info
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
            order.soldProducts=soldProducts
            // 4- set order info old sold products to be the sold products of the order
            _orderInfoOldSoldProducts.postValue(soldProducts.toList())
            // 5- update order info live data
            orderInfo.postValue(order)
        }
    }

    fun setOrderInfoToNew(){
        val lastOrderId=allOrders.value!!.size
        val currentDate=GregorianCalendar.getInstance().time
        orderInfo.value=Order(orderId=lastOrderId+1,date=currentDate,total=0.0).also{it.soldProducts=ArrayList()}
        _orderInfoOldSoldProducts.value=emptyList()
    }

    private suspend fun updateProducts(products:List<Product>)=orderRepository.updateProducts(products)

    fun delete(order:Order,onComplete:()->Unit={}){
        /*
        1- delete order from database
        2- increase available qty of sold products in database
        3- update products in database
        4- delete orderProductCrossRefs in database
        5- call on complete
        */
        viewModelScope.launch(ioDispatcher){
            // 1- delete order from database
            orderRepository.deleteOrder(order)
            // 2- increase available qty of sold products in database
            order.soldProducts!!.forEach{soldProduct->
                with(soldProduct){
                    availableQuantity+=soldQuantity
                }
            }

            // 3- update products in database
            orderRepository.updateProducts(order.soldProducts!!)

            // 4- delete orderProductCrossRefs in database
            orderRepository.deleteAllOrderProductCrossRefWhere(order.orderId)

            // 5- call on complete
            onComplete.invoke()
        }
    }

    fun deleteOrderFromUiAt(index:Int){

    }

    fun undoOrderInUiAt(index:Int){

    }

    val allOrders get()=_allOrders

    val orderInfo get()=_orderInfo

    val areOrderInfoSoldProductsChanged get()=_orderInfoOldSoldProducts.value!=orderInfo.value!!.soldProducts

}