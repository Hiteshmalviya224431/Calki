package com.calki.app.domain.model

data class UserSettings(
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val hapticsEnabled: Boolean = true,
    val soundEnabled: Boolean = false,
    val decimalPlaces: Int = 8,
    val accentPreset: AccentPreset = AccentPreset.CORAL_POP
)
