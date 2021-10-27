package com.example.data.settings


import android.content.Context
import android.content.SharedPreferences

/**
 * Shared preference class used to store simple data in the internal storage privately for each application
 * that will be used to save setting preferences.
 *
 *
 * @param context application context
 */
class SharedPreferenceHelper(context: Context) {
    private val PREFS_NAME = "Settings"
    val sharedPref: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)


    /**
     * Saves shared preference
     *
     * @param KEY_NAME The key name of the preference.
     * @param value The Int value of the key.
     */
    internal fun save(KEY_NAME: String, value: Int) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putInt(KEY_NAME, value)
        editor.apply()
    }

    /**
     * Saves shared preference
     *
     * @param KEY_NAME The key name of the preference.
     * @param status The boolean value of the key.
     */
    internal fun save(KEY_NAME: String, status: Boolean) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putBoolean(KEY_NAME, status)
            editor.apply()
    }


    /**
     * Gets the Int value of the shared preference
     *
     * @param KEY_NAME The key name of the preference.
     */
    fun getValueInt(KEY_NAME: String, defaultValue: Int): Int {
        return sharedPref.getInt(KEY_NAME, 0)
    }

    /**
     * Gets the boolean value of the shared preference
     *
     * @param KEY_NAME The key name of the preference.
     */
    fun getValueBoolean(KEY_NAME: String, defaultValue: Boolean): Boolean {
        return sharedPref.getBoolean(KEY_NAME, defaultValue)
    }

}