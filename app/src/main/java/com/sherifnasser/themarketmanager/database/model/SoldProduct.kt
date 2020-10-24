package com.sherifnasser.themarketmanager.database.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SoldProduct(
    override val productId:Int,
    override var name:String,
    override var price:Double,
    override var availableQuantity:Int,
    var soldQuantity:Int
):Product(productId,name,price,availableQuantity),Parcelable