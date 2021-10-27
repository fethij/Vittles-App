package com.example.data.room.productdictionary

import com.example.data.retrofit.off.OffApiService
import com.example.data.retrofit.tsco.TscoApiService
import com.example.data.room.productdictionary.BarcodeDao
import com.example.data.room.productdictionary.ProductDictionaryModelMapper
import com.example.domain.barcode.ProductDictionary
import com.example.domain.product.ProductNotFoundException
import com.example.domain.barcode.BarcodesRepository
import io.reactivex.Completable
import io.reactivex.Observable

/**
 * This is the implementation of the BarcodesRepository in the Domain layer.
 *

 *
 * @property barcodeDao Reference to the BarcodeDao.
 * @property productsApiTSCO Reference to the TscoApiService.
 * @property productsApiOFF Reference to the OffApiService.
 * @property mapper The mapper used to map the barcode dictionary data class.
 */
class BarcodesRepositoryImpl(
    private val barcodeDao: BarcodeDao,
    private val productsApiTSCO: TscoApiService,
    private val productsApiOFF: OffApiService,
    private val mapper: ProductDictionaryModelMapper
) : BarcodesRepository {

    /** {@inheritDoc} */
    override fun getProductNameByBarcodeTSCO(barcode: String): Observable<ProductDictionary> {
        return productsApiTSCO.getProductName(barcode).map {
            if (it.products?.size!! > 0) {
                ProductDictionary(barcode, it.products?.get(0)?.value)
            } else {
                throw ProductNotFoundException(barcode)
            }
        }
    }

    /** {@inheritDoc} */
    override fun getProductNameByBarcodeOFF(barcode: String): Observable<ProductDictionary> {
        return productsApiOFF.getProductName(barcode).map {
            if (it.status == 1) {
                ProductDictionary(barcode, it.product?.productName)
            } else {
                throw ProductNotFoundException(barcode)
            }
        }
    }

    /** {@inheritDoc} */
    override fun getProductNameByBarcodeRoom(barcode: String): Observable<ProductDictionary> =
        barcodeDao.getProductDictionary(barcode).map {
            mapper.fromEntity(it)
        }.doOnComplete {
            throw ProductNotFoundException(barcode)
        }.toObservable()

    /** {@inheritDoc} */
    override fun insertProductDictionaryRoom(productDictionary: ProductDictionary): Completable =
        Completable.fromAction { barcodeDao.insertProductDictionary(mapper.toEntity(productDictionary)) }

    /** {@inheritDoc} */
    override fun updateProductDictionaryRoom(productDictionary: ProductDictionary): Completable =
        Completable.fromAction { barcodeDao.updateProductDictionary(mapper.toEntity(productDictionary)) }

    /** {@inheritDoc} */
    override fun emptyProductDictionaryRoom(): Completable =
        Completable.fromAction { barcodeDao.emptyProductDictionary() }
}