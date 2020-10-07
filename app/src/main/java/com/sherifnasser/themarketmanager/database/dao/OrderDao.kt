package com.sherifnasser.themarketmanager.database.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.sherifnasser.themarketmanager.database.model.Order
import com.sherifnasser.themarketmanager.database.model.OrderWithProducts

@Dao
interface OrderDao{

    @Insert
    suspend fun insert(order:Order):Long

    @Query("SELECT * FROM orders_table")
    fun getAllOrders():DataSource.Factory<Int,Order>

    @Transaction
    @Query("SELECT * FROM orders_table WHERE orderId=:orderId")
    suspend fun getOrderWithProducts(orderId:Int):OrderWithProducts
}