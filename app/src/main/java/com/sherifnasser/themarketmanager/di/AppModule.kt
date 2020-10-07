package com.sherifnasser.themarketmanager.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import java.text.DateFormat
import java.text.SimpleDateFormat

@Module
@InstallIn(ApplicationComponent::class)
object AppModule{
    @Provides
    fun provideInt()=0

    @Provides
    fun provideDouble()=0.0

    @Provides
    fun provideString()=""

    @Provides
    fun provideSimpleDateFormat():DateFormat=SimpleDateFormat.getDateTimeInstance()
}