package com.sherifnasser.themarketmanager.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="products_table")
open class Product(
    @PrimaryKey(autoGenerate=true)open val productId:Int,
    open var name:String,
    open var price:Double,
    open var availableQuantity:Int
){
    override fun equals(other:Any?):Boolean{
        if(this===other)return true
        if(javaClass!=other?.javaClass)return false

        other as Product

        if(productId!=other.productId)return false
        if(name!=other.name)return false
        if(price!=other.price) return false
        if(availableQuantity!=other.availableQuantity)return false

        return true
    }

    override fun hashCode():Int{
        var result=productId
        result=31*result+name.hashCode()
        result=31*result+price.hashCode()
        result=31*result+availableQuantity
        return result
    }

}