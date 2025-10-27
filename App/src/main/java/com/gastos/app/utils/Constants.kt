package com.gastos.app.utils

/**
 * Constantes utilizadas en toda la aplicación
 */
object Constants {
    
    // ==================== Firebase ====================
    
    /** Nombre de la colección de gastos en Firestore */
    const val EXPENSES_COLLECTION = "expenses"
    
    /** Nombre de la colección de usuarios en Firestore */
    const val USERS_COLLECTION = "users"
    
    /** Clave para el campo userId */
    const val FIELD_USER_ID = "userId"
    
    /** Clave para el campo de fecha */
    const val FIELD_DATE = "date"
    
    /** Clave para el campo de categoría */
    const val FIELD_CATEGORY = "category"
    
    /** Clave para el campo de monto */
    const val FIELD_AMOUNT = "amount"
    
    // ==================== Validación ====================
    
    /** Longitud mínima de contraseña */
    const val MIN_PASSWORD_LENGTH = 6
    
    /** Monto mínimo permitido */
    const val MIN_AMOUNT = 0.01
    
    /** Monto máximo permitido (1 millón) */
    const val MAX_AMOUNT = 1000000.0
    
    /** Longitud máxima del nombre de gasto */
    const val MAX_EXPENSE_NAME_LENGTH = 100
    
    // ==================== Formato ====================
    
    /** Formato de fecha corto: dd/MM/yyyy */
    const val DATE_FORMAT_SHORT = "dd/MM/yyyy"
    
    /** Formato de fecha largo: EEEE, d 'de' MMMM 'de' yyyy */
    const val DATE_FORMAT_LONG = "EEEE, d 'de' MMMM 'de' yyyy"
    
    /** Formato de fecha con hora: dd/MM/yyyy HH:mm */
    const val DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm"
    
    /** Formato de hora: HH:mm */
    const val TIME_FORMAT = "HH:mm"
    
    // ==================== Locale ====================
    
    /** Código de idioma español */
    const val LANGUAGE_SPANISH = "es"
    
    /** Código de país España */
    const val COUNTRY_SPAIN = "ES"
    
    // ==================== UI ====================
    
    /** Duración de animaciones en milisegundos */
    const val ANIMATION_DURATION = 300
    
    /** Tiempo de espera para búsqueda (debounce) */
    const val SEARCH_DEBOUNCE_TIME = 500L
    
    /** Número de items a cargar por página (si se implementa paginación) */
    const val ITEMS_PER_PAGE = 20
    
    // ==================== Errores ====================
    
    /** Mensaje de error genérico */
    const val ERROR_GENERIC = "Ha ocurrido un error. Por favor, inténtalo de nuevo."
    
    /** Mensaje de error de conexión */
    const val ERROR_CONNECTION = "No hay conexión a Internet. Verifica tu conexión."
    
    /** Mensaje de error de autenticación */
    const val ERROR_AUTH = "Error de autenticación. Por favor, inicia sesión nuevamente."
    
    /** Mensaje de error de permisos */
    const val ERROR_PERMISSION = "No tienes permisos para realizar esta acción."
    
    // ==================== Mensajes ====================
    
    /** Mensaje de éxito al guardar */
    const val SUCCESS_SAVED = "Guardado correctamente"
    
    /** Mensaje de éxito al eliminar */
    const val SUCCESS_DELETED = "Eliminado correctamente"
    
    /** Mensaje de éxito al actualizar */
    const val SUCCESS_UPDATED = "Actualizado correctamente"
    
    // ==================== SharedPreferences Keys ====================
    
    /** Clave para preferencia de tema oscuro */
    const val PREF_DARK_THEME = "dark_theme"
    
    /** Clave para preferencia de moneda */
    const val PREF_CURRENCY = "currency"
    
    /** Clave para preferencia de formato de fecha */
    const val PREF_DATE_FORMAT = "date_format"
    
    /** Clave para última sincronización */
    const val PREF_LAST_SYNC = "last_sync"
    
    // ==================== Valores Predeterminados ====================
    
