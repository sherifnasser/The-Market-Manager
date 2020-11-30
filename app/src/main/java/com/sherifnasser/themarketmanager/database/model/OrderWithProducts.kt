package com.sherifnasser.themarketmanager.database.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class OrderWithProducts(
    @Embedded val order:Order,
    @Relation(
        parentColumn="orderId",
        entityColumn="productId",
        associateBy=Junction(OrderProductCrossRef::class)
    )
    val products:List<Product>
)