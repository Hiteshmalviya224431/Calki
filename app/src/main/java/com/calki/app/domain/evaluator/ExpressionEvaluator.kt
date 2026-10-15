package com.calki.app.domain.evaluator

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.ArrayDeque
import java.util.Locale
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

class ExpressionEvaluator {
    private val mc = MathContext(34, RoundingMode.HALF_UP)

    private sealed interface Token {
        data class NumberToken(val value: BigDecimal) : Token
        data class OperatorToken(val symbol: String) : Token
        data class FunctionToken(val name: String) : Token
        data object LeftParen : Token
        data object RightParen : Token
        data object Percent : Token
    }

    fun evaluate(
        expression: String,
        maxDecimalPlaces: Int = 8,
        stripTrailingOperatorsForLivePreview: Boolean = false
    ): EvalResult {
        var cleaned = expression.replace(",", "").replace(" ", "")
        if (cleaned.isEmpty()) return EvalResult.Empty

        if (stripTrailingOperatorsForLivePreview) {
            cleaned = cleaned.trimEnd { it in listOf('+', '−', '-', '×', '*', '÷', '/', '^', '.') }
            if (cleaned.isEmpty()) return EvalResult.Empty
        }

        val openParens = cleaned.count { it == '(' }
        val closeParens = cleaned.count { it == ')' }
        if (openParens > closeParens) {
            cleaned += ")".repeat(openParens - closeParens)
        }

        return try {
            val tokens = tokenize(cleaned)
            if (tokens.isEmpty()) return EvalResult.Empty
            val rpn = shuntingYard(tokens)
            val result = evaluateRpn(rpn)
            EvalResult.Success(
                rawValue = result,
                formattedValue = formatNumber(result, maxDecimalPlaces)
            )
        } catch (e: ArithmeticException) {
            if (e.message == DIV_ZERO) EvalResult.DivideByZero else EvalResult.InvalidExpression
        } catch (_: Exception) {
            EvalResult.InvalidExpression
        }
    }

    fun formatInputExpression(rawExpression: String): String {
        if (rawExpression.isEmpty()) return ""
        val numberRegex = Regex("""(?<![a-zA-Z0-9.])[0-9]+(\.[0-9]*)?""")
        return numberRegex.replace(rawExpression) { match ->
            val text = match.value
            val parts = text.split(".")
            val intPart = parts[0].toBigIntegerOrNull() ?: return@replace text
            val symbols = DecimalFormatSymbols(Locale.US)
            val formatter = DecimalFormat("#,###", symbols)
            val formattedInt = formatter.format(intPart)
            if (parts.size > 1) "$formattedInt.${parts[1]}" else formattedInt
        }
    }

    fun formatNumber(value: BigDecimal, maxDecimalPlaces: Int): String {
        val stripped = value.stripTrailingZeros()
        if (stripped.compareTo(BigDecimal.ZERO) == 0) return "0"
        val absVal = stripped.abs()
        val symbols = DecimalFormatSymbols(Locale.US)

        if (absVal >= BigDecimal("1000000000000") ||
            (absVal > BigDecimal.ZERO && absVal < BigDecimal("0.000001"))
        ) {
            val pattern = "0." + "#".repeat(maxDecimalPlaces.coerceIn(1, 10)) + "E0"
            val sciFormatter = DecimalFormat(pattern, symbols)
            sciFormatter.roundingMode = RoundingMode.HALF_UP
            return sciFormatter.format(stripped)
        }

        val rounded = stripped.setScale(maxDecimalPlaces, RoundingMode.HALF_UP).stripTrailingZeros()
        val pattern = if (maxDecimalPlaces > 0) "#,##0." + "#".repeat(maxDecimalPlaces) else "#,##0"
        val formatter = DecimalFormat(pattern, symbols)
        formatter.roundingMode = RoundingMode.HALF_UP
        return formatter.format(rounded)
    }

    private fun tokenize(expr: String): List<Token> {
        val rawTokens = mutableListOf<Token>()
        var i = 0
        val normalized = expr.replace('−', '-').replace('×', '*').replace('÷', '/')

        while (i < normalized.length) {
            val c = normalized[i]
            when {
                c.isDigit() || c == '.' -> {
                    val start = i
                    var dotCount = 0
                    while (i < normalized.length && (normalized[i].isDigit() || normalized[i] == '.')) {
                        if (normalized[i] == '.') dotCount++
                        if (dotCount > 1) throw IllegalArgumentException("Multiple decimals")
                        i++
                    }
                    val numStr = normalized.substring(start, i)
                    val bd = if (numStr == ".") BigDecimal.ZERO else BigDecimal(numStr)
                    rawTokens.add(Token.NumberToken(bd))
                }
                c == 'π' -> {
                    rawTokens.add(Token.NumberToken(BigDecimal(Math.PI.toString())))
                    i++
                }
                c == 'e' -> {
                    rawTokens.add(Token.NumberToken(BigDecimal(Math.E.toString())))
                    i++
                }
                c == '√' -> {
                    rawTokens.add(Token.FunctionToken("sqrt"))
                    i++
                }
                c.isLetter() -> {
                    val start = i
                    while (i < normalized.length && normalized[i].isLetter()) i++
                    rawTokens.add(Token.FunctionToken(normalized.substring(start, i)))
                }
                c == '(' -> { rawTokens.add(Token.LeftParen); i++ }
                c == ')' -> { rawTokens.add(Token.RightParen); i++ }
                c == '%' -> { rawTokens.add(Token.Percent); i++ }
                c in listOf('+', '-', '*', '/', '^') -> {
                    val isUnaryMinus = c == '-' && (rawTokens.isEmpty() ||
                            rawTokens.last() is Token.OperatorToken ||
                            rawTokens.last() is Token.LeftParen)
                    rawTokens.add(Token.OperatorToken(if (isUnaryMinus) "u-" else c.toString()))
                    i++
                }
                else -> throw IllegalArgumentException("Unexpected char: $c")
            }
        }

        val result = mutableListOf<Token>()
        for (idx in rawTokens.indices) {
            val current = rawTokens[idx]
            if (idx > 0) {
                val prev = rawTokens[idx - 1]
                val prevIsValue = prev is Token.NumberToken || prev is Token.RightParen || prev is Token.Percent
                val currStartsValue = current is Token.NumberToken || current is Token.LeftParen || current is Token.FunctionToken
                if (prevIsValue && currStartsValue) {
                    result.add(Token.OperatorToken("*"))
                }
            }
            result.add(current)
        }
        return result
    }

