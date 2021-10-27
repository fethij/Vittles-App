package com.example.vittles.wastereport

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.vittles.wastereport.barchart.BarChartFragment
import com.example.vittles.wastereport.circlechart.CircleChartFragment
import com.example.vittles.wastereport.circlechart.RefreshData
import org.joda.time.DateTime

/**
 * Binds fragments to views that are displayed in the view pager
 *

 *
 * @property date From this date up to now the statistic should be shown
 * @property vittlesEaten Amount of eaten vittles
 * @property vittlesExpired Amount of expired vittles
 *
 * @param fm Fragment Manager
 */
class ViewPagerAdapter internal constructor(
    fm: FragmentManager,
    var date: DateTime,
    var vittlesEaten: Int,
    var vittlesExpired: Int
) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    /** @suppress*/
    private val count = 2
    /** @suppress*/
    lateinit var fragment: Fragment


    /**
     * Is called when time range is changed
     *
     * @param date From this date up to now the statistic should be shown
     * @param vittlesEaten Amount of eaten vittles
     * @param vittlesExpired Amount of expired vittles
     */
    fun updateDate(date: DateTime, vittlesEaten: Int, vittlesExpired: Int) {
        this.date = date
        this.vittlesEaten = vittlesEaten
        this.vittlesExpired = vittlesExpired
        notifyDataSetChanged()

        CircleChartFragment.hasDelighted = false
    }

    /** {@inheritDoc} */
    override fun getItemPosition(obj: Any): Int {
        if (obj is RefreshData) {
            obj.refresh(date, vittlesEaten, vittlesExpired)
        }
        return super.getItemPosition(obj)
    }

    /** {@inheritDoc} */
    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> { fragment = CircleChartFragment(date, vittlesEaten, vittlesExpired) }
            1 -> fragment = BarChartFragment(date)
        }
        return fragment
    }

    /** {@inheritDoc} */
    override fun getCount(): Int {
        return count
    }

    /** {@inheritDoc} */
    override fun getPageTitle(position: Int): CharSequence? {
        return "Tab " + (position + 1)
    }
}
