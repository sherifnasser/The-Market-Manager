package com.sherifnasser.themarketmanager.database.dao

import androidx.paging.DataSource
import androidx.room.*
import com.sherifnasser.themarketmanager.database.model.Order
import com.sherifnasser.themarketmanager.database.model.OrderWithProducts

@Dao
interface OrderDao{

    @Insert
    suspend fun insert(order:Order):Long

    @Delete
    suspend fun delete(order:Order)

    @Query("SELECT * FROM orders_table ORDER BY orderId DESC")
    fun getAllOrders():DataSource.Factory<Int,Order>

    @Transaction
    @Query("SELECT * FROM orders_table WHERE orderId=:orderId")
    suspend fun getOrderWithProducts(orderId:Int):OrderWithProducts


}