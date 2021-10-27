package com.example.vittles.productlist.productinfo

import com.example.domain.product.Product
import com.example.vittles.enums.DeleteType
import org.joda.time.DateTime

/**
 * MVP Contract for products info page.
 *
 */
interface ProductInfoContract {

    interface View{
        fun initViews()
        fun setListeners()
        fun updateViews()
        fun onEditNameClicked()
        fun onEditExpirationDateClicked()
        fun onEatenButtonClicked()
        fun onDeleteButtonClicked()
        fun onNameChanged(productName: String)
        fun onExpirationDateChanged(expirationDate: DateTime)
        fun onProductUpdateSuccess()
        fun onProductUpdateFail()
    }

    interface Presenter{
        fun updateProduct(product: Product)
    }
}