package com.sherifnasser.themarketmanager.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.sherifnasser.themarketmanager.repository.DashboardRepository

class DashboardViewModel
@ViewModelInject
constructor(
    private val dashboardRepository:DashboardRepository
):ViewModel(){

    private val _lastOrder by lazy{dashboardRepository.lastOrder}

    private val _unavailableProductsCount by lazy{dashboardRepository.unavailableProductsCount}

    private val _todayRevenue by lazy{dashboardRepository.todayRevenue}

    private val _todayOrdersDoneCount by lazy{dashboardRepository.todayOrdersDoneCount}

    val lastOrder get()=_lastOrder
    val unavailableProductsCount get()=_unavailableProductsCount
    val todayRevenue get()=_todayRevenue
    val todayOrdersDoneCount get()=_todayOrdersDoneCount
}