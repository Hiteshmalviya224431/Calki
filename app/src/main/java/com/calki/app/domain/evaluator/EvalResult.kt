package com.calki.app.domain.evaluator

import java.math.BigDecimal

sealed interface EvalResult {
    data class Success(val rawValue: BigDecimal, val formattedValue: String) : EvalResult
    data object DivideByZero : EvalResult
    data object InvalidExpression : EvalResult
    data object Empty : EvalResult
}
