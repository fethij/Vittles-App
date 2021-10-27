package com.example.domain.wasteReport

import io.reactivex.Single
import org.joda.time.DateTime
import javax.inject.Inject


/**
 * This class handles te business logic of getting amount of eaten vittles
 *

 *
 * @property repository The WasteReportRepository.
 */
class GetCountEatenProducts @Inject constructor(private val repository: WasteReportRepository) {

    /**
     * Gets the amount of eaten vittles
     *
     * @param date From this date up to now the amount is calculated
     * @return amount of eaten vittles
     */
    operator fun invoke(date: DateTime): Single<Int>  = repository.getCountEatenProducts(date.millis)

}