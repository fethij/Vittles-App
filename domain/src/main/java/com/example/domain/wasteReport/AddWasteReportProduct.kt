package com.example.domain.wasteReport

import io.reactivex.Completable
import javax.inject.Inject

/**
 * This class handles te business logic of adding a new waste report product to the application.
 *

 *
 * @property repository The WasteReportRepository.
 */
class AddWasteReportProduct @Inject constructor(private val repository: WasteReportRepository) {

    /**
     * This method is used to add a wasteReportProduct to the database.
     *
     * @param wasteReportProduct The wasteReportProduct that will be added.
     * @return The compatibility status of adding the product ot the database.
     */
    fun invoke(wasteReportProduct: WasteReportProduct): Completable = repository.post(wasteReportProduct)

}