package com.example.domain.product

import com.example.domain.consts.DAYS_REMAINING_BOUNDARY_CLOSE
import io.reactivex.Completable
import java.lang.Exception
import javax.inject.Inject

/**
 * This class handles te business logic of adding a new product to the application.
 *
 * @property repository The productsRepository.
 */
class AddProduct @Inject constructor(private val repository: ProductsRepository) {

    /**
     * This method is used to add a product to the database.
     *
     * @param product The product that will be added.
     * @return The compatibility status of adding the product ot the database.
     */
    operator fun invoke(product: Product, checkDate: Boolean): Completable = validate(product, checkDate).andThen(repository.post(product))

    /**
     * Validates if the product can be added to the database.
     *
     * @param product The product that will be added to the database.
     * @return The compatibility status.
     */
    private fun validate(product: Product, checkDate: Boolean): Completable {

        return if (!product.isValidForAdd()) {
            Completable.error(IllegalArgumentException("product failed validation before add"))
        }else if (checkDate && isExpiredOrCloseToExpiring(product.getDaysRemaining())){
            Completable.error(Exception("Date is to close, show popup"))
        } else {
            Completable.complete()
        }
    }

    /**
     * Checks the expiration date and invokes the onProductCloseToExpiring when it is close to expiring.
     *
     * @param daysRemaining The amount of days until expiring.
     */
    private fun isExpiredOrCloseToExpiring(daysRemaining: Int): Boolean{
        return daysRemaining <= DAYS_REMAINING_BOUNDARY_CLOSE
    }
}