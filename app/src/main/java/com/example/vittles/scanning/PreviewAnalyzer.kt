package com.example.vittles.scanning

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.domain.settings.model.PerformanceSetting
import com.example.vittles.services.scanner.ScanningService
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * Analyzer used by CameraX to analyze frames from the camera preview.
 *
 *
 * @property onBarcodeSuccess Callback function for successful barcode scan.
 * @property onBarcodeFailure Callback function for unsuccessful barcode scan.
 * @property onOcrSuccess Callback function for successful OCR scan.
 * @property onOcrFailure Callback function for unsuccessful OCR scan.
 */
class PreviewAnalyzer(
    private val onBarcodeFailure: (exception: Exception) -> Unit,
    private val onBarcodeSuccess: (barcodes: List<FirebaseVisionBarcode>) -> Unit,
    private val onOcrFailure: (exception: Exception) -> Unit,
    private val onOcrSuccess: (text: String) -> Unit,
    private val performanceSetting: PerformanceSetting
    ) : ImageAnalysis.Analyzer {

    companion object ProductProps {
        /** Value that indicates if a barcode has been scanned already */
        var hasBarCode = false
        /** Value that indicates if an expiration date has been scanned already */
        var hasExpirationDate = false
    }

    /** Value used for the scanning delay */
    private var lastAnalyzedTimestamp = 0L

    /**
     * Convert the rotation degree to firebase rotation degree.
     *
     * @param degrees The degrees value from CameraX.
     * @return Firebase rotation degree value.
     */
    private fun degreesToFirebaseRotation(degrees: Int): Int = when (degrees) {
        0 -> FirebaseVisionImageMetadata.ROTATION_0
        90 -> FirebaseVisionImageMetadata.ROTATION_90
        180 -> FirebaseVisionImageMetadata.ROTATION_180
        270 -> FirebaseVisionImageMetadata.ROTATION_270
        else -> throw Exception("Rotation must be 0, 90, 180, or 270.")
    }

    /**
     * {@link ImageAnalysis.Analyzer}
     *  Processes the frames of the camera.
     *
     * @param imageProxy Current frame of the camera.
     * @param degrees Rotation degree of the camera.
     */
    override fun analyze(imageProxy: ImageProxy?, degrees: Int) {
        var interval: Long = performanceSetting.ms.toLong()

        if (interval.toString().isEmpty()) {
            interval = 500L
        }

        // Scan only every 500 ms instead of every frame.
        val currentTimestamp = System.currentTimeMillis()
        if (currentTimestamp - lastAnalyzedTimestamp >=
            TimeUnit.MILLISECONDS.toMillis(interval)
        ) {
            val mediaImage = imageProxy?.image
            val imageRotation = degreesToFirebaseRotation(degrees)
            if (mediaImage != null) {
                var image: FirebaseVisionImage? = null
                try {
                    image = FirebaseVisionImage.fromMediaImage(mediaImage, imageRotation)
                } catch (e: IllegalStateException) {
                    /*
                    NOTE: Non-fixable bug. Bug appears in fewer than 1% of all cases. At least this
                    try-catch might prevent the app from crashing.
                     */
                }
                if (!hasBarCode && image != null) {
                    CoroutineScope(Dispatchers.Main).launch {
                        withContext(Dispatchers.IO) {
                            ScanningService.scanForBarcode(
                                image,
                                onBarcodeSuccess,
                                onBarcodeFailure
                            )
                        }
                    }
                }
                if (!hasExpirationDate && image != null) {
                    CoroutineScope(Dispatchers.Main).launch {
                        withContext(Dispatchers.IO) {
                            ScanningService.scanForExpirationDate(
                                image,
                                onOcrSuccess,
                                onOcrFailure
                            )
                        }
                    }
                }
                lastAnalyzedTimestamp = currentTimestamp
            }
        }
    }
}
