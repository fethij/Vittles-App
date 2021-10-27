package com.example.data.room.product

import com.example.domain.product.Product
import javax.inject.Inject

/**
 * Maps between Room database entity and model.
 *
 */
class ProductModelMapper @Inject constructor() {

    /**
     * Maps product entity to product model.
     *
     * @param from The product entity.
     */
    fun fromEntity(from: ProductEntity) = Product(
        from.uid,
        from.productName,
        from.expirationDate,
        from.creationDate,
        null)

    /**
     * Maps product model to product entity.
     *
     * @param from The product model.
     */
    fun toEntity(from: Product) = ProductEntity(
        from.uid,
        from.productName,
        from.expirationDate,
        from.creationDate
    )
}