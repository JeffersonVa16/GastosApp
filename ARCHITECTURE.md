# 🏗️ Arquitectura del Proyecto - Gastos App

## Visión General

La aplicación sigue una arquitectura **MVVM (Model-View-ViewModel)** con **Repository Pattern** y utiliza **Jetpack Compose** para la interfaz de usuario. Esta arquitectura proporciona una clara separación de responsabilidades y facilita el mantenimiento y las pruebas.

## 📐 Diagrama de Arquitectura

```
┌─────────────────────────────────────────────────────┐
│                    UI Layer                         │
│  (Jetpack Compose Screens + Navigation)           │
│  - LoginScreen                                      │
│  - HomeScreen                                       │
│  - AddEditExpenseScreen                            │
└─────────────────────────────────────────────────────┘
                        ↕
┌─────────────────────────────────────────────────────┐
│                ViewModel Layer                      │
│  (State Management + Business Logic)               │
│  - AuthViewModel                                    │
│  - ExpenseViewModel                                │
└─────────────────────────────────────────────────────┘
                        ↕
┌─────────────────────────────────────────────────────┐
│              Repository Layer                       │
│  (Data Operations + API Abstraction)               │
│  - AuthRepository                                   │
│  - ExpenseRepository                               │
└─────────────────────────────────────────────────────┘
                        ↕
┌─────────────────────────────────────────────────────┐
│                Data Sources                         │
│  (Firebase Services + Models)                      │
│  - Firebase Authentication                          │
│  - Cloud Firestore                                 │
│  - Data Models (Expense, Category, UserProfile)   │
└─────────────────────────────────────────────────────┘
```

## 📂 Estructura de Capas

### 1. UI Layer (Capa de Interfaz de Usuario)

**Ubicación**: `ui/screens/` y `ui/navigation/`

**Responsabilidades:**
- Renderizar la interfaz de usuario con Jetpack Compose
- Observar cambios de estado desde los ViewModels
- Manejar interacciones del usuario
- Navegación entre pantallas

**Componentes:**
- **LoginScreen**: Pantalla de autenticación (email/password y Google)
- **HomeScreen**: Pantalla principal con lista de gastos y resumen
- **AddEditExpenseScreen**: Pantalla para agregar/editar gastos
- **Navigation**: Sistema de navegación con NavHost

**Características:**
- Componentes `@Composable` reutilizables
- Estado manejado con `State` y `StateFlow`
- UI reactiva que responde a cambios de datos
- Material Design 3

### 2. ViewModel Layer (Capa de Lógica de Presentación)

**Ubicación**: `viewmodel/`

**Responsabilidades:**
- Gestionar el estado de la UI
- Procesar lógica de negocio
- Comunicarse con la capa Repository
- Exponer datos a la UI mediante Flows y States

**Componentes:**

#### AuthViewModel
```kotlin
Estados:
- AuthState: Idle, Loading, Success, Error
- currentUser: UserProfile?

Funciones principales:
- loginWithEmail(email, password)
- registerWithEmail(email, password)
- beginGoogleSignIn()
- signInWithGoogle(intent)
- logout()
- isUserLoggedIn()
```

#### ExpenseViewModel
```kotlin
Estados:
- ExpenseState: Idle, Loading, Success, Error
- expenses: List<Expense>
- filter: ExpenseFilter
- totalExpenses: Double
- totalByCategory: Map<Category, Double>

Funciones principales:
- addExpense(name, amount, category, date)
- updateExpense(id, name, amount, category, date)
- deleteExpense(id)
- setFilter(filter)
- clearFilter()
- getCurrentMonthExpenses()
```

**Características:**
- Hereda de `ViewModel` para sobrevivir a cambios de configuración
- Usa Kotlin Coroutines para operaciones asíncronas
- Expone datos con `StateFlow` para observación reactiva
- Maneja errores y estados de carga

### 3. Repository Layer (Capa de Repositorio)

**Ubicación**: `data/repository/`

