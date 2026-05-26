package com.example.profitly.data.api

import com.example.profitly.domain.model.ChartPoint
import com.example.profitly.domain.model.Expense
import com.example.profitly.domain.model.FinancialSummary
import com.example.profitly.domain.model.ProductSale
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

data class PingResponse(val status: String)
data class ApiInfoResponse(val message: String?, val version: String?, val timestamp: Long?)

data class AuthRegisterRequest(val name: String, val email: String, val password: String)
data class AuthLoginRequest(val email: String, val password: String)
data class AuthUserResponse(val id: Long, val name: String, val email: String)
data class AuthLoginResponse(val token: String, val user: AuthUserResponse)

data class AccountRequest(
    val name: String,
    val type: String,
    val balance: Double? = 0.0,
    val color: String? = null,
    val icon: String? = null
)
data class AccountUpdateRequest(
    val name: String? = null,
    val type: String? = null,
    val color: String? = null,
    val icon: String? = null
)
data class AccountResponse(
    val id: Long,
    val name: String,
    val type: String,
    val balance: Double,
    val color: String?,
    val icon: String?
)
data class TotalBalanceResponse(val totalBalance: Double)

data class CategoryRequest(
    val name: String,
    val type: String,
    val icon: String? = null,
    val color: String? = null
)
data class CategoryUpdateRequest(
    val name: String? = null,
    val type: String? = null,
    val icon: String? = null,
    val color: String? = null
)
data class CategoryResponse(
    val id: Long,
    val name: String,
    val type: String,
    val icon: String?,
    val color: String?
)

data class TransactionRequest(
    val title: String,
    val amount: Double,
    val date: String,
    val type: String,
    val description: String? = null,
    val category_id: Long? = null,
    val payment_method: String? = null,
    val account_id: Long? = null,
    val source_account_id: Long? = null,
    val destination_account_id: Long? = null
)
data class TransactionUpdateRequest(
    val title: String? = null,
    val amount: Double? = null,
    val date: String? = null,
    val type: String? = null,
    val description: String? = null,
    val category_id: Long? = null,
    val payment_method: String? = null,
    val account_id: Long? = null,
    val source_account_id: Long? = null,
    val destination_account_id: Long? = null
)
data class TransactionResponse(
    val id: Long,
    val title: String,
    val amount: Double,
    val date: String,
    val type: String,
    val description: String?,
    val category_id: Long?,
    val payment_method: String?,
    val account_id: Long?,
    val source_account_id: Long?,
    val destination_account_id: Long?
)

data class TransferRequest(
    val amount: Double,
    val date: String,
    val title: String,
    val source_account_id: Long,
    val destination_account_id: Long,
    val description: String? = null,
    val category_id: Long? = null,
    val payment_method: String? = null
)
data class TransferResponse(
    val id: Long,
    val amount: Double,
    val date: String,
    val title: String,
    val source_account_id: Long,
    val destination_account_id: Long,
    val description: String?,
    val category_id: Long?,
    val payment_method: String?
)

data class CategoryTotal(val categoryId: Long, val name: String, val type: String, val total: Double)
data class DashboardSummaryResponse(
    val totalBalance: Double,
    val monthlyIncome: Double,
    val monthlyExpense: Double,
    val monthlyNet: Double,
    val categories: List<CategoryTotal>
)
data class CategoryBreakdownItem(
    val categoryId: Long,
    val name: String,
    val type: String,
    val amount: Double
)
data class DashboardHistoryItem(
    val date: String,
    val income: Double,
    val expense: Double,
    val net: Double
)
data class DashboardHistoryResponse(
    val page: Int,
    val limit: Int,
    val total: Int,
    val items: List<DashboardHistoryItem>
)
data class DashboardStatisticsResponse(
    val incomeByMonth: Map<String, Double>,
    val expenseByMonth: Map<String, Double>,
    val topCategories: List<CategoryTotal>,
    val dailyCashFlow: List<ChartPoint>
)
data class DashboardTrendsResponse(
    val income: List<ChartPoint>,
    val expense: List<ChartPoint>,
    val net: List<ChartPoint>
)

interface ProfitlyApiService {
    @GET("")
    suspend fun getApiInfo(): ApiInfoResponse

    @GET("ping")
    suspend fun ping(): PingResponse

    @POST("auth/register")
    suspend fun register(@Body request: AuthRegisterRequest): AuthUserResponse

    @POST("auth/login")
    suspend fun login(@Body request: AuthLoginRequest): AuthLoginResponse

    @GET("auth/profile")
    suspend fun getProfile(): AuthUserResponse

    @POST("accounts")
    suspend fun createAccount(@Body request: AccountRequest): AccountResponse

    @GET("accounts")
    suspend fun getAccounts(): List<AccountResponse>

    @GET("accounts/total-balance")
    suspend fun getTotalBalance(): TotalBalanceResponse

    @GET("accounts/{id}")
    suspend fun getAccount(@Path("id") id: Long): AccountResponse

    @PUT("accounts/{id}")
    suspend fun updateAccount(@Path("id") id: Long, @Body request: AccountUpdateRequest): AccountResponse

    @DELETE("accounts/{id}")
    suspend fun deleteAccount(@Path("id") id: Long)

    @POST("categories")
    suspend fun createCategory(@Body request: CategoryRequest): CategoryResponse

    @GET("categories")
    suspend fun getCategories(): List<CategoryResponse>

    @GET("categories/{id}")
    suspend fun getCategory(@Path("id") id: Long): CategoryResponse

    @PUT("categories/{id}")
    suspend fun updateCategory(@Path("id") id: Long, @Body request: CategoryUpdateRequest): CategoryResponse

    @DELETE("categories/{id}")
    suspend fun deleteCategory(@Path("id") id: Long)

    @POST("transactions")
    suspend fun createTransaction(@Body request: TransactionRequest): TransactionResponse

    @GET("transactions")
    suspend fun getTransactions(
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null,
        @Query("type") type: String? = null,
        @Query("categoryId") categoryId: Long? = null,
        @Query("accountId") accountId: Long? = null,
        @Query("limit") limit: Int? = null,
        @Query("page") page: Int? = null
    ): List<TransactionResponse>

    @GET("transactions/{id}")
    suspend fun getTransaction(@Path("id") id: Long): TransactionResponse

    @PUT("transactions/{id}")
    suspend fun updateTransaction(@Path("id") id: Long, @Body request: TransactionUpdateRequest): TransactionResponse

    @DELETE("transactions/{id}")
    suspend fun deleteTransaction(@Path("id") id: Long)

    @POST("transfers")
    suspend fun createTransfer(@Body request: TransferRequest): TransferResponse

    @GET("transfers")
    suspend fun getTransfers(): List<TransferResponse>

    @GET("dashboard/summary")
    suspend fun getDashboardSummary(): DashboardSummaryResponse

    @GET("dashboard/category-breakdown")
    suspend fun getDashboardCategoryBreakdown(): List<CategoryBreakdownItem>

    @GET("dashboard/history")
    suspend fun getDashboardHistory(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): DashboardHistoryResponse

    @GET("dashboard/statistics")
    suspend fun getDashboardStatistics(): DashboardStatisticsResponse

    @GET("dashboard/trends")
    suspend fun getDashboardTrends(): DashboardTrendsResponse

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
