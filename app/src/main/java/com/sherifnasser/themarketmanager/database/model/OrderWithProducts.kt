package com.sherifnasser.themarketmanager.database.model

import androidx.room.Embedded
import androidx.room.Relation
import androidx.room.Junction

data class OrderWithProducts(
    @Embedded val order:Order,
    @Relation(
        parentColumn="orderId",
        entityColumn="productId",
        associateBy=Junction(OrderProductCrossRef::class)
    )
    val products:List<Product>
)