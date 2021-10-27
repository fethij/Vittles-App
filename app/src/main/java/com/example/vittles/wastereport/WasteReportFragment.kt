package com.example.vittles.wastereport

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.example.vittles.R
import com.example.vittles.enums.TimeRange
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.content_waste_history.*
import kotlinx.android.synthetic.main.fragment_waste_report.*
import kotlinx.coroutines.*
import org.joda.time.DateTime
import javax.inject.Inject

/**
 * Fragment class for the waste report.
 *
 */
class WasteReportFragment : DaggerFragment(), WasteReportContract.View {

    /**
     * The presenter of the fragment
     */
    @Inject
    lateinit var presenter: WasteReportPresenter

    /**@suppress*/
    private lateinit var timeRangeMenu: WasteTimeRangeMenu
    /**@suppress*/
    lateinit var adapter: ViewPagerAdapter

    /** {@inheritDoc} */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        timeRangeMenu = WasteTimeRangeMenu { date -> changeDate(date) }
        return inflater.inflate(R.layout.fragment_waste_report, container, false)
    }

    /** {@inheritDoc} */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.start(this)

        initData()
    }

    /** {@inheritDoc} */
    override fun onDestroy() {
        super.onDestroy()
        presenter.destroy()
    }

    /**
     * Initializes the data including eaten and expired vittles
     *
     */
    override fun initData() {
        // Call get methods asynchronously, then call initViews synchronously
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                val vittlesEaten =
                    async { presenter.getCountEatenProducts(TimeRange.LAST_SEVEN_DAYS.date) }
                val vittlesExpired =
                    async { presenter.getCountExpiredProducts(TimeRange.LAST_SEVEN_DAYS.date) }

                initViews(vittlesEaten.await(), vittlesExpired.await())
            }
        }
    }

    /**
     * Initializes view elements including the onClickListener
     *
     * @param vittlesEaten Eaten vittles that should be displayed
     * @param vittlesExpired Expired vittles that should be displayed
     */
    override fun initViews(vittlesEaten: Int, vittlesExpired: Int) {
        // Inside handler because android doesn't allow UI changes outside main thread.
        Handler(Looper.getMainLooper()).post {
            adapter = ViewPagerAdapter(
                activity!!.supportFragmentManager,
                TimeRange.LAST_SEVEN_DAYS.date,
                vittlesEaten,
                vittlesExpired
            )
            viewPager.adapter = adapter
            addOnPageChangeListener()

            showEatenVittles(vittlesEaten)
            showExpiredVittles(vittlesExpired)
        }
        timeRange.setOnClickListener { showTimeRangeSelector() }
        changeDate(TimeRange.LAST_SEVEN_DAYS.date)
    }


    /**
     * Opens the timeRange menu when the button is clicked
     *
     */
    override fun showTimeRangeSelector() {
        context?.let { timeRangeMenu.openMenu(it, btnSort) }
    }

    /**
     * Called when the time range is changed.
     *
     * @param date From this date up to now the statistic should be shown
     */
    override fun changeDate(date: DateTime) {
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                val vittlesEaten =
                    async { presenter.getCountEatenProducts(date) }
                val vittlesExpired =
                    async { presenter.getCountExpiredProducts(date) }
                showChangeDate(vittlesEaten.await(), vittlesExpired.await(), date)
            }
        }
    }

    /**
     * Calls methods to show data after date is changed
     *
     * @param vittlesEaten Amount of eaten vittles
     * @param vittlesExpired Amount of expired vittles
     * @param date From this date up to now the statistic should be shown
     */
    override fun showChangeDate(vittlesEaten: Int, vittlesExpired: Int, date: DateTime) {
        Handler(Looper.getMainLooper()).post {
            showEatenVittles(vittlesEaten)
            showExpiredVittles(vittlesExpired)
            adapter.updateDate(date, vittlesEaten, vittlesExpired)
        }
    }


    /**
     * Displays amount of eaten vittles
     *
     * @param eatenVittles Amount of eaten vittles
     */
    override fun showEatenVittles(eatenVittles: Int) {
        tvVittlesEaten.text = eatenVittles.toString()
    }

    /**
     * Displays amount of expired vittles
     *
     * @param expiredVittles Amount of expired vittles
     */
    override fun showExpiredVittles(expiredVittles: Int) {
        tvVittlesExpired.text = expiredVittles.toString()
    }

    /**
     * Shows toast if an error during loading statistics occurs
     *
     */
    override fun setNoResultsView() {
        Toast.makeText(context, R.string.count_fail, Toast.LENGTH_SHORT).show()
    }

    /**
     * Sets listener to viewPager
     *
     */
    override fun addOnPageChangeListener() {
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    ivDotRight.setImageDrawable(context?.let {
                        ContextCompat.getDrawable(
                            it,
                            R.drawable.dot
                        )
                    })
                    ivDotLeft.setImageDrawable(context?.let {
                        ContextCompat.getDrawable(
                            it,
                            R.drawable.dot_selected
                        )
                    })
                } else if (position == 1) {
                    ivDotLeft.setImageDrawable(context?.let {
                        ContextCompat.getDrawable(
                            it,
                            R.drawable.dot
                        )
                    })
                    ivDotRight.setImageDrawable(context?.let {
                        ContextCompat.getDrawable(
                            it,
                            R.drawable.dot_selected
                        )
                    })
                }

            }

        })
    }


}
