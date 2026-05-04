package com.example.profitly.ui.dashboard

import com.example.profitly.domain.model.AnalyticsSnapshot
import com.example.profitly.domain.model.Expense
import com.example.profitly.domain.model.FinancialSummary
import com.example.profitly.domain.model.ProductSale

data class DashboardUiState(
    val sales: List<ProductSale> = emptyList(),
    val expenses: List<Expense> = emptyList(),
    val analytics: AnalyticsSnapshot = AnalyticsSnapshot(
        summary = FinancialSummary(0.0, 0.0, 0.0, 0.0),
        salesOverTime = emptyList(),
        profitOverTime = emptyList()
    ),
    val insights: List<String> = listOf("Tap Generate Insights for recommendations."),
    val isLoadingInsights: Boolean = false,
    val errorMessage: String? = null
)
