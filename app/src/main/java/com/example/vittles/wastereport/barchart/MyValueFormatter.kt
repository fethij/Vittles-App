package com.example.vittles.wastereport.barchart

import java.text.DecimalFormat
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter

/**
 * Class for formatting axis labels
 *
 */
class MyValueFormatter : ValueFormatter() {

    /** @suppress*/
    private val format = DecimalFormat("###")

    /** {@inheritDoc} */
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        return format.format(100 - value.toInt())
    }
}