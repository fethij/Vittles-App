package com.example.domain.barcode

import com.example.domain.product.ProductDictionaryStatus
import io.reactivex.Observable
import javax.inject.Inject

/**
 * This class handles the business logic for barcode data retrieval.
 *
 *
 * @property repository The products repository.
 */
class GetProductByBarcode @Inject constructor(private val repository: BarcodesRepository) {

    /**
     * Calls the [BarcodesRepository] to retrieve data from a barcode.
     *
     * @param barcode The barcode that has been scanned.
     * @return String value that represents the product description/name.
     */
    operator fun invoke(barcode: String): Observable<ProductDictionary> {
        return repository.getProductNameByBarcodeTSCO(barcode).onExceptionResumeNext(
            repository.getProductNameByBarcodeOFF(barcode).onExceptionResumeNext(
                repository.getProductNameByBarcodeRoom(barcode).onExceptionResumeNext(
                    Observable.just(ProductDictionary(barcode, ProductDictionaryStatus.NOT_FOUND() as String))
                )
            )
        )
    }
}