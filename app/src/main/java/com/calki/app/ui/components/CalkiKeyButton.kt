package com.calki.app.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.calki.app.domain.model.CalculatorKey
import com.calki.app.domain.model.KeyCategory
import com.calki.app.ui.theme.FunkyBlobKeyShape
import com.calki.app.ui.theme.LocalCalkiPalette
import com.calki.app.ui.theme.LocalReduceMotion
import com.calki.app.ui.theme.PillKeyShape

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalkiKeyButton(
    key: CalculatorKey,
    hapticsEnabled: Boolean,
    soundEnabled: Boolean,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val palette = LocalCalkiPalette.current
    val reduceMotion = LocalReduceMotion.current
    val view = LocalView.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && !reduceMotion) 0.92f else 1f,
        animationSpec = if (reduceMotion) tween(0) else spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow),
        label = "keyScale"
    )

    val (bgColor, contentColor, shape: Shape) = when (key.category) {
        KeyCategory.NUMBER -> Triple(palette.numberKey, palette.onNumberKey, RoundedCornerShape(26.dp))
        KeyCategory.OPERATOR -> Triple(palette.operatorKey, palette.onOperatorKey, PillKeyShape)
        KeyCategory.EQUALS -> Triple(palette.equalsKey, palette.onEqualsKey, PillKeyShape)
        KeyCategory.UTILITY -> Triple(palette.utilityKey, palette.onUtilityKey, FunkyBlobKeyShape)
        KeyCategory.SCIENTIFIC -> Triple(palette.scientificKey, palette.onScientificKey, FunkyBlobKeyShape)
    }

    val textStyle = if (key.category == KeyCategory.SCIENTIFIC) MaterialTheme.typography.titleMedium else MaterialTheme.typography.headlineMedium

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .scale(scale)
            .clip(shape)
            .background(bgColor)
            .semantics {
                contentDescription = key.contentDescription
                role = Role.Button
            }
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    FeedbackHelper.performFeedback(view, hapticsEnabled, soundEnabled)
                    onClick()
                },
                onLongClick = onLongClick?.let {
                    {
                        FeedbackHelper.performFeedback(view, hapticsEnabled, soundEnabled, isLongPress = true)
                        it()
                    }
                }
            )
            .fillMaxSize()
    ) {
        Text(text = key.label, style = textStyle, color = contentColor)
    }
}
