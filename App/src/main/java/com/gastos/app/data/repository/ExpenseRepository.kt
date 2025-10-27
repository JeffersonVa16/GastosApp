package com.gastos.app.data.repository

import android.util.Log
import com.gastos.app.data.model.Category
import com.gastos.app.data.model.Expense
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Calendar

class ExpenseRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val expensesCollection = firestore.collection("expenses")
    
    // Obtener el ID del usuario actual
    private fun getCurrentUserId(): String? = auth.currentUser?.uid
    
    // Agregar un nuevo gasto
    suspend fun addExpense(expense: Expense): Result<String> {
        return try {
            Log.d("ExpenseRepository", "=== INICIANDO GUARDADO DE GASTO ===")
            
            // Verificar FirebaseAuth
            val currentUser = auth.currentUser
            Log.d("ExpenseRepository", "🔑 FirebaseAuth - Usuario actual: ${currentUser?.email}")
            Log.d("ExpenseRepository", "🔑 Usuario UID: ${currentUser?.uid}")
            Log.d("ExpenseRepository", "🔑 isAnonymous: ${currentUser?.isAnonymous}")
            
            val userId = getCurrentUserId()
            Log.d("ExpenseRepository", "📋 userId obtenido: $userId")
            
            if (userId == null) {
                Log.e("ExpenseRepository", "❌ Usuario no autenticado - FirebaseAuth.currentUser es NULL")
                return Result.failure(Exception("No estás autenticado. Por favor, cierra sesión e inicia sesión de nuevo."))
            }
            
            val expenseWithUser = expense.copy(
                userId = userId,
                createdAt = Timestamp.now(),
                updatedAt = Timestamp.now()
            )
            
            val expenseMap = expenseWithUser.toMap()
            Log.d("ExpenseRepository", "📝 Datos a guardar:")
            Log.d("ExpenseRepository", "   - nombre: ${expense.name}")
            Log.d("ExpenseRepository", "   - monto: ${expense.amount}")
            Log.d("ExpenseRepository", "   - categoria: ${expense.category}")
            Log.d("ExpenseRepository", "   - userId: $userId")
            
            Log.d("ExpenseRepository", "🌐 Intentando conectar con Firestore...")
            Log.d("ExpenseRepository", "🌐 Colección: expenses")
            Log.d("ExpenseRepository", "🌐 Esperando respuesta de Firebase...")
            
            val docRef = expensesCollection.add(expenseMap).await()
            
            Log.d("ExpenseRepository", "✅✅✅ ¡ÉXITO! Gasto guardado con ID: ${docRef.id}")
            
            Result.success(docRef.id)
        } catch (e: com.google.firebase.FirebaseNetworkException) {
            Log.e("ExpenseRepository", "❌ ERROR DE RED: No hay conexión a internet", e)
            Result.failure(Exception("Sin conexión a internet. Verifica tu conexión y vuelve a intentar."))
        } catch (e: com.google.firebase.FirebaseException) {
            Log.e("ExpenseRepository", "❌ ERROR DE FIREBASE: ${e.javaClass.simpleName} - ${e.message}", e)
            Result.failure(Exception("Error de Firebase: ${e.message}. ¿Está configurado Firestore en Firebase Console?"))
        } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
            Log.e("ExpenseRepository", "❌ TIMEOUT: Firebase no respondió a tiempo", e)
            Result.failure(Exception("Firebase no responde. Verifica:\n1) Que Firestore esté creado\n2) Las reglas de Firestore\n3) Tu conexión a internet"))
        } catch (e: Exception) {
            Log.e("ExpenseRepository", "❌ ERROR GENERAL: ${e.javaClass.simpleName} - ${e.message}", e)
            Result.failure(Exception("Error: ${e.message}"))
        }
    }
    
    // Actualizar un gasto existente
    suspend fun updateExpense(expense: Expense): Result<Unit> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("Usuario no autenticado"))
            
            if (expense.id.isEmpty()) {
                return Result.failure(Exception("ID de gasto inválido"))
            }
            
            expensesCollection.document(expense.id)
                .update(expense.copy(userId = userId).toMap())
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Eliminar un gasto
    suspend fun deleteExpense(expenseId: String): Result<Unit> {
        return try {
            expensesCollection.document(expenseId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Obtener todos los gastos del usuario en tiempo real
    fun getUserExpenses(): Flow<List<Expense>> = callbackFlow {
        val userId = getCurrentUserId()
        if (userId == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        
        val listener = expensesCollection
            .whereEqualTo("userId", userId)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val expenses = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        Expense.fromMap(doc.id, doc.data ?: emptyMap())
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()
                
                trySend(expenses)
            }
        
        awaitClose { listener.remove() }
    }
    
    // Obtener gastos filtrados por mes y año
    fun getExpensesByMonth(month: Int, year: Int): Flow<List<Expense>> = callbackFlow {
        val userId = getCurrentUserId()
        if (userId == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, 1, 0, 0, 0)
        val startDate = Timestamp(calendar.time)
        
        calendar.set(year, month - 1, calendar.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59)
        val endDate = Timestamp(calendar.time)
        
        val listener = expensesCollection
            .whereEqualTo("userId", userId)
            .whereGreaterThanOrEqualTo("date", startDate)
            .whereLessThanOrEqualTo("date", endDate)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val expenses = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        Expense.fromMap(doc.id, doc.data ?: emptyMap())
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()
                
                trySend(expenses)
            }
        
        awaitClose { listener.remove() }
    }
    
    // Obtener gastos filtrados por categoría
    fun getExpensesByCategory(category: Category): Flow<List<Expense>> = callbackFlow {
        val userId = getCurrentUserId()
        if (userId == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        
        val listener = expensesCollection
            .whereEqualTo("userId", userId)
            .whereEqualTo("category", category.name)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val expenses = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        Expense.fromMap(doc.id, doc.data ?: emptyMap())
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()
                
                trySend(expenses)
            }
        
        awaitClose { listener.remove() }
    }
    
    // Calcular el total de gastos
    fun calculateTotal(expenses: List<Expense>): Double {
        return expenses.sumOf { it.amount }
    }
    
    // Calcular total por categoría
    fun calculateTotalByCategory(expenses: List<Expense>): Map<Category, Double> {
        return expenses.groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
    }
}

