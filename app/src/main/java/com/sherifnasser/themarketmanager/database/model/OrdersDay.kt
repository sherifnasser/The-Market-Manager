package com.sherifnasser.themarketmanager.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName="orders_days_table")
data class OrdersDay(
    @PrimaryKey val day:Date,
    var doneOrdersCount:Int=0,
    var revenue:Double=0.0
)