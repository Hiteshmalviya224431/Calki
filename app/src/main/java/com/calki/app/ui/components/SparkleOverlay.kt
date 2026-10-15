package com.calki.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.calki.app.ui.theme.BlushPink
import com.calki.app.ui.theme.CoralLight
import com.calki.app.ui.theme.LocalReduceMotion
import com.calki.app.ui.theme.SunYellowLight
import com.calki.app.ui.theme.VioletLight
import kotlin.math.cos
import kotlin.math.sin

private data class SparkleParticle(val angleRad: Float, val speed: Float, val color: Color, val radius: Float)

@Composable
fun SparkleOverlay(trigger: Int, modifier: Modifier = Modifier) {
    val reduceMotion = LocalReduceMotion.current
    val progress = remember { Animatable(1f) }
    val particles = remember {
        val colors = listOf(SunYellowLight, CoralLight, VioletLight, BlushPink)
        List(14) { idx ->
            SparkleParticle(
                angleRad = (idx * (360f / 14f)) * (Math.PI.toFloat() / 180f),
                speed = if (idx % 2 == 0) 110f else 70f,
                color = colors[idx % colors.size],
                radius = if (idx % 3 == 0) 9f else 6f
            )
        }
    }

    LaunchedEffect(trigger) {
        if (trigger > 0 && !reduceMotion) {
            progress.snapTo(0f)
            progress.animateTo(1f, tween(durationMillis = 280, easing = FastOutSlowInEasing))
        }
    }

    if (progress.value < 1f && !reduceMotion) {
        Canvas(modifier = modifier.fillMaxSize()) {
            val t = progress.value
            val alpha = (1f - t).coerceIn(0f, 1f)
            val center = Offset(size.width * 0.82f, size.height * 0.65f)
            particles.forEach { p ->
                val dist = p.speed * t
                drawCircle(
                    color = p.color.copy(alpha = alpha),
                    radius = p.radius * (1f - t * 0.4f),
                    center = Offset(center.x + cos(p.angleRad) * dist, center.y + sin(p.angleRad) * dist)
                )
            }
        }
    }
}
