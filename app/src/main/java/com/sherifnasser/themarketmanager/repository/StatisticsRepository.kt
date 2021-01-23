package com.sherifnasser.themarketmanager.repository

import com.sherifnasser.themarketmanager.database.dao.OrdersDayDao
import java.util.Date
import javax.inject.Inject

class StatisticsRepository
@Inject
constructor(
    private val ordersDayDao:OrdersDayDao
){

    suspend fun getBetween(from:Date,to:Date)=ordersDayDao.getBetween(from,to)

}