package com.example.vittles.wastereport.barchart

import com.example.domain.wasteReport.GetWasteReportProducts
import com.example.vittles.mvp.BasePresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.joda.time.DateTime
import javax.inject.Inject

/**
 * This is the presenter for the bar chart.
 *
 *
 * @property getWasteReportProducts The GetWasteReportProducts use case from the domain module.
 */
class BarChartPresenter @Inject internal constructor(private val getWasteReportProducts: GetWasteReportProducts) :
    BasePresenter<BarChartFragment>(), BarChartContract.Presenter {

    /**
     * Loads the waste report products
     *
     * @param date From this date up to now the products are given
     */
    override fun getWasteReportProducts(date: DateTime) {
        disposables.add(getWasteReportProducts.invoke(date)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ view?.setupCharts(it) }, { view?.fail() })
        )
    }
}