package com.example.domain.notification

/**
 * Model for notifications.
 *

 *
 * @property title The title of the notification.
 * @property message The message of the notification.
 * @property bigText The message when the notification is expanded.
 * @property autoCancel Boolean value that represents if the notification should be automatically
 *                      dismissed when the user touches it.
 */
data class Notification(
    val title: String,
    val message: String,
    val bigText: String,
    val autoCancel: Boolean
)