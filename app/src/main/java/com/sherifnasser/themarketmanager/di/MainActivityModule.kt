package com.sherifnasser.themarketmanager.di

import com.sherifnasser.themarketmanager.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@Module
@InstallIn(ApplicationComponent::class)
object MainActivityModule{

    @Provides
    fun provideTopLevelDestinationIds()=setOf(
        R.id.nav_dashboard,
        R.id.nav_statistics,
        R.id.nav_orders,
        R.id.nav_store
    )
}