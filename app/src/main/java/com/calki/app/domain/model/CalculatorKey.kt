package com.calki.app.domain.model

enum class KeyCategory {
    NUMBER, OPERATOR, UTILITY, EQUALS, SCIENTIFIC
}

enum class CalculatorKey(
    val label: String,
    val token: String = label,
    val category: KeyCategory,
    val contentDescription: String
) {
    CLEAR("C", "", KeyCategory.UTILITY, "Clear expression. Long press to clear all history"),
    BACKSPACE("⌫", "", KeyCategory.UTILITY, "Delete last character"),
    PERCENT("%", "%", KeyCategory.UTILITY, "Percent"),
    PLUS_MINUS("+/−", "", KeyCategory.UTILITY, "Toggle positive or negative"),
    DIVIDE("÷", "÷", KeyCategory.OPERATOR, "Divide"),
    MULTIPLY("×", "×", KeyCategory.OPERATOR, "Multiply"),
    SUBTRACT("−", "−", KeyCategory.OPERATOR, "Subtract"),
    ADD("+", "+", KeyCategory.OPERATOR, "Add"),
    EQUALS("=", "=", KeyCategory.EQUALS, "Equals"),
    ZERO("0", "0", KeyCategory.NUMBER, "Zero"),
    ONE("1", "1", KeyCategory.NUMBER, "One"),
    TWO("2", "2", KeyCategory.NUMBER, "Two"),
    THREE("3", "3", KeyCategory.NUMBER, "Three"),
    FOUR("4", "4", KeyCategory.NUMBER, "Four"),
    FIVE("5", "5", KeyCategory.NUMBER, "Five"),
    SIX("6", "6", KeyCategory.NUMBER, "Six"),
    SEVEN("7", "7", KeyCategory.NUMBER, "Seven"),
    EIGHT("8", "8", KeyCategory.NUMBER, "Eight"),
    NINE("9", "9", KeyCategory.NUMBER, "Nine"),
    DECIMAL(".", ".", KeyCategory.NUMBER, "Decimal point"),
    SIN("sin", "sin(", KeyCategory.SCIENTIFIC, "Sine"),
    COS("cos", "cos(", KeyCategory.SCIENTIFIC, "Cosine"),
    TAN("tan", "tan(", KeyCategory.SCIENTIFIC, "Tangent"),
    LOG("log", "log(", KeyCategory.SCIENTIFIC, "Base 10 logarithm"),
    LN("ln", "ln(", KeyCategory.SCIENTIFIC, "Natural logarithm"),
    SQRT("√", "√(", KeyCategory.SCIENTIFIC, "Square root"),
    SQUARE("x²", "^2", KeyCategory.SCIENTIFIC, "Square"),
    POWER("xʸ", "^", KeyCategory.SCIENTIFIC, "Power"),
    PI("π", "π", KeyCategory.SCIENTIFIC, "Pi constant"),
    E("e", "e", KeyCategory.SCIENTIFIC, "Euler's constant"),
    LEFT_PAREN("(", "(", KeyCategory.SCIENTIFIC, "Left parenthesis"),
    RIGHT_PAREN(")", ")", KeyCategory.SCIENTIFIC, "Right parenthesis")
}
