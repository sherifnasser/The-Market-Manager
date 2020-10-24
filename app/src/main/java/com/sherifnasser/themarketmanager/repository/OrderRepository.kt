package com.sherifnasser.themarketmanager.repository

import com.sherifnasser.themarketmanager.database.dao.OrderDao
import com.sherifnasser.themarketmanager.database.dao.OrderProductCrossRefDao
import com.sherifnasser.themarketmanager.database.dao.ProductDao
import com.sherifnasser.themarketmanager.database.model.Order
import com.sherifnasser.themarketmanager.database.model.OrderProductCrossRef
import com.sherifnasser.themarketmanager.database.model.Product
import javax.inject.Inject

class OrderRepository
@Inject
constructor(
    private val productDao:ProductDao,
    private val orderDao:OrderDao,
    private val orderProductCrossRefDao:OrderProductCrossRefDao
){

    private val _allOrders by lazy{orderDao.getAllOrders()}

    suspend fun insertOrder(order:Order)=orderDao.insert(order)

    suspend fun insertOrderProductCrossRef(orderProductCrossRefs:List<OrderProductCrossRef>)=
        orderProductCrossRefDao.insertAll(orderProductCrossRefs)

    suspend fun updateProducts(products:List<Product>)=productDao.updateAll(products)

    suspend fun getQuantityOfSoldProduct(orderId:Int,productId:Int)=
        orderProductCrossRefDao.getQuantityOfSoldProduct(orderId,productId)

    suspend fun getOrderWithProducts(orderId:Int)=orderDao.getOrderWithProducts(orderId)

    suspend fun deleteOrder(order:Order)=orderDao.delete(order)

    suspend fun deleteAllOrderProductCrossRefWhere(orderId:Int)=orderProductCrossRefDao.deleteAllWhere(orderId)

    val allOrders get()=_allOrders
}