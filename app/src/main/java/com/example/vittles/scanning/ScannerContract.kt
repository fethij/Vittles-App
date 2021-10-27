package com.example.vittles.scanning

import android.view.MotionEvent
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import com.example.domain.barcode.ProductDictionary
import com.example.domain.product.Product

/**
 * MVP Contract for scanning products.
 *
 */
interface ScannerContract {

    interface View {
        fun initViews(view: android.view.View)
        fun initListeners()
        fun onTapToFocus(event: MotionEvent): Boolean
        fun onAddVittleButtonClick()
        fun onBarcodeScanned(productDictionary: ProductDictionary)
        fun onBarcodeNotFound()
        fun onTextScanned(text: String)
        fun onTextNotFound()
        fun onNoPermissionGranted()
        fun onRequestPermissionsFromFragment()
        fun onEditNameButtonClick()
        fun onEditExpirationButtonClick()
        fun onTorchButtonClicked()
        fun onResetView()
        fun onResetDate()
        fun onResetProductName()
        fun onShowAddProductError()
        fun onShowAddProductSucceed()
        fun onShowExpirationPopup(product: Product)
        fun onShowCloseToExpirationPopup(product: Product)
        fun onShowAlreadyExpiredPopup(product: Product)
        fun onProductNameEdited(productDictionary: ProductDictionary, insertLocal: Boolean = false)
        fun onExpirationDateEdited(text: String)
        fun onProductNameCheckboxChecked(productName: String)
        fun onExpirationDateCheckboxChecked(text: String)
        fun onShowEditNameDialog(productDictionary: ProductDictionary)
    }

    interface Presenter {
        fun addProductToList(product: Product, checkDate: Boolean)
        fun insertProductDictionary(productDictionary: ProductDictionary)
        fun patchProductDictionary(productDictionary: ProductDictionary)
        fun getVibrationSetting(): Boolean
        fun startCamera()
        fun getPreview(): Preview
        fun getImageAnalysis(): ImageAnalysis
        fun checkPermissions()
        fun allPermissionsGranted(): Boolean
    }
}