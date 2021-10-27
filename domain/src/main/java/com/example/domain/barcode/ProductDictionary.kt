package com.example.domain.barcode

import com.example.domain.product.ProductDictionaryStatus

/**
 * Dictionary model for barcode (key), product name (value) pairs.
 *
 *
 * @property barcode The key.
 * @property productName The value.
 */
data class ProductDictionary(
    val barcode: String,
    val productName: String?
) {
    fun containsNotReady(): Boolean {
        return barcode == ProductDictionaryStatus.NOT_READY() || productName == ProductDictionaryStatus.NOT_READY()
    }

    fun containsNotFound(): Boolean {
        return barcode == ProductDictionaryStatus.NOT_FOUND() || productName == ProductDictionaryStatus.NOT_FOUND()
    }
}