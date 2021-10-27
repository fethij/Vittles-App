package com.example.domain.settings

import javax.inject.Inject

/**
 * This class handles the business logic of setting data
 *
 *
 * @property settingsRepository The SettingsRepository.
 */
class GetNotificationEnabled @Inject constructor(private val settingsRepository: SettingsRepository) {

    /**
     * @return the boolean value of notification
     */
    operator fun invoke(): Boolean {
        return settingsRepository.getNotificationEnabled()
    }
}