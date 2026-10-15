package com.calki.app.ui.screens

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.calki.app.R
import com.calki.app.data.local.CalculationEntity
import com.calki.app.domain.model.AccentPreset
import com.calki.app.domain.model.AppThemeMode
import com.calki.app.domain.model.CalculatorKey
import com.calki.app.ui.CalculatorState
import com.calki.app.ui.components.CalculatorDisplay
import com.calki.app.ui.components.CalculatorKeypad
import com.calki.app.ui.theme.LocalCalkiPalette

@Composable
fun CalculatorScreen(
    state: CalculatorState,
    onKeyPress: (CalculatorKey) -> Unit,
    onSwipeDelete: () -> Unit,
    onLongPressClearAll: () -> Unit,
    onReuseHistory: (CalculationEntity) -> Unit,
    onDeleteHistoryItem: (CalculationEntity) -> Unit,
    onClearHistory: () -> Unit,
    onThemeChange: (AppThemeMode) -> Unit,
    onHapticsChange: (Boolean) -> Unit,
    onSoundChange: (Boolean) -> Unit,
    onDecimalPlacesChange: (Int) -> Unit,
    onAccentPresetChange: (AccentPreset) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalCalkiPalette.current
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    var showHistorySheet by rememberSaveable { mutableStateOf(false) }
    var showSettingsSheet by rememberSaveable { mutableStateOf(false) }
    val historyClearedText = stringResource(R.string.history_cleared_toast)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background)
            .safeDrawingPadding()
            .padding(horizontal = 18.dp, vertical = if (isLandscape) 8.dp else 14.dp),
        verticalArrangement = Arrangement.spacedBy(if (isLandscape) 10.dp else 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("calki", style = MaterialTheme.typography.titleLarge, color = palette.textPrimary, modifier = Modifier.padding(start = 8.dp))
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { showHistorySheet = true }) {
                Icon(Icons.Outlined.History, contentDescription = stringResource(R.string.cd_history_button), tint = palette.textPrimary)
            }
            IconButton(onClick = { showSettingsSheet = true }) {
                Icon(Icons.Outlined.Tune, contentDescription = stringResource(R.string.cd_settings_button), tint = palette.textPrimary)
            }
        }

        CalculatorDisplay(
            formattedExpression = state.formattedExpression,
            livePreview = state.livePreview,
            lastExpressionPeek = state.lastExpressionPeek,
            errorMessage = state.errorMessage,
            evaluationSparkleTrigger = state.evaluationSparkleTrigger,
            hapticsEnabled = state.settings.hapticsEnabled,
            soundEnabled = state.settings.soundEnabled,
            onSwipeDelete = onSwipeDelete,
            onHistoryPeekClick = { showHistorySheet = true },
            modifier = Modifier.weight(if (isLandscape) 0.34f else 0.32f)
        )

        CalculatorKeypad(
            isLandscape = isLandscape,
            hapticsEnabled = state.settings.hapticsEnabled,
            soundEnabled = state.settings.soundEnabled,
            onKeyPress = onKeyPress,
            onLongPressClear = {
                onLongPressClearAll()
                Toast.makeText(context, historyClearedText, Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.weight(if (isLandscape) 0.66f else 0.68f)
        )
    }

    if (showHistorySheet) {
        HistoryBottomSheet(
            history = state.history,
            onSelectItem = { onReuseHistory(it); showHistorySheet = false },
            onDeleteItem = onDeleteHistoryItem,
            onClearAll = onClearHistory,
            onDismiss = { showHistorySheet = false }
        )
    }

    if (showSettingsSheet) {
        SettingsBottomSheet(
            settings = state.settings,
            onThemeChange = onThemeChange,
            onHapticsChange = onHapticsChange,
            onSoundChange = onSoundChange,
            onDecimalPlacesChange = onDecimalPlacesChange,
            onAccentPresetChange = onAccentPresetChange,
            onDismiss = { showSettingsSheet = false }
        )
    }
}
