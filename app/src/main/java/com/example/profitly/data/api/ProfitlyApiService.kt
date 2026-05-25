package com.example.profitly.data.api

import com.example.profitly.domain.model.Expense
import com.example.profitly.domain.model.FinancialSummary
import com.example.profitly.domain.model.ProductSale
import com.example.profitly.domain.model.ChartPoint
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class CreateSaleRequest(
    val productName: String,
    val sellingPrice: Double,
    val productionCost: Double,
    val quantitySold: Int,
    val createdAtMillis: Long
)

data class CreateExpenseRequest(
    val description: String,
    val amount: Double,
    val createdAtMillis: Long
)

data class ChartsResponse(
    val salesOverTime: List<ChartPoint>,
    val profitOverTime: List<ChartPoint>
)

interface ProfitlyApiService {
    @retrofit2.http.Headers("Cache-Control: no-cache")
    @GET("sales")
    suspend fun getSales(): List<ProductSale>

    @POST("sales")
    suspend fun createSale(@Body request: CreateSaleRequest): Map<String, Long>

    @retrofit2.http.Headers("Cache-Control: no-cache")
    @GET("expenses")
    suspend fun getExpenses(): List<Expense>

    @POST("expenses")
    suspend fun createExpense(@Body request: CreateExpenseRequest): Map<String, Long>

    @retrofit2.http.Headers("Cache-Control: no-cache")
    @GET("analytics/summary")
    suspend fun getSummary(): FinancialSummary

    @retrofit2.http.Headers("Cache-Control: no-cache")
    @GET("analytics/charts")
    suspend fun getCharts(): ChartsResponse

    @GET("analytics/insights")
    suspend fun getInsights(): List<String>
}
