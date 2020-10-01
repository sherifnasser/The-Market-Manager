package com.sherifnasser.themarketmanager.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sherifnasser.themarketmanager.database.dao.ProductDao
import com.sherifnasser.themarketmanager.database.model.Product
import com.sherifnasser.themarketmanager.database.model.ProductFts


@Database(entities=[Product::class,ProductFts::class],version=1)
abstract class TheMarketManagerDatabase:RoomDatabase(){

    abstract fun productDao():ProductDao

    companion object{
        const val DATABASE_NAME="the_market_manager_db"
    }
}