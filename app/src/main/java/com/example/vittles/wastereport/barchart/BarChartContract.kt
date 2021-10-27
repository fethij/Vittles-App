package com.example.vittles.wastereport.barchart

import com.example.domain.wasteReport.BarChartEntry
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import org.joda.time.DateTime


/**
 * MVP Contract for bar chart.
 *
 */
interface BarChartContract {

    interface View {
        fun setupBarChartDataEaten(barChartEntries: List<BarChartEntry>)
        fun setupBarChartDataExpired(barChartEntries: List<BarChartEntry>)
        fun setupDesign(barChart: BarChart)
        fun setupCharts(barChartData: List<BarChartEntry>)
        fun setDataEaten(barChartEntries: List<BarChartEntry>): BarData
        fun setDataExpired(barChartEntries: List<BarChartEntry>): BarData
        fun setYAxisRenderer(barChart: BarChart)
        fun setXAxisLabels(barChartEntries: List<BarChartEntry>, barChart: BarChart)
        fun setRadius(barChart: BarChart)
        fun fail()

    }

    interface Presenter {
        fun getWasteReportProducts(date: DateTime)

    }
}