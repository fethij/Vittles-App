package com.example.domain.barcode

import io.reactivex.Completable
import javax.inject.Inject

/**
 * This class handles the business logic for barcode data updating.
 *
 *
 * @property repository The products repository.
 */
class UpdateProductDictionary @Inject constructor(private val repository: BarcodesRepository) {

    /**
     * Calls the repository to update a product dictionary.
     *
     * @param productDictionary The product dictionary to insert.
     * @return Completable to indicate if the action has succeeded.
     */
    operator fun invoke(productDictionary: ProductDictionary): Completable =
        repository.updateProductDictionaryRoom(productDictionary)
}