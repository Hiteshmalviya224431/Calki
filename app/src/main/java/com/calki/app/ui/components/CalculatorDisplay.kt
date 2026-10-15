package com.calki.app.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calki.app.R
import com.calki.app.ui.theme.DisplayCardShape
import com.calki.app.ui.theme.LocalCalkiPalette
import com.calki.app.ui.theme.LocalReduceMotion

@Composable
fun CalculatorDisplay(
    formattedExpression: String,
    livePreview: String,
    lastExpressionPeek: String,
    errorMessage: String?,
    evaluationSparkleTrigger: Int,
    hapticsEnabled: Boolean,
    soundEnabled: Boolean,
    onSwipeDelete: () -> Unit,
    onHistoryPeekClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalCalkiPalette.current
    val reduceMotion = LocalReduceMotion.current
    val view = LocalView.current
    val swipeDescription = stringResource(R.string.cd_display_swipe)
    var dragAccumulated by remember { mutableFloatStateOf(0f) }

    val primaryText = formattedExpression.ifEmpty { "0" }
    val dynamicFontSize = when {
        primaryText.length > 16 -> 30.sp
        primaryText.length > 12 -> 38.sp
        primaryText.length > 8 -> 46.sp
        else -> 58.sp
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(DisplayCardShape)
            .background(palette.displayCard)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = { dragAccumulated = 0f },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        dragAccumulated += dragAmount
                    },
                    onDragEnd = {
                        if (dragAccumulated < -65f) {
                            FeedbackHelper.performFeedback(view, hapticsEnabled, soundEnabled)
                            onSwipeDelete()
                        }
                    }
                )
            }
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = lastExpressionPeek.ifEmpty { " " },
                style = MaterialTheme.typography.titleMedium,
                color = palette.textSecondary.copy(alpha = 0.75f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth().clickable(enabled = lastExpressionPeek.isNotEmpty()) { onHistoryPeekClick() }
            )

            AnimatedContent(
                targetState = primaryText to evaluationSparkleTrigger,
                transitionSpec = {
                    val isEquals = targetState.second != initialState.second && !reduceMotion
                    if (isEquals) {
                        (slideInVertically(tween(240)) { it / 2 } + fadeIn(tween(240)))
                            .togetherWith(slideOutVertically(tween(200)) { -it / 2 } + fadeOut(tween(180)))
                    } else {
                        fadeIn(tween(0)).togetherWith(fadeOut(tween(0)))
                    }.using(SizeTransform(clip = false))
                },
                label = "mainDisplayAnim"
            ) { (displayValue, _) ->
                Text(
                    text = displayValue,
                    style = MaterialTheme.typography.displayLarge.copy(fontSize = dynamicFontSize, lineHeight = dynamicFontSize * 1.15f),
                    color = palette.textPrimary,
                    textAlign = TextAlign.End,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth().semantics {
                        liveRegion = LiveRegionMode.Polite
                        contentDescription = "$swipeDescription Current value: $displayValue"
                    }
                )
            }

            val bottomText = errorMessage ?: livePreview.ifEmpty { " " }
            val isError = errorMessage != null
            Text(
                text = bottomText,
                style = MaterialTheme.typography.titleLarge,
                color = if (isError) palette.operatorKey else palette.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }
        SparkleOverlay(trigger = evaluationSparkleTrigger)
    }
}
