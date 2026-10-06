package com.example.core.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "wafa_settings")

enum class AppLanguage {
    BANGLA,
    ENGLISH
}

data class UserSettings(
    val language: AppLanguage = AppLanguage.BANGLA,
    val soundEnabled: Boolean = true,
    val musicEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val highPerformance: Boolean = true
)

class GameSettingsManager(private val context: Context) {
    companion object {
        private val KEY_LANG = stringPreferencesKey("app_language")
        private val KEY_SFX = booleanPreferencesKey("sound_effects")
        private val KEY_MUSIC = booleanPreferencesKey("music")
        private val KEY_HAPTICS = booleanPreferencesKey("haptics")
        private val KEY_PERF = booleanPreferencesKey("high_perf")
    }

    val settingsFlow: Flow<UserSettings> = context.dataStore.data.map { prefs ->
        val langStr = prefs[KEY_LANG] ?: AppLanguage.BANGLA.name
        val lang = try {
            AppLanguage.valueOf(langStr)
        } catch (e: Exception) {
            AppLanguage.BANGLA
        }
        UserSettings(
            language = lang,
            soundEnabled = prefs[KEY_SFX] ?: true,
            musicEnabled = prefs[KEY_MUSIC] ?: true,
            hapticsEnabled = prefs[KEY_HAPTICS] ?: true,
            highPerformance = prefs[KEY_PERF] ?: true
        )
    }

    suspend fun setLanguage(language: AppLanguage) {
        context.dataStore.edit { prefs ->
            prefs[KEY_LANG] = language.name
        }
    }

    suspend fun setSound(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_SFX] = enabled
        }
    }

    suspend fun setMusic(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_MUSIC] = enabled
        }
    }

    suspend fun setHaptics(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_HAPTICS] = enabled
        }
    }

    suspend fun setHighPerformance(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_PERF] = enabled
        }
    }
}
