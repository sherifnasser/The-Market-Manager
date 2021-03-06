package com.sherifnasser.themarketmanager.di

import androidx.lifecycle.MutableLiveData
import com.sherifnasser.themarketmanager.database.model.Order
import com.sherifnasser.themarketmanager.database.model.SoldProduct
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
object OrderModule{

    @Provides
    fun provideSoldProductsLiveData()=MutableLiveData<List<SoldProduct>>(null)

    @Provides
    fun provideOrderInfoLiveData()=MutableLiveData<Order>(null)
}