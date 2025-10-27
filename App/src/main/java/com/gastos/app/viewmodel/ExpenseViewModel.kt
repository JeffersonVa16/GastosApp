package com.gastos.app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gastos.app.data.model.Category
import com.gastos.app.data.model.Expense
import com.gastos.app.data.repository.ExpenseRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.util.Calendar

sealed class ExpenseState {
    object Idle : ExpenseState()
    object Loading : ExpenseState()
    object Success : ExpenseState()
    data class Error(val message: String) : ExpenseState()
}

data class ExpenseFilter(
    val category: Category? = null,
    val month: Int? = null,
    val year: Int? = null
)

class ExpenseViewModel : ViewModel() {
    private val expenseRepository = ExpenseRepository()
    
    private val _expenseState = MutableStateFlow<ExpenseState>(ExpenseState.Idle)
    val expenseState: StateFlow<ExpenseState> = _expenseState.asStateFlow()
    
    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses.asStateFlow()
    
    private val _filter = MutableStateFlow(ExpenseFilter())
    val filter: StateFlow<ExpenseFilter> = _filter.asStateFlow()
    
    private val _totalExpenses = MutableStateFlow(0.0)
    val totalExpenses: StateFlow<Double> = _totalExpenses.asStateFlow()
    
    private val _totalByCategory = MutableStateFlow<Map<Category, Double>>(emptyMap())
    val totalByCategory: StateFlow<Map<Category, Double>> = _totalByCategory.asStateFlow()
    
    init {
        loadExpenses()
    }
    
    private fun loadExpenses() {
        viewModelScope.launch {
            val currentFilter = _filter.value
            
            val flow = when {
                currentFilter.month != null && currentFilter.year != null -> {
                    expenseRepository.getExpensesByMonth(currentFilter.month, currentFilter.year)
                }
                currentFilter.category != null -> {
                    expenseRepository.getExpensesByCategory(currentFilter.category)
                }
                else -> {
                    expenseRepository.getUserExpenses()
                }
            }
            
            flow.collect { expenseList ->
                _expenses.value = expenseList
                _totalExpenses.value = expenseRepository.calculateTotal(expenseList)
                _totalByCategory.value = expenseRepository.calculateTotalByCategory(expenseList)
            }
        }
    }
    
    fun addExpense(name: String, amount: String, category: Category, date: Timestamp, notes: String = "") {
        Log.d("ExpenseViewModel", "addExpense llamado - name: $name, amount: $amount")
        
        if (name.isBlank()) {
            Log.w("ExpenseViewModel", "Nombre vacío")
            _expenseState.value = ExpenseState.Error("El nombre del gasto es obligatorio")
            return
        }
        
        val amountDouble = amount.toDoubleOrNull()
        if (amountDouble == null || amountDouble <= 0) {
            Log.w("ExpenseViewModel", "Monto inválido: $amount")
            _expenseState.value = ExpenseState.Error("El monto debe ser un número válido mayor a 0")
            return
        }
        
        viewModelScope.launch {
            try {
                Log.d("ExpenseViewModel", "Cambiando estado a Loading")
                _expenseState.value = ExpenseState.Loading
                
                val expense = Expense(
                    name = name,
                    amount = amountDouble,
                    category = category,
                    date = date,
                    notes = notes,
                    createdAt = Timestamp.now(),
                    updatedAt = Timestamp.now()
                )
                
                Log.d("ExpenseViewModel", "Llamando a repository.addExpense")
                
                // Agregar timeout de 10 segundos
                val result = withTimeout(10000L) {
                    expenseRepository.addExpense(expense)
                }
                
                Log.d("ExpenseViewModel", "Resultado recibido: ${result.isSuccess}")
                _expenseState.value = result.fold(
                    onSuccess = { id ->
                        Log.d("ExpenseViewModel", "✅ Gasto agregado exitosamente con ID: $id")
                        ExpenseState.Success
                    },
                    onFailure = { exception ->
                        Log.e("ExpenseViewModel", "❌ Error al agregar gasto", exception)
                        ExpenseState.Error(exception.message ?: "Error al agregar gasto")
                    }
                )
                Log.d("ExpenseViewModel", "Estado final: ${_expenseState.value}")
            } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
                Log.e("ExpenseViewModel", "❌ Timeout: Firebase tardó demasiado")
                _expenseState.value = ExpenseState.Error("La operación tardó demasiado. Verifica tu conexión a internet.")
            } catch (e: Exception) {
                Log.e("ExpenseViewModel", "❌ Excepción inesperada", e)
                _expenseState.value = ExpenseState.Error(e.message ?: "Error inesperado")
            }
        }
    }
    
    fun updateExpense(expenseId: String, name: String, amount: String, category: Category, date: Timestamp, notes: String = "") {
        if (name.isBlank()) {
            _expenseState.value = ExpenseState.Error("El nombre del gasto es obligatorio")
            return
        }
        
        val amountDouble = amount.toDoubleOrNull()
        if (amountDouble == null || amountDouble <= 0) {
            _expenseState.value = ExpenseState.Error("El monto debe ser un número válido mayor a 0")
            return
        }
        
        viewModelScope.launch {
            _expenseState.value = ExpenseState.Loading
            
            val expense = Expense(
                id = expenseId,
                name = name,
                amount = amountDouble,
                category = category,
                date = date,
                notes = notes,
                updatedAt = Timestamp.now()
            )
            
            val result = expenseRepository.updateExpense(expense)
            
            _expenseState.value = result.fold(
                onSuccess = { ExpenseState.Success },
                onFailure = { exception ->
                    ExpenseState.Error(exception.message ?: "Error al actualizar gasto")
                }
            )
        }
    }
    
    fun deleteExpense(expenseId: String) {
        viewModelScope.launch {
            _expenseState.value = ExpenseState.Loading
            
            val result = expenseRepository.deleteExpense(expenseId)
            
            _expenseState.value = result.fold(
                onSuccess = { ExpenseState.Success },
                onFailure = { exception ->
                    ExpenseState.Error(exception.message ?: "Error al eliminar gasto")
                }
            )
        }
    }
    
    fun setFilter(filter: ExpenseFilter) {
        _filter.value = filter
        loadExpenses()
    }
    
    fun clearFilter() {
        _filter.value = ExpenseFilter()
        loadExpenses()
    }
    
    fun getCurrentMonthExpenses() {
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH) + 1
        val currentYear = calendar.get(Calendar.YEAR)
        
        setFilter(ExpenseFilter(month = currentMonth, year = currentYear))
    }
    
    fun resetExpenseState() {
        _expenseState.value = ExpenseState.Idle
    }
}

