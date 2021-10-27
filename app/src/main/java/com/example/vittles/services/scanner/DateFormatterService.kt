package com.example.vittles.services.scanner

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.util.*

/**
 * Service for formatting date strings given by the ScanningService.
 *
 */
object DateFormatterService {

    /** @suppress */
    private val regex = Regex("^([0-9&/]*)\$")
    /** @suppress */
    private val shortNumberRegex = Regex("^([0-9]*)")
    /** @suppress */
    private val shortCharRegex = Regex("^([a-z]*)")
    /** @suppress */
    private val charFormat = DateTimeFormat.forPattern("dd/MMM/yyyy").withLocale(Locale.ROOT)
    /** @suppress */
    val numberFormat: DateTimeFormatter = DateTimeFormat.forPattern("dd/MM/yyyy").withLocale(Locale.ROOT)
    /** @suppress */
    private var replacedText = "Place Holder Text"
    /** @suppress */
    lateinit var formatter: DateTimeFormatter
    /** @suppress */
    lateinit var checkMonth: MatchResult

    /**
     * sets the retrieved text in the correct date format and returns the date
     *
     * @param text The text that has been retrieved from the camera
     */
    fun expirationDateFormatter(text: String): DateTime? {
        replacedText = text.replace('-', '/').replace(':', '/').replace('.', '/').replace(' ', '/')
        replacedText = replacedText.replace("okt", "oct").replace("mei", "may").replace("mrt", "mar")

        if (replacedText.matches(regex)){
            if (replacedText.length < 8) {
                if (replacedText.length < 6) {
                    addYear()
                } else {
                    checkMonth = shortNumberRegex.find(replacedText)!!
                    addDayOfMonth(checkMonth)
                }
            }
            formatter = numberFormat
        } else {
            if (replacedText.length < 9) {
                if (replacedText.length < 7) {
                    addYear()
                } else {
                    checkMonth = shortCharRegex.find(replacedText)!!
                    addDayOfMonth(checkMonth)
                }
            }
            formatter = charFormat
        }

        var expirationDate = formatter.parseDateTime(replacedText)
        expirationDate = expirationDate.withCenturyOfEra(DateTime.now().centuryOfEra)

        return expirationDate
    }

    /**
     * Sets the correct amount of days to the corresponding month and adds it to the date
     *
     * @param monthToCheck The month that determines the amount of days
     */
    private fun addDayOfMonth(monthToCheck: MatchResult?) {
        if (monthToCheck !== null) {
            if (checkMonth.value == "01" || checkMonth.value == "03" || checkMonth.value == "05" || checkMonth.value == "07" || checkMonth.value == "08" || checkMonth.value == "10" || checkMonth.value == "12" ||
                checkMonth.value == "jan" || checkMonth.value == "mar" || checkMonth.value == "may" || checkMonth.value == "jul" || checkMonth.value == "aug" || checkMonth.value == "oct" || checkMonth.value == "dec") {
                replacedText = "31/$replacedText"
            } else if (checkMonth.value == "04" || checkMonth.value == "06" || checkMonth.value == "09" || checkMonth.value == "11" ||
                checkMonth.value == "apr" || checkMonth.value == "jun" || checkMonth.value == "sep" || checkMonth.value == "nov") {
                replacedText = "30/$replacedText"
            } else {
                replacedText = "28/$replacedText"
                println("newText: $replacedText")
            }
        }
    }

    /**
     * Adds the current calender year to the date
     */
    private fun addYear() {
        replacedText = replacedText + "/" + Calendar.getInstance().get(Calendar.YEAR)
    }
}