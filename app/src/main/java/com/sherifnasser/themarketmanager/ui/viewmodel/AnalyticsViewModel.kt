package com.sherifnasser.themarketmanager.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.sherifnasser.themarketmanager.repository.AnalyticsRepository

class AnalyticsViewModel
@ViewModelInject
constructor(
    analyticsRepository:AnalyticsRepository
):ViewModel(){
    private val _allOrdersDays by lazy{analyticsRepository.allOrdersDay}

    private val _ordersDay by lazy{_allOrdersDays}

    val ordersDay get()=_ordersDay
}