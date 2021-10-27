package com.example.domain.settings

import javax.inject.Inject

/**
 * This class handles the business logic of setting data
 *

 *
 * @property settingsRepository The SettingsRepository
 */
class GetVibrationEnabled @Inject constructor(private val settingsRepository: SettingsRepository) {

    /**
     * @return the boolean value of vibration
     */
    operator fun invoke(): Boolean {
        return settingsRepository.getVibrationEnabled()
    }
}