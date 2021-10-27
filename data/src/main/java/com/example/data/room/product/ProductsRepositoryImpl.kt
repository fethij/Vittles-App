package com.example.data.room.product

import com.example.domain.product.Product
import com.example.domain.product.ProductsRepository
import io.reactivex.Completable
import io.reactivex.Single

/**
 * This is the implementation of the ProductsRepository in the Domain layer.
 *
 *
 * @property productDao Reference to the ProductDao.
 * @property mapper The mapper used to map the product data class.
 */
class ProductsRepositoryImpl(
    private val productDao: ProductDao,
    private val mapper: ProductModelMapper
) :
    ProductsRepository {

    /** {@inheritDoc} */
    override fun get(): Single<List<Product>> {
        return productDao.getAll()
            .map { it.map(mapper::fromEntity) }
    }

    /** {@inheritDoc} */
    override fun patch(product: Product): Completable =
        Completable.fromAction { productDao.update(mapper.toEntity(product)) }

    /** {@inheritDoc} */
    override fun delete(product: Product): Completable =
        Completable.fromAction { productDao.delete(mapper.toEntity(product)) }

    /** {@inheritDoc} */
    override fun post(product: Product): Completable =
        Completable.fromAction { productDao.insert(mapper.toEntity(product)) }
}