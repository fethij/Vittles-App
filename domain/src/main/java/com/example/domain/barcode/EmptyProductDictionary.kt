package com.example.domain.barcode

import io.reactivex.Completable
import javax.inject.Inject

/**
 * This class handles the business logic for barcode data updating.
 *

 *
 * @property repository The products repository.
 */
class EmptyProductDictionary @Inject constructor(private val repository: BarcodesRepository) {

    /**
     * Calls the repository to delete all product dictionaries.
     *
     * @return Completable to indicate if the action has succeeded.
     */
    operator fun invoke(): Completable = repository.emptyProductDictionaryRoom()
}