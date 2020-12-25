package com.sherifnasser.themarketmanager.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import com.sherifnasser.themarketmanager.database.model.OrdersDay
import java.util.Date

@Dao
interface OrdersDayDao{
    @Update
    suspend fun update(ordersDay:OrdersDay)

    @Query("SELECT * FROM orders_days_table WHERE day=:day")
    suspend fun get(day:Date):OrdersDay
}