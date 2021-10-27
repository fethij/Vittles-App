package com.example.vittles.scanning

import android.Manifest
import android.content.pm.PackageManager
import android.util.Size
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.example.domain.barcode.AddProductDictionary
import com.example.domain.barcode.GetProductByBarcode
import com.example.domain.barcode.ProductDictionary
import com.example.domain.barcode.UpdateProductDictionary
import com.example.domain.product.AddProduct
import com.example.domain.product.Product
import com.example.domain.settings.GetPerformanceSetting
import com.example.domain.settings.GetVibrationEnabled
import com.example.domain.settings.model.PerformanceSetting
import com.example.vittles.VittlesApp.PermissionProperties.REQUIRED_PERMISSIONS
import com.example.vittles.mvp.BasePresenter
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_scanner.*
import java.util.concurrent.Executors
import javax.inject.Inject

/**
 * The presenter for the scanning activity.
 *
 * @property getProductByBarcode The GetProductByBarcode use case from the domain module.
 * @property addProduct The AddProduct use case from the domain module.
 * @property insertProductDictionary The AddBarcodeDictionary use case from the domain module.
 * @property updateProductDictionary The UpdateBarcodeDictionary use case from the domain module.
 * @property getVibrationEnabled The GetVibrationEnabled use case from the domain module.
 */
