package com.example.profitly.data.repository

import com.example.profitly.data.api.CreateExpenseRequest
import com.example.profitly.data.api.CreateSaleRequest
import com.example.profitly.data.api.ProfitlyApiService
import com.example.profitly.domain.model.AnalyticsSnapshot
import com.example.profitly.domain.model.Expense
import com.example.profitly.domain.model.ProductSale

class ProfitlyRepository(private val apiService: ProfitlyApiService) {
    
    suspend fun getSales(): List<ProductSale> {
        return apiService.getSales()
    }

    suspend fun addSale(
        productName: String,
        sellingPrice: Double,
        productionCost: Double,
        quantitySold: Int
    ) {
        apiService.createSale(
            CreateSaleRequest(
                productName = productName,
                sellingPrice = sellingPrice,
                productionCost = productionCost,
                quantitySold = quantitySold
            )
        )
    }

    suspend fun getExpenses(): List<Expense> {
        return apiService.getExpenses()
    }

    suspend fun addExpense(description: String, amount: Double) {
        apiService.createExpense(
            CreateExpenseRequest(
                description = description,
                amount = amount
            )
        )
    }

    suspend fun getAnalytics(): AnalyticsSnapshot {
        val summary = apiService.getSummary()
        val charts = apiService.getCharts()
        return AnalyticsSnapshot(
            summary = summary,
            salesOverTime = charts.salesOverTime,
            profitOverTime = charts.profitOverTime
        )
    }

    suspend fun getInsights(): List<String> {
        return apiService.getInsights()
    }
}
