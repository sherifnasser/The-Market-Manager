package com.sherifnasser.themarketmanager.database.model

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

// To search in products by productName or to get unavailable products.
@Entity(tableName="products_fts")
@Fts4(contentEntity=Product::class)
data class ProductFts(@PrimaryKey val rowid:Int,var name:String,var availableQuantity:Int)