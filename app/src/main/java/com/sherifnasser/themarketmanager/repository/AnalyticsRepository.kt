package com.sherifnasser.themarketmanager.repository

import com.sherifnasser.themarketmanager.database.dao.OrdersDayDao
import javax.inject.Inject

class AnalyticsRepository
@Inject
constructor(
    ordersDayDao:OrdersDayDao
){
    private val _allOrdersDays by lazy{ordersDayDao.getAll()}

    val allOrdersDay get()=_allOrdersDays

}