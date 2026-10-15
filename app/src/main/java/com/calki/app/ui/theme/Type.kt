package com.calki.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val RoundedSansFamily = FontFamily.SansSerif

val CalkiTypography = Typography(
    displayLarge = TextStyle(fontFamily = RoundedSansFamily, fontWeight = FontWeight.Light, fontSize = 60.sp, lineHeight = 68.sp, letterSpacing = (-1.2).sp),
    displayMedium = TextStyle(fontFamily = RoundedSansFamily, fontWeight = FontWeight.Light, fontSize = 42.sp, lineHeight = 50.sp, letterSpacing = (-0.5).sp),
    headlineMedium = TextStyle(fontFamily = RoundedSansFamily, fontWeight = FontWeight.SemiBold, fontSize = 26.sp, lineHeight = 32.sp),
    titleLarge = TextStyle(fontFamily = RoundedSansFamily, fontWeight = FontWeight.SemiBold, fontSize = 22.sp, lineHeight = 28.sp),
    titleMedium = TextStyle(fontFamily = RoundedSansFamily, fontWeight = FontWeight.Medium, fontSize = 18.sp, lineHeight = 24.sp),
    bodyLarge = TextStyle(fontFamily = RoundedSansFamily, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
    labelLarge = TextStyle(fontFamily = RoundedSansFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp)
)
