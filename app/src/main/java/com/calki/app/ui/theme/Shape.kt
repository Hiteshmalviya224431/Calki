package com.calki.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val CalkiShapes = Shapes(
    small = RoundedCornerShape(18.dp),
    medium = RoundedCornerShape(24.dp),
    large = RoundedCornerShape(32.dp),
    extraLarge = RoundedCornerShape(40.dp)
)

val FunkyBlobKeyShape = RoundedCornerShape(topStart = 28.dp, topEnd = 22.dp, bottomEnd = 28.dp, bottomStart = 22.dp)
val PillKeyShape = RoundedCornerShape(percent = 50)
val DisplayCardShape = RoundedCornerShape(32.dp)
