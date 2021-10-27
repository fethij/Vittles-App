package com.example.vittles.wastereport.barchart

import android.graphics.*

import com.github.mikephil.charting.utils.Utils.convertDpToPixel
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.utils.ViewPortHandler
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.renderer.BarChartRenderer

/**
 * Renderer for adding radius to bar chart entries
 *
 * https://stackoverflow.com/questions/30761082/mpandroidchart-round-edged-bar-chart
 *
 *
 * @param chart
 * @param animator
 * @param viewPortHandler
 */
class CustomBarChartRender(
    chart: BarDataProvider,
    animator: ChartAnimator,
    viewPortHandler: ViewPortHandler
) : BarChartRenderer(chart, animator, viewPortHandler) {

    /** @suppress */
    private val mBarShadowRectBuffer = RectF()
    /** @suppress */
    private var mRadius: Int = 0

    /**
     * Sets radius
     *
     * @param mRadius Radius to be set
     */
    fun setRadius(mRadius: Int) {
        this.mRadius = mRadius
    }

    /** {@inheritDoc} */
    override fun drawDataSet(c: Canvas, dataSet: IBarDataSet, index: Int) {

        val trans = mChart.getTransformer(dataSet.axisDependency)
        mBarBorderPaint.color = dataSet.barBorderColor
        mBarBorderPaint.strokeWidth = convertDpToPixel(dataSet.barBorderWidth)
        mShadowPaint.color = dataSet.barShadowColor
        val drawBorder = dataSet.barBorderWidth > 0f

        val phaseX = mAnimator.phaseX
        val phaseY = mAnimator.phaseY

        if (mChart.isDrawBarShadowEnabled) {
            mShadowPaint.color = dataSet.barShadowColor

            val barData = mChart.barData

            val barWidth = barData.barWidth
            val barWidthHalf = barWidth / 2.0f
            var x: Float

            var i = 0
            val count = Math.min(
                Math.ceil((dataSet.entryCount.toFloat() * phaseX).toDouble().toInt().toDouble()),
                dataSet.entryCount.toDouble()
            )
            while (i < count) {

                val e = dataSet.getEntryForIndex(i)

                x = e.x

                mBarShadowRectBuffer.left = x - barWidthHalf
                mBarShadowRectBuffer.right = x + barWidthHalf

                trans.rectValueToPixel(mBarShadowRectBuffer)

                if (!mViewPortHandler.isInBoundsLeft(mBarShadowRectBuffer.right)) {
                    i++
                    continue
                }

                if (!mViewPortHandler.isInBoundsRight(mBarShadowRectBuffer.left))
                    break

                mBarShadowRectBuffer.top = mViewPortHandler.contentTop()
                mBarShadowRectBuffer.bottom = mViewPortHandler.contentBottom()

                c.drawRoundRect(mBarRect, mRadius.toFloat(), mRadius.toFloat(), mShadowPaint)
                i++
            }
        }

        // initialize the buffer
        val buffer = mBarBuffers[index]
        buffer.setPhases(phaseX, phaseY)
        buffer.setDataSet(index)
        buffer.setInverted(mChart.isInverted(dataSet.axisDependency))
        buffer.setBarWidth(mChart.barData.barWidth)

        buffer.feed(dataSet)

        trans.pointValuesToPixel(buffer.buffer)

        val isSingleColor = dataSet.colors.size == 1

        if (isSingleColor) {
            mRenderPaint.color = dataSet.color
        }

        var j = 0
        while (j < buffer.size()) {

            if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2])) {
                j += 4
                continue
            }

            if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j]))
                break

            if (!isSingleColor) {
                // Set the color for the currently drawn date. If the index
                // is out of bounds, reuse colors.
                mRenderPaint.color = dataSet.getColor(j / 4)
            }

            if (dataSet.gradientColor != null) {
                val gradientColor = dataSet.gradientColor
                mRenderPaint.shader = LinearGradient(
                    buffer.buffer[j],
                    buffer.buffer[j + 3],
                    buffer.buffer[j],
                    buffer.buffer[j + 1],
                    gradientColor.startColor,
                    gradientColor.endColor,
                    Shader.TileMode.MIRROR
                )
            }

            if (dataSet.gradientColors != null) {
                mRenderPaint.shader = LinearGradient(
                    buffer.buffer[j],
                    buffer.buffer[j + 3],
                    buffer.buffer[j],
                    buffer.buffer[j + 1],
                    dataSet.getGradientColor(j / 4).startColor,
                    dataSet.getGradientColor(j / 4).endColor,
                    Shader.TileMode.MIRROR
                )
            }
            val path2 = roundRect(
                RectF(
                    buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                    buffer.buffer[j + 3]
                ), mRadius.toFloat(), mRadius.toFloat(), true, true, false, false
            )
            c.drawPath(path2, mRenderPaint)
            if (drawBorder) {
                val path = roundRect(
                    RectF(
                        buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                        buffer.buffer[j + 3]
                    ), mRadius.toFloat(), mRadius.toFloat(), true, true, false, false
                )
                c.drawPath(path, mBarBorderPaint)
            }
            j += 4
        }

    }

    private fun roundRect(
        rect: RectF,
        rx: Float,
        ry: Float,
        tl: Boolean,
        tr: Boolean,
        br: Boolean,
        bl: Boolean
    ): Path {
        var radiusX = rx
        var radiusY = ry
        val top = rect.top
        val left = rect.left
        val right = rect.right
        val bottom = rect.bottom
        val path = Path()
        if (radiusX < 0) radiusX = 0f
        if (radiusY < 0) radiusY = 0f
        val width = right - left
        val height = bottom - top
        if (radiusX > width / 2) radiusX = width / 2
        if (radiusY > height / 2) radiusY = height / 2
        val widthMinusCorners = width - 2 * radiusX
        val heightMinusCorners = height - 2 * radiusY

        path.moveTo(right, top + radiusY)
        if (tr)
            path.rQuadTo(0f, -radiusY, -radiusX, -radiusY)//top-right corner
        else {
            path.rLineTo(0f, -radiusY)
            path.rLineTo(-radiusX, 0f)
        }
        path.rLineTo(-widthMinusCorners, 0f)
        if (tl)
            path.rQuadTo(-radiusX, 0f, -radiusX, radiusY) //top-left corner
        else {
            path.rLineTo(-radiusX, 0f)
            path.rLineTo(0f, radiusY)
        }
        path.rLineTo(0f, heightMinusCorners)

        if (bl)
            path.rQuadTo(0f, radiusY, radiusX, radiusY)//bottom-left corner
        else {
            path.rLineTo(0f, radiusY)
            path.rLineTo(radiusX, 0f)
        }

        path.rLineTo(widthMinusCorners, 0f)
        if (br)
            path.rQuadTo(radiusX, 0f, radiusX, -radiusY) //bottom-right corner
        else {
            path.rLineTo(radiusX, 0f)
            path.rLineTo(0f, -radiusY)
        }

        path.rLineTo(0f, -heightMinusCorners)

        path.close()//Given close, last lineto can be removed.

        return path
    }

}