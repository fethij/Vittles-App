package com.example.domain.notification

import com.example.domain.consts.DAYS_REMAINING_BOUNDARY_CLOSE
import com.example.domain.consts.DAYS_REMAINING_EXPIRED
import com.example.domain.product.ProductsRepository
import io.reactivex.Single
import java.lang.StringBuilder
import javax.inject.Inject

/**
 * This class handles the business logic for notification data.
 *
 *
 * @property repository The products repository.
 */
class GetNotificationProductsExpired @Inject constructor(private val repository: ProductsRepository) {

    /**
     * Method that handles the use case.
     *
     * @return The newly created notification.
     */
    operator fun invoke(): Single<Notification> {
        return repository.get()
            .map { products ->
                val expiring =
                    products.count {
                        it.getDaysRemaining() in (DAYS_REMAINING_EXPIRED + 1) until DAYS_REMAINING_BOUNDARY_CLOSE
                    }
                val expired =
                    products.count { it.getDaysRemaining() <= DAYS_REMAINING_EXPIRED }
                if (expiring > 0 || expired > 0) {
                    createNotification(expiring, expired)
                } else {
                    throw NotificationDataException()
                }
            }
    }

    /**
     * Creates the notification.
     *
     * @param expiring Property that represents the amount of products that are gonna expire soon.
     * @param expired Property that represents the amount of expired products.
     * @return The notification.
     */
    private fun createNotification(expiring: Int, expired: Int): Notification {
        val message = createMessage(expiring, expired)
        return Notification(
            "Vittles",
            "Reduce food waste",
            message,
            false
        )
    }

    /**
     * Creates a message as a string with the correct format.
     *
     * @param expiring Property that represents the amount of products that are gonna expire soon.
     * @param expired Property that represents the amount of expired products.
     * @return The message as string format.
     */
    private fun createMessage(expiring: Int, expired: Int): String {
        val stringBuilder = StringBuilder()
        when {
            expired == 1 -> stringBuilder.append("You have $expired expired product")
            expired > 1 -> stringBuilder.append("You have $expired expired products")
        }
        when {
            expired <= 0 && expiring > 0 -> stringBuilder.append("You have ")
            expired > 0 && expiring > 0 -> stringBuilder.append(" and ")
        }
        when {
            expiring == 1 -> stringBuilder.append("$expiring product expiring within 2 days")
            expiring > 1 -> stringBuilder.append("$expiring products expiring within 2 days")
        }
        stringBuilder.append(".")
        return stringBuilder.toString()
    }
}