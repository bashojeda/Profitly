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
    val quantitySold: Int
)

data class CreateExpenseRequest(
    val description: String,
    val amount: Double
)

data class ChartsResponse(
    val salesOverTime: List<ChartPoint>,
    val profitOverTime: List<ChartPoint>
)

interface ProfitlyApiService {
    @GET("sales")
    suspend fun getSales(): List<ProductSale>

    @POST("sales")
    suspend fun createSale(@Body request: CreateSaleRequest): Map<String, Long>

    @GET("expenses")
    suspend fun getExpenses(): List<Expense>

    @POST("expenses")
    suspend fun createExpense(@Body request: CreateExpenseRequest): Map<String, Long>

    @GET("summary")
    suspend fun getSummary(): FinancialSummary

    @GET("charts")
    suspend fun getCharts(): ChartsResponse

    @POST("insights")
    suspend fun getInsights(): List<String>
}
