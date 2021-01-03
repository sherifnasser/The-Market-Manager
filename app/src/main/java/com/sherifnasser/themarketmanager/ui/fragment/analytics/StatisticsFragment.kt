package com.sherifnasser.themarketmanager.ui.fragment.analytics

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.sherifnasser.themarketmanager.R
import com.sherifnasser.themarketmanager.databinding.FragmentStatisticsBinding
import com.sherifnasser.themarketmanager.ui.viewmodel.AnalyticsViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class StatisticsFragment:Fragment(){

    private var binding:FragmentStatisticsBinding?=null
    private val analyticsViewModel by viewModels<AnalyticsViewModel>()

    override fun onCreateView(inflater:LayoutInflater,container:ViewGroup?,savedInstanceState:Bundle?):View{
        binding=FragmentStatisticsBinding.inflate(inflater,container,false)
        return binding!!.root
    }

    override fun onDestroyView(){
        binding=null
        super.onDestroyView()
    }

    override fun onViewCreated(view:View,savedInstanceState:Bundle?){
        super.onViewCreated(view,savedInstanceState)

        setupRevenueLineChart()

        setupOrdersDoneCountBarChart()


        analyticsViewModel.ordersDay.observe(viewLifecycleOwner,{ordersDays->
            val daysXAxisData=arrayListOf<String>()
            val revenueYAxisData=arrayListOf<Double>()
            val orderDoneCountYAxisData=arrayListOf<Int>()

            ordersDays?.forEach{
                daysXAxisData.add(SimpleDateFormat("MMM d",Locale.getDefault()).format(it.day))
                revenueYAxisData.add(it.revenue)
                orderDoneCountYAxisData.add(it.ordersDoneCount)
            }
            updateRevenueLineChart(daysXAxisData,revenueYAxisData)
            updateOrdersDoneCountBarChart(daysXAxisData,orderDoneCountYAxisData)
        })

    }

    private fun setupRevenueLineChart(){
        with(binding!!.revenueLineChart){
            description.isEnabled=false

            with(xAxis){
                position=XAxis.XAxisPosition.BOTTOM
                granularity=1f
                axisMinimum=0f
            }
            with(axisLeft){
                granularity=1f
                axisMinimum=0f
            }
            axisRight.isEnabled=false
        }
    }

    private fun setupOrdersDoneCountBarChart(){
        with(binding!!.ordersDoneCountBarChart){
            description.isEnabled=false
            setDrawValueAboveBar(false)

            with(xAxis){
                position=XAxis.XAxisPosition.BOTTOM
                granularity=1f
                axisMinimum=0f
            }
            with(axisLeft){
                granularity=1f
                axisMinimum=0f
            }
            axisRight.isEnabled=false
        }
    }

    private fun updateRevenueLineChart(daysXAxisData:List<String>,revenueYAxisData:List<Double>){
        val values=arrayListOf<Entry>()
        for(i in daysXAxisData.indices){
            val x=i.toFloat()
            val y=revenueYAxisData[i].toFloat()
            values.add(Entry(x,y))
        }

        val lineDataSet=LineDataSet(values,getString(R.string.revenue)).apply{
            valueTextColor=Color.BLACK
            valueTextSize=8f
        }

        with(binding!!.revenueLineChart){
            xAxis.valueFormatter=IndexAxisValueFormatter(daysXAxisData)
            data=LineData(lineDataSet)
            invalidate()
        }
    }

    private fun updateOrdersDoneCountBarChart(daysXAxisData:List<String>,orderDoneCountYAxisData:List<Int>){
        val values=arrayListOf<BarEntry>()
        for(i in daysXAxisData.indices){
            val x=i.toFloat()
            val y=orderDoneCountYAxisData[i].toFloat()
            values.add(BarEntry(x,y))
        }

        val barDataSet=BarDataSet(values,getString(R.string.orders_done_count)).apply{
            valueTextColor=Color.BLACK
            valueTextSize=8f
        }

        with(binding!!.ordersDoneCountBarChart){
            xAxis.valueFormatter=IndexAxisValueFormatter(daysXAxisData)
            data=BarData(barDataSet).apply{
                barWidth=0.375f
            }
            invalidate()
        }
    }
}
