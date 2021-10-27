package com.example.domain.product

import io.reactivex.Single
import javax.inject.Inject

/**
 * This class handles the business logic of fetching all the products received from the local database.
 *
 *
 * @property repository The ProductsRepository.
 */
class GetProducts @Inject constructor(private val repository: ProductsRepository) {
    /**
     * Gets all the products from the local database.
     *
     * @return A list containing all the products.
     */
    operator fun invoke(): Single<List<Product>> = repository.get()
}