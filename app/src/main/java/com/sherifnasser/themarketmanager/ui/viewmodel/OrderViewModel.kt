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

class OrderViewModel
@ViewModelInject
constructor(
    private val orderRepository:OrderRepository,
    private val ioDispatcher:CoroutineDispatcher,
    private val _orderInfo:MutableLiveData<Order>,
    /*
    pagedListConfig: The configuration of the paged list to achieve paging when displaying orders.
    */
    pagedListConfig:PagedList.Config
):ViewModel(){

    private val _allOrders by lazy{orderRepository.allOrders.toLiveData(pagedListConfig)}

    fun insert(order:Order){
        /*
        1- insert order in database
        2- update products in database
        3- insert orderProductCrossRefs in database
        */

        // 1- insert order in database
        viewModelScope.launch(ioDispatcher){
            val orderId=orderRepository.insertOrder(order).toInt()
            val orderProductCrossRefs=arrayListOf<OrderProductCrossRef>()

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

            // 2- update products in database

            updateProducts(order.soldProducts!!)

            // 3- insert orderProductCrossRefs in database
            orderRepository.insertOrderProductCrossRef(orderProductCrossRefs)
        }
    }

    fun setOrderInfo(order:Order){
        val orderId=order.orderId
        val soldProducts=arrayListOf<SoldProduct>()
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
            // 4- update order info live data
            orderInfo.postValue(order)
        }
    }

    private suspend fun updateProducts(products:List<Product>)=orderRepository.updateProducts(products)

    val allOrders get()=_allOrders

    val orderInfo get()=_orderInfo
}