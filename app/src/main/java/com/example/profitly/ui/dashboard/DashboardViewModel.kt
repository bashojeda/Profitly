package com.example.profitly.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.profitly.data.repository.ProfitlyRepository
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
        loadAllData()
    }

    private fun loadAllData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingInsights = true) }
            runCatching {
                val sales = repository.getSales()
                val expenses = repository.getExpenses()
                val analytics = repository.getAnalytics()
                
                _uiState.update {
                    it.copy(
                        sales = sales,
                        expenses = expenses,
                        analytics = analytics,
                        errorMessage = null
                    )
                }
            }.onFailure { throwable ->
                _uiState.update { state ->
                    state.copy(
                        errorMessage = "Failed to load data: ${throwable.message ?: "Unknown error"}"
                    )
                }
            }
            _uiState.update { it.copy(isLoadingInsights = false) }
        }
    }

    fun addSale(
        productName: String,
        sellingPrice: Double,
        productionCost: Double,
        quantitySold: Int
    ) {
        viewModelScope.launch {
            runCatching {
                repository.addSale(productName, sellingPrice, productionCost, quantitySold)
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
            runCatching {
                repository.addExpense(description, amount)
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
