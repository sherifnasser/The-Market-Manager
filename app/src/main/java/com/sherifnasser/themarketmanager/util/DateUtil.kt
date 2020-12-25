package com.sherifnasser.themarketmanager.util

import java.util.Date
import java.util.GregorianCalendar

// the properties day,month,year are deprecated so I defined them this way

val Date.dayOfDate:Int get(){
    val tmpCal=GregorianCalendar().apply{
        time=this@dayOfDate
    }
    return tmpCal[GregorianCalendar.DAY_OF_MONTH]
}

val Date.monthOfDate:Int get(){
    val tmpCal=GregorianCalendar().apply{
        time=this@monthOfDate
    }
    return tmpCal[GregorianCalendar.MONTH]
}

val Date.yearOfDate:Int get(){
    val tmpCal=GregorianCalendar().apply{
        time=this@yearOfDate
    }
    return tmpCal[GregorianCalendar.YEAR]
}