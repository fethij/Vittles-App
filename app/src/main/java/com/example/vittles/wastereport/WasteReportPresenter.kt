package com.example.vittles.wastereport

import com.example.domain.wasteReport.GetCountEatenProducts
import com.example.domain.wasteReport.GetCountExpiredProducts
import com.example.domain.wasteReport.GetWastePercent
import com.example.vittles.mvp.BasePresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.joda.time.DateTime
import javax.inject.Inject

/**
 * This is the presenter for the waste report
 *
 *
 * @property getCountEatenProducts The GetCountEatenProducts use case from the domain module.
 * @property getCountExpiredProducts The GetCountExpiredProducts use case from the domain module.
 */
class WasteReportPresenter @Inject internal constructor(
    private val getCountEatenProducts: GetCountEatenProducts,
    private val getCountExpiredProducts: GetCountExpiredProducts) :
    BasePresenter<WasteReportFragment>(), WasteReportContract.Presenter {


    /**
     * Loads the amount of eaten products
     *
     * @param date From this date up to now the amount is calculated
     * @return Amount of eaten products
     */
    override fun getCountEatenProducts(date: DateTime): Int {
        return try {
            getCountEatenProducts.invoke(date)
                .subscribeOn(Schedulers.io())
                .blockingGet()
        } catch (e: Exception) {
            view?.setNoResultsView()
            -1
        }
    }

    /**
     * Loads the amount of expired products
     *
     * @param date From this date up to now the amount is calculated
     * @return Amount of expired products
     */
    override fun getCountExpiredProducts(date: DateTime): Int {
        return try {
            getCountExpiredProducts.invoke(date)
                .subscribeOn(Schedulers.io())
                .blockingGet()
        } catch (e: Exception) {
            view?.setNoResultsView()
            -1
        }
    }
}