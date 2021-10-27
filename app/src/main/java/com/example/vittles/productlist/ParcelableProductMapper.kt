package com.example.vittles.productlist

import com.example.domain.product.Product
import com.example.vittles.enums.DeleteType

/**
 * Maps between the parcelable product in the app layer and the product in the domain.
 *
 */
class ParcelableProductMapper {

    companion object {
        /**
         * Maps Parcelable product to Product in domain layer.
         *
         * @param from The ParcelableProduct.
         */
        fun fromParcelable(from: ParcelableProduct) = Product(
            from.uid,
            from.productName,
            from.expirationDate,
            from.creationDate,
            null
        )

        /**
         * Maps Product to ParcelableProduct.
         *
         * @param from The Product in the domain layer.
         */
        fun toParcelable(from: Product, deleteType: DeleteType? = null) = ParcelableProduct(
            from.uid,
            from.productName,
            from.expirationDate,
            from.creationDate,
            deleteType
        )
    }
}