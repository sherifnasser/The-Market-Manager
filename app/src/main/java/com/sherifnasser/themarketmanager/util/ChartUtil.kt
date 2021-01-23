package com.sherifnasser.themarketmanager.util

import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DecimalFormat

class ChartDecimalValueFormatter:ValueFormatter(){
    private val decimalFormatter=DecimalFormat("###,###,##0.00")
    override fun getFormattedValue(value:Float):String{
        if(value==0f)return ""
        return decimalFormatter.format(value)
    }
}