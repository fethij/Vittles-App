package com.example.data.settings

import android.content.Context
import com.example.domain.settings.SettingsRepository
import com.example.domain.settings.model.NotificationSchedule
import com.example.domain.settings.model.PerformanceSetting
import javax.inject.Inject

class  SharedPrefsSettingsRepository @Inject constructor(context: Context): SettingsRepository {

    /** The shared preferences instance. */
    private val sharedPreferences = SharedPreferenceHelper(context)

    /** performanceKey key value */
    private val performanceKey = "SCANNING_PERFORMANCE"
    /** notificationTimeKey key value */
    private val notificationTimeKey = "NOTIFICATION_TIME"
    /** notificationsKey key value */
    private val notificationsKey = "NOTIFICATIONS"
    /** vibrationKey key value */
    private val vibrationKey = "VIBRATION"

    /** {@inheritDoc} */
    override fun getNotificationSchedule(): NotificationSchedule {
         return NotificationSchedule.values()[sharedPreferences.getValueInt(notificationTimeKey, 0)]
    }

    /** {@inheritDoc} */
    override fun setNotificationSchedule(notificationSchedule: NotificationSchedule) {
        sharedPreferences.save(notificationTimeKey, notificationSchedule.ordinal)
    }

    /** {@inheritDoc} */
    override fun setNotificationEnabled(isEnabled: Boolean) {
        sharedPreferences.save(notificationsKey, isEnabled)
    }

    /** {@inheritDoc} */
    override fun getNotificationEnabled(): Boolean {
        return sharedPreferences.getValueBoolean(notificationsKey, true)
    }

    /** {@inheritDoc} */
    override fun getVibrationEnabled(): Boolean {
        return sharedPreferences.getValueBoolean(vibrationKey, true)
    }

    /** {@inheritDoc} */
    override fun setVibrationEnabled(isEnabled: Boolean) {
        sharedPreferences.save(vibrationKey, isEnabled)
    }

    /** {@inheritDoc} */
    override fun getPerformanceSetting(): PerformanceSetting {
       return PerformanceSetting.values()[sharedPreferences.getValueInt(performanceKey, 1)]
    }

    /** {@inheritDoc} */
    override fun setPerformanceSetting(performanceSetting: PerformanceSetting) {
        sharedPreferences.save(performanceKey, performanceSetting.ordinal)
    }
}