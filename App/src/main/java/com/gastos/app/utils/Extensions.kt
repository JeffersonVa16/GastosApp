package com.gastos.app.utils

import com.google.firebase.Timestamp
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Extensiones útiles para la aplicación de Gastos
 */

// ==================== Extensiones de Date ====================

/**
 * Convierte un Timestamp de Firebase a Date
 */
fun Timestamp.toDate(): Date = this.toDate()

/**
 * Formatea una fecha en formato dd/MM/yyyy
 */
fun Date.toFormattedString(): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(this)
}

/**
 * Formatea una fecha en formato completo: "Martes, 24 de octubre de 2023"
 */
fun Date.toFullFormattedString(): String {
    val formatter = SimpleDateFormat("EEEE, d 'de' MMMM 'de' yyyy", Locale("es", "ES"))
    return formatter.format(this)
}

/**
 * Obtiene el mes de una fecha (1-12)
 */
fun Date.getMonth(): Int {
    val calendar = Calendar.getInstance()
    calendar.time = this
    return calendar.get(Calendar.MONTH) + 1
}

/**
 * Obtiene el año de una fecha
 */
fun Date.getYear(): Int {
    val calendar = Calendar.getInstance()
    calendar.time = this
    return calendar.get(Calendar.YEAR)
}

/**
 * Verifica si una fecha es del mes actual
 */
fun Date.isCurrentMonth(): Boolean {
    val current = Calendar.getInstance()
    val date = Calendar.getInstance().apply { time = this@isCurrentMonth }
    
    return current.get(Calendar.MONTH) == date.get(Calendar.MONTH) &&
           current.get(Calendar.YEAR) == date.get(Calendar.YEAR)
}

/**
 * Verifica si una fecha es de hoy
 */
fun Date.isToday(): Boolean {
    val current = Calendar.getInstance()
    val date = Calendar.getInstance().apply { time = this@isToday }
    
    return current.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
           current.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR)
}

// ==================== Extensiones de Number ====================

/**
 * Formatea un número como moneda en euros
 */
fun Double.toCurrencyString(locale: Locale = Locale("es", "ES")): String {
    val formatter = NumberFormat.getCurrencyInstance(locale)
    formatter.maximumFractionDigits = 2
    return formatter.format(this)
}

/**
 * Formatea un número con separador de miles
 */
fun Double.toFormattedString(): String {
    return String.format(Locale.getDefault(), "%,.2f", this)
}

/**
 * Redondea a 2 decimales
 */
fun Double.roundToTwoDecimals(): Double {
    return String.format(Locale.getDefault(), "%.2f", this).toDouble()
}

// ==================== Extensiones de String ====================

/**
 * Valida si un string es un email válido
 */
fun String.isValidEmail(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

/**
 * Valida si un string es un número válido
 */
fun String.isValidNumber(): Boolean {
    return this.toDoubleOrNull() != null
}

/**
 * Valida si un string es un monto válido (número positivo)
 */
fun String.isValidAmount(): Boolean {
    val number = this.toDoubleOrNull()
    return number != null && number > 0
}

/**
 * Capitaliza la primera letra de cada palabra
 */
fun String.capitalizeWords(): String {
    return this.split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { 
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() 
        }
    }
}

// ==================== Funciones Utilitarias ====================

/**
 * Obtiene el primer día del mes actual
 */
fun getFirstDayOfMonth(): Date {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.time
}

/**
 * Obtiene el último día del mes actual
 */
fun getLastDayOfMonth(): Date {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)
    calendar.set(Calendar.MILLISECOND, 999)
    return calendar.time
}

/**
 * Obtiene el primer día de un mes específico
 */
fun getFirstDayOfMonth(month: Int, year: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.set(year, month - 1, 1, 0, 0, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.time
}

/**
 * Obtiene el último día de un mes específico
 */
fun getLastDayOfMonth(month: Int, year: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.set(year, month - 1, 1)
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)
    calendar.set(Calendar.MILLISECOND, 999)
    return calendar.time
}

/**
 * Obtiene la fecha actual sin horas
 */
fun getTodayDate(): Date {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.time
}

/**
 * Obtiene el nombre del mes en español
 */
fun getMonthName(month: Int): String {
    val months = arrayOf(
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    )
    return months.getOrElse(month - 1) { "Mes inválido" }
}

/**
 * Obtiene una lista de los últimos N meses
 */
fun getLastNMonths(n: Int): List<Pair<Int, Int>> {
    val result = mutableListOf<Pair<Int, Int>>()
    val calendar = Calendar.getInstance()
    
    repeat(n) {
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)
        result.add(Pair(month, year))
        calendar.add(Calendar.MONTH, -1)
    }
    
    return result
}

/**
 * Calcula la diferencia en días entre dos fechas
 */
fun daysBetween(date1: Date, date2: Date): Int {
    val diff = date2.time - date1.time
    return (diff / (1000 * 60 * 60 * 24)).toInt()
}

/**
 * Obtiene el timestamp de Firebase para la fecha actual
 */
fun getCurrentTimestamp(): Timestamp {
    return Timestamp.now()
}

/**
 * Convierte una fecha a Timestamp de Firebase
 */
fun Date.toFirebaseTimestamp(): Timestamp {
    return Timestamp(this)
}

