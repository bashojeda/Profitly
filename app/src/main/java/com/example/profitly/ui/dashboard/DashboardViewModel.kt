package com.example.profitly.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.profitly.data.repository.ProfitlyRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val repository: ProfitlyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState

    init {
        viewModelScope.launch {
            loadAllData()
        }
    }

    private suspend fun loadAllData() {
        _uiState.update { it.copy(isLoadingInsights = true) }
        
        coroutineScope {
            val salesDeferred = async { runCatching { repository.getSales() } }
            val expensesDeferred = async { runCatching { repository.getExpenses() } }
            val analyticsDeferred = async { runCatching { repository.getAnalytics() } }

            val salesRes = salesDeferred.await()
            val expensesRes = expensesDeferred.await()
            val analyticsRes = analyticsDeferred.await()

            _uiState.update { state ->
                state.copy(
                    sales = salesRes.getOrDefault(state.sales),
                    expenses = expensesRes.getOrDefault(state.expenses),
                    analytics = analyticsRes.getOrDefault(state.analytics),
                    errorMessage = when {
                        salesRes.isFailure -> "Failed to load sales: ${salesRes.exceptionOrNull()?.message}"
                        expensesRes.isFailure -> "Failed to load expenses: ${expensesRes.exceptionOrNull()?.message}"
                        analyticsRes.isFailure -> "Failed to load analytics: ${analyticsRes.exceptionOrNull()?.message}"
                        else -> null
                    }
                )
            }
        }
        
        _uiState.update { it.copy(isLoadingInsights = false) }
    }

    fun addSale(
        productName: String,
        sellingPrice: Double,
        productionCost: Double,
        quantitySold: Int
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(errorMessage = null) }
            runCatching {
                repository.addSale(productName, sellingPrice, productionCost, quantitySold)
                delay(1000) // Aumentamos a 1 segundo para dar tiempo al servidor
                loadAllData()
            }.onFailure { throwable ->
                _uiState.update { state ->
                    state.copy(errorMessage = "Could not add product sale: ${throwable.message}")
                }
            }
        }
    }

    fun addExpense(description: String, amount: Double) {
        viewModelScope.launch {
            _uiState.update { it.copy(errorMessage = null) }
            runCatching {
                repository.addExpense(description, amount)
                delay(1000) // Aumentamos a 1 segundo para dar tiempo al servidor
                loadAllData()
            }.onFailure { throwable ->
                _uiState.update { state ->
                    state.copy(errorMessage = "Could not add expense: ${throwable.message}")
                }
            }
        }
    }

    fun generateInsights() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingInsights = true) }
            runCatching {
                val insights = repository.getInsights()
                _uiState.update {
                    it.copy(
                        insights = insights,
                        errorMessage = null
                    )
                }
            }.onFailure { throwable ->
                _uiState.update { state ->
                    state.copy(
                        insights = listOf("Unable to generate insights right now."),
                        errorMessage = "Failed to generate insights: ${throwable.message}"
                    )
                }
            }
            _uiState.update { it.copy(isLoadingInsights = false) }
        }
    }

    class Factory(
        private val repository: ProfitlyRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DashboardViewModel(repository) as T
        }
    }
}
