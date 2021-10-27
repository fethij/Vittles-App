package com.example.domain.wasteReport

/**
 * Enumerator which holds the days of different time ranges
 *
 *
 * @property steps steps the time range has
 */

enum class TimeRangeSteps(val steps: Int) {
    SEVEN_DAYS(7),
    THIRTY_DAYS(30),
    LAST_YEAR(365),
    MONTH_YEAR(12)
}