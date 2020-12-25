package com.sherifnasser.themarketmanager.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName="orders_days_table")
data class OrdersDay(
    @PrimaryKey val day:Date,
    var ordersDoneCount:Int,
    var revenue:Double
)