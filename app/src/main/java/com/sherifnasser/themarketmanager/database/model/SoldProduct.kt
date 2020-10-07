package com.sherifnasser.themarketmanager.database.model

data class SoldProduct(
    override val productId:Int,
    override var name:String,
    override var price:Double,
    override var availableQuantity:Int,
    var soldQuantity:Int
):Product(productId,name,price,availableQuantity)