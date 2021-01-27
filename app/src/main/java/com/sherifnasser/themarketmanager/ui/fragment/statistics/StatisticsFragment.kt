package com.sherifnasser.themarketmanager.ui.fragment.statistics

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.sherifnasser.themarketmanager.R
import com.sherifnasser.themarketmanager.databinding.FragmentStatisticsBinding
import com.sherifnasser.themarketmanager.di.OrdersDaysChartDayFormat
import com.sherifnasser.themarketmanager.ui.viewmodel.StatisticsViewModel
import com.sherifnasser.themarketmanager.util.ChartDecimalValueFormatter
import com.sherifnasser.themarketmanager.util.DateInterval
import dagger.hilt.android.AndroidEntryPoint
import java.text.DateFormat
import javax.inject.Inject

@AndroidEntryPoint
class StatisticsFragment:Fragment(){

    private var binding:FragmentStatisticsBinding?=null
    private val statisticsViewModel by viewModels<StatisticsViewModel>()

    @Inject lateinit var timeFiltersWithStrResIds:Map<DateInterval,Int>

    @OrdersDaysChartDayFormat
    @Inject lateinit var ordersDaysChartDayFormat:DateFormat

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

        setupTimeFilters()

        setupRevenueLineChart()

        setupOrdersDoneCountBarChart()
    }

    private fun setupTimeFilters(){

        val timeFiltersStrList=timeFiltersWithStrResIds.values.map{getString(it)}

        val chartsMap=hashMapOf(
            binding!!.revenueTimeFilter to statisticsViewModel.revenueChartTimeFilter,
            binding!!.ordersDoneCountTimeFilter to statisticsViewModel.ordersDoneCountChartTimeFilter
        )

        chartsMap.forEach{entry->
            val chartTimeFilter=entry.key
            val timeFilterLiveData=entry.value

            (chartTimeFilter.editText as AutoCompleteTextView).apply{
                // set text
                timeFiltersWithStrResIds[timeFilterLiveData.value!!]?.let(::setText)

                // when user change filter update livedata
                setOnItemClickListener{_,_,position,_->
                    val timeFilter=timeFiltersWithStrResIds.keys.toList()[position]
                    timeFilterLiveData.value=timeFilter
                }

                // set the adapter
                setAdapter(
                    ArrayAdapter(requireContext(),R.layout.auto_complete_text_view_list_item_layout,timeFiltersStrList)
                )
            }
        }

    }

    private fun setupRevenueLineChart(){
        with(binding!!.revenueLineChart){
            setupChart(this)
            axisLeft.valueFormatter=LargeValueFormatter()
            xAxis.setDrawGridLines(false)
            marker=RevenueChartMarkerView(context,R.layout.chart_marker_view_layout).also{
                it.chartView=this
            }
        }


        statisticsViewModel.revenueChartOrdersDays.observe(viewLifecycleOwner){ordersDaysChart->
            val entries=
                ordersDaysChart.getRevenueEntries(dayFormat=ordersDaysChartDayFormat){day,revenue->
                    Entry(day,revenue)
                }

            val lineDataSet=LineDataSet(entries,getString(R.string.revenue)).apply{
                setDrawValues(false)
                setDrawCircles(false)
                lineWidth=3.5f
            }

            with(binding!!.revenueLineChart){
                xAxis.valueFormatter=ordersDaysChart.xAxisValueFormatter
                data=LineData(lineDataSet)
                animateX(600)
            }
        }
    }

    private fun setupOrdersDoneCountBarChart(){
        with(binding!!.ordersDoneCountBarChart){
            description.isEnabled=false
            setupChart(this)
        }

        statisticsViewModel.ordersDoneCountChartOrdersDays.observe(viewLifecycleOwner,{ordersDaysChart->
            val entries=
                ordersDaysChart.getDoneOrdersCountEntries(dayFormat=ordersDaysChartDayFormat){ day, revenue->
                    BarEntry(day,revenue)
                }

            val barDataSet=BarDataSet(entries,getString(R.string.done_orders_count)).apply{
                valueTextColor=Color.BLACK
                valueTextSize=8f
            }

            with(binding!!.ordersDoneCountBarChart){
                xAxis.valueFormatter=ordersDaysChart.xAxisValueFormatter
                data=BarData(barDataSet).apply{
                    barWidth=0.375f
                }
                data.setValueFormatter(ChartDecimalValueFormatter())
                animateY(600)
            }
        })
    }

    private fun setupChart(chart:BarLineChartBase<*>){
        with(chart){
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
            setScaleEnabled(false)
        }
    }

    private class RevenueChartMarkerView(
        context:Context?,
        layoutResource:Int
    ):MarkerView(context,layoutResource){

        private val xTextView by lazy{findViewById<TextView>(R.id.x_value_text_view)}
        private val yTextView by lazy{findViewById<TextView>(R.id.y_value_text_view)}
        private val mpPointF by lazy{
            MPPointF((-width/2).toFloat(),-(height/2).toFloat())
        }

        override fun refreshContent(e:Entry?,highlight:Highlight?){
            e?.let{
                with(chartView as BarLineChartBase<*>){
                    xTextView.text=xAxis.valueFormatter.getFormattedValue(e.x)
                    yTextView.text=e.y.toString()
                }
            }
            super.refreshContent(e,highlight)
        }

        override fun getOffset()=mpPointF

        override fun draw(canvas:Canvas?,posX:Float,posY:Float){
            super.draw(canvas,posX,posY)
            getOffsetForDrawingAtPoint(posX,posY)
        }
    }
}
