package com.example.profitly.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.profitly.domain.model.AnalyticsSnapshot
import com.example.profitly.domain.model.ChartPoint
import com.example.profitly.domain.model.Expense
import com.example.profitly.domain.model.FinancialSummary
import com.example.profitly.domain.model.ProductSale
import com.example.profitly.ui.theme.ProfitlyTheme
import java.text.NumberFormat
import java.util.Locale

@Composable
@OptIn(ExperimentalLayoutApi::class)
fun DashboardScreen(
    uiState: DashboardUiState,
    onAddSale: (String, Double, Double, Int) -> Unit,
    onAddExpense: (String, Double) -> Unit,
    onGenerateInsights: () -> Unit,
    modifier: Modifier = Modifier
) {
    var productName by remember { mutableStateOf("") }
    var sellingPrice by remember { mutableStateOf("") }
    var productionCost by remember { mutableStateOf("") }
    var quantitySold by remember { mutableStateOf("") }

    var expenseDesc by remember { mutableStateOf("") }
    var expenseAmount by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Profitly", style = MaterialTheme.typography.headlineMedium)
        Text("Product and small-business profitability dashboard", style = MaterialTheme.typography.bodyMedium)

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard("Revenue", currency(uiState.analytics.summary.totalRevenue))
            MetricCard("Costs", currency(uiState.analytics.summary.totalCosts))
            MetricCard("Profit", currency(uiState.analytics.summary.netProfit))
            MetricCard("Margin", "${"%.2f".format(uiState.analytics.summary.profitMarginPercent)}%")
        }

        SalesChartCard(uiState.analytics.salesOverTime)
        ProfitChartCard(uiState.analytics.profitOverTime)

        Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Add Product Sale", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(productName, { productName = it }, label = { Text("Product name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(
                    value = sellingPrice,
                    onValueChange = { sellingPrice = it },
                    label = { Text("Selling price") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = productionCost,
                    onValueChange = { productionCost = it },
                    label = { Text("Production cost") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = quantitySold,
                    onValueChange = { quantitySold = it },
                    label = { Text("Quantity sold") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        val sell = sellingPrice.toDoubleOrNull()
                        val cost = productionCost.toDoubleOrNull()
                        val qty = quantitySold.toIntOrNull()
                        if (productName.isNotBlank() && sell != null && cost != null && qty != null) {
                            onAddSale(productName.trim(), sell, cost, qty)
                            productName = ""
                            sellingPrice = ""
                            productionCost = ""
                            quantitySold = ""
                        }
                    }
                ) {
                    Text("Save Product Sale")
                }
            }
        }

        Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Add Additional Expense", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(expenseDesc, { expenseDesc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(
                    value = expenseAmount,
                    onValueChange = { expenseAmount = it },
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        val amount = expenseAmount.toDoubleOrNull()
                        if (expenseDesc.isNotBlank() && amount != null) {
                            onAddExpense(expenseDesc.trim(), amount)
                            expenseDesc = ""
                            expenseAmount = ""
                        }
                    }
                ) {
                    Text("Save Expense")
                }
            }
        }

        Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("AI Insights", style = MaterialTheme.typography.titleMedium)
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onGenerateInsights
                ) {
                    Text(if (uiState.isLoadingInsights) "Analyzing..." else "Generate Insights")
                }
                uiState.insights.forEach { insight ->
                    Text("• $insight")
                }
                uiState.errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            }
        }

        Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Recent Products", style = MaterialTheme.typography.titleMedium)
                if (uiState.sales.isEmpty()) {
                    Text("No products yet.")
                } else {
                    uiState.sales.take(8).forEach { sale ->
                        Text("${sale.productName}: ${sale.quantitySold} units at ${currency(sale.sellingPrice)}")
                    }
                }
            }
        }

        Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Recent Expenses", style = MaterialTheme.typography.titleMedium)
                if (uiState.expenses.isEmpty()) {
                    Text("No expenses yet.")
                } else {
                    uiState.expenses.take(8).forEach { expense ->
                        Row {
                            Text(expense.description)
                            Spacer(Modifier.width(12.dp))
                            Text(currency(expense.amount))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricCard(title: String, value: String) {
    Card(
        modifier = Modifier.width(160.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, style = MaterialTheme.typography.labelLarge)
            Text(value, style = MaterialTheme.typography.titleLarge)
        }
    }
}

private fun currency(value: Double): String {
    return NumberFormat.getCurrencyInstance(Locale.US).format(value)
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    val sampleUiState = DashboardUiState(
        sales = listOf(
            ProductSale(1, "Widget A", 25.0, 10.0, 100, System.currentTimeMillis()),
            ProductSale(2, "Gadget B", 50.0, 20.0, 50, System.currentTimeMillis())
        ),
        expenses = listOf(
            Expense(1, "Office Rent", 1000.0, System.currentTimeMillis()),
            Expense(2, "Internet", 100.0, System.currentTimeMillis())
        ),
        analytics = AnalyticsSnapshot(
            summary = FinancialSummary(
                totalRevenue = 5000.0,
                totalCosts = 3100.0,
                netProfit = 1900.0,
                profitMarginPercent = 38.0
            ),
            salesOverTime = listOf(
                ChartPoint("Jan", 1000.0),
                ChartPoint("Feb", 1500.0),
                ChartPoint("Mar", 1200.0),
                ChartPoint("Apr", 1300.0)
            ),
            profitOverTime = listOf(
                ChartPoint("Jan", 200.0),
                ChartPoint("Feb", 500.0),
                ChartPoint("Mar", 300.0),
                ChartPoint("Apr", 400.0)
            )
        ),
        insights = listOf(
            "Increase production of Widget A to meet demand.",
            "Consider reducing Office Rent expenses."
        )
    )

    ProfitlyTheme {
        DashboardScreen(
            uiState = sampleUiState,
            onAddSale = { _, _, _, _ -> },
            onAddExpense = { _, _ -> },
            onGenerateInsights = {}
        )
    }
}
