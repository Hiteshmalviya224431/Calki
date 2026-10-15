package com.calki.app.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.calki.app.domain.model.AccentPreset
import com.calki.app.domain.model.AppThemeMode
import com.calki.app.domain.model.UserSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "calki_settings")

@Singleton
class SettingsDataStore @Inject constructor(@ApplicationContext private val context: Context) {
    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val HAPTICS_ENABLED = booleanPreferencesKey("haptics_enabled")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val DECIMAL_PLACES = intPreferencesKey("decimal_places")
        val ACCENT_PRESET = stringPreferencesKey("accent_preset")
    }

    val settingsFlow: Flow<UserSettings> = context.dataStore.data.map { prefs ->
        UserSettings(
            themeMode = prefs[Keys.THEME_MODE]?.let { runCatching { AppThemeMode.valueOf(it) }.getOrNull() } ?: AppThemeMode.SYSTEM,
            hapticsEnabled = prefs[Keys.HAPTICS_ENABLED] ?: true,
            soundEnabled = prefs[Keys.SOUND_ENABLED] ?: false,
            decimalPlaces = prefs[Keys.DECIMAL_PLACES] ?: 8,
            accentPreset = prefs[Keys.ACCENT_PRESET]?.let { runCatching { AccentPreset.valueOf(it) }.getOrNull() } ?: AccentPreset.CORAL_POP
        )
    }

    suspend fun setThemeMode(mode: AppThemeMode) { context.dataStore.edit { it[Keys.THEME_MODE] = mode.name } }
    suspend fun setHapticsEnabled(enabled: Boolean) { context.dataStore.edit { it[Keys.HAPTICS_ENABLED] = enabled } }
    suspend fun setSoundEnabled(enabled: Boolean) { context.dataStore.edit { it[Keys.SOUND_ENABLED] = enabled } }
    suspend fun setDecimalPlaces(places: Int) { context.dataStore.edit { it[Keys.DECIMAL_PLACES] = places.coerceIn(0, 10) } }
    suspend fun setAccentPreset(preset: AccentPreset) { context.dataStore.edit { it[Keys.ACCENT_PRESET] = preset.name } }
}
