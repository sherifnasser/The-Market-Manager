package com.sherifnasser.themarketmanager.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Query
import com.sherifnasser.themarketmanager.database.model.OrdersDay
import java.util.Date

@Dao
interface OrdersDayDao{
    @Insert
    suspend fun insert(ordersDay:OrdersDay)

    @Update
    suspend fun update(ordersDay:OrdersDay)

    @Query("SELECT * FROM orders_days_table WHERE day=:day")
    suspend fun get(day:Date):OrdersDay?

    @Query("SELECT * FROM orders_days_table")
    fun getAll():LiveData<List<OrdersDay>>
}