package com.example.profitly.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.profitly.AppContainer
import com.example.profitly.data.api.AccountRequest
import com.example.profitly.data.api.AccountResponse
import com.example.profitly.data.api.CategoryRequest
import com.example.profitly.data.api.CategoryResponse
import com.example.profitly.data.api.CategoryUpdateRequest
import com.example.profitly.data.api.DashboardHistoryResponse
import com.example.profitly.data.api.DashboardStatisticsResponse
import com.example.profitly.data.api.DashboardSummaryResponse
import com.example.profitly.data.api.DashboardTrendsResponse
import com.example.profitly.data.api.TransactionRequest
import com.example.profitly.data.api.TransactionResponse
import com.example.profitly.data.api.TransactionUpdateRequest
import com.example.profitly.data.api.TransferRequest
import com.example.profitly.data.api.TransferResponse
import com.example.profitly.data.api.AuthUserResponse
import com.example.profitly.data.api.AuthLoginResponse
import com.example.profitly.data.api.AuthRegisterRequest
import kotlinx.coroutines.launch

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
}

sealed class BottomScreen(val route: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val title: String) {
    object Accounts : BottomScreen("accounts", Icons.Default.CreditCard, "Cuentas")
    object Categories : BottomScreen("categories", Icons.Default.Category, "Categorías")
    object Operations : BottomScreen("operations", Icons.Default.ReceiptLong, "Operaciones")
    object Dashboard : BottomScreen("dashboard", Icons.Default.Assessment, "Estadísticas")
    object More : BottomScreen("more", Icons.Default.Settings, "Más")

    companion object {
        val items = listOf(Accounts, Categories, Operations, Dashboard, More)
    }
}

@Composable
fun ProfitlyApp(container: AppContainer) {
    val navController = rememberNavController()

    NavHost(navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(
                repository = container.repository,
                tokenProvider = container.tokenProvider,
                onLoginSuccess = { navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                } },
                onRegister = { navController.navigate(Screen.Register.route) }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                repository = container.repository,
                onRegisterSuccess = { navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Register.route) { inclusive = true }
                } }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                repository = container.repository,
                tokenProvider = container.tokenProvider,
                onLogout = {
                    container.tokenProvider.clear()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0)
                    }
                }
            )
        }
    }
}

