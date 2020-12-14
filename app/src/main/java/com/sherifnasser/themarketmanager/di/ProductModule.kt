package com.sherifnasser.themarketmanager.di

import androidx.lifecycle.MutableLiveData
import com.sherifnasser.themarketmanager.database.TheMarketManagerDatabase
import com.sherifnasser.themarketmanager.database.model.Product
import com.sherifnasser.themarketmanager.ui.viewmodel.ProductViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
object ProductModule{

    @Provides
    fun provideProductDao(db:TheMarketManagerDatabase)=db.productDao()

    @Provides
    fun provideProductNameQueryLiveData()=MutableLiveData<String>(null)

    @Provides
    fun provideProductsFilterLiveData()=MutableLiveData(ProductViewModel.ALL_PRODUCTS_FILTER)

    @Provides
    fun provideProductInfoLiveData()=MutableLiveData<Product>(null)
}