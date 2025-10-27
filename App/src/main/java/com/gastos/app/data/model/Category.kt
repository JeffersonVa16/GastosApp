package com.gastos.app.data.model

enum class Category(val displayName: String, val emoji: String) {
    COMIDA("Comida", "🍔"),
    TRANSPORTE("Transporte", "🚗"),
    ENTRETENIMIENTO("Entretenimiento", "🎬"),
    SALUD("Salud", "🏥"),
    EDUCACION("Educación", "📚"),
    VIVIENDA("Vivienda", "🏠"),
    SERVICIOS("Servicios", "💡"),
    ROPA("Ropa", "👕"),
    TECNOLOGIA("Tecnología", "💻"),
    OTROS("Otros", "📦");
    
    companion object {
        fun getAllCategories(): List<Category> {
            return values().toList()
        }
        
        fun fromString(value: String): Category {
            return try {
                valueOf(value.uppercase())
            } catch (e: IllegalArgumentException) {
                OTROS
            }
        }
    }
}

