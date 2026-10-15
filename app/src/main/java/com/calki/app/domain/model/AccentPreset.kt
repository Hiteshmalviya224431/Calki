package com.calki.app.domain.model

import androidx.compose.ui.graphics.Color

enum class AccentPreset(
    val title: String,
    val operatorLight: Color,
    val operatorDark: Color,
    val equalsLight: Color,
    val equalsDark: Color,
    val utilityLight: Color,
    val utilityDark: Color
) {
    CORAL_POP(
        title = "Coral Pop",
        operatorLight = Color(0xFFFF8A65),
        operatorDark = Color(0xFFFF9E80),
        equalsLight = Color(0xFF7C5CFF),
        equalsDark = Color(0xFF967BFF),
        utilityLight = Color(0xFFFFD166),
        utilityDark = Color(0xFFFFDD85)
    ),
    MINT_FIZZ(
        title = "Mint Fizz",
        operatorLight = Color(0xFF2EC4B6),
        operatorDark = Color(0xFF48DFD0),
        equalsLight = Color(0xFF6C63FF),
        equalsDark = Color(0xFF8A84FF),
        utilityLight = Color(0xFFFFBF69),
        utilityDark = Color(0xFFFFCE8A)
    ),
    BERRY_GROOVE(
        title = "Berry Groove",
        operatorLight = Color(0xFFFF5D8F),
        operatorDark = Color(0xFFFF7AA2),
        equalsLight = Color(0xFF8338EC),
        equalsDark = Color(0xFF9D5CFF),
        utilityLight = Color(0xFFFFBE0B),
        utilityDark = Color(0xFFFFD147)
    ),
    MATCHA_SUN(
        title = "Matcha Sun",
        operatorLight = Color(0xFF6A994E),
        operatorDark = Color(0xFF88B76B),
        equalsLight = Color(0xFFBC4749),
        equalsDark = Color(0xFFD86567),
        utilityLight = Color(0xFFF2CC8F),
        utilityDark = Color(0xFFF7DBA7)
    )
}
