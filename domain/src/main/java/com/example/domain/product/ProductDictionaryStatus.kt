package com.example.domain.product

import com.example.domain.barcode.Invokable

/**
 * Enumerator for the status of a product dictionary.
 *
 *
 * @property status The status value of the enumerator.
 */
enum class ProductDictionaryStatus(protected val status: String):
    Invokable {
    /** Error code for when a product could not be found in the remote databases */
    NOT_FOUND("@NOT_FOUND@") {
        /** {@inheritDoc} */
        override operator fun invoke(): String = status
    },
    /** Error code for when a dictionary has been created without valid values */
    NOT_READY("@NOT_READY@") {
        /** {@inheritDoc} */
        override operator fun invoke(): String = status
    }
}