package com.calki.app.ui

import com.calki.app.data.local.CalculationEntity
import com.calki.app.domain.model.UserSettings

data class CalculatorState(
    val expression: String = "",
    val formattedExpression: String = "",
    val livePreview: String = "",
    val lastExpressionPeek: String = "",
    val errorMessage: String? = null,
    val isResultCommitted: Boolean = false,
    val evaluationSparkleTrigger: Int = 0,
    val history: List<CalculationEntity> = emptyList(),
    val settings: UserSettings = UserSettings()
)
