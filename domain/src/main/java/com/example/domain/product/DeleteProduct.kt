package com.example.domain.product

import io.reactivex.Completable
import javax.inject.Inject

/**
 * This class handles te business logic of deleting a product from the application.
 *
 *
 * @property repository The productsRepository.
 */
class DeleteProduct @Inject constructor(private val repository: ProductsRepository) {

    /**
     * This method is used to delete a product from the database.
     *
     * @param product The product that will be deleted.
     * @return The compatibility status of deleting product from the database.
     */
    operator fun invoke(product: Product): Completable = repository.delete(product)


}