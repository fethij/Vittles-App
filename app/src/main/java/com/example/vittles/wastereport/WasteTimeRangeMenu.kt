package com.example.vittles.wastereport

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.example.vittles.R
import com.example.vittles.enums.TimeRange
import kotlinx.android.synthetic.main.popup_report_time_range.view.*
import org.joda.time.DateTime

/**
 * Class for the sorting Menu, This menu shows all the sorting types and the current sorting type.
 *
 *
 * @property currentTimeRange The current time range that is used.
 * @property previousTimeRange The previous time range type that was used.
 * @property btnTimeRange The button which shows the current time range.
 * @property alertDialog The entire alertDialog of the time range.
 * @property view The View which holds the sortingMenu.
 */
class WasteTimeRangeMenu (private val onDateChange: (DateTime) -> Unit) {

    /**@suppress*/
    private var currentTimeRange: TimeRange = TimeRange.LAST_SEVEN_DAYS
    /**@suppress*/
    private lateinit var previousTimeRange: TimeRange
    /**@suppress*/
    private lateinit var btnTimeRange: TextView
    /**@suppress*/
    lateinit var alertDialog: AlertDialog
    /**@suppress*/
    lateinit var view: View

    /**
     * Inflates the timeRangeMenu.
     *
     * @param context The context in which the time range is active.
     * @param button The button which shows the current time range.
     */
    fun openMenu(context: Context, button: TextView) {

        val mDialogView =
            LayoutInflater.from(context).inflate(R.layout.popup_report_time_range, null)
        val mBuilder =
            AlertDialog.Builder(context).setView(mDialogView)
        val  mAlertDialog = mBuilder.show()

        btnTimeRange = button
        alertDialog = mAlertDialog
        view = mDialogView

        setCircleColor()
        setListeners()
    }

    /**
     * Sets all the onClickListeners
     *
     */
    private fun setListeners() {
        view.lastSevenDays.setOnClickListener { onSortClick(TimeRange.LAST_SEVEN_DAYS)  }
        view.last30Days.setOnClickListener { onSortClick(TimeRange.LAST_30_DAYS) }
        view.lastYear.setOnClickListener { onSortClick(TimeRange.LAST_YEAR) }
    }

    /**
     * Handles all actions that happen when a button is clicked
     *
     * @param timeRange time range of clicked button
     *
     */
    private fun onSortClick(timeRange: TimeRange) {
        when(timeRange) {
            TimeRange.LAST_SEVEN_DAYS -> loadData(TimeRange.LAST_SEVEN_DAYS.date)
            TimeRange.LAST_30_DAYS -> loadData(TimeRange.LAST_30_DAYS.date)
            TimeRange.LAST_YEAR -> loadData(TimeRange.LAST_YEAR.date)
        }

        previousTimeRange = currentTimeRange
        currentTimeRange = timeRange

        if (currentTimeRange != previousTimeRange) {
            setTimeRangeBtnText(timeRange)
        }

        if (::alertDialog.isInitialized) {
            alertDialog.dismiss()
        }
    }

    /**
     * * calls the onDateChange method in the wasteReportFragment when a button is clicked
     *
     * @param date Date from which the statistics should be displayed
     */
    private fun loadData(date: DateTime) {
        onDateChange(date)
    }

    /**
     * Sets the circle alpha to 1 of the selected time range method.
     *
     */
    private fun setCircleColor() {
        when (currentTimeRange) {
            TimeRange.LAST_SEVEN_DAYS -> view.lastSevenDaysDot.alpha = 1f
            TimeRange.LAST_30_DAYS-> view.last30DaysDot.alpha = 1f
            TimeRange.LAST_YEAR -> view.lastYearDot.alpha = 1f
        }
    }

    /**
     * Sets the buttonText to the current time range.
     *
     * @param timeRange The timeRange which was selected by the user.
     */
    private fun setTimeRangeBtnText(timeRange: TimeRange) {
        when(timeRange) {
            TimeRange.LAST_SEVEN_DAYS -> btnTimeRange.text = view.lastSevenDays.text
            TimeRange.LAST_30_DAYS -> btnTimeRange.text = view.last30Days.text
            TimeRange.LAST_YEAR -> btnTimeRange.text = view.lastYear.text
        }
    }
}