**Responsabilidades:**
- Abstraer las fuentes de datos de Firebase
- Proporcionar una API limpia para los ViewModels
- Manejar operaciones CRUD
- Transformar datos entre modelos de Firebase y de la app

**Componentes:**

#### AuthRepository
```kotlin
Servicios:
- FirebaseAuth: Autenticación
- SignInClient: Google One Tap Sign-In

Operaciones:
- getCurrentUser(): FirebaseUser?
- loginWithEmail(email, password): Result<UserProfile>
- registerWithEmail(email, password): Result<UserProfile>
- beginGoogleSignIn(): IntentSender?
- signInWithGoogle(intent): Result<UserProfile>
- logout()
```

#### ExpenseRepository
```kotlin
Servicios:
- FirebaseFirestore: Base de datos

Operaciones:
- addExpense(expense): Result<String>
- updateExpense(expense): Result<Unit>
- deleteExpense(id): Result<Unit>
- getUserExpenses(): Flow<List<Expense>>
- getExpensesByMonth(month, year): Flow<List<Expense>>
- getExpensesByCategory(category): Flow<List<Expense>>
- calculateTotal(expenses): Double
- calculateTotalByCategory(expenses): Map<Category, Double>
```

**Características:**
- Usa `Result` para manejo de errores tipo-seguro
- Emplea `Flow` para datos en tiempo real de Firestore
- Implementa patrones de transformación de datos
- Valida datos antes de enviarlos a Firebase

### 4. Data Layer (Capa de Datos)

**Ubicación**: `data/model/`

**Responsabilidades:**
- Definir estructuras de datos
- Modelar entidades del dominio
- Proporcionar funciones de conversión

**Modelos:**

#### Expense (Gasto)
```kotlin
data class Expense(
    val id: String,
    val userId: String,
    val name: String,
    val amount: Double,
    val category: Category,
    val date: Timestamp,
    val createdAt: Timestamp
)

Funciones:
- toMap(): Map<String, Any>
- fromMap(id, map): Expense
```

#### Category (Categoría)
```kotlin
enum class Category(val displayName: String, val emoji: String) {
    COMIDA, TRANSPORTE, ENTRETENIMIENTO,
    SALUD, EDUCACION, VIVIENDA,
    SERVICIOS, ROPA, TECNOLOGIA, OTROS
}
```

#### UserProfile (Perfil de Usuario)
```kotlin
data class UserProfile(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?
)
```

## 🔄 Flujo de Datos

### Flujo de Lectura (Firestore → UI)

```
1. Firestore emite cambios
        ↓
2. Repository escucha con Flow
        ↓
3. Repository transforma DocumentSnapshot → Expense
        ↓
4. ViewModel recibe Flow<List<Expense>>
        ↓
5. ViewModel actualiza StateFlow
        ↓
6. UI observa StateFlow y se recompone
```

### Flujo de Escritura (UI → Firestore)

```
1. Usuario interactúa con UI
        ↓
2. UI llama función del ViewModel
        ↓
3. ViewModel valida datos
        ↓
4. ViewModel llama Repository
        ↓
5. Repository transforma Expense → Map
        ↓
6. Repository guarda en Firestore
        ↓
7. Repository devuelve Result<T>
        ↓
8. ViewModel actualiza estado (Loading/Success/Error)
        ↓
9. UI reacciona al cambio de estado
```

## 🔐 Gestión de Autenticación

### Flujo de Login

```
┌─────────────────────────────────────────────┐
│ Usuario ingresa credenciales en LoginScreen│
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│ LoginScreen llama AuthViewModel.login()     │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│ AuthViewModel valida entrada                │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│ AuthViewModel llama AuthRepository.login()  │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│ AuthRepository comunica con Firebase Auth   │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│ Repository devuelve Result<UserProfile>     │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│ ViewModel actualiza authState y currentUser │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│ LoginScreen observa Success → navega a Home│
└─────────────────────────────────────────────┘
```

### Flujo de Google Sign-In

