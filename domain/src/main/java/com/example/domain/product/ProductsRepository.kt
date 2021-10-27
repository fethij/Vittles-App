package com.example.domain.product

import com.example.domain.product.Product
import io.reactivex.Completable
import io.reactivex.Single

/**
 * Repository interface for the products.
 *
 */
interface ProductsRepository {

    /**
     * Gets all the products from the local database.
     *
     * @return A list containing all the products.
     */
    fun get(): Single<List<Product>>

    /**
     * Updates a product in the database.
     *
     * @param product The product to update.
     * @return A completable status.
     */
    fun patch(product: Product): Completable

    /**
     * Deletes a product in the database.
     *
     * @param product The product to delete.
     * @return A completable status.
     */
    fun delete(product: Product): Completable

    /**
     * Adds a product in the database.
     *
     * @param product The product to invoke.
     * @return A completable status.
     */
    fun post(product: Product): Completable
}