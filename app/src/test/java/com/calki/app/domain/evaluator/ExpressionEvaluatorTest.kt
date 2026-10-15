package com.calki.app.domain.evaluator

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ExpressionEvaluatorTest {
    private lateinit var evaluator: ExpressionEvaluator

    @Before
    fun setUp() { evaluator = ExpressionEvaluator() }

    @Test
    fun `exact BigDecimal addition 0_1 plus 0_2 equals 0_3`() {
        val result = evaluator.evaluate("0.1+0.2")
        assertTrue(result is EvalResult.Success)
        assertEquals("0.3", (result as EvalResult.Success).formattedValue)
    }

    @Test
    fun `operator precedence multiplication before addition`() {
        val result = evaluator.evaluate("2+3×4")
        assertEquals("14", (result as EvalResult.Success).formattedValue)
    }

    @Test
    fun `divide by zero returns DivideByZero result`() {
        assertTrue(evaluator.evaluate("42÷0") is EvalResult.DivideByZero)
    }
}
