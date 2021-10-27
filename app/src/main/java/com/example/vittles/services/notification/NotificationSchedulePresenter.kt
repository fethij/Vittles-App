package com.example.vittles.services.notification

import android.content.Context
import com.example.domain.notification.GetNotificationProductsExpired
import com.example.domain.notification.Notification
import com.example.domain.settings.*
import com.example.domain.settings.model.NotificationSchedule
import com.example.vittles.mvp.BasePresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * The presenter for the NotificationSchedule
 *
 * @property getNotificationSchedule The getNotificationSchedule use case from the domain module.
 * @property getNotificationEnabled The getNotificationSchedule use case from the domain module.
 * @property getNotification The getNotification use case from the domain module.
 */
class NotificationSchedulePresenter @Inject constructor(
    private val getNotificationSchedule: GetNotificationSchedule,
    private val getNotificationEnabled: GetNotificationEnabled,
    private val getNotification: GetNotificationProductsExpired

) : BasePresenter<NotificationScheduleService>(), NotificationScheduleContract.Presenter {

    /**
     * Creates the notification in the notification tray and schedule
     *
     * @param context The application context needed for the notification service.
     */
    override fun startPresenting(context: Context) {
        disposables.add(
            getNotification()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view?.notify(it, context)
                    schedule(context)
                }, {
                    view?.onNotifyFail(it)
                    schedule(context)
                })
        )
    }

    /**
     * Method used to schedule notification
     *
     * @param context The application context needed for the notification service.
     */
    private fun schedule(context: Context) {
        NotificationScheduleService.scheduleNotificationAudit(
            context,
            getNotificationSchedule(),
            getNotificationEnabled()
        )
    }
}