    private fun shuntingYard(tokens: List<Token>): List<Token> {
        val output = mutableListOf<Token>()
        val stack = ArrayDeque<Token>()

        fun precedence(op: String): Int = when (op) {
            "+", "-" -> 1
            "*", "/" -> 2
            "^" -> 3
            "u-" -> 4
            else -> 0
        }
        fun isRightAssociative(op: String): Boolean = op == "^" || op == "u-"

        for (token in tokens) {
            when (token) {
                is Token.NumberToken, is Token.Percent -> output.add(token)
                is Token.FunctionToken, is Token.LeftParen -> stack.push(token)
                is Token.RightParen -> {
                    while (stack.isNotEmpty() && stack.peek() !is Token.LeftParen) {
                        output.add(stack.pop())
                    }
                    if (stack.isEmpty() || stack.peek() !is Token.LeftParen) {
                        throw IllegalArgumentException("Mismatched parentheses")
                    }
                    stack.pop()
                    if (stack.isNotEmpty() && stack.peek() is Token.FunctionToken) {
                        output.add(stack.pop())
                    }
                }
                is Token.OperatorToken -> {
                    while (stack.isNotEmpty()) {
                        val top = stack.peek()
                        if (top is Token.OperatorToken) {
                            val p1 = precedence(token.symbol)
                            val p2 = precedence(top.symbol)
                            if ((!isRightAssociative(token.symbol) && p1 <= p2) ||
                                (isRightAssociative(token.symbol) && p1 < p2)
                            ) {
                                output.add(stack.pop())
                                continue
                            }
                        }
                        break
                    }
                    stack.push(token)
                }
            }
        }
        while (stack.isNotEmpty()) {
            val top = stack.pop()
            if (top is Token.LeftParen || top is Token.RightParen) {
                throw IllegalArgumentException("Mismatched parentheses")
            }
            output.add(top)
        }
        return output
    }

    private fun evaluateRpn(rpn: List<Token>): BigDecimal {
        val stack = ArrayDeque<BigDecimal>()
        val hundred = BigDecimal("100")
        var i = 0
        while (i < rpn.size) {
            when (val token = rpn[i]) {
                is Token.NumberToken -> stack.push(token.value)
                is Token.Percent -> {
                    val current = stack.pop()
                    val nextToken = rpn.getOrNull(i + 1)
                    if (stack.isNotEmpty() && nextToken is Token.OperatorToken &&
                        (nextToken.symbol == "+" || nextToken.symbol == "-")
                    ) {
                        val base = stack.peek()
                        stack.push(base.multiply(current, mc).divide(hundred, mc))
                    } else {
                        stack.push(current.divide(hundred, mc))
                    }
                }
                is Token.OperatorToken -> {
                    if (token.symbol == "u-") {
                        stack.push(stack.pop().negate())
                    } else {
                        val b = stack.pop()
                        val a = stack.pop()
                        val res = when (token.symbol) {
                            "+" -> a.add(b, mc)
                            "-" -> a.subtract(b, mc)
                            "*" -> a.multiply(b, mc)
                            "/" -> {
                                if (b.compareTo(BigDecimal.ZERO) == 0) throw ArithmeticException(DIV_ZERO)
                                a.divide(b, mc)
                            }
                            "^" -> BigDecimal.valueOf(a.toDouble().pow(b.toDouble()))
                            else -> throw IllegalArgumentException("Unknown op")
                        }
                        stack.push(res)
                    }
                }
                is Token.FunctionToken -> {
                    val arg = stack.pop().toDouble()
                    val res = when (token.name) {
                        "sin" -> sin(Math.toRadians(arg))
                        "cos" -> cos(Math.toRadians(arg))
                        "tan" -> tan(Math.toRadians(arg))
                        "log" -> log10(arg)
                        "ln" -> ln(arg)
                        "sqrt" -> sqrt(arg)
                        else -> throw IllegalArgumentException("Unknown func")
                    }
                    stack.push(BigDecimal.valueOf(res).setScale(12, RoundingMode.HALF_UP).stripTrailingZeros())
                }
                else -> throw IllegalArgumentException("Unexpected token")
            }
            i++
        }
        if (stack.size != 1) throw IllegalArgumentException("Invalid stack")
        return stack.pop()
    }

    companion object {
        private const val DIV_ZERO = "DIV_ZERO"
    }
}
