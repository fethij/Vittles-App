package com.example.domain.wasteReport

import org.joda.time.DateTime
import org.joda.time.Days
import org.joda.time.format.DateTimeFormat

/**
 * The bar chart entry entity
 *
 *
 * @property vittlesEatenPercent Percentage of eaten vittles
 * @property vittlesExpiredPercent Percentage of expired vittles
 * @property deleteDate Date of deletion
 */
data class BarChartEntry(
    val vittlesEatenPercent: Float,
    val vittlesExpiredPercent: Float,
    val deleteDate: DateTime
) {

    /**
     * Gets the weekday of delete date
     *
     * @return String with first three letters of delete date
     */
    fun getWeekday(): String {
        val fmt = DateTimeFormat.forPattern("EEE")
        return deleteDate.toString(fmt)
    }

    /**
     * Gets date of delete date
     *
     * @return String with day and month of delete date
     */
    fun getDateOfMonth(): String {
        val fmt = DateTimeFormat.forPattern("dd.MM")
        return deleteDate.toString(fmt)
    }
}