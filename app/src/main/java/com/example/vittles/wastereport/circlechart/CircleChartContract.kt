package com.example.vittles.wastereport.circlechart

import org.joda.time.DateTime


/**
 * MVP Contract for circle chart.
 *
 */
interface CircleChartContract {

    interface View {
        fun drawCircleChart(percent: Int)
        fun setNoResultsView()


    }

    interface Presenter {
        fun getEatenPercent(date: DateTime, vittlesEaten: Int, vittlesExpired: Int)
    }
}