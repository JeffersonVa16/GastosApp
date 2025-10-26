# ❓ Preguntas Frecuentes (FAQ)

## Índice

- [Instalación y Configuración](#instalación-y-configuración)
- [Autenticación](#autenticación)
- [Gastos](#gastos)
- [Filtros y Resumen](#filtros-y-resumen)
- [Problemas Técnicos](#problemas-técnicos)
- [Seguridad y Privacidad](#seguridad-y-privacidad)

---

## Instalación y Configuración

### ¿Qué necesito para ejecutar esta aplicación?

Necesitas:
- Android Studio (última versión)
- Un proyecto de Firebase configurado
- JDK 11 o superior
- Un dispositivo o emulador Android con API 24 (Android 7.0) o superior

### ¿Cómo obtengo el archivo google-services.json?

1. Ve a [Firebase Console](https://console.firebase.google.com/)
2. Selecciona tu proyecto
3. Ve a Configuración del proyecto (⚙️)
4. En la sección "Tus apps", encuentra tu app Android
5. Haz clic en "Descargar google-services.json"
6. Coloca el archivo en la carpeta `app/` de tu proyecto

### ¿Es obligatorio configurar Google Sign-In?

No, pero es altamente recomendado. Los usuarios pueden iniciar sesión con email/contraseña si no configuras Google Sign-In. Sin embargo, ofrecer ambas opciones mejora la experiencia del usuario.

### ¿Puedo usar mi propio dominio/servidor?

Esta aplicación usa Firebase como backend. Si quieres usar tu propio servidor, necesitarás:
1. Implementar una API REST personalizada
2. Modificar los repositorios para comunicarse con tu API
3. Implementar tu propio sistema de autenticación

---

## Autenticación

### ¿Puedo cambiar mi contraseña?

La funcionalidad de cambio de contraseña no está implementada en la versión actual. Para cambiar tu contraseña:
1. Usa el portal de Firebase Console > Authentication
2. O implementa la funcionalidad usando `FirebaseAuth.sendPasswordResetEmail()`

### ¿Cómo recupero mi contraseña si la olvido?

Actualmente no hay un "olvidé mi contraseña" en la app. Puedes implementarlo agregando:

```kotlin
// En AuthRepository
suspend fun resetPassword(email: String): Result<Unit> {
    return try {
        auth.sendPasswordResetEmail(email).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
```

### ¿Mis datos se guardan localmente?

No. Todos los datos se guardan en Firebase Firestore en la nube. Esto significa:
- ✅ Sincronización automática entre dispositivos
- ✅ Backup automático
- ❌ Requiere conexión a Internet
- ❌ Sin modo offline (por ahora)

### ¿Puedo usar la app sin conexión a Internet?

Actualmente no. La app requiere conexión a Internet para:
- Autenticarse
- Leer gastos
- Guardar nuevos gastos

Para implementar modo offline, necesitarías agregar Room Database y sincronización.

---

## Gastos

### ¿Cuántos gastos puedo registrar?

No hay límite técnico en la app, pero Firebase Firestore tiene límites en el plan gratuito:
- **Spark Plan (Gratis)**:
  - 50,000 lecturas/día
  - 20,000 escrituras/día
  - 20,000 eliminaciones/día
  - 1 GB de almacenamiento

Para la mayoría de usuarios, esto es más que suficiente.

### ¿Puedo agregar más categorías?

Sí. Para agregar categorías:

1. Abre `app/src/main/java/com/gastos/app/data/model/Category.kt`
2. Agrega nuevas categorías al enum:

```kotlin
enum class Category(val displayName: String, val emoji: String) {
    COMIDA("Comida", "🍔"),
    // ... categorías existentes ...
    MASCOTAS("Mascotas", "🐕"),  // Nueva categoría
    REGALO("Regalos", "🎁")       // Nueva categoría
}
```

### ¿Puedo editar un gasto después de crearlo?

Sí. Simplemente toca el gasto en la lista para editarlo. Puedes cambiar:
- Nombre del gasto
- Monto
- Categoría
- Fecha

### ¿Puedo adjuntar recibos o fotos a los gastos?

No, esta funcionalidad no está implementada. Para agregarla necesitarías:
1. Usar Firebase Storage para almacenar imágenes
2. Agregar un campo `imageUrl` al modelo `Expense`
3. Implementar la carga de imágenes en `AddEditExpenseScreen`

### ¿Puedo exportar mis gastos a Excel o PDF?

No está implementado, pero puedes agregarlo:

```kotlin
// Exportar a CSV
fun exportToCSV(expenses: List<Expense>): String {
    val header = "Fecha,Nombre,Categoría,Monto\n"
    val rows = expenses.joinToString("\n") { expense ->
        "${expense.date},${expense.name},${expense.category},${expense.amount}"
    }
    return header + rows
}
```

---

## Filtros y Resumen

### ¿Puedo ver gastos de meses anteriores?

Sí. Usa el botón de filtro (⚙️) y selecciona "Filtrar por mes/año". Por defecto, la app muestra el mes actual.

### ¿Cómo veo el total por categoría?

El `ExpenseViewModel` ya calcula esto en `totalByCategory`. Para mostrarlo en la UI, puedes agregar una sección en `HomeScreen`:

```kotlin
// Agregar después del Card de resumen total
LazyRow {
    items(totalByCategory.entries.toList()) { (category, total) ->
        CategoryCard(category = category, total = total)
    }
}
```

### ¿Puedo ver estadísticas o gráficos?

No está implementado, pero puedes agregar gráficos usando bibliotecas como:
- **MPAndroidChart**
- **Vico** (Compose Charts)
- **Compose Charts**

### ¿Los filtros se guardan?

No, los filtros son temporales. Si cierras la app, se restablecen. Para guardarlos permanentemente:
1. Usa DataStore para persistir preferencias
2. Guarda el filtro seleccionado
3. Restáuralo al iniciar

---

## Problemas Técnicos

### La app se bloquea al iniciar

**Posibles causas:**
1. `google-services.json` no está configurado correctamente
2. Firebase no está inicializado
3. Problema de conectividad

**Soluciones:**
- Verifica que `google-services.json` esté en `app/`
- Sincroniza el proyecto con Gradle
- Revisa los logs en Logcat

### "FirebaseApp is not initialized"

**Solución:**
1. Asegúrate de que `google-services.json` esté en la carpeta `app/`
2. Verifica que el plugin `google-services` esté en `build.gradle.kts`
3. Limpia y reconstruye: Build > Clean Project > Rebuild Project

### Google Sign-In da error 10

**Solución:**
1. Verifica el Web Client ID en `AuthRepository.kt`
2. Asegúrate de haber agregado el SHA-1 en Firebase Console
3. Espera 5-10 minutos después de configurar (propagación de cambios)
4. Verifica que el package name sea correcto

### Los gastos no se sincronizan

**Solución:**
1. Verifica tu conexión a Internet
2. Revisa las reglas de Firestore (pueden estar bloqueando las operaciones)
3. Asegúrate de que el usuario esté autenticado
4. Verifica que el campo `userId` se esté guardando correctamente

### La app es muy lenta

**Posibles causas:**
1. Muchos gastos en la base de datos
2. Consultas ineficientes
3. Imágenes pesadas (si las agregaste)

**Soluciones:**
- Implementa paginación para cargar gastos por lotes
- Usa índices compuestos en Firestore
- Optimiza las imágenes antes de subirlas

---

## Seguridad y Privacidad

### ¿Mis datos están seguros?

Sí, si has configurado las reglas de seguridad correctamente. Las reglas aseguran que:
- Solo usuarios autenticados pueden leer/escribir
- Cada usuario solo ve sus propios gastos
- Los datos están encriptados en tránsito y en reposo por Firebase

### ¿Quién puede ver mis gastos?

Solo tú. Las reglas de Firestore aseguran que:
```javascript
allow read: if request.auth.uid == resource.data.userId;
```

Esto significa que solo el propietario del gasto (usuario autenticado) puede leerlo.

### ¿Puedo eliminar mi cuenta?

La eliminación de cuenta no está implementada. Para agregarla:

```kotlin
// En AuthRepository
suspend fun deleteAccount(): Result<Unit> {
    return try {
        // 1. Eliminar todos los gastos del usuario
        // 2. Eliminar el usuario de Authentication
        auth.currentUser?.delete()?.await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
```

### ¿Firebase vende mis datos?

No. Firebase/Google no vende datos de usuarios. Lee la [Política de Privacidad de Firebase](https://firebase.google.com/support/privacy).

### ¿Puedo usar esta app en producción?

Sí, pero antes:
1. ✅ Configura reglas de seguridad adecuadas
2. ✅ Actualiza de Spark Plan (gratis) a Blaze Plan si esperas muchos usuarios
3. ✅ Agrega manejo de errores robusto
4. ✅ Implementa analytics para monitorear
5. ✅ Agrega pruebas automatizadas
6. ✅ Considera agregar modo offline

---

## Personalización

### ¿Puedo cambiar los colores de la app?

Sí. Edita los archivos en `ui/theme/`:
- `Color.kt`: Define los colores
- `Theme.kt`: Configura el tema claro/oscuro

### ¿Puedo cambiar el formato de moneda?

Sí. En `HomeScreen.kt` y `ExpenseItem`, busca:

```kotlin
NumberFormat.getCurrencyInstance(Locale("es", "ES"))
```

Cámbialo a tu locale:
```kotlin
NumberFormat.getCurrencyInstance(Locale("en", "US")) // Dólares
NumberFormat.getCurrencyInstance(Locale("es", "MX")) // Pesos mexicanos
```

### ¿Puedo traducir la app a otros idiomas?

Sí. Necesitas:
1. Crear carpetas `values-en`, `values-fr`, etc. en `res/`
2. Traducir `strings.xml` en cada carpeta
3. Reemplazar textos hardcoded en Compose con `stringResource(R.string.key)`

---

## Desarrollo y Contribución

### ¿Puedo contribuir al proyecto?

¡Sí! Algunas ideas para contribuir:
- Agregar modo oscuro
- Implementar estadísticas y gráficos
- Agregar soporte offline con Room
- Implementar exportación a PDF/Excel
- Agregar widget de inicio
- Mejorar la UI/UX
- Agregar tests unitarios e integración

### ¿Cómo reporto un bug?

Puedes:
1. Abrir un issue en GitHub (si el proyecto está en GitHub)
2. Revisar los logs en Logcat y documentar el error
3. Proporcionar pasos para reproducir el problema

### ¿Qué tecnologías adicionales puedo aprender de este proyecto?

- Kotlin Coroutines y Flow
- Jetpack Compose
- MVVM Architecture
- Firebase (Auth, Firestore)
- Material Design 3
- Navigation Component
- State Management
- Repository Pattern

---

## Recursos Adicionales

### Documentación
- [Firebase Docs](https://firebase.google.com/docs)
- [Jetpack Compose Docs](https://developer.android.com/jetpack/compose)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)

### Tutoriales
- [Firebase Authentication Codelab](https://firebase.google.com/codelabs)
- [Compose Pathway](https://developer.android.com/courses/pathways/compose)

### Comunidad
- [r/androiddev](https://reddit.com/r/androiddev)
- [Kotlin Slack](https://surveys.jetbrains.com/s3/kotlin-slack-sign-up)
- [Stack Overflow](https://stackoverflow.com/questions/tagged/android)

---

¿Tienes más preguntas? Consulta los archivos README.md, ARCHITECTURE.md y FIREBASE_SETUP.md para más información. 💡

