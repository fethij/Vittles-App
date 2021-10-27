package com.example.vittles.wastereport.barchart


import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.domain.wasteReport.BarChartEntry
import com.example.vittles.R
import com.example.vittles.enums.TimeRangeSteps
import com.example.vittles.wastereport.circlechart.RefreshData
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_bar_chart.*
import org.joda.time.DateTime
import org.joda.time.Days
import javax.inject.Inject

/**
 * Fragment class for the circle chart.
 *
 *
 * @property date From this date up to now the bar chart is calculated
 */
class BarChartFragment @Inject internal constructor(var date: DateTime) : DaggerFragment(),
    BarChartContract.View, RefreshData {

    /**
     * The presenter of the fragment.
     */
    @Inject
    lateinit var presenter: BarChartPresenter

    /** @suppress */
    var timeRangeSteps = 0

    /** {@inheritDoc} */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bar_chart, container, false)
    }

    /** {@inheritDoc} */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        timeRangeSteps = Days.daysBetween(date, DateTime.now().withTimeAtStartOfDay()).days
        presenter.start(this)
        presenter.getWasteReportProducts(date)
    }

    /** {@inheritDoc} */
    override fun onDestroy() {
        super.onDestroy()
        presenter.destroy()
    }

    /** {@inheritDoc} */
    override fun refresh(date: DateTime, vittlesEaten: Int, vittlesExpired: Int) {
        this.date = date
        timeRangeSteps = Days.daysBetween(date, DateTime.now().withTimeAtStartOfDay()).days
        if (timeRangeSteps == TimeRangeSteps.LAST_YEAR.steps) {
            timeRangeSteps = TimeRangeSteps.MONTH_YEAR.steps
        }
        presenter.getWasteReportProducts(date)
    }

    /**
     * Calls methods to draw charts.
     *
     * @param barChartData Data to be shown in the charts.
     */
    override fun setupCharts(barChartData: List<BarChartEntry>) {
        setupBarChartDataEaten(barChartData)
        setupBarChartDataExpired(barChartData)
    }

    /**
     * Draws chart which shows percentage of eaten products
     *
     * @param barChartEntries Data to be shown in the charts.
     */
    override fun setupBarChartDataEaten(barChartEntries: List<BarChartEntry>) {

        val barData = setDataEaten(barChartEntries)
        setRadius(barChartEaten)

        barChartEaten.apply {
            data = barData
            xAxis.isEnabled = false
        }

        setupDesign(barChartEaten)
    }

    /**
     * Draws chart which shows percentage of expired products
     *
     * @param barChartEntries Data to be shown in the charts.
     */
    override fun setupBarChartDataExpired(barChartEntries: List<BarChartEntry>) {
        val barData = setDataExpired(barChartEntries)
        setRadius(barChartExpired)

        setYAxisRenderer(barChartExpired)

        barChartExpired.apply {
            data = barData
            rotation = 180f
            axisRight.valueFormatter = MyValueFormatter()
            axisLeft.valueFormatter = MyValueFormatter()
        }

        setupDesign(barChartExpired)
        setXAxisLabels(barChartEntries, barChartExpired)
    }

    /**
     * Sets bar data for bar chart of eaten products
     *
     * @param barChartEntries Data to be shown in the charts.
     * @return Bar data to be displayed
     */
    override fun setDataEaten(barChartEntries: List<BarChartEntry>): BarData {
        val barGroup = ArrayList<BarEntry>()
        for (i in 0 until timeRangeSteps) {
            if (timeRangeSteps == TimeRangeSteps.MONTH_YEAR.steps) {
                barGroup.add(BarEntry(i.toFloat(), barChartEntries[i].vittlesEatenPercent, i.toString()))

            } else {
                barGroup.add(
                    BarEntry(
                        i.toFloat(),
                        barChartEntries[timeRangeSteps - 1 - i].vittlesEatenPercent,
                        i.toString()
                    )
                )
            }
        }
        val barDataSet = BarDataSet(barGroup, "")
        barDataSet.color = Color.rgb(38, 243, 145)
        return BarData(barDataSet)
    }

    /**
     * Sets bar data for bar chart of expired products
     *
     * @param barChartEntries Data to be shown in the charts.
     * @return Bar data to be displayed
     */
    override fun setDataExpired(barChartEntries: List<BarChartEntry>): BarData {
        val barGroup = ArrayList<BarEntry>()
        for (i in 0 until timeRangeSteps) {
            if (timeRangeSteps == TimeRangeSteps.MONTH_YEAR.steps) {
                barGroup.add(
                    BarEntry(
                        i.toFloat(),
                        barChartEntries[timeRangeSteps - 1 - i].vittlesExpiredPercent,
                        i.toString()
                    )
                )
            } else {
                barGroup.add(BarEntry(i.toFloat(), barChartEntries[i].vittlesExpiredPercent, i.toString()))
            }
        }
        val barDataSet = BarDataSet(barGroup, "")
        barDataSet.color = Color.RED
        return BarData(barDataSet)
    }

    /**
     * Sets y axis renderer
     *
     * @param barChart bar chart whose renderer should be set
     */
    override fun setYAxisRenderer(barChart: BarChart) {
        barChart.rendererLeftYAxis = CustomYAxisRenderer(
            barChart.viewPortHandler,
            barChart.axisLeft,
            barChart.rendererXAxis.transformer,
            barChart.xAxis,
            requireContext().resources.displayMetrics.density
        )
    }

    /**
     * Sets radius of bars.
     *
     * @param barChart Bar chart whose radius should be set
     */
    override fun setRadius(barChart: BarChart) {
        val barChartRender =
            CustomBarChartRender(
                barChart,
                barChart.animator,
                barChart.viewPortHandler
            )
        when (timeRangeSteps) {
            TimeRangeSteps.SEVEN_DAYS.steps -> barChartRender.setRadius(30)
            TimeRangeSteps.THIRTY_DAYS.steps -> barChartRender.setRadius(10)
            TimeRangeSteps.MONTH_YEAR.steps -> barChartRender.setRadius(18)
        }

        barChart.renderer = barChartRender
    }

    /**
     * Sets x axis labels
     *
     * @param barChartEntries Data to be shown in the charts.
     * @param barChart Bar chart whose labels should be set
     */
    override fun setXAxisLabels(barChartEntries: List<BarChartEntry>, barChart: BarChart) {
        val labels = ArrayList<String>()
        when (timeRangeSteps) {
            TimeRangeSteps.SEVEN_DAYS.steps -> {
                for (i in 0 until timeRangeSteps) {
                    labels.add(barChartEntries[i].getWeekday())
                }
                barChart.xAxis.granularity = 1f
            }
            TimeRangeSteps.THIRTY_DAYS.steps -> {
                for (i in 0 until timeRangeSteps) {
                    labels.add(barChartEntries[i].getDateOfMonth())
                }
                barChart.xAxis.granularity = 5f
            }
            TimeRangeSteps.MONTH_YEAR.steps -> {
                for (i in 0 until timeRangeSteps) {
                    labels.add((TimeRangeSteps.MONTH_YEAR.steps - i).toString())
                }
                barChart.xAxis.granularity = 1f
            }
        }
        barChart.xAxis.apply {
            valueFormatter = IndexAxisValueFormatter(labels)
            isGranularityEnabled = true
            textColor = Color.BLACK
            labelRotationAngle = 180f
            position = XAxis.XAxisPosition.TOP
            yOffset = 15f
            mLabelRotatedHeight = 50
        }

    }

    /**
     * Sets design properties of bar chart
     *
     * @param barChart bar Chart whose design should be set
     */
    override fun setupDesign(barChart: BarChart) {
        barChart.apply {
            setPinchZoom(false)
            setScaleEnabled(false)
            isHighlightPerTapEnabled = false
            isHighlightPerDragEnabled = false
            barChart.clipToPadding = true
            description.isEnabled = false
            animateY(1000)
            legend.isEnabled = false

            data.apply {
                barWidth = 0.4f
                setDrawValues(false)
            }
            xAxis.apply {
                setDrawGridLines(false)
                setDrawAxisLine(false)
                xOffset = 0f
                yOffset = 0f
            }
            axisLeft.apply {
                setDrawAxisLine(false)
                axisMinimum = 0f
                spaceBottom = 0f
                axisMaximum = 100f
                granularity = 50f
            }
            axisRight.apply {
                setDrawAxisLine(false)
                spaceBottom = 0f
                axisMaximum = 100f
                axisMinimum = 0f
                granularity = 50f
            }
        }
    }

    /**
     * Shows toast if an error occurs
     *
     */
    override fun fail() {
        Toast.makeText(context, R.string.bar_chart_error, Toast.LENGTH_SHORT).show()

    }

}