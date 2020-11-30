package com.sherifnasser.themarketmanager.database.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName="orders_table")
data class Order
constructor(
    @PrimaryKey(autoGenerate=true)val orderId:Int,
    val date:Date,
    var total:Double
){
    @Ignore
    var soldProducts:List<SoldProduct>?=null

    fun addToSoldProducts(product:SoldProduct){
        val soldProductsArrayList=soldProducts!! as ArrayList
        // If the product exists, remove it and update total
        soldProductsArrayList.forEachIndexed{index,soldProduct->
            if(soldProduct.productId==product.productId){
                total-=soldProduct.soldQuantity*soldProduct.price
                soldProductsArrayList[index]=product
                return@forEachIndexed
            }
        }

        // Add just the new product and update total
        if(product !in soldProducts!!)
            soldProductsArrayList.add(product)
        total+=product.soldQuantity*product.price
    }

    fun removeFromSoldProductsAt(index:Int){
        val soldProductsArrayList=soldProducts!! as ArrayList
        val product=soldProductsArrayList[index]
        total-=product.soldQuantity*product.price
        soldProductsArrayList.removeAt(index)
    }
}