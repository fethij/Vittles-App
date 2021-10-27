package com.example.vittles.enums

import org.joda.time.DateTime

/**
 * Enumerator which holds the date of time ranges
 *
 *
 * @property date date where the time range starts
 */
enum class TimeRange(val date: DateTime) {
    LAST_SEVEN_DAYS(DateTime.now().minusDays(7).withTimeAtStartOfDay()),
    LAST_30_DAYS(DateTime.now().minusDays(30).withTimeAtStartOfDay()),
    LAST_YEAR(DateTime.now().minusDays(365).withTimeAtStartOfDay())
}