@Composable
private fun ScreenTitle(title: String) {
    Text(
        text = title,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun LoginScreen(
    repository: com.example.profitly.data.repository.ProfitlyRepository,
    tokenProvider: com.example.profitly.data.api.AuthTokenProvider,
    onLoginSuccess: () -> Unit,
    onRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Surface(modifier = Modifier.fillMaxSize().background(Color(0xFF101219)).padding(24.dp)) {
        Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize()) {
            Text("Iniciar sesión", fontSize = 32.sp, color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.White,
                    focusedBorderColor = Color(0xFF4F86FF),
                    unfocusedBorderColor = Color.Gray,
                    placeholderColor = Color.LightGray,
                    cursorColor = Color.White
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.White,
                    focusedBorderColor = Color(0xFF4F86FF),
                    unfocusedBorderColor = Color.Gray,
                    placeholderColor = Color.LightGray,
                    cursorColor = Color.White
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        error = "Email y contraseña son requeridos"
                        return@Button
                    }
                    loading = true
                    error = null
                    scope.launch {
                        runCatching {
                            val response = repository.login(email.trim(), password)
                            tokenProvider.saveToken(response.token)
                            onLoginSuccess()
                        }.onFailure {
                            error = it.message ?: "Error al iniciar sesión"
                        }
                        loading = false
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !loading
            ) {
                Text(if (loading) "Cargando..." else "Entrar")
            }
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(onClick = onRegister, modifier = Modifier.fillMaxWidth()) {
                Text("Crear cuenta")
            }
            error?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Text(it, color = Color(0xFFFF6B6B), modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
fun RegisterScreen(
    repository: com.example.profitly.data.repository.ProfitlyRepository,
    onRegisterSuccess: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Surface(modifier = Modifier.fillMaxSize().background(Color(0xFF101219)).padding(24.dp)) {
        Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize()) {
            Text("Crear cuenta", fontSize = 32.sp, color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.White,
                    focusedBorderColor = Color(0xFF4F86FF),
                    unfocusedBorderColor = Color.Gray,
                    placeholderColor = Color.LightGray,
                    cursorColor = Color.White
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.White,
                    focusedBorderColor = Color(0xFF4F86FF),
                    unfocusedBorderColor = Color.Gray,
                    placeholderColor = Color.LightGray,
                    cursorColor = Color.White
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.White,
                    focusedBorderColor = Color(0xFF4F86FF),
                    unfocusedBorderColor = Color.Gray,
                    placeholderColor = Color.LightGray,
                    cursorColor = Color.White
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (name.isBlank() || email.isBlank() || password.isBlank()) {
                        error = "Todos los campos son obligatorios"
                        return@Button
                    }
                    loading = true
                    error = null
                    scope.launch {
                        runCatching {
                            repository.register(name.trim(), email.trim(), password)
                            onRegisterSuccess()
                        }.onFailure {
                            error = it.message ?: "Error al registrar"
                        }
                        loading = false
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !loading
            ) {
                Text(if (loading) "Guardando..." else "Registrarse")
            }
            error?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Text(it, color = Color(0xFFFF6B6B), modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
fun HomeScreen(
    repository: com.example.profitly.data.repository.ProfitlyRepository,
    tokenProvider: com.example.profitly.data.api.AuthTokenProvider,
    onLogout: () -> Unit
) {
    val items = BottomScreen.items
    var selectedItem by remember { mutableStateOf<BottomScreen>(BottomScreen.Dashboard) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profitly", color = Color.White) },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = "Cerrar sesión", tint = Color.White)
                    }
                },
                modifier = Modifier.background(Color(0xFF101219))
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF141824))
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                items.forEach { item ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { selectedItem = item }
                    ) {
                        Icon(item.icon, contentDescription = item.title, tint = if (selectedItem == item) Color(0xFF4F86FF) else Color.LightGray)
                        Text(item.title, color = if (selectedItem == item) Color.White else Color.LightGray, fontSize = 12.sp)
                    }
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFF101219))
        ) {
            when (selectedItem) {
                BottomScreen.Accounts -> AccountsTab(repository)
                BottomScreen.Categories -> CategoriesTab(repository)
                BottomScreen.Operations -> TransactionsTab(repository)
                BottomScreen.Dashboard -> DashboardTab(repository)
                BottomScreen.More -> MoreTab(repository, tokenProvider)
            }
        }
    }
}

@Composable
fun AccountsTab(repository: com.example.profitly.data.repository.ProfitlyRepository) {
    var accounts by remember { mutableStateOf<List<AccountResponse>>(emptyList()) }
    var balance by remember { mutableStateOf(0.0) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val showDialog = remember { mutableStateOf(false) }
    val editAccount = remember { mutableStateOf<AccountResponse?>(null) }
    val scope = rememberCoroutineScope()

    fun loadAccounts() {
        scope.launch {
            isLoading = true
            runCatching {
                accounts = repository.getAccounts()
                balance = repository.getTotalBalance().totalBalance
                error = null
            }.onFailure {
                error = it.message ?: "No se pudieron cargar las cuentas"
            }
            isLoading = false
        }
    }

    LaunchedEffect(true) { loadAccounts() }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        ScreenTitle("Cuentas")
        Text("Saldo total: ${String.format("%.2f", balance)} MXN", color = Color(0xFF4EF57D), fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = { editAccount.value = null; showDialog.value = true }) {
                Icon(Icons.Default.Add, contentDescription = "Añadir")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Nueva cuenta")
            }
            Text("${accounts.size} cuentas", color = Color.LightGray)
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF4F86FF))
            }
        } else if (error != null) {
            Text(error!!, color = Color(0xFFFF6B6B))
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(accounts) { account ->
                    AccountItem(account = account, onEdit = {
                        editAccount.value = it
                        showDialog.value = true
                    }, onDelete = {
                        scope.launch {
                            runCatching {
                                repository.deleteAccount(it.id)
                                loadAccounts()
                            }.onFailure { error = it.message ?: "No se pudo eliminar" }
                        }
                    })
                }
            }
        }
    }

    if (showDialog.value) {
        AccountDialog(
            current = editAccount.value,
            onDismiss = { showDialog.value = false },
            onSubmit = { request ->
                scope.launch {
                    runCatching {
                        if (editAccount.value == null) {
                            repository.createAccount(request)
                        } else {
                            repository.updateAccount(editAccount.value!!.id, request)
                        }
                        showDialog.value = false
                        loadAccounts()
                    }.onFailure { error = it.message ?: "Error al guardar cuenta" }
                }
            }
        )
    }
}

