package com.example.domain.settings

import javax.inject.Inject

/**
 * This class handles the business logic of setting data
 *

 *
 * @property settingsRepository The SettingsRepository
 */
class SetNotificationEnabled @Inject constructor(private val settingsRepository: SettingsRepository) {

    /**
     * Calls the repository to set the value of notification
     *
     * @param isEnabled The boolean value to be set
     */
    operator fun invoke(isEnabled: Boolean) {
        settingsRepository.setNotificationEnabled(isEnabled)
    }
}