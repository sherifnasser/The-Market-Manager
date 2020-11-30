package com.sherifnasser.themarketmanager.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sherifnasser.themarketmanager.database.dao.OrderDao
import com.sherifnasser.themarketmanager.database.dao.OrderProductCrossRefDao
import com.sherifnasser.themarketmanager.database.dao.ProductDao
import com.sherifnasser.themarketmanager.database.model.Order
import com.sherifnasser.themarketmanager.database.model.OrderProductCrossRef
import com.sherifnasser.themarketmanager.database.model.Product
import com.sherifnasser.themarketmanager.database.model.ProductFts

@Database(
    entities=[
        Product::class,
        ProductFts::class,
        Order::class,
        OrderProductCrossRef::class
    ],
    version=1
)
@TypeConverters(Converters::class)
abstract class TheMarketManagerDatabase:RoomDatabase(){

    abstract fun productDao():ProductDao
    abstract fun orderDao():OrderDao
    abstract fun orderProductCrossRefDao():OrderProductCrossRefDao

    companion object{
        const val DATABASE_NAME="the_market_manager_db"
    }
}