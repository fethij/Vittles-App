package com.example.vittles.wastereport.circlechart

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
 * @property getWastePercent The GetWastePercent use case from the domain module.
 */
class CircleChartPresenter @Inject internal constructor( private val getWastePercent: GetWastePercent) :
    BasePresenter<CircleChartFragment>(), CircleChartContract.Presenter {


    /**
     * Loads the percent date of eaten products
     *
     * @param date From this date up to now the date is calculated
     * @param vittlesEaten Amount of eaten vittles
     * @param vittlesExpired Amount of expired vittles
     */
    override fun getEatenPercent(date: DateTime, vittlesEaten: Int, vittlesExpired: Int) {
        disposables.add(getWastePercent.invoke(vittlesEaten, vittlesExpired)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ view?.drawCircleChart(it) }, { view?.setNoResultsView() })
        )
    }
}