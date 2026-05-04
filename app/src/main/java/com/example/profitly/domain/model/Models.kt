package com.example.profitly.domain.model

data class ProductSale(
    val id: Long,
    val productName: String,
    val sellingPrice: Double,
    val productionCost: Double,
    val quantitySold: Int,
    val createdAtMillis: Long
)

data class Expense(
    val id: Long,
    val description: String,
    val amount: Double,
    val createdAtMillis: Long
)

data class FinancialSummary(
    val totalRevenue: Double,
    val totalCosts: Double,
    val netProfit: Double,
    val profitMarginPercent: Double
)

data class ChartPoint(
    val label: String,
    val value: Double
)

data class AnalyticsSnapshot(
    val summary: FinancialSummary,
    val salesOverTime: List<ChartPoint>,
    val profitOverTime: List<ChartPoint>
)
