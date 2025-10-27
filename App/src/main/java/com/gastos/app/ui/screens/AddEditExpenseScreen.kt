package com.gastos.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gastos.app.data.model.Category
import com.gastos.app.data.model.Expense
import com.gastos.app.viewmodel.ExpenseState
import com.gastos.app.viewmodel.ExpenseViewModel
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditExpenseScreen(
    expenseViewModel: ExpenseViewModel,
    expense: Expense? = null,
    onNavigateBack: () -> Unit
) {
    var name by remember { mutableStateOf(expense?.name ?: "") }
    var amount by remember { mutableStateOf(expense?.amount?.toString() ?: "") }
    var selectedCategory by remember { mutableStateOf(expense?.category ?: Category.COMIDA) }
    var selectedDate by remember { mutableStateOf(expense?.date?.toDate() ?: Date()) }
    var notes by remember { mutableStateOf(expense?.notes ?: "") }
    var showCategoryPicker by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var amountError by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf(false) }
    
    val expenseState by expenseViewModel.expenseState.collectAsState()
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val context = LocalContext.current
    
    val isEditMode = expense != null
    
    // Observar el estado de la operación
    LaunchedEffect(expenseState) {
        android.util.Log.d("AddEditExpenseScreen", "Estado cambió a: $expenseState")
        when (expenseState) {
            is ExpenseState.Success -> {
                android.util.Log.d("AddEditExpenseScreen", "Mostrando toast de éxito")
                Toast.makeText(context, "✅ Gasto guardado exitosamente", Toast.LENGTH_SHORT).show()
                kotlinx.coroutines.delay(300) // Pequeña demora para que el toast sea visible
                android.util.Log.d("AddEditExpenseScreen", "Navegando hacia atrás")
                onNavigateBack()
                expenseViewModel.resetExpenseState()
            }
            is ExpenseState.Error -> {
                android.util.Log.e("AddEditExpenseScreen", "Error: ${(expenseState as ExpenseState.Error).message}")
                Toast.makeText(
                    context,
                    "❌ Error: ${(expenseState as ExpenseState.Error).message}",
                    Toast.LENGTH_LONG
                ).show()
                expenseViewModel.resetExpenseState()
            }
            is ExpenseState.Loading -> {
                android.util.Log.d("AddEditExpenseScreen", "Cargando...")
            }
            is ExpenseState.Idle -> {
                android.util.Log.d("AddEditExpenseScreen", "Estado Idle")
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(
                            text = if (isEditMode) "Editar Gasto" else "Nuevo Gasto",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isEditMode) "Actualiza los detalles" else "Registra un nuevo gasto",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            // Sección: Detalles básicos
            Text(
                text = "📝 Detalles del Gasto",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            // Card para campos principales
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Campo de nombre
                    OutlinedTextField(
                        value = name,
                        onValueChange = { 
                            name = it
                            nameError = false
                        },
                        label = { Text("¿En qué gastaste?") },
                        placeholder = { Text("Ej: Comida, Gasolina, Ropa...") },
                        leadingIcon = { 
                            Icon(
                                Icons.Default.ShoppingCart, 
                                contentDescription = null,
                                tint = if (nameError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            ) 
                        },
                        isError = nameError,
                        supportingText = if (nameError) { { Text("El nombre es obligatorio") } } else null,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Campo de monto
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { 
                            if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                                amount = it
                                amountError = false
                            }
                        },
                        label = { Text("¿Cuánto gastaste?") },
                        placeholder = { Text("0.00") },
                        leadingIcon = { 
                            Text(
                                text = "$",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (amountError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        },
                        isError = amountError,
                        supportingText = if (amountError) { { Text("Ingresa un monto válido") } } else null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
            
            // Sección: Categoría y Fecha
            Text(
                text = "📂 Categoría y Fecha",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Card para categoría y fecha
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Selector de categoría
                    Card(
                        onClick = { showCategoryPicker = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedCategory.emoji,
                                fontSize = 32.sp,
                                modifier = Modifier.padding(end = 16.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Categoría",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = selectedCategory.displayName,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Icon(
                                Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    
                    // Selector de fecha
                    Card(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.DateRange,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(32.dp)
                                    .padding(end = 16.dp),
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Fecha",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = dateFormatter.format(selectedDate),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                            Icon(
                                Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
            
            // Sección: Notas
            Text(
                text = "📌 Notas Adicionales",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Campo de notas
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notas opcionales") },
                    placeholder = { Text("Agrega detalles adicionales sobre este gasto...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .padding(16.dp),
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
            
            // Botón de guardar
            Button(
                onClick = {
                    // Validaciones
                    nameError = name.isBlank()
                    val amountDouble = amount.toDoubleOrNull()
                    amountError = amountDouble == null || amountDouble <= 0
                    
                    if (!nameError && !amountError) {
                        val timestamp = Timestamp(selectedDate)
                        if (isEditMode) {
                            expenseViewModel.updateExpense(
                                expenseId = expense.id,
                                name = name,
                                amount = amount,
                                category = selectedCategory,
                                date = timestamp,
                                notes = notes
                            )
                        } else {
                            expenseViewModel.addExpense(
                                name = name,
                                amount = amount,
                                category = selectedCategory,
                                date = timestamp,
                                notes = notes
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = expenseState !is ExpenseState.Loading,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (expenseState is ExpenseState.Loading) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Guardando...",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isEditMode) Icons.Default.Check else Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier
                                .size(24.dp)
                                .padding(end = 8.dp)
                        )
                        Text(
                            text = if (isEditMode) "Actualizar Gasto" else "Guardar Gasto",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            // Botón de cancelar cuando está cargando
            if (expenseState is ExpenseState.Loading) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = {
                        expenseViewModel.resetExpenseState()
                        onNavigateBack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Cancelar",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // Mensajes de validación y error
            if (nameError || amountError || expenseState is ExpenseState.Error) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (nameError) {
                            Text(
                                text = "⚠️ El nombre del gasto es obligatorio",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        if (amountError) {
                            if (nameError) Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "⚠️ Ingresa un monto válido mayor a 0",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        if (expenseState is ExpenseState.Error) {
                            if (nameError || amountError) Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "❌ ${(expenseState as ExpenseState.Error).message}",
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
        }
    }
    
    // Diálogo de selección de categoría
    if (showCategoryPicker) {
        AlertDialog(
            onDismissRequest = { showCategoryPicker = false },
            title = { Text("Seleccionar categoría") },
            text = {
                Column {
                    Category.getAllCategories().forEach { category ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedCategory = category
                                    showCategoryPicker = false
                                }
                                .padding(vertical = 12.dp)
                        ) {
                            Text(
                                text = category.emoji,
                                fontSize = 24.sp,
                                modifier = Modifier.padding(end = 16.dp)
                            )
                            Text(
                                text = category.displayName,
                                fontSize = 16.sp
                            )
                            if (selectedCategory == category) {
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCategoryPicker = false }) {
                    Text("Cerrar")
                }
            }
        )
    }
    
    // Diálogo de selección de fecha (simplificado)
    if (showDatePicker) {
        val calendar = Calendar.getInstance()
        calendar.time = selectedDate
        
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate.time
        )
        
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            selectedDate = Date(millis)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

