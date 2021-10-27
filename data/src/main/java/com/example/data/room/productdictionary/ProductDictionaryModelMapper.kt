package com.example.data.room.productdictionary

import com.example.domain.barcode.ProductDictionary
import javax.inject.Inject

/**
 * Maps between Room database entity and model.
 *
 */
class ProductDictionaryModelMapper @Inject constructor() {

    fun fromEntity(productDictionaryEntity: ProductDictionaryEntity) =
        ProductDictionary(
            productDictionaryEntity.barcode,
            productDictionaryEntity.productName
        )

    fun toEntity(productDictionary: ProductDictionary) =
        ProductDictionaryEntity(
            productDictionary.barcode,
            productDictionary.productName
        )
}