@Composable
fun AccountItem(account: AccountResponse, onEdit: (AccountResponse) -> Unit, onDelete: (AccountResponse) -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF141824)), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(account.name, color = Color.White, fontWeight = FontWeight.Bold)
                Text(account.type.replaceFirstChar { it.uppercaseChar() }, color = Color.LightGray, fontSize = 12.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("${String.format("%.2f", account.balance)} MXN", color = Color(0xFF4EF57D), fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    TextButton(onClick = { onEdit(account) }) { Text("Editar") }
                    TextButton(onClick = { onDelete(account) }) { Text("Eliminar") }
                }
            }
        }
    }
}

@Composable
fun AccountDialog(
    current: AccountResponse?,
    onDismiss: () -> Unit,
    onSubmit: (AccountRequest) -> Unit
) {
    var name by remember { mutableStateOf(current?.name ?: "") }
    var type by remember { mutableStateOf(current?.type ?: "cash") }
    var balance by remember { mutableStateOf(current?.balance?.toString() ?: "0") }
    var color by remember { mutableStateOf(current?.color ?: "") }
    var icon by remember { mutableStateOf(current?.icon ?: "") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (current == null) "Crear cuenta" else "Editar cuenta") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = type, onValueChange = { type = it }, label = { Text("Tipo (cash/card/savings)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = balance, onValueChange = { balance = it }, label = { Text("Balance") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = color, onValueChange = { color = it }, label = { Text("Color (hex opcional)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = icon, onValueChange = { icon = it }, label = { Text("Icono (opcional)") }, modifier = Modifier.fillMaxWidth())
                error?.let { Text(it, color = Color(0xFFFF6B6B)) }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (name.isBlank() || type.isBlank()) {
                    error = "Nombre y tipo son requeridos"
                    return@Button
                }
                val parsedBalance = balance.toDoubleOrNull() ?: 0.0
                onSubmit(AccountRequest(name = name.trim(), type = type.trim(), balance = parsedBalance, color = color.trim().ifBlank { null }, icon = icon.trim().ifBlank { null }))
            }) {
                Text("Guardar")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
fun CategoriesTab(repository: com.example.profitly.data.repository.ProfitlyRepository) {
    var categories by remember { mutableStateOf<List<CategoryResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val showDialog = remember { mutableStateOf(false) }
    val editCategory = remember { mutableStateOf<CategoryResponse?>(null) }
    val scope = rememberCoroutineScope()

    fun loadCategories() {
        scope.launch {
            isLoading = true
            runCatching {
                categories = repository.getCategories()
                error = null
            }.onFailure {
                error = it.message ?: "No se pudieron cargar categorías"
            }
            isLoading = false
        }
    }

    LaunchedEffect(true) { loadCategories() }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        ScreenTitle("Categorías")
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(onClick = { editCategory.value = null; showDialog.value = true }) {
            Icon(Icons.Default.Add, contentDescription = "Añadir categoría")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Nueva categoría")
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF4F86FF))
            }
        } else if (error != null) {
            Text(error!!, color = Color(0xFFFF6B6B))
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(categories) { category ->
                    CategoryItem(category = category, onEdit = {
                        editCategory.value = it
                        showDialog.value = true
                    }, onDelete = {
                        scope.launch {
                            runCatching {
                                repository.deleteCategory(it.id)
                                loadCategories()
                            }.onFailure { error = it.message ?: "No se pudo eliminar" }
                        }
                    })
                }
            }
        }
    }

    if (showDialog.value) {
        CategoryDialog(
            current = editCategory.value,
            onDismiss = { showDialog.value = false },
            onSubmit = { request ->
                scope.launch {
                    runCatching {
                        if (editCategory.value == null) {
                            repository.createCategory(request)
                        } else {
                            repository.updateCategory(editCategory.value!!.id, request)
                        }
                        showDialog.value = false
                        loadCategories()
                    }.onFailure { error = it.message ?: "Error al guardar categoría" }
                }
            }
        )
    }
}

