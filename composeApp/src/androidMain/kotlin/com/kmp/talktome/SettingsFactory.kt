package com.kmp.talktome

import android.content.Context
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings

object SettingsFactory {
    fun createSettings(context: Context): Settings {
        val sharedPreferences = context.getSharedPreferences(
            "app_settings",
            Context.MODE_PRIVATE
        )
        return SharedPreferencesSettings(sharedPreferences)
    }
}