class ScannerPresenter @Inject internal constructor(
    private val getProductByBarcode: GetProductByBarcode,
    private val addProduct: AddProduct,
    private val addProductDictionary: AddProductDictionary,
    private val updateProductDictionary: UpdateProductDictionary,
    private val getVibrationEnabled: GetVibrationEnabled,
    private val getPerformanceSetting: GetPerformanceSetting
    ) :
    BasePresenter<ScannerFragment>(), ScannerContract.Presenter {

    /** CameraX preview element. */
    private lateinit var preview: Preview
    /** ImageAnalysis object with the PreviewAnalyzer in it for the preview analysis. */
    private lateinit var imageAnalysis: ImageAnalysis
    /** Executor for the analysis on a different thread. */
    private val executor = Executors.newSingleThreadExecutor()
    /** The analyzer for the preview */
    private lateinit var analyzer: PreviewAnalyzer
    /** observes LiveData objects for changes.**/
    private val performanceSetting = MutableLiveData<PerformanceSetting>()

    /**
     * Method used to add a product.
     *
     * @param product The product to add.
     * @param checkDate If the date should be checked to show a popup.
     */
    override fun addProductToList(product: Product, checkDate: Boolean) {
        disposables.add(addProduct(product, checkDate)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    view?.onShowAddProductSucceed()
                    view?.onResetView()
                },
                {
                    if (it is IllegalArgumentException) {
                        view?.onShowAddProductError() // Show snack bar that tells it failed
                    } else if (it is Exception) {
                        view?.onShowExpirationPopup(product) // Show close to expiring popup
                    }
                }
            )
        )
    }

    /**
     * Calls the use case to add a product dictionary.
     *
     * @param productDictionary The product dictionary to add.
     */
    override fun insertProductDictionary(productDictionary: ProductDictionary) {
        if (!productDictionary.containsNotReady()) {
            disposables.add(addProductDictionary(productDictionary)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe())
        }
    }

    /**
     * Calls the use case to update a barcode dictionary.
     *
     * @param productDictionary The barcode dictionary to update.
     */
    override fun patchProductDictionary(productDictionary: ProductDictionary) {
        disposables.add(updateProductDictionary(productDictionary)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe())
    }

    /**
     * Gets the boolean value of vibration setting.
     *
     * @return The boolean value of Vibration setting.
     */
    override fun getVibrationSetting(): Boolean {
        return getVibrationEnabled()
    }

    /**
     * Sets up everything for the camera to start.
     *
     */
    override fun startCamera() {
        imageAnalysis = getImageAnalysis()
        preview = getPreview()

        CameraX.bindToLifecycle(view, preview, imageAnalysis)

        disableTorch()
    }

    /**
     * Creates a CameraX preview object.
     *
     * @return The CameraX preview.
     */
    override fun getPreview(): Preview {
        // Create configuration object for the viewfinder use case
        val previewConfig = PreviewConfig.Builder().apply {
            setLensFacing(CameraX.LensFacing.BACK)
            setTargetAspectRatio(AspectRatio.RATIO_16_9)
        }.build()

        // Build the viewfinder use case
        val preview = Preview(previewConfig)

        // Every time the viewfinder is updated, recompute layout
        preview.setOnPreviewOutputUpdateListener {
            // To update the SurfaceTexture, we have to remove it and re-add it
            val parent = view?.textureView?.parent as ViewGroup
            val textureView = view?.textureView
            parent.removeView(view?.textureView)
            parent.addView(textureView, 0)

            view?.textureView?.surfaceTexture = it.surfaceTexture
        }
        return preview
    }

    /**
     * Creates an ImageAnalysis used for the preview analysis.
     *
     * @return The CameraX ImageAnalysis.
     */
    override fun getImageAnalysis(): ImageAnalysis {
        // Setup image analysis pipeline that computes average pixel luminance
        val analyzerConfig = ImageAnalysisConfig.Builder().apply {
            setTargetResolution(Size(720, 480)) // 480p resolution
            setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE) // Take newest available frame on analyze call
        }.build()

        PreviewAnalyzer.hasBarCode = false
        PreviewAnalyzer.hasExpirationDate = false

        fetchPerformanceSetting()

        // Build the image analysis use case and instantiate our analyzer
        return ImageAnalysis(analyzerConfig).apply {
            setAnalyzer(executor, PreviewAnalyzer(
                onBarcodeFailure = { view?.onBarcodeNotFound() },
                onBarcodeSuccess = { getProductNameByBarcode(it) },
                onOcrFailure = { view?.onTextNotFound() },
                onOcrSuccess = { view?.onTextScanned(it) },
                performanceSetting = performanceSetting.value!!
            ))
        }
    }

    /**
     * Checks for PerformanceSetting settings changes
     *
     */
    private fun fetchPerformanceSetting() {
        performanceSetting.value = getPerformanceSetting()
    }

    /**
     * Checks if the user gave all needed permissions for the activity to work properly.
     *
     */
    override fun checkPermissions() {
        // Request camera permissions
        if (allPermissionsGranted()) {
            view?.textureView?.post { startCamera() }
        } else {
            view?.let {
                view?.onRequestPermissionsFromFragment()
            }
        }
    }

    /**
     * Check if all permission specified in the manifest have been granted.
     */
    override fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        view?.context.let { it1 ->
            ContextCompat.checkSelfPermission(
                it1!!, it
            )
        } == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Calls repository to retrieve the product name with the given barcodes.
     *
     * @param barcodes All the barcodes retrieved from the camera.
     */
    private fun getProductNameByBarcode(barcodes: List<FirebaseVisionBarcode>) {
        if (barcodes.isNotEmpty()) {
            val barcode = barcodes[0].rawValue.toString()
            disposables.add(
                getProductByBarcode(barcode).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ view?.onBarcodeScanned(it) }, { view?.onBarcodeNotFound() })
            )
        }
    }

    /**
     * Toggles the flash (torch) of the camera.
     *
     * @return Boolean value that represents if torch is on or off.
     */
    fun toggleTorch(): Boolean {
        return if (preview.isTorchOn) {
            disableTorch()
            false
        } else {
            enableTorch()
            true
        }
    }

    /**
     * Returns the value of the current torch state.
     *
     * @return The boolean value of the current torch state; enabled or not.
     */
    fun torchEnabled(): Boolean {
        return if (::preview.isInitialized) {
            preview.isTorchOn
        } else {
            false
        }
    }

    /**
     * Enables the torch of the preview.
     *
     */
    private fun enableTorch() {
        if (::preview.isInitialized) {
            preview.enableTorch(true)
        }
    }

    /**
     * Disabled teh torch of the preview.
     *
     */
    private fun disableTorch() {
        if (::preview.isInitialized) {
            preview.enableTorch(false)
        }
    }
}