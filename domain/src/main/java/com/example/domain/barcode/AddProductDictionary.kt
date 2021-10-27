package com.example.domain.barcode

import io.reactivex.Completable
import javax.inject.Inject

/**
 * This class handles the business logic for barcode data insertion.
 *
 *
 * @property repository The products repository.
 */
class AddProductDictionary @Inject constructor(private val repository: BarcodesRepository) {

    /**
     * Calls the repository to insert a product dictionary.
     *
     * @param productDictionary The product dictionary to insert.
     * @return Completable to indicate if the action has succeeded.
     */
    operator fun invoke(productDictionary: ProductDictionary): Completable = repository.insertProductDictionaryRoom(productDictionary)
}