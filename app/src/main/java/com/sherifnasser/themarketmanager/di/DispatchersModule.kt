package com.sherifnasser.themarketmanager.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(ApplicationComponent::class)
object DispatchersModule{
    
    @Provides fun provideIoDispatcher()=Dispatchers.IO
    
}