package com.calki.app.data.repository

import com.calki.app.data.local.CalculationEntity
import com.calki.app.data.local.HistoryDao
import com.calki.app.data.preferences.SettingsDataStore
import com.calki.app.domain.model.AccentPreset
import com.calki.app.domain.model.AppThemeMode
import com.calki.app.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalculatorRepository @Inject constructor(
    private val historyDao: HistoryDao,
    private val settingsDataStore: SettingsDataStore
) {
    val historyFlow: Flow<List<CalculationEntity>> = historyDao.observeAll()
    val settingsFlow: Flow<UserSettings> = settingsDataStore.settingsFlow

    suspend fun addHistory(expression: String, result: String) {
        historyDao.insert(CalculationEntity(expression = expression, result = result))
    }
    suspend fun deleteHistoryItem(item: CalculationEntity) = historyDao.delete(item)
    suspend fun clearAllHistory() = historyDao.clearAll()
    suspend fun updateTheme(mode: AppThemeMode) = settingsDataStore.setThemeMode(mode)
    suspend fun updateHaptics(enabled: Boolean) = settingsDataStore.setHapticsEnabled(enabled)
    suspend fun updateSound(enabled: Boolean) = settingsDataStore.setSoundEnabled(enabled)
    suspend fun updateDecimalPlaces(places: Int) = settingsDataStore.setDecimalPlaces(places)
    suspend fun updateAccentPreset(preset: AccentPreset) = settingsDataStore.setAccentPreset(preset)
}