```
1. Usuario toca botón "Continuar con Google"
2. AuthViewModel.beginGoogleSignIn() obtiene IntentSender
3. LoginScreen lanza el ActivityResult
4. Google One Tap muestra selector de cuentas
5. Usuario selecciona cuenta
6. OnActivityResult recibe el Intent
7. AuthViewModel.signInWithGoogle(intent) procesa token
8. AuthRepository valida con Firebase
9. ViewModel actualiza estado
10. UI navega a Home
```

## 📊 Gestión de Estado

### Estados de Autenticación (AuthState)

```kotlin
sealed class AuthState {
    object Idle              // Estado inicial
    object Loading           // Procesando autenticación
    data class Success(...)  // Login exitoso
    data class Error(...)    // Error en login
}
```

### Estados de Gastos (ExpenseState)

```kotlin
sealed class ExpenseState {
    object Idle              // Sin operación
    object Loading           // Guardando/eliminando
    object Success           // Operación exitosa
    data class Error(...)    // Error en operación
}
```

## 🧪 Manejo de Errores

### Estrategia de Result Pattern

```kotlin
// En Repository
suspend fun addExpense(expense: Expense): Result<String> {
    return try {
        val id = firestore.add(expense).await().id
        Result.success(id)
    } catch (e: Exception) {
        Result.failure(e)
    }
}

// En ViewModel
viewModelScope.launch {
    val result = repository.addExpense(expense)
    result.fold(
        onSuccess = { id -> /* Éxito */ },
        onFailure = { error -> /* Error */ }
    )
}
```

## 🔄 Sincronización en Tiempo Real

### Listeners de Firestore

```kotlin
fun getUserExpenses(): Flow<List<Expense>> = callbackFlow {
    val listener = firestore
        .collection("expenses")
        .whereEqualTo("userId", currentUserId)
        .addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val expenses = snapshot?.mapToExpenses() ?: emptyList()
            trySend(expenses)
        }
    
    awaitClose { listener.remove() }
}
```

## 🎯 Principios Aplicados

### SOLID

- **Single Responsibility**: Cada clase tiene una responsabilidad única
- **Open/Closed**: Extensible mediante interfaces y clases selladas
- **Liskov Substitution**: Interfaces bien definidas
- **Interface Segregation**: Interfaces específicas por funcionalidad
- **Dependency Inversion**: Dependencias inyectadas (contexto)

### Clean Architecture

- Separación clara de capas
- Independencia de frameworks
- Testeable en cada capa
- Flujo de dependencias hacia adentro

### Android Best Practices

- ViewModel para gestión de estado
- StateFlow para datos reactivos
- Coroutines para operaciones asíncronas
- Compose para UI declarativa
- Navigation Component para navegación
- Repository Pattern para abstracción de datos

## 🔮 Posibles Mejoras Futuras

### Arquitectura

1. **Dependency Injection** (Hilt/Koin)
   - Inyectar ViewModels
   - Inyectar Repositories
   - Facilitar testing

2. **Use Cases / Interactors**
   - Extraer lógica de negocio compleja
   - Reutilizar operaciones comunes

3. **Domain Layer**
   - Separar modelos de datos de dominio
   - Mappers entre capas

### Funcionalidades

4. **Caching Local**
   - Room Database para offline-first
   - Sincronización bidireccional

5. **Estado UI más granular**
   - Estados específicos por operación
   - Mejor UX de loading

6. **Paginación**
   - Cargar gastos por lotes
   - Mejor rendimiento con muchos datos

7. **Testing**
   - Unit tests para ViewModels
   - Unit tests para Repositories
   - UI tests con Compose Test

## 📚 Recursos de Aprendizaje

- [Android Architecture Components](https://developer.android.com/topic/architecture)
- [Guide to app architecture](https://developer.android.com/topic/architecture)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Firebase for Android](https://firebase.google.com/docs/android/setup)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [StateFlow and SharedFlow](https://developer.android.com/kotlin/flow/stateflow-and-sharedflow)

---

Esta arquitectura proporciona una base sólida, escalable y mantenible para la aplicación de Control de Gastos. 🏗️✨

