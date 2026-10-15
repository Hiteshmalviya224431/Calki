package com.calki.app.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.calki.app.data.local.CalculationEntity
import com.calki.app.ui.theme.LocalCalkiPalette

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryBottomSheet(
    history: List<CalculationEntity>,
    onSelectItem: (CalculationEntity) -> Unit,
    onDeleteItem: (CalculationEntity) -> Unit,
    onClearAll: () -> Unit,
    onDismiss: () -> Unit
) {
    val palette = LocalCalkiPalette.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = palette.background,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("History", style = MaterialTheme.typography.titleLarge, color = palette.textPrimary)
                if (history.isNotEmpty()) {
                    TextButton(onClick = onClearAll) {
                        Text("Clear all", style = MaterialTheme.typography.labelLarge, color = palette.operatorKey)
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            if (history.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    Text("No calculations yet ✨", style = MaterialTheme.typography.bodyLarge, color = palette.textSecondary)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
                ) {
                    items(items = history, key = { it.id }) { item ->
                        val dismissState = rememberSwipeToDismissBoxState()
                        LaunchedEffect(dismissState.currentValue) {
                            if (dismissState.currentValue != SwipeToDismissBoxValue.Settled) onDeleteItem(item)
                        }
                        SwipeToDismissBox(
                            state = dismissState,
                            backgroundContent = {
                                val bg by animateColorAsState(
                                    if (dismissState.targetValue == SwipeToDismissBoxValue.Settled) Color.Transparent else palette.operatorKey.copy(alpha = 0.25f),
                                    label = "swipeBg"
                                )
                                Box(
                                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(24.dp)).background(bg).padding(horizontal = 20.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Icon(Icons.Outlined.DeleteOutline, contentDescription = "Delete", tint = palette.operatorKey)
                                }
                            }
                        ) {
                            Column(
                                horizontalAlignment = Alignment.End,
                                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(palette.displayCard)
                                    .clickable { onSelectItem(item) }.padding(horizontal = 20.dp, vertical = 16.dp)
                            ) {
                                Text(item.expression, style = MaterialTheme.typography.bodyLarge, color = palette.textSecondary, textAlign = TextAlign.End)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("= ${item.result}", style = MaterialTheme.typography.headlineMedium, color = palette.textPrimary, textAlign = TextAlign.End)
                            }
                        }
                    }
                }
            }
        }
    }
}
