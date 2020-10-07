package com.sherifnasser.themarketmanager.database.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import dagger.hilt.android.scopes.FragmentScoped
import java.util.Date
import javax.inject.Inject

@FragmentScoped
@Entity(tableName="orders_table")
data class Order
@Inject
constructor(
    @PrimaryKey(autoGenerate=true)val orderId:Int,
    val date:Date,
    var total:Double
){
    @Ignore
    var soldProducts:ArrayList<SoldProduct>?=null
}