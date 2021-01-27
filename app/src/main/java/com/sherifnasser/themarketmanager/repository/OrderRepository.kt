package com.sherifnasser.themarketmanager.repository

import com.sherifnasser.themarketmanager.database.dao.OrderDao
import com.sherifnasser.themarketmanager.database.dao.OrderProductCrossRefDao
import com.sherifnasser.themarketmanager.database.dao.OrdersDayDao
import com.sherifnasser.themarketmanager.database.dao.ProductDao
import com.sherifnasser.themarketmanager.database.model.Order
import com.sherifnasser.themarketmanager.database.model.OrderProductCrossRef
import com.sherifnasser.themarketmanager.database.model.OrdersDay
import com.sherifnasser.themarketmanager.database.model.Product
import javax.inject.Inject
import java.util.Date

class OrderRepository
@Inject
constructor(
    private val productDao:ProductDao,
    private val orderDao:OrderDao,
    private val orderProductCrossRefDao:OrderProductCrossRefDao,
    private val ordersDayDao:OrdersDayDao
){

    private val _allOrders by lazy{orderDao.getAllOrders()}

    val allOrders get()=_allOrders


    // orders
    suspend fun getLastOrderId()=orderDao.getLastOrderId()

    suspend fun getOrderWithProducts(orderId:Int)=orderDao.getOrderWithProducts(orderId)

    suspend fun insertOrder(order:Order)=orderDao.insert(order)

    suspend fun updateOrder(order:Order)=orderDao.update(order)

    suspend fun deleteOrder(order:Order)=orderDao.delete(order)


    // order product cross reference
    suspend fun insertOrderProductCrossRefs(orderProductCrossRefs:List<OrderProductCrossRef>)=
        orderProductCrossRefDao.insertAll(orderProductCrossRefs)

    suspend fun updateOrderProductCrossRefs(orderProductCrossRefs:List<OrderProductCrossRef>)=
        orderProductCrossRefDao.updateAll(orderProductCrossRefs)

    suspend fun deleteOrderProductCrossRefs(orderProductCrossRefs:List<OrderProductCrossRef>)=
        orderProductCrossRefDao.deleteAll(orderProductCrossRefs)

    suspend fun deleteAllOrderProductCrossRefsWhere(orderId:Int)=
        orderProductCrossRefDao.deleteAllWhere(orderId)


    // products
    suspend fun updateProducts(products:List<Product>)=productDao.updateAll(products)

    suspend fun getQuantityOfSoldProduct(orderId:Int,productId:Int)=
        orderProductCrossRefDao.getQuantityOfSoldProduct(orderId,productId)


    // orders days
    suspend fun insertOrdersDay(ordersDay:OrdersDay)=ordersDayDao.insert(ordersDay)

    suspend fun updateOrdersDay(ordersDay:OrdersDay)=ordersDayDao.update(ordersDay)

    suspend fun getOrdersDay(day:Date)=ordersDayDao.get(day)
}