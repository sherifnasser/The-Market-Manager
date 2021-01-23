package com.sherifnasser.themarketmanager.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.sherifnasser.themarketmanager.repository.StatisticsRepository
import com.sherifnasser.themarketmanager.util.DateInterval
import com.sherifnasser.themarketmanager.util.OrdersDaysChart
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.runBlocking
import java.util.Date

class StatisticsViewModel
@ViewModelInject
constructor(
    private val statisticsRepository:StatisticsRepository,
    private val _revenueChartTimeFilter:MutableLiveData<DateInterval>,
    private val _ordersDoneCountChartTimeFilter:MutableLiveData<DateInterval>,
    private val ioDispatcher:CoroutineDispatcher
):ViewModel(){

    private val _revenueChartOrdersDays by lazy{
        revenueChartTimeFilter.switchMap{timeFilter->
            getOrdersDaysChartByTimeFilter(timeFilter)
        }
    }

    private val _ordersDoneCountChartOrdersDays by lazy{
        ordersDoneCountChartTimeFilter.switchMap{timeFilter->
            getOrdersDaysChartByTimeFilter(timeFilter)
        }
    }

    private fun getOrdersDaysChartByTimeFilter(timeFilter:DateInterval):LiveData<OrdersDaysChart> =
        runBlocking(ioDispatcher){
            val from=Date(timeFilter.from)
            val to=Date(timeFilter.to)
            val ordersDays=statisticsRepository.getBetween(from,to)
            return@runBlocking MutableLiveData(
                OrdersDaysChart(
                    ordersDays=ordersDays,
                    dateInterval=timeFilter
                )
            )
        }


    val revenueChartOrdersDays get()=_revenueChartOrdersDays

    val ordersDoneCountChartOrdersDays get()=_ordersDoneCountChartOrdersDays

    val revenueChartTimeFilter get()=_revenueChartTimeFilter

    val ordersDoneCountChartTimeFilter get()=_ordersDoneCountChartTimeFilter
}