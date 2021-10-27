package com.example.domain.wasteReport

import io.reactivex.Single
import org.joda.time.DateTime
import javax.inject.Inject

/**
 * This class handles te business logic of getting amount of expired vittles
 *

 *
 * @property repository The WasteReportRepository.
 */
class GetCountExpiredProducts @Inject constructor(private val repository: WasteReportRepository) {

    /**
     * Gets the amount of expired vittles
     *
     * @param date From this date up to now the amount is calculated
     * @return amount of expired vittles
     */
    operator fun invoke(date: DateTime): Single<Int> = repository.getCountExpiredProducts(date.millis)


}