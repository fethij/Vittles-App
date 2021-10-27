package com.example.domain.product

import io.reactivex.Completable
import javax.inject.Inject

/**
 * This class handles te business logic of updating a product in the application.
 *
 *
 * @property repository The ProductsRepository
 */
class UpdateProduct @Inject constructor(private val repository: ProductsRepository) {

    /**
     * Updates a product in the room database.
     *
     * @param product The updated product (id has to be the same.
     * @return The compatibility status for updating a product in the database.
     */
    fun invoke(product: Product): Completable = validate(product).andThen(repository.patch(product))

    /**
     * Validates if the product can be updated.
     *
     * @param product The product to update.
     * @return The Compatibility status.
     */
    private fun validate(product: Product): Completable{
        return if(!product.isValidForEdit()){
            Completable.error(IllegalArgumentException("product failed validation before add"))
        }else{
            Completable.complete()
        }
    }
}