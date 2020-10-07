package com.sherifnasser.themarketmanager.di

import androidx.lifecycle.MutableLiveData
import com.sherifnasser.themarketmanager.database.TheMarketManagerDatabase
import com.sherifnasser.themarketmanager.database.model.Order
import com.sherifnasser.themarketmanager.database.model.SoldProduct
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import java.util.Date
import java.util.GregorianCalendar

@Module
@InstallIn(ActivityRetainedComponent::class)
object OrderModule{

    @Provides
    fun provideOrderDate():Date=GregorianCalendar.getInstance().time

    @Provides
    fun provideSoldProducts()=arrayListOf<SoldProduct>()

    @Provides
    fun provideOrderInfoLiveData()=MutableLiveData<Order>(null)

    @Provides
    fun provideOrderDao(db:TheMarketManagerDatabase)=db.orderDao()

    @Provides
    fun provideOrderProductCrossRefDao(db:TheMarketManagerDatabase)=db.orderProductCrossRefDao()
}