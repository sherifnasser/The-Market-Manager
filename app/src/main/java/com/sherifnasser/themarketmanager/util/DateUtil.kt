package com.sherifnasser.themarketmanager.util

import java.util.*

data class DateInterval(val from:Long,val to:Long){

    fun forEachDay(block:(day:Date)->Unit){
        Calendar.getInstance().apply{
            time=Date(from)
            while(time.time<=to){
                block.invoke(time)
                add(Calendar.DAY_OF_MONTH,1)
            }
        }
    }

}

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

fun Calendar.getCalendarAfterAdd(field:Int,amount:Int):Calendar{
    val calendarAfter=Calendar.getInstance()
    add(field,amount)
    calendarAfter.time=Date(time.time)

    add(field,-1*amount)

    return calendarAfter
}