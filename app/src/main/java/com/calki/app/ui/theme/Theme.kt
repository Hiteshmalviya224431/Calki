package com.calki.app.ui.theme

import android.content.Context
import android.provider.Settings
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.calki.app.domain.model.AccentPreset
import com.calki.app.domain.model.AppThemeMode

@Immutable
data class CalkiPalette(
    val background: Color,
    val displayCard: Color,
    val numberKey: Color,
    val onNumberKey: Color,
    val operatorKey: Color,
    val onOperatorKey: Color,
    val equalsKey: Color,
    val onEqualsKey: Color,
    val utilityKey: Color,
    val onUtilityKey: Color,
    val scientificKey: Color,
    val onScientificKey: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val isDark: Boolean
)

val LocalCalkiPalette = staticCompositionLocalOf {
    CalkiPalette(
        background = LightBackground, displayCard = LightSurfaceCard, numberKey = LightKeySurface,
        onNumberKey = LightOnBackground, operatorKey = CoralLight, onOperatorKey = LightOnBackground,
        equalsKey = VioletLight, onEqualsKey = Color.White, utilityKey = SunYellowLight,
        onUtilityKey = LightOnBackground, scientificKey = LightSurfaceCard, onScientificKey = LightOnBackground,
        textPrimary = LightOnBackground, textSecondary = LightOnSurfaceVariant, isDark = false
    )
}

val LocalReduceMotion = staticCompositionLocalOf { false }

@Composable
fun rememberReduceMotion(): Boolean {
    val context = LocalContext.current
    return remember(context) {
        val scale = runCatching {
            Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f)
        }.getOrDefault(1f)
        scale == 0f
    }
}

@Composable
fun CalkiTheme(
    themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    accentPreset: AccentPreset = AccentPreset.CORAL_POP,
    content: @Composable () -> Unit
) {
    val systemDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        AppThemeMode.SYSTEM -> systemDark
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }

    val reduceMotion = rememberReduceMotion()
    val animDuration = if (reduceMotion) 0 else 240

    val bg by animateColorAsState(if (isDark) DarkBackground else LightBackground, tween(animDuration), label = "bg")
    val card by animateColorAsState(if (isDark) DarkSurfaceCard else LightSurfaceCard, tween(animDuration), label = "card")
    val numKey by animateColorAsState(if (isDark) DarkKeySurface else LightKeySurface, tween(animDuration), label = "numKey")
    val opKey by animateColorAsState(if (isDark) accentPreset.operatorDark else accentPreset.operatorLight, tween(animDuration), label = "opKey")
    val eqKey by animateColorAsState(if (isDark) accentPreset.equalsDark else accentPreset.equalsLight, tween(animDuration), label = "eqKey")
    val utilKey by animateColorAsState(if (isDark) accentPreset.utilityDark else accentPreset.utilityLight, tween(animDuration), label = "utilKey")
    val textPrimary by animateColorAsState(if (isDark) DarkOnBackground else LightOnBackground, tween(animDuration), label = "textPrimary")
    val textSecondary by animateColorAsState(if (isDark) DarkOnSurfaceVariant else LightOnSurfaceVariant, tween(animDuration), label = "textSecondary")

    val calkiPalette = CalkiPalette(
        background = bg, displayCard = card, numberKey = numKey, onNumberKey = textPrimary,
        operatorKey = opKey, onOperatorKey = Color(0xFF14121F), equalsKey = eqKey, onEqualsKey = Color.White,
        utilityKey = utilKey, onUtilityKey = Color(0xFF14121F), scientificKey = card, onScientificKey = textPrimary,
        textPrimary = textPrimary, textSecondary = textSecondary, isDark = isDark
    )

    val colorScheme = if (isDark) {
        darkColorScheme(background = bg, surface = card, primary = eqKey, secondary = opKey, tertiary = utilKey, onBackground = textPrimary, onSurface = textPrimary)
    } else {
        lightColorScheme(background = bg, surface = card, primary = eqKey, secondary = opKey, tertiary = utilKey, onBackground = textPrimary, onSurface = textPrimary)
    }

    CompositionLocalProvider(LocalCalkiPalette provides calkiPalette, LocalReduceMotion provides reduceMotion) {
        MaterialTheme(colorScheme = colorScheme, typography = CalkiTypography, shapes = CalkiShapes, content = content)
    }
}
