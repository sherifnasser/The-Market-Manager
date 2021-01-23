package com.sherifnasser.themarketmanager.di

import androidx.lifecycle.MutableLiveData
import com.sherifnasser.themarketmanager.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import com.sherifnasser.themarketmanager.util.DateInterval
import com.sherifnasser.themarketmanager.util.getCalendarAfterAdd
import com.sherifnasser.themarketmanager.util.startOfDay
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.collections.LinkedHashMap

@Module
@InstallIn(ActivityRetainedComponent::class)
object StatisticsModule{

    @Provides
    @ActivityRetainedScoped
    fun provideTimeFiltersWithStrResIdsMap():Map<DateInterval,Int>{

        val timeFiltersHashMap=LinkedHashMap<DateInterval,Int>()

        val calendar=Calendar.getInstance()
        val today=calendar.time.startOfDay.time

        val createTimeFilterAfterAdd={field:Int,amount:Int->
            DateInterval(
                from=calendar.getCalendarAfterAdd(field,amount).time.startOfDay.time,
                to=today
            )
        }

        val fromWeekAgoToToday=createTimeFilterAfterAdd(Calendar.WEEK_OF_YEAR,-1)
        val fromMonthAgoToToday=createTimeFilterAfterAdd(Calendar.MONTH,-1)
        val from2MonthsAgoToToday=createTimeFilterAfterAdd(Calendar.MONTH,-2)
        val fromYearAgoToToday=createTimeFilterAfterAdd(Calendar.YEAR,-1)

        timeFiltersHashMap[fromWeekAgoToToday]=R.string.last_week
        timeFiltersHashMap[fromMonthAgoToToday]=R.string.last_month
        timeFiltersHashMap[from2MonthsAgoToToday]=R.string.last_2_months
        timeFiltersHashMap[fromYearAgoToToday]=R.string.last_year

        return timeFiltersHashMap
    }

    @Provides
    fun provideDateIntervalLiveData(availableTimeFilters:Map<DateInterval,Int>)=
        MutableLiveData(availableTimeFilters.keys.toList().first())

    @Provides
    @OrdersDaysChartDayFormat
    fun provideOrdersDaysChartDayFormat():DateFormat=SimpleDateFormat("MMM dd",Locale.getDefault())
}