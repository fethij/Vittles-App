package com.example.domain.wasteReport

import com.example.domain.wasteReport.WasteReportProduct
import io.reactivex.Completable
import io.reactivex.Single


/**
 * Repository interface for the wasteReportProducts witch is implemented in the data module.
 *

 */
interface WasteReportRepository {

    /**
     * Adds a wasteReportProduct in the database.
     *
     * @param wasteReportProduct The product to invoke.
     * @return A completable status.
     */
    fun post(wasteReportProduct: WasteReportProduct): Completable

    /**
     * Gets amount of eaten vittles
     *
     * @param date From this date up to now the amount is calculated
     * @return amount of eaten vittles
     */
    fun getCountEatenProducts(date: Long): Single<Int>

    /**
     * Gets amount of expired vittles
     *
     * @param date From this date up to now the amount is calculated
     * @return amount of expired vittles
     */
    fun getCountExpiredProducts(date: Long): Single<Int>

    /**
    Gets the waste report products
     *
     * @param date From this date up to now the data should be given
     * @return List of vittles
     *
     */
    fun getWasteReportProducts(date: Long): Single<List<WasteReportProduct>>
}