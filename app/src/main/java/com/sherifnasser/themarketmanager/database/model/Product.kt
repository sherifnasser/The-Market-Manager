package com.sherifnasser.themarketmanager.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import javax.inject.Inject

@Entity(tableName="products_table")
data class Product(
    @PrimaryKey(autoGenerate=true)val id:Int=0,
    var name:String="",
    var price:Double=0.0,
    var availableQuantity:Int=0
)