@Composable
fun CategoryItem(category: CategoryResponse, onEdit: (CategoryResponse) -> Unit, onDelete: (CategoryResponse) -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF141824)), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(category.name, color = Color.White, fontWeight = FontWeight.Bold)
                Text(category.type.replaceFirstChar { it.uppercaseChar() }, color = Color.LightGray, fontSize = 12.sp)
            }
            Row {
                TextButton(onClick = { onEdit(category) }) { Text("Editar") }
                TextButton(onClick = { onDelete(category) }) { Text("Eliminar") }
            }
        }
    }
}

@Composable
fun CategoryDialog(
    current: CategoryResponse?,
    onDismiss: () -> Unit,
    onSubmit: (CategoryRequest) -> Unit
) {
    var name by remember { mutableStateOf(current?.name ?: "") }
    var type by remember { mutableStateOf(current?.type ?: "expense") }
    var icon by remember { mutableStateOf(current?.icon ?: "") }
    var color by remember { mutableStateOf(current?.color ?: "") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (current == null) "Crear categoría" else "Editar categoría") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = type, onValueChange = { type = it }, label = { Text("Tipo (expense/income)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = icon, onValueChange = { icon = it }, label = { Text("Icono (opcional)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = color, onValueChange = { color = it }, label = { Text("Color (opcional)") }, modifier = Modifier.fillMaxWidth())
                error?.let { Text(it, color = Color(0xFFFF6B6B)) }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (name.isBlank() || type.isBlank()) {
                    error = "Nombre y tipo son requeridos"
                    return@Button
                }
                onSubmit(CategoryRequest(name = name.trim(), type = type.trim(), icon = icon.trim().ifBlank { null }, color = color.trim().ifBlank { null }))
            }) {
                Text("Guardar")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
fun TransactionsTab(repository: com.example.profitly.data.repository.ProfitlyRepository) {
    var transactions by remember { mutableStateOf<List<TransactionResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val showDialog = remember { mutableStateOf(false) }
    var filterType by remember { mutableStateOf("") }
    var filterStart by remember { mutableStateOf("") }
    var filterEnd by remember { mutableStateOf("") }
    var filterCategory by remember { mutableStateOf("") }
    var filterAccount by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    fun loadTransactions() {
        scope.launch {
            isLoading = true
            runCatching {
                val categoryId = filterCategory.toLongOrNull()
                val accountId = filterAccount.toLongOrNull()
                transactions = repository.getTransactions(
                    startDate = filterStart.ifBlank { null },
                    endDate = filterEnd.ifBlank { null },
                    type = filterType.ifBlank { null },
                    categoryId = categoryId,
                    accountId = accountId,
                    limit = 50,
                    page = 1
                )
                error = null
            }.onFailure {
                error = it.message ?: "No se pudieron cargar las transacciones"
            }
            isLoading = false
        }
    }

    LaunchedEffect(true) { loadTransactions() }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        ScreenTitle("Operaciones")
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            OutlinedButton(onClick = { showDialog.value = true }) {
                Icon(Icons.Default.Add, contentDescription = "Nueva transacción")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Nueva operación")
            }
            OutlinedButton(onClick = { loadTransactions() }) { Text("Filtrar") }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(value = filterType, onValueChange = { filterType = it }, label = { Text("Tipo") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = filterCategory, onValueChange = { filterCategory = it }, label = { Text("Categoría ID") }, modifier = Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(value = filterAccount, onValueChange = { filterAccount = it }, label = { Text("Cuenta ID") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = filterStart, onValueChange = { filterStart = it }, label = { Text("Desde (YYYY-MM-DD)") }, modifier = Modifier.weight(1f))
            }
            OutlinedTextField(value = filterEnd, onValueChange = { filterEnd = it }, label = { Text("Hasta (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth())
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF4F86FF))
            }
        } else if (error != null) {
            Text(error!!, color = Color(0xFFFF6B6B))
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(transactions) { transaction ->
                    TransactionItem(transaction, onDelete = {
                        scope.launch {
                            runCatching {
                                repository.deleteTransaction(it.id)
                                loadTransactions()
                            }.onFailure { error = it.message ?: "No se pudo eliminar" }
                        }
                    })
                }
            }
        }
    }

    if (showDialog.value) {
        TransactionDialog(onDismiss = { showDialog.value = false }, onSubmit = { transactionRequest, transferRequest, isTransfer ->
            scope.launch {
                runCatching {
                    if (isTransfer) {
                        transferRequest?.let { repository.createTransfer(it) }
                    } else {
                        transactionRequest?.let { repository.createTransaction(it) }
                    }
                    showDialog.value = false
                    loadTransactions()
                }.onFailure { error = it.message ?: "Error al guardar operación" }
            }
        })
    }
}

@Composable
fun TransactionItem(transaction: TransactionResponse, onDelete: (TransactionResponse) -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF141824)), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(transaction.title, color = Color.White, fontWeight = FontWeight.Bold)
                    Text(transaction.type.replaceFirstChar { it.uppercaseChar() }, color = Color.LightGray, fontSize = 12.sp)
                }
                Text("${String.format("%.2f", transaction.amount)} MXN", color = if (transaction.type == "income") Color(0xFF4EF57D) else Color(0xFFFF6B6B), fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(transaction.date, color = Color.Gray, fontSize = 12.sp)
            transaction.description?.let { Text(it, color = Color.LightGray, fontSize = 12.sp) }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = { onDelete(transaction) }) { Text("Eliminar") }
        }
    }
}

