package com.example.vittles.productlist

import com.example.domain.product.Product
import com.example.vittles.enums.DeleteType

/**
 * MVP Contract for products overview.
 *
 */
interface ProductListContract {

    interface View {
        fun initViews()
        fun setListeners()
        fun setItemTouchHelper()
        fun setEmptyView()
        fun onNoResults()
        fun setAllNoResultStates()
        fun onSearchBarOpened()
        fun onSearchBarClosed()
        fun onPopulateRecyclerView()
        fun onShowProducts(products: List<Product>)
        fun onShowProductDeleteError()
        fun onSortMenuOpened()
        fun onSafeDeleteProduct(product: Product, deleteType: DeleteType)
        fun initUndoSnackbar()
        fun onShowUndoSnackbar()
        fun onRemoveButtonClicked(product: Product)
        fun checkForDeletedProduct()
        fun getProductToDelete(product: Product): Product?
        fun filter(query: String)
        fun onItemViewClicked(product: Product)
    }

    interface Presenter {
        fun startPresenting()
        fun loadIndicationColors(products: List<Product>)
        fun deleteProduct(product: Product, deleteType: DeleteType)
    }
}