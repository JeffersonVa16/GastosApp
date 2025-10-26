package com.gastos.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gastos.app.data.model.Category
import com.gastos.app.data.model.Expense
import com.gastos.app.viewmodel.AuthViewModel
import com.gastos.app.viewmodel.ExpenseFilter
import com.gastos.app.viewmodel.ExpenseViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    expenseViewModel: ExpenseViewModel,
    onAddExpenseClick: () -> Unit,
    onExpenseClick: (Expense) -> Unit,
    onProfileClick: () -> Unit,
    onLogout: () -> Unit
) {
    val expenses by expenseViewModel.expenses.collectAsState()
    val totalExpenses by expenseViewModel.totalExpenses.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val filter by expenseViewModel.filter.collectAsState()
    
    var showMenu by remember { mutableStateOf(false) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf<Expense?>(null) }
    
    val currencyFormatter = remember {
        NumberFormat.getCurrencyInstance(Locale("en", "US")).apply {
            maximumFractionDigits = 2
        }
    }
    
    // Cargar gastos del mes actual al iniciar
    LaunchedEffect(Unit) {
        expenseViewModel.getCurrentMonthExpenses()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(
                            "💰 Control de Gastos",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        currentUser?.email?.let { email ->
                            Text(
                                text = email,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(
                            Icons.Default.FilterList,
                            contentDescription = "Filtrar",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "Menú",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Perfil") },
                            onClick = {
                                showMenu = false
                                onProfileClick()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Cerrar sesión") },
                            onClick = {
                                showMenu = false
                                authViewModel.logout()
                                onLogout()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Logout, contentDescription = null)
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddExpenseClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar gasto")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Card de resumen total con diseño mejorado
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalance,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Total del mes actual",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = currencyFormatter.format(totalExpenses),
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Receipt,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${expenses.size} gastos registrados",
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                        )
                    }
                }
            }
            
            // Mostrar filtros activos
            if (filter.category != null || (filter.month != null && filter.year != null)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Filtros activos: ",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (filter.category != null) {
                        FilterChip(
                            selected = true,
                            onClick = { },
                            label = { Text(filter.category!!.displayName) },
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                    if (filter.month != null && filter.year != null) {
                        FilterChip(
                            selected = true,
                            onClick = { },
                            label = { Text("${filter.month}/${filter.year}") },
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                    TextButton(onClick = { expenseViewModel.clearFilter() }) {
                        Text("Limpiar")
                    }
                }
            }
            
            // Lista de gastos
            if (expenses.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "📝",
                            fontSize = 64.sp
                        )
                        Text(
                            text = "No hay gastos registrados",
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Toca el botón + para agregar uno",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(expenses, key = { it.id }) { expense ->
                        ExpenseItem(
                            expense = expense,
                            onClick = { onExpenseClick(expense) },
                            onDeleteClick = { showDeleteDialog = expense },
                            currencyFormatter = currencyFormatter
                        )
                    }
                }
            }
        }
    }
    
    // Diálogo de filtros
    if (showFilterDialog) {
        FilterDialog(
            currentFilter = filter,
            onDismiss = { showFilterDialog = false },
            onApplyFilter = { newFilter ->
                expenseViewModel.setFilter(newFilter)
                showFilterDialog = false
            }
        )
    }
    
    // Diálogo de confirmación de eliminación
    showDeleteDialog?.let { expense ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Eliminar gasto") },
            text = { Text("¿Estás seguro de que deseas eliminar el gasto \"${expense.name}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        expenseViewModel.deleteExpense(expense.id)
                        showDeleteDialog = null
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun ExpenseItem(
    expense: Expense,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    currencyFormatter: NumberFormat
) {
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono de categoría con color vibrante
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = expense.category.emoji,
                    fontSize = 28.sp
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Información del gasto
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = expense.name,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = expense.category.displayName,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = " • ",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = dateFormatter.format(expense.date.toDate()),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Columna de monto y botón
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = currencyFormatter.format(expense.amount),
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                // Botón de eliminar más pequeño
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDialog(
    currentFilter: ExpenseFilter,
    onDismiss: () -> Unit,
    onApplyFilter: (ExpenseFilter) -> Unit
) {
    var selectedCategory by remember { mutableStateOf(currentFilter.category) }
    var selectedMonth by remember { mutableStateOf(currentFilter.month ?: Calendar.getInstance().get(Calendar.MONTH) + 1) }
    var selectedYear by remember { mutableStateOf(currentFilter.year ?: Calendar.getInstance().get(Calendar.YEAR)) }
    var filterByCategory by remember { mutableStateOf(currentFilter.category != null) }
    var filterByDate by remember { mutableStateOf(currentFilter.month != null) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filtrar gastos") },
        text = {
            Column {
                // Filtro por categoría
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = filterByCategory,
                        onCheckedChange = { filterByCategory = it }
                    )
                    Text("Filtrar por categoría")
                }
                
                if (filterByCategory) {
                    Category.getAllCategories().forEach { category ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedCategory = category }
                                .padding(vertical = 8.dp)
                        ) {
                            RadioButton(
                                selected = selectedCategory == category,
                                onClick = { selectedCategory = category }
                            )
                            Text("${category.emoji} ${category.displayName}")
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Filtro por fecha (actualmente filtrado por mes actual)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = filterByDate,
                        onCheckedChange = { filterByDate = it }
                    )
                    Text("Filtrar por mes/año")
                }
                
                if (filterByDate) {
                    Text(
                        text = "Mes: $selectedMonth, Año: $selectedYear",
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 40.dp, top = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val newFilter = ExpenseFilter(
                        category = if (filterByCategory) selectedCategory else null,
                        month = if (filterByDate) selectedMonth else null,
                        year = if (filterByDate) selectedYear else null
                    )
                    onApplyFilter(newFilter)
                }
            ) {
                Text("Aplicar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

