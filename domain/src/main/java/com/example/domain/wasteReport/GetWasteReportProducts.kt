package com.example.domain.wasteReport

import io.reactivex.Single
import org.joda.time.DateTime
import org.joda.time.Days
import javax.inject.Inject

/**
 * This class handles te business logic of getting waste report products
 *
 *
 * @property repository The WasteReportRepository.
 */
class GetWasteReportProducts @Inject constructor(private val repository: WasteReportRepository) {

    /**
     * Gets the waste report products
     *
     * @param date From this date up to now the data should be given
     * @return List of Bar chart entries
     */
    operator fun invoke(date: DateTime): Single<List<BarChartEntry>> {

        return repository.getWasteReportProducts(date.millis).map { products ->
            val listBarChartEntry = mutableListOf<BarChartEntry>()
            var timeRangeDays = Days.daysBetween(date, DateTime.now().withTimeAtStartOfDay()).days
            if(timeRangeDays == TimeRangeSteps.LAST_YEAR.steps) {
                timeRangeDays = TimeRangeSteps.MONTH_YEAR.steps
            }

            for (i in 0 until timeRangeDays) {
                var productAmount: Int
                var eaten: Int
                var barEntryDate: DateTime

                if(timeRangeDays == TimeRangeSteps.MONTH_YEAR.steps) {
                    barEntryDate = DateTime.now().minusMonths(i-1)
                    productAmount = products.count {
                        it.creationDate.monthOfYear == i + 1
                    }
                    eaten = products.count{
                        it.wasteType == "EATEN" && it.creationDate.monthOfYear == i + 1
                    }
                } else {
                    barEntryDate = DateTime.now().minusDays(i).withTimeAtStartOfDay()
                    productAmount = products.count {
                        it.creationDate == barEntryDate
                    }
                    eaten =
                        products.count {
                            it.wasteType == "EATEN" && it.creationDate == barEntryDate
                        }
                }
                val percentEaten: Float
                val percentExpired: Float
                if (productAmount != 0) {
                    percentEaten = eaten.toFloat() / productAmount.toFloat() * 100
                    percentExpired =
                        (productAmount - eaten.toFloat()) / productAmount.toFloat() * 100
                } else {
                    percentEaten = 0f
                    percentExpired = 0f
                }
                listBarChartEntry.add(BarChartEntry(percentEaten, percentExpired, barEntryDate))
            }
            listBarChartEntry
        }
    }

}