package com.calki.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.calki.app.domain.model.CalculatorKey

private val standardKeyRows = listOf(
    listOf(CalculatorKey.CLEAR, CalculatorKey.BACKSPACE, CalculatorKey.PERCENT, CalculatorKey.DIVIDE),
    listOf(CalculatorKey.SEVEN, CalculatorKey.EIGHT, CalculatorKey.NINE, CalculatorKey.MULTIPLY),
    listOf(CalculatorKey.FOUR, CalculatorKey.FIVE, CalculatorKey.SIX, CalculatorKey.SUBTRACT),
    listOf(CalculatorKey.ONE, CalculatorKey.TWO, CalculatorKey.THREE, CalculatorKey.ADD),
    listOf(CalculatorKey.PLUS_MINUS, CalculatorKey.ZERO, CalculatorKey.DECIMAL, CalculatorKey.EQUALS)
)

private val scientificKeyRows = listOf(
    listOf(CalculatorKey.SIN, CalculatorKey.COS, CalculatorKey.TAN),
    listOf(CalculatorKey.LOG, CalculatorKey.LN, CalculatorKey.SQRT),
    listOf(CalculatorKey.SQUARE, CalculatorKey.POWER, CalculatorKey.PI),
    listOf(CalculatorKey.LEFT_PAREN, CalculatorKey.RIGHT_PAREN, CalculatorKey.E)
)

@Composable
fun CalculatorKeypad(
    isLandscape: Boolean,
    hapticsEnabled: Boolean,
    soundEnabled: Boolean,
    onKeyPress: (CalculatorKey) -> Unit,
    onLongPressClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isLandscape) {
        Row(modifier = modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(modifier = Modifier.weight(0.42f).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                scientificKeyRows.forEach { row ->
                    Row(modifier = Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        row.forEach { key ->
                            CalkiKeyButton(
                                key = key,
                                hapticsEnabled = hapticsEnabled,
                                soundEnabled = soundEnabled,
                                onClick = { onKeyPress(key) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
            StandardKeyGrid(
                hapticsEnabled = hapticsEnabled,
                soundEnabled = soundEnabled,
                onKeyPress = onKeyPress,
                onLongPressClear = onLongPressClear,
                spacing = 10,
                modifier = Modifier.weight(0.58f).fillMaxHeight()
            )
        }
    } else {
        StandardKeyGrid(
            hapticsEnabled = hapticsEnabled,
            soundEnabled = soundEnabled,
            onKeyPress = onKeyPress,
            onLongPressClear = onLongPressClear,
            spacing = 12,
            modifier = modifier.fillMaxSize()
        )
    }
}

@Composable
private fun StandardKeyGrid(
    hapticsEnabled: Boolean,
    soundEnabled: Boolean,
    onKeyPress: (CalculatorKey) -> Unit,
    onLongPressClear: () -> Unit,
    spacing: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(spacing.dp)) {
        standardKeyRows.forEach { row ->
            Row(modifier = Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(spacing.dp)) {
                row.forEach { key ->
                    CalkiKeyButton(
                        key = key,
                        hapticsEnabled = hapticsEnabled,
                        soundEnabled = soundEnabled,
                        onClick = { onKeyPress(key) },
                        onLongClick = if (key == CalculatorKey.CLEAR) onLongPressClear else null,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
