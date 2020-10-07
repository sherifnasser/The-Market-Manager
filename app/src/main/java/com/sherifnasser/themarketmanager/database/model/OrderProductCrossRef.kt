package com.sherifnasser.themarketmanager.database.model

import androidx.room.Entity

@Entity(
    tableName="orders_products_cross_ref_table",
    primaryKeys=["orderId","productId"]
)
data class OrderProductCrossRef(
    val orderId:Int,
    val productId:Int,
    var soldQuantity:Int=0
)