@Composable
fun TransactionDialog(onDismiss: () -> Unit, onSubmit: (TransactionRequest?, TransferRequest?, Boolean) -> Unit) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("expense") }
    var description by remember { mutableStateOf("") }
    var categoryId by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("") }
    var accountId by remember { mutableStateOf("") }
    var sourceAccountId by remember { mutableStateOf("") }
    var destinationAccountId by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    val isTransfer = type == "transfer"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva operación") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Monto") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Fecha (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = type, onValueChange = { type = it }, label = { Text("Tipo (income/expense/transfer)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripción (opcional)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = categoryId, onValueChange = { categoryId = it }, label = { Text("Categoría ID (opcional)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = paymentMethod, onValueChange = { paymentMethod = it }, label = { Text("Método de pago (opcional)") }, modifier = Modifier.fillMaxWidth())
                if (!isTransfer) {
                    OutlinedTextField(value = accountId, onValueChange = { accountId = it }, label = { Text("Cuenta ID") }, modifier = Modifier.fillMaxWidth())
                } else {
                    OutlinedTextField(value = sourceAccountId, onValueChange = { sourceAccountId = it }, label = { Text("Cuenta origen ID") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = destinationAccountId, onValueChange = { destinationAccountId = it }, label = { Text("Cuenta destino ID") }, modifier = Modifier.fillMaxWidth())
                }
                error?.let { Text(it, color = Color(0xFFFF6B6B)) }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (title.isBlank() || amount.toDoubleOrNull() == null || date.isBlank() || type.isBlank()) {
                    error = "Título, monto, fecha y tipo son requeridos"
                    return@Button
                }
                if (isTransfer) {
                    if (sourceAccountId.toLongOrNull() == null || destinationAccountId.toLongOrNull() == null) {
                        error = "Las cuentas origen y destino son requeridas para transferencia"
                        return@Button
                    }
                    val request = TransferRequest(
                        title = title.trim(),
                        amount = amount.toDouble(),
                        date = date.trim(),
                        source_account_id = sourceAccountId.toLong(),
                        destination_account_id = destinationAccountId.toLong(),
                        description = description.trim().ifBlank { null },
                        category_id = categoryId.toLongOrNull(),
                        payment_method = paymentMethod.trim().ifBlank { null }
                    )
                    onSubmit(null, request, true)
                } else {
                    if (accountId.toLongOrNull() == null) {
                        error = "La cuenta es requerida para income/expense"
                        return@Button
                    }
                    val request = TransactionRequest(
                        title = title.trim(),
                        amount = amount.toDouble(),
                        date = date.trim(),
                        type = type.trim(),
                        description = description.trim().ifBlank { null },
                        category_id = categoryId.toLongOrNull(),
                        payment_method = paymentMethod.trim().ifBlank { null },
                        account_id = accountId.toLongOrNull(),
                        source_account_id = null,
                        destination_account_id = null
                    )
                    onSubmit(request, null, false)
                }
            }) {
                Text("Guardar")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
}

@Composable
fun DashboardTab(repository: com.example.profitly.data.repository.ProfitlyRepository) {
    var summary by remember { mutableStateOf<DashboardSummaryResponse?>(null) }
    var breakdown by remember { mutableStateOf<List<com.example.profitly.data.api.CategoryBreakdownItem>>(emptyList()) }
    var history by remember { mutableStateOf<DashboardHistoryResponse?>(null) }
    var statistics by remember { mutableStateOf<DashboardStatisticsResponse?>(null) }
    var trends by remember { mutableStateOf<DashboardTrendsResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    fun loadDashboard() {
        scope.launch {
            isLoading = true
            runCatching {
                summary = repository.getDashboardSummary()
                breakdown = repository.getDashboardCategoryBreakdown()
                history = repository.getDashboardHistory(page = 1, limit = 5)
                statistics = repository.getDashboardStatistics()
                trends = repository.getDashboardTrends()
                error = null
            }.onFailure {
                error = it.message ?: "Error al cargar dashboard"
            }
            isLoading = false
        }
    }

    LaunchedEffect(true) { loadDashboard() }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        ScreenTitle("Estadísticas")
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF4F86FF))
            }
            return@Column
        }
        error?.let { Text(it, color = Color(0xFFFF6B6B)) }
        summary?.let {
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF141824)), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Resumen mensual", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("Saldo total: ${String.format("%.2f", it.totalBalance)} MXN", color = Color(0xFF4EF57D))
                    Text("Ingreso: ${String.format("%.2f", it.monthlyIncome)} MXN", color = Color(0xFF4EF57D))
                    Text("Gasto: ${String.format("%.2f", it.monthlyExpense)} MXN", color = Color(0xFFFF6B6B))
                    Text("Neto: ${String.format("%.2f", it.monthlyNet)} MXN", color = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        Text("Por categoría", color = Color.White, fontWeight = FontWeight.SemiBold)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth().weight(1f, fill = false)) {
            items(breakdown) { item ->
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF141824)), modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text(item.name, color = Color.White, fontWeight = FontWeight.Bold)
                            Text(item.type.replaceFirstChar { it.uppercaseChar() }, color = Color.LightGray, fontSize = 12.sp)
                        }
                        Text("${String.format("%.2f", item.amount)} MXN", color = Color(0xFF4EF57D))
                    }
                }
            }
        }
        statistics?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Top categorías", color = Color.White, fontWeight = FontWeight.SemiBold)
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth().weight(1f, fill = false)) {
                items(it.topCategories) { category ->
                    Text("${category.name}: ${String.format("%.2f", category.total)} MXN", color = Color.LightGray)
                }
            }
        }
    }
}

@Composable
fun MoreTab(
    repository: com.example.profitly.data.repository.ProfitlyRepository,
    tokenProvider: com.example.profitly.data.api.AuthTokenProvider
) {
    var profile by remember { mutableStateOf<AuthUserResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(true) {
        scope.launch {
            runCatching {
                profile = repository.getProfile()
                error = null
            }.onFailure {
                error = it.message ?: "No se pudo cargar perfil"
            }
            isLoading = false
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        ScreenTitle("Más")
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF4F86FF))
            }
            return@Column
        }
        error?.let { Text(it, color = Color(0xFFFF6B6B)) }
        profile?.let {
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF141824)), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Perfil", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("Nombre: ${it.name}", color = Color.LightGray)
                    Text("Email: ${it.email}", color = Color.LightGray)
                    Text("Usuario ID: ${it.id}", color = Color.LightGray)
                }
            }
        }
    }
}
