package com.gastos.app.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Expense(
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val amount: Double = 0.0,
    val category: Category = Category.OTROS,
    val date: Timestamp = Timestamp.now(),
    val notes: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
) {
    // Constructor vacío requerido por Firestore
    constructor() : this("", "", "", 0.0, Category.OTROS, Timestamp.now(), "", Timestamp.now(), Timestamp.now())
    
    // Función para convertir a Map para Firestore
    fun toMap(): Map<String, Any> {
        return hashMapOf(
            "userId" to userId,
            "name" to name,
            "amount" to amount,
            "category" to category.name,
            "date" to date,
            "notes" to notes,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt
        )
    }
    
    companion object {
        // Función para crear desde Map de Firestore
        fun fromMap(id: String, map: Map<String, Any>): Expense {
            return Expense(
                id = id,
                userId = map["userId"] as? String ?: "",
                name = map["name"] as? String ?: "",
                amount = (map["amount"] as? Number)?.toDouble() ?: 0.0,
                category = try {
                    Category.valueOf(map["category"] as? String ?: "OTROS")
                } catch (e: Exception) {
                    Category.OTROS
                },
                date = map["date"] as? Timestamp ?: Timestamp.now(),
                notes = map["notes"] as? String ?: "",
                createdAt = map["createdAt"] as? Timestamp ?: Timestamp.now(),
                updatedAt = map["updatedAt"] as? Timestamp ?: Timestamp.now()
            )
        }
    }
}

