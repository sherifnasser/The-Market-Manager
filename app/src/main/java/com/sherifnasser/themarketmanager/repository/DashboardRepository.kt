package com.sherifnasser.themarketmanager.repository

import com.sherifnasser.themarketmanager.database.dao.OrderDao
import com.sherifnasser.themarketmanager.database.dao.OrdersDayDao
import com.sherifnasser.themarketmanager.database.dao.ProductDao
import javax.inject.Inject

class DashboardRepository
@Inject
constructor(
    private val productDao:ProductDao,
    private val orderDao:OrderDao,
    private val ordersDayDao:OrdersDayDao
){
    private val _lastOrder by lazy{orderDao.getLastOrder()}

    private val _unavailableProductsCount by lazy{productDao.getUnavailableProductsCount()}

    private val _todayRevenue by lazy{ordersDayDao.getTodayRevenue()}

    private val _todayOrdersDoneCount by lazy{ordersDayDao.getTodayDoneOrdersCount()}

    val lastOrder get()=_lastOrder
    val unavailableProductsCount get()=_unavailableProductsCount
    val todayRevenue get()=_todayRevenue
    val todayOrdersDoneCount get()=_todayOrdersDoneCount
}