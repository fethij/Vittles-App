package com.example.vittles.wastereport.barchart

import android.graphics.Canvas
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.renderer.YAxisRenderer
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.ViewPortHandler
import android.graphics.Paint.Align
import android.opengl.ETC1.getWidth
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition
import com.github.mikephil.charting.components.YAxis.AxisDependency
import com.github.mikephil.charting.utils.Utils

/**
 * Custom Y Axis Renderer. Rotates y labels 180°.
 *
 *
 * @property xAxis X axis of the bar chart.
 *
 * @param viewPortHandler View port handler of the bar chart.
 * @param yAxis Y axis of the bar chart whose labels are to be rotated.
 * @param trans Transformer of the bar chart.
 * @param density screen density
 */


class CustomYAxisRenderer(
    viewPortHandler: ViewPortHandler,
    yAxis: YAxis,
    trans: Transformer,
    private val xAxis: XAxis,
    private val density: Float
) : YAxisRenderer(viewPortHandler, yAxis, trans) {


    /**
     * Enumerator for screen density.
     *
     *
     * @property value Value of the density.
     */
    enum class Density(val value: Float) {
        /** Indicates the density hdpi. */
        HDPI(1.5f) {
            /** {@inheritDoc} */
            override fun calculateOffset() = -13
        },
        /** Indicates the density xhdpi. */
        XHDPI(2f){
            /** {@inheritDoc} */
            override fun calculateOffset() = 0
        },
        /**Indicates the density xxhdpi. */
        XXHDPI(3f){
            /** {@inheritDoc} */
            override fun calculateOffset() = 21
        },
        /** Indicates the density xxxhdpi. */
        XXXHDPI(4f){
            /** {@inheritDoc} */
            override fun calculateOffset() = 46
        };

        /**
         * Calculates offset
         *
         * @return offset
         */
        abstract fun calculateOffset(): Int
    }

    /** {@inheritDoc} */
    override fun renderAxisLabels(c: Canvas) {
        var actualOffset = 0
        when {
            density <= Density.HDPI.value -> {
                actualOffset = Density.HDPI.calculateOffset()
            }
            density <= Density.XHDPI.value -> {
                actualOffset = Density.XHDPI.calculateOffset()
            }
            density <= Density.XXHDPI.value -> {
                actualOffset = Density.XXHDPI.calculateOffset()
            }
            density <= Density.XXXHDPI.value || density > Density.XXXHDPI.value -> {
                actualOffset = Density.XXXHDPI.calculateOffset()
            }
        }

        c.translate(c.width.toFloat(), 0f)
        c.rotate(180f)
        c.translate(0f, -c.height.toFloat() - xAxis.mLabelHeight - xAxis.yOffset + actualOffset)

        if (!mYAxis.isEnabled || !mYAxis.isDrawLabelsEnabled)
            return

        super.renderAxisLabels(c)
    }
}
