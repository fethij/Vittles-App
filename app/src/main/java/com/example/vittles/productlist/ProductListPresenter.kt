package com.example.vittles.productlist

import com.example.domain.product.ExpirationIndicationColor
import com.example.domain.product.DeleteProduct
import com.example.domain.product.Product
import com.example.domain.product.GetProducts
import com.example.domain.settings.GetVibrationEnabled
import com.example.domain.wasteReport.AddWasteReportProduct
import com.example.domain.wasteReport.WasteReportProduct
import com.example.vittles.enums.DeleteType
import com.example.vittles.enums.IndicationColor
import com.example.vittles.mvp.BasePresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.joda.time.DateTime
import javax.inject.Inject

/**
 * The presenter for the main product activity.
 *
 *
 * @property getProducts The GetProducts use case from the domain module.
 * @property deleteProduct The DeleteProduct use cane from the domain module.
 * @property getVibrationEnabled The getVibrationEnabled use case from the domain module
 */
class ProductListPresenter @Inject internal constructor(
    private val getProducts: GetProducts,
    private val deleteProduct: DeleteProduct,
    private val addWasteReportProduct: AddWasteReportProduct,
    private val getVibrationEnabled: GetVibrationEnabled
) :
    BasePresenter<ProductListFragment>(), ProductListContract.Presenter {

    /**
     * Loads the products.
     *
     */
    override fun startPresenting() {
        disposables.add(
            getProducts().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ view?.onShowProducts(it) }, { view?.onNoResults() })
        )
    }

    /**
     * Loads the indication colors for the products.
     *
     * @param products The list containing the products that are shown in the ListView.
     */
    override fun loadIndicationColors(products: List<Product>) {
        products.forEach { product ->
            product.indicationColor = when (product.getIndicationColor()) {
                ExpirationIndicationColor.RED -> IndicationColor.RED.value
                ExpirationIndicationColor.YELLOW -> IndicationColor.YELLOW.value
                ExpirationIndicationColor.GREEN -> IndicationColor.GREEN.value
            }
        }
    }

    /**
     * Deletes a product.
     *
     * @param product The product that will be deleted.
     * @param deleteType The Delete Type, EATEN, THROWN_AWAY or REMOVED
     */
    override fun deleteProduct(product: Product, deleteType: DeleteType) {
        disposables.add(deleteProduct.invoke(product)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ view?.setAllNoResultStates() }, { view?.onShowProductDeleteError() })
        )
        addWasteReportProduct(deleteType)
    }

    /**
     * Adds a waste report product to database when a product is deleted
     *
     * @param deleteType The delete type of the deleted product
     */
    private fun addWasteReportProduct(deleteType: DeleteType) {
        disposables.add(addWasteReportProduct.invoke(WasteReportProduct(null, DateTime.now().withTimeAtStartOfDay(), deleteType.name))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
        )
    }

    /**
     * @return the boolean value of vibration
     */
    fun getVibrationSetting(): Boolean {
        return getVibrationEnabled()
    }
}