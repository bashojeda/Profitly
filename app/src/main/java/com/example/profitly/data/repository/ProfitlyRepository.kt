package com.example.profitly.data.repository

import com.example.profitly.data.api.AccountRequest
import com.example.profitly.data.api.AccountResponse
import com.example.profitly.data.api.AccountUpdateRequest
import com.example.profitly.data.api.ApiInfoResponse
import com.example.profitly.data.api.AuthLoginRequest
import com.example.profitly.data.api.AuthLoginResponse
import com.example.profitly.data.api.AuthRegisterRequest
import com.example.profitly.data.api.AuthUserResponse
import com.example.profitly.data.api.CategoryRequest
import com.example.profitly.data.api.CategoryResponse
import com.example.profitly.data.api.CategoryUpdateRequest
import com.example.profitly.data.api.DashboardHistoryResponse
import com.example.profitly.data.api.DashboardStatisticsResponse
import com.example.profitly.data.api.DashboardSummaryResponse
import com.example.profitly.data.api.DashboardTrendsResponse
import com.example.profitly.data.api.PingResponse
import com.example.profitly.data.api.ProfitlyApiService
import com.example.profitly.data.api.TotalBalanceResponse
import com.example.profitly.data.api.TransactionRequest
import com.example.profitly.data.api.TransactionResponse
import com.example.profitly.data.api.TransactionUpdateRequest
import com.example.profitly.data.api.TransferRequest
import com.example.profitly.data.api.TransferResponse

class ProfitlyRepository(private val apiService: ProfitlyApiService) {

    suspend fun getApiInfo(): ApiInfoResponse = apiService.getApiInfo()
    suspend fun ping(): PingResponse = apiService.ping()

    suspend fun register(name: String, email: String, password: String): AuthUserResponse {
        return apiService.register(AuthRegisterRequest(name, email, password))
    }

    suspend fun login(email: String, password: String): AuthLoginResponse {
        return apiService.login(AuthLoginRequest(email, password))
    }

    suspend fun getProfile(): AuthUserResponse = apiService.getProfile()

    suspend fun getAccounts(): List<AccountResponse> = apiService.getAccounts()
    suspend fun getTotalBalance(): TotalBalanceResponse = apiService.getTotalBalance()
    suspend fun getAccount(id: Long): AccountResponse = apiService.getAccount(id)
    suspend fun createAccount(request: AccountRequest): AccountResponse = apiService.createAccount(request)
    suspend fun updateAccount(id: Long, request: AccountUpdateRequest): AccountResponse = apiService.updateAccount(id, request)
    suspend fun deleteAccount(id: Long) = apiService.deleteAccount(id)

    suspend fun getCategories(): List<CategoryResponse> = apiService.getCategories()
    suspend fun getCategory(id: Long): CategoryResponse = apiService.getCategory(id)
    suspend fun createCategory(request: CategoryRequest): CategoryResponse = apiService.createCategory(request)
    suspend fun updateCategory(id: Long, request: CategoryUpdateRequest): CategoryResponse = apiService.updateCategory(id, request)
    suspend fun deleteCategory(id: Long) = apiService.deleteCategory(id)

    suspend fun getTransactions(
        startDate: String? = null,
        endDate: String? = null,
        type: String? = null,
        categoryId: Long? = null,
        accountId: Long? = null,
        limit: Int? = null,
        page: Int? = null
    ): List<TransactionResponse> = apiService.getTransactions(startDate, endDate, type, categoryId, accountId, limit, page)

    suspend fun getTransaction(id: Long): TransactionResponse = apiService.getTransaction(id)
    suspend fun createTransaction(request: TransactionRequest): TransactionResponse = apiService.createTransaction(request)
    suspend fun updateTransaction(id: Long, request: TransactionUpdateRequest): TransactionResponse = apiService.updateTransaction(id, request)
    suspend fun deleteTransaction(id: Long) = apiService.deleteTransaction(id)

    suspend fun getTransfers(): List<TransferResponse> = apiService.getTransfers()
    suspend fun createTransfer(request: TransferRequest): TransferResponse = apiService.createTransfer(request)

    suspend fun getDashboardSummary(): DashboardSummaryResponse = apiService.getDashboardSummary()
    suspend fun getDashboardCategoryBreakdown(): List<com.example.profitly.data.api.CategoryBreakdownItem> = apiService.getDashboardCategoryBreakdown()
    suspend fun getDashboardHistory(page: Int? = null, limit: Int? = null): DashboardHistoryResponse = apiService.getDashboardHistory(page, limit)
    suspend fun getDashboardStatistics(): DashboardStatisticsResponse = apiService.getDashboardStatistics()
    suspend fun getDashboardTrends(): DashboardTrendsResponse = apiService.getDashboardTrends()
}
