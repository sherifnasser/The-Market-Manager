package com.sherifnasser.themarketmanager.util

 import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
 import com.github.mikephil.charting.formatter.ValueFormatter
 import com.sherifnasser.themarketmanager.database.model.OrdersDay
import java.text.DateFormat

data class OrdersDaysChart(
    val ordersDays:List<OrdersDay>,
    private val dateInterval:DateInterval
){

    private var revenueEntries:List<Entry>?=null
    private var doneOrdersCountEntries:List<Entry>?=null

    private var _xAxisValueFormatter:ValueFormatter?=null

    private var createRevenueEntry:((Float,Float)->Entry)?=null
    private var createDoneOrdersCountEntry:((Float, Float)->Entry)?=null

    val xAxisValueFormatter get()=_xAxisValueFormatter

    @Suppress("UNCHECKED_CAST")
    fun< E:Entry>getRevenueEntries(dayFormat:DateFormat,createRevenueEntry:(day:Float,revenue:Float)->E):List<E>{
        this.createRevenueEntry=createRevenueEntry
        val entries=provideEntries(
            listOfEntries=revenueEntries,
            dayFormat=dayFormat,
            oldCreateEntry=this.createRevenueEntry,
            newCreateEntry=createRevenueEntry,
            getY={it.revenue.toFloat()}
        )
        revenueEntries=entries
        return revenueEntries as List<E>
    }

    @Suppress("UNCHECKED_CAST")
    fun< E:Entry>getDoneOrdersCountEntries(dayFormat:DateFormat,createOrdersDoneCountEntry:(day:Float,ordersDoneCount:Float)->E):List<E>{
        this.createDoneOrdersCountEntry=createOrdersDoneCountEntry
        val entries=provideEntries(
            listOfEntries=doneOrdersCountEntries,
            dayFormat=dayFormat,
            oldCreateEntry=this.createDoneOrdersCountEntry,
            newCreateEntry=createOrdersDoneCountEntry,
            getY={it.doneOrdersCount.toFloat()}
        )
        doneOrdersCountEntries=entries
        return entries as List<E>
    }

    private fun<E:Entry>provideEntries(
        listOfEntries:List<E>?,
        dayFormat:DateFormat,
        oldCreateEntry:((x:Float,y:Float)->Entry)?,
        newCreateEntry:(x:Float,y:Float)->E,
        getY:(OrdersDay)->Float
    ):List<E>{
        var entries=listOfEntries
        if(
            entries==null
            ||
            oldCreateEntry?.invoke(0f,0f)?.javaClass==newCreateEntry(0f,0f).javaClass
        ){
            entries=ArrayList()
            val daysXAxisData=ArrayList<String>()
            var i=0
            var j=0
            dateInterval.forEachDay{day->
                daysXAxisData.add(dayFormat.format(day))
                val x=i.toFloat()
                val y=
                    if(ordersDays.isNotEmpty()){
                        ordersDays[j].let{
                            if(it.day!=day)0f
                            else{
                                if(j+1<ordersDays.size)j++
                                getY(it)
                            }
                        }
                    }else 0f

                (entries as MutableList).add(newCreateEntry(x,y))
                i++
            }
            _xAxisValueFormatter=IndexAxisValueFormatter(daysXAxisData)
        }
        return entries
    }
}