package com.sherifnasser.themarketmanager.di

import android.content.Context
import androidx.room.Room
import com.sherifnasser.themarketmanager.database.TheMarketManagerDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object DatabaseModule{

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext ctx:Context)=
        Room.databaseBuilder(
            ctx.applicationContext,
            TheMarketManagerDatabase::class.java,
            TheMarketManagerDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()

    @Provides
    fun provideProductDao(db:TheMarketManagerDatabase)=db.productDao()

    @Provides
    fun provideOrderDao(db:TheMarketManagerDatabase)=db.orderDao()

    @Provides
    fun provideOrderProductCrossRefDao(db:TheMarketManagerDatabase)=db.orderProductCrossRefDao()

    @Provides
    fun provideOrdersDayDao(db:TheMarketManagerDatabase)=db.ordersDayDao()

}