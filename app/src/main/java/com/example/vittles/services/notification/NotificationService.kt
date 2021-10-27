@file:Suppress("DEPRECATION")

package com.example.vittles.services.notification


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build


import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.domain.notification.Notification
import com.example.vittles.R
import com.example.vittles.productlist.ProductListFragment

/**
 * Creates a notification Channel and Notifications
 *
 *
 */
object NotificationService {


    /**
     * Creates a notification channel for API 26+
     *
     * @param context   application context
     * @param importance   importance level of the notification
     * @param showBadge   whether the channel should have a Badge
     * @param name   name of the notification channel
     * @param description   description of the notification channel
     */
    fun createNotificationChannel(
        context: Context,
        importance: Int,
        showBadge: Boolean,
        name: String,
        description: String
    ) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "${context.packageName}-$name"
            val channel = NotificationChannel(channelId, name, importance)
            channel.description = description
            channel.setShowBadge(showBadge)

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }


    /**
     * Creates a Notification
     *
     * @param context   application context
     * @param notification The notification to be displayed.
     */
    fun createDataNotification(
        context: Context, notification: Notification
    ) {

        val channelId = "${context.packageName}-${context.getString(R.string.app_name)}"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationBuilder = NotificationCompat.Builder(context, channelId).apply {
                setSmallIcon(R.drawable.logo)
                setContentTitle(notification.title)
                setContentText(notification.message)
                setStyle(NotificationCompat.BigTextStyle().bigText(notification.bigText))
                priority = NotificationCompat.PRIORITY_DEFAULT
                setAutoCancel(notification.autoCancel)

                //Open on Click
                val intent = Intent(context, ProductListFragment::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0 )
                setContentIntent(pendingIntent)

            }
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(1001, notificationBuilder.build())

        } else {
            val notificationBuilder = NotificationCompat.Builder(context).apply {
                setSmallIcon(R.drawable.logo) // 3
                setContentTitle(notification.title) // 4
                setContentText(notification.message) // 5
                setStyle(NotificationCompat.BigTextStyle().bigText(notification.bigText)) // 6
                priority = NotificationCompat.PRIORITY_DEFAULT // 7
                setAutoCancel(notification.autoCancel) // 8

                //open on click
                val intent = Intent(context, ProductListFragment::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
                setContentIntent(pendingIntent)

            }
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(1001, notificationBuilder.build())
        }


    }
}