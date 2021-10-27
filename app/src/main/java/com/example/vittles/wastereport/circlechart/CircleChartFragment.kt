package com.example.vittles.wastereport.circlechart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat.animate
import androidx.core.view.ViewPropertyAnimatorListener
import androidx.core.view.setPadding
import com.example.vittles.R
import com.hookedonplay.decoviewlib.charts.DecoDrawEffect
import com.hookedonplay.decoviewlib.charts.SeriesItem
import com.hookedonplay.decoviewlib.events.DecoEvent
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_circle_chart.*
import org.joda.time.DateTime
import javax.inject.Inject


/**
 * Interface for updating date
 *
 *
 */
interface RefreshData {

    /**
     * Refreshes data after setting new date
     *
     * @param date From this date up to now the date is calculated
     * @param vittlesEaten Amount of eaten vittles
     * @param vittlesExpired Amount of expired vittles
     */
    fun refresh(date: DateTime, vittlesEaten: Int, vittlesExpired: Int)
}

/**
 * Fragment class for the waste report.
 *
 * @property date From this date up to now the date is calculated
 * @property vittlesEaten Amount of eaten vittles
 * @property vittlesExpired Amount of expired vittles
 */
class CircleChartFragment @Inject internal constructor(var date: DateTime, var vittlesEaten: Int, var vittlesExpired: Int) : DaggerFragment(), CircleChartContract.View, RefreshData  {

    /**
     * The presenter of the fragment.
     */
    @Inject
    lateinit var presenter: CircleChartPresenter

    companion object {
        /** @suppress*/
        var hasDelighted = false
    }

    /** {@inheritDoc} */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_circle_chart, container, false)
    }

    /** {@inheritDoc} */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.start(this)
        presenter.getEatenPercent(date, vittlesEaten, vittlesExpired)
    }

    /** {@inheritDoc} */
    override fun onDestroy() {
        super.onDestroy()
        presenter.destroy()
    }

    /**
     * Implementation of the refresh interface
     *
     * @param date New date from which the date is calculated
     * @param vittlesEaten New amount of eaten vittles
     * @param vittlesExpired New amount of expired vittles
     */
    override fun refresh(date: DateTime, vittlesEaten: Int, vittlesExpired: Int) {
        this.date = date
        this.vittlesEaten = vittlesEaten
        this.vittlesExpired = vittlesExpired
        presenter.getEatenPercent(date, vittlesEaten, vittlesExpired)
    }

    /**
     * Draws circle chart
     *
     * @param percent percent date
     */
    override fun drawCircleChart(percent: Int) {
        dynamicArcView.executeReset()

        val seriesItem = SeriesItem.Builder(ContextCompat.getColor(context!!, R.color.lightGrey))
            .setRange(0f, 100f, 0f)
            .build()

        val seriesItem2 = SeriesItem.Builder(ContextCompat.getColor(context!!, R.color.colorPrimary))
            .setRange(0f, 100f, 0f)
            .build()

        val backIndex = dynamicArcView.addSeries(seriesItem)

        seriesItem2.addArcSeriesItemListener(object : SeriesItem.SeriesItemListener {
            override fun onSeriesItemAnimationProgress(
                percentComplete: Float,
                currentPosition: Float
            ) {
                val percentFilled =
                    (currentPosition - seriesItem2.minValue) / (seriesItem2.maxValue - seriesItem2.minValue)
                    tvPercentage.text = String.format("%.0f%%", percentFilled * 100f)
            }

            override fun onSeriesItemDisplayProgress(percentComplete: Float) { }
        })

        dynamicArcView.addEvent(
            DecoEvent.Builder(100f)
                .setIndex(backIndex)
                .setDuration(1)
                .build()
        )

        if(percent.toFloat() == 0f) {
            tvPercentage.text = "0%"
        } else {
            val series1Index = dynamicArcView.addSeries(seriesItem2)
            dynamicArcView.addEvent(
                DecoEvent.Builder(percent.toFloat())
                    .setIndex(series1Index)
                    .build()
            )

            // When percentage is 100% and delighter is not yet shown Show delighter
            if (percent.toFloat() == 100f && !hasDelighted) {

                dynamicArcView.addEvent(DecoEvent.Builder(DecoDrawEffect.EffectType.EFFECT_SPIRAL_EXPLODE)
                    .setIndex(series1Index)
                    .setDelay(2100)
                    .setDuration(3000)
                    .setDisplayText(getRandomPraise())
                    .setListener(object : DecoEvent.ExecuteEventListener {
                        override fun onEventStart(p0: DecoEvent?) {
                            fadeOutAnim(tvLabel)
                            fadeOutAnim(tvPercentage)
                        }
                        override fun onEventEnd(p0: DecoEvent?) {
                            fadeInAnim(tvLabel)
                            fadeInAnim(tvPercentage)

                            hasDelighted = true
                            drawCircleChart(percent)
                        }
                    })
                    .build()
                )
            }
        }
        hasDelighted = false
    }

    /**
     * Shows toast if an error occurs
     *
     */
    override fun setNoResultsView() {
        Toast.makeText(context, R.string.circle_chart_error, Toast.LENGTH_SHORT).show()
    }

    /**
     * Get's a random praise message from the strings resources file
     *
     * @return Returns praise message as string
     */
    private fun getRandomPraise(): String {
        val praises = resources.getStringArray(R.array.praises).toList()
        return praises.shuffled().take(1)[0]
    }

    /**
     * Animation for fade in
     *
     * @param elem Element to animate
     */
    private fun fadeOutAnim(elem: View) {
        animate(elem).apply {
            interpolator = AccelerateInterpolator()
            alpha(0f)
            duration = 1000
            start()
        }
    }

    /**
     * Animation for fade out
     *
     * @param elem Element to animate
     */
    private fun fadeInAnim(elem: View) {
        animate(elem).apply {
            interpolator = AccelerateInterpolator()
            alpha(1f)
            duration = 1000
            start()
        }
    }
}
