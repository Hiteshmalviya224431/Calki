package com.calki.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.calki.app.domain.model.AccentPreset
import com.calki.app.domain.model.AppThemeMode
import com.calki.app.domain.model.UserSettings
import com.calki.app.ui.theme.LocalCalkiPalette
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBottomSheet(
    settings: UserSettings,
    onThemeChange: (AppThemeMode) -> Unit,
    onHapticsChange: (Boolean) -> Unit,
    onSoundChange: (Boolean) -> Unit,
    onDecimalPlacesChange: (Int) -> Unit,
    onAccentPresetChange: (AccentPreset) -> Unit,
    onDismiss: () -> Unit
) {
    val palette = LocalCalkiPalette.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = palette.background,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp).padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text("Calki Settings", style = MaterialTheme.typography.titleLarge, color = palette.textPrimary)

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Theme", style = MaterialTheme.typography.titleMedium, color = palette.textPrimary)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AppThemeMode.entries.forEach { mode ->
                        FilterChip(
                            selected = settings.themeMode == mode,
                            onClick = { onThemeChange(mode) },
                            label = { Text(mode.label) },
                            shape = RoundedCornerShape(50)
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Funky Accent Palette", style = MaterialTheme.typography.titleMedium, color = palette.textPrimary)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AccentPreset.entries.forEach { preset ->
                        val isSelected = settings.accentPreset == preset
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f).clip(RoundedCornerShape(20.dp)).background(palette.displayCard)
                                .border(if (isSelected) 2.5.dp else 0.dp, if (isSelected) palette.equalsKey else palette.displayCard, RoundedCornerShape(20.dp))
                                .clickable { onAccentPresetChange(preset) }.padding(vertical = 12.dp, horizontal = 6.dp)
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy((-6).dp)) {
                                Box(modifier = Modifier.size(22.dp).clip(CircleShape).background(preset.operatorLight))
                                Box(modifier = Modifier.size(22.dp).clip(CircleShape).background(preset.equalsLight))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(preset.title, style = MaterialTheme.typography.labelLarge, color = palette.textPrimary, maxLines = 1)
                        }
                    }
                }
            }

            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Max Decimal Places", style = MaterialTheme.typography.titleMedium, color = palette.textPrimary)
                    Text(settings.decimalPlaces.toString(), style = MaterialTheme.typography.titleMedium, color = palette.equalsKey)
                }
                Slider(
                    value = settings.decimalPlaces.toFloat(),
                    onValueChange = { onDecimalPlacesChange(it.roundToInt()) },
                    valueRange = 0f..10f,
                    steps = 9
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Haptic Feedback", style = MaterialTheme.typography.titleMedium, color = palette.textPrimary)
                Switch(checked = settings.hapticsEnabled, onCheckedChange = onHapticsChange)
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Key Click Sounds", style = MaterialTheme.typography.titleMedium, color = palette.textPrimary)
                Switch(checked = settings.soundEnabled, onCheckedChange = onSoundChange)
            }
        }
    }
}