    /** Categoría predeterminada */
    const val DEFAULT_CATEGORY = "OTROS"
    
    /** Moneda predeterminada */
    const val DEFAULT_CURRENCY = "EUR"
    
    /** Locale predeterminado */
    const val DEFAULT_LOCALE = "es_ES"
    
    // ==================== Límites ====================
    
    /** Número máximo de gastos a mostrar sin paginación */
    const val MAX_EXPENSES_WITHOUT_PAGINATION = 100
    
    /** Tiempo máximo de espera para operaciones de red (segundos) */
    const val NETWORK_TIMEOUT_SECONDS = 30L
    
    // ==================== Request Codes ====================
    
    /** Request code para login con Google */
    const val RC_GOOGLE_SIGN_IN = 9001
    
    /** Request code para selección de imagen */
    const val RC_PICK_IMAGE = 9002
    
    // ==================== Tags para Logging ====================
    
    /** Tag para logs de autenticación */
    const val TAG_AUTH = "AUTH"
    
    /** Tag para logs de Firestore */
    const val TAG_FIRESTORE = "FIRESTORE"
    
    /** Tag para logs de UI */
    const val TAG_UI = "UI"
    
    /** Tag para logs de navegación */
    const val TAG_NAVIGATION = "NAVIGATION"
    
    /** Tag para logs de ViewModels */
    const val TAG_VIEWMODEL = "VIEWMODEL"
}

/**
 * Mensajes de validación
 */
object ValidationMessages {
    const val EMAIL_REQUIRED = "El correo electrónico es obligatorio"
    const val EMAIL_INVALID = "El correo electrónico no es válido"
    const val PASSWORD_REQUIRED = "La contraseña es obligatoria"
    const val PASSWORD_TOO_SHORT = "La contraseña debe tener al menos ${Constants.MIN_PASSWORD_LENGTH} caracteres"
    const val NAME_REQUIRED = "El nombre es obligatorio"
    const val NAME_TOO_LONG = "El nombre es demasiado largo (máximo ${Constants.MAX_EXPENSE_NAME_LENGTH} caracteres)"
    const val AMOUNT_REQUIRED = "El monto es obligatorio"
    const val AMOUNT_INVALID = "El monto no es válido"
    const val AMOUNT_TOO_SMALL = "El monto debe ser mayor a ${Constants.MIN_AMOUNT}"
    const val AMOUNT_TOO_LARGE = "El monto debe ser menor a ${Constants.MAX_AMOUNT}"
    const val CATEGORY_REQUIRED = "La categoría es obligatoria"
    const val DATE_REQUIRED = "La fecha es obligatoria"
}

/**
 * Rutas de navegación
 */
object Routes {
    const val LOGIN = "login"
    const val HOME = "home"
    const val ADD_EXPENSE = "add_expense"
    const val EDIT_EXPENSE = "edit_expense/{expenseId}"
    const val PROFILE = "profile"
    const val SETTINGS = "settings"
    const val STATISTICS = "statistics"
    
    fun editExpense(expenseId: String) = "edit_expense/$expenseId"
}

/**
 * Categorías disponibles (nombres de enum)
 */
object Categories {
    const val COMIDA = "COMIDA"
    const val TRANSPORTE = "TRANSPORTE"
    const val ENTRETENIMIENTO = "ENTRETENIMIENTO"
    const val SALUD = "SALUD"
    const val EDUCACION = "EDUCACION"
    const val VIVIENDA = "VIVIENDA"
    const val SERVICIOS = "SERVICIOS"
    const val ROPA = "ROPA"
    const val TECNOLOGIA = "TECNOLOGIA"
    const val OTROS = "OTROS"
}

/**
 * Configuración de Firebase
 */
object FirebaseConfig {
    /** Timeout para operaciones de Firestore en milisegundos */
    const val FIRESTORE_TIMEOUT_MS = 30000L
    
    /** Habilitar persistencia offline */
    const val ENABLE_PERSISTENCE = true
    
    /** Tamaño del cache de Firestore en MB */
    const val CACHE_SIZE_MB = 100L
}

