package com.example.domain.settings

import com.example.domain.settings.model.NotificationSchedule
import javax.inject.Inject

/**
 * This class handles the business logic of setting data
 *
 * @property settingsRepository The SettingsRepository
 */
class SetNotificationSchedule @Inject constructor(private val settingsRepository: SettingsRepository) {

    /**
     * Calls the repository to set the value of notificationSchedule
     *
     * @param notificationSchedule
     */
    operator fun invoke(notificationSchedule: NotificationSchedule) {
        settingsRepository.setNotificationSchedule(notificationSchedule)
    }
}