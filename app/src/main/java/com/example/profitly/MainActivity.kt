package com.example.profitly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Modifier
import com.example.profitly.ui.dashboard.DashboardScreen
import com.example.profitly.ui.dashboard.DashboardViewModel
import com.example.profitly.ui.theme.ProfitlyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = AppContainer()
        val dashboardViewModel = ViewModelProvider(
            this,
            DashboardViewModel.Factory(
                repository = container.repository
            )
        )[DashboardViewModel::class.java]
        enableEdgeToEdge()
        setContent {
            ProfitlyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val state = dashboardViewModel.uiState.collectAsStateWithLifecycle()
                    DashboardScreen(
                        uiState = state.value,
                        onAddSale = dashboardViewModel::addSale,
                        onAddExpense = dashboardViewModel::addExpense,
                        onGenerateInsights = dashboardViewModel::generateInsights,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}