package com.calki.app.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calki.app.data.local.CalculationEntity
import com.calki.app.data.repository.CalculatorRepository
import com.calki.app.domain.evaluator.EvalResult
import com.calki.app.domain.evaluator.ExpressionEvaluator
import com.calki.app.domain.model.AccentPreset
import com.calki.app.domain.model.AppThemeMode
import com.calki.app.domain.model.CalculatorKey
import com.calki.app.domain.model.KeyCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalculatorViewModel @Inject constructor(
    private val repository: CalculatorRepository,
    private val evaluator: ExpressionEvaluator,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val internalState = MutableStateFlow(
        CalculatorState(
            expression = savedStateHandle[KEY_EXPR] ?: "",
            lastExpressionPeek = savedStateHandle[KEY_PEEK] ?: "",
            isResultCommitted = savedStateHandle[KEY_COMMITTED] ?: false
        )
    )

    val uiState: StateFlow<CalculatorState> = combine(
        internalState,
        repository.historyFlow,
        repository.settingsFlow
    ) { calcState, history, settings ->
        val formattedExpr = evaluator.formatInputExpression(calcState.expression)
        val live = computeLivePreview(calcState.expression, settings.decimalPlaces, calcState.isResultCommitted)
        calcState.copy(
            formattedExpression = formattedExpr,
            livePreview = live,
            history = history,
            settings = settings,
            lastExpressionPeek = calcState.lastExpressionPeek.ifEmpty {
                history.firstOrNull()?.let { "${it.expression} = ${it.result}" } ?: ""
            }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CalculatorState())

    fun onKeyPress(key: CalculatorKey) {
        when (key) {
            CalculatorKey.CLEAR -> updateExpression("", isCommitted = false)
            CalculatorKey.BACKSPACE -> deleteLastChar()
            CalculatorKey.EQUALS -> commitEquals()
            CalculatorKey.PLUS_MINUS -> toggleSign()
            CalculatorKey.DECIMAL -> appendDecimal()
            else -> appendToken(key)
        }
    }

    fun deleteLastChar() {
        val current = internalState.value.expression
        if (current.isEmpty()) return
        val multiTokens = listOf("sin(", "cos(", "tan(", "log(", "ln(", "√(")
        val matchedSuffix = multiTokens.firstOrNull { current.endsWith(it) }
        val updated = if (matchedSuffix != null) current.dropLast(matchedSuffix.length) else current.dropLast(1)
        updateExpression(updated, isCommitted = false)
    }

    fun onLongPressClearAllHistory() {
        viewModelScope.launch {
            repository.clearAllHistory()
            updateExpression("", isCommitted = false, peek = "")
        }
    }

    fun reuseHistoryItem(item: CalculationEntity) {
        updateExpression(expr = item.result.replace(",", ""), isCommitted = true, peek = "${item.expression} =")
    }

    fun deleteHistoryItem(item: CalculationEntity) = viewModelScope.launch { repository.deleteHistoryItem(item) }
    fun clearAllHistory() = viewModelScope.launch { repository.clearAllHistory() }
    fun updateTheme(mode: AppThemeMode) = viewModelScope.launch { repository.updateTheme(mode) }
    fun updateHaptics(enabled: Boolean) = viewModelScope.launch { repository.updateHaptics(enabled) }
    fun updateSound(enabled: Boolean) = viewModelScope.launch { repository.updateSound(enabled) }
    fun updateDecimalPlaces(places: Int) = viewModelScope.launch { repository.updateDecimalPlaces(places) }
    fun updateAccentPreset(preset: AccentPreset) = viewModelScope.launch { repository.updateAccentPreset(preset) }

    private fun appendDecimal() {
        val state = internalState.value
        val expr = if (state.isResultCommitted) "" else state.expression
        val lastNumberSegment = expr.takeLastWhile { it.isDigit() || it == '.' }
        if (lastNumberSegment.contains('.')) return
        val prefix = if (lastNumberSegment.isEmpty()) "0." else "."
        updateExpression(expr + prefix, isCommitted = false)
    }

    private fun toggleSign() {
        val expr = internalState.value.expression
        if (expr.isEmpty()) {
            updateExpression("−", isCommitted = false)
            return
        }
        val regex = Regex("""([0-9.]+)$""")
        val match = regex.find(expr) ?: return
        val numberPart = match.value
        val startIdx = match.range.first
        val updated = if (startIdx >= 2 && expr.substring(startIdx - 2, startIdx) == "(−") {
            expr.substring(0, startIdx - 2) + numberPart
        } else if (startIdx == 1 && expr[0] == '−') {
            numberPart
        } else {
            expr.substring(0, startIdx) + "(−$numberPart"
        }
        updateExpression(updated, isCommitted = false)
    }

    private fun appendToken(key: CalculatorKey) {
        val state = internalState.value
        var current = state.expression
        if (state.isResultCommitted) {
            if (key.category == KeyCategory.NUMBER || (key.category == KeyCategory.SCIENTIFIC && key.token !in listOf("^2", "^"))) {
                current = ""
            }
        }
        if (key.category == KeyCategory.OPERATOR) {
            if (current.isEmpty()) {
                if (key == CalculatorKey.SUBTRACT) updateExpression("−", isCommitted = false)
                return
            }
            if (current.last() in listOf('+', '−', '×', '÷', '^')) {
                current = current.dropLast(1)
            }
        }
        updateExpression(current + key.token, isCommitted = false)
    }

    private fun commitEquals() {
        val state = uiState.value
        val expr = state.expression
        if (expr.isEmpty()) return

        when (val result = evaluator.evaluate(expr, state.settings.decimalPlaces, false)) {
            is EvalResult.Success -> {
                val formattedInput = evaluator.formatInputExpression(expr)
                val formattedOutput = result.formattedValue
                viewModelScope.launch { repository.addHistory(formattedInput, formattedOutput) }
                val rawNext = formattedOutput.replace(",", "")
                savedStateHandle[KEY_EXPR] = rawNext
                savedStateHandle[KEY_PEEK] = "$formattedInput ="
                savedStateHandle[KEY_COMMITTED] = true
                internalState.update {
                    it.copy(
                        expression = rawNext,
                        lastExpressionPeek = "$formattedInput =",
                        errorMessage = null,
                        isResultCommitted = true,
                        evaluationSparkleTrigger = it.evaluationSparkleTrigger + 1
                    )
                }
            }
            is EvalResult.DivideByZero -> internalState.update { it.copy(errorMessage = "Oops, can't divide by 0 🙈") }
            is EvalResult.InvalidExpression -> internalState.update { it.copy(errorMessage = "Hmm, check that expression ✨") }
            is EvalResult.Empty -> Unit
        }
    }

    private fun computeLivePreview(expression: String, decimalPlaces: Int, isCommitted: Boolean): String {
        if (isCommitted || expression.isEmpty()) return ""
        val hasOperator = expression.drop(if (expression.startsWith("−")) 1 else 0).any { !it.isDigit() && it != '.' }
        if (!hasOperator) return ""
        return when (val res = evaluator.evaluate(expression, decimalPlaces, true)) {
            is EvalResult.Success -> res.formattedValue
            is EvalResult.DivideByZero -> "Oops, can't divide by 0 🙈"
            else -> ""
        }
    }

    private fun updateExpression(expr: String, isCommitted: Boolean, peek: String? = null) {
        savedStateHandle[KEY_EXPR] = expr
        savedStateHandle[KEY_COMMITTED] = isCommitted
        if (peek != null) savedStateHandle[KEY_PEEK] = peek
        internalState.update {
            it.copy(expression = expr, lastExpressionPeek = peek ?: it.lastExpressionPeek, errorMessage = null, isResultCommitted = isCommitted)
        }
    }

    companion object {
        private const val KEY_EXPR = "saved_expression"
        private const val KEY_PEEK = "saved_peek"
        private const val KEY_COMMITTED = "saved_committed"
    }
}
