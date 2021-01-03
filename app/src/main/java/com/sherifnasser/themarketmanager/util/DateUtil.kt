package com.sherifnasser.themarketmanager.util

import java.util.Date
import java.util.GregorianCalendar

val Date.startOfDay:Date get(){
    val calendar=GregorianCalendar().also{
        it.time=this
    }
    return GregorianCalendar(
        calendar[GregorianCalendar.YEAR],
        calendar[GregorianCalendar.MONTH],
        calendar[GregorianCalendar.DAY_OF_MONTH]
    ).time
}