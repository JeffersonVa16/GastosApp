# ⚡ Guía de Inicio Rápido

¿Quieres probar la app rápidamente? Sigue estos 5 pasos esenciales.

## 🚀 5 Pasos para Ejecutar la App

### 1️⃣ Crear Proyecto en Firebase (5 minutos)

1. Ve a [Firebase Console](https://console.firebase.google.com/)
2. Clic en **"Agregar proyecto"**
3. Nombre: `Gastos App`
4. Clic en **"Crear proyecto"**

### 2️⃣ Habilitar Servicios de Firebase (5 minutos)

**Authentication:**
1. Menú lateral > **Authentication** > **"Comenzar"**
2. Habilita **"Email/Password"**
3. Habilita **"Google"** (copia el Web Client ID)

**Firestore:**
1. Menú lateral > **Firestore Database** > **"Crear base de datos"**
2. Selecciona **"Comenzar en modo de prueba"**
3. Elige tu ubicación
4. Clic en **"Habilitar"**

### 3️⃣ Registrar App Android (3 minutos)

1. Icono de Android en Firebase Console
2. Package name: `com.gastos.app`
3. Descargar **`google-services.json`**
4. Copiar archivo a carpeta `app/` del proyecto

### 4️⃣ Configurar Web Client ID (2 minutos)

1. Abre: `app/src/main/java/com/gastos/app/data/repository/AuthRepository.kt`
2. Línea 41: Busca `"TU_WEB_CLIENT_ID.apps.googleusercontent.com"`
3. Reemplaza con tu Web Client ID de Firebase
4. Guarda el archivo

```kotlin
// Antes:
.setServerClientId("TU_WEB_CLIENT_ID.apps.googleusercontent.com")

// Después:
.setServerClientId("123456789-abc123.apps.googleusercontent.com")
```

### 5️⃣ Ejecutar la App (2 minutos)

1. Abre el proyecto en Android Studio
2. **File** > **Sync Project with Gradle Files**
3. Conecta un dispositivo o inicia un emulador
4. Clic en **Run** ▶️

## ✅ Checklist Rápido

```
[ ] Proyecto Firebase creado
[ ] Authentication habilitado (Email + Google)
[ ] Firestore Database creado
[ ] google-services.json en carpeta app/
[ ] Web Client ID configurado en AuthRepository.kt
[ ] Proyecto sincronizado con Gradle
[ ] App ejecutándose en dispositivo/emulador
```

## 🎯 Primeros Pasos en la App

1. **Registrarse**: Crea cuenta con email y contraseña
2. **Agregar Gasto**: Toca el botón ➕
3. **Ver Resumen**: Observa el total mensual
4. **Filtrar**: Usa el icono ⚙️ para filtrar por categoría

## ⚠️ Problemas Comunes

### Google Sign-In no funciona

**Solución rápida:**
1. Verifica el Web Client ID
2. Espera 5-10 minutos (propagación de cambios)
3. Reinicia la app

### App no compila

**Solución:**
```
Build > Clean Project
Build > Rebuild Project
File > Invalidate Caches > Invalidate and Restart
```

### "FirebaseApp not initialized"

**Solución:**
- Verifica que `google-services.json` esté en `app/`
- Sincroniza Gradle
- Reconstruye el proyecto

## 📖 ¿Necesitas más ayuda?

- **Configuración detallada**: Lee [FIREBASE_SETUP.md](FIREBASE_SETUP.md)
- **Arquitectura del proyecto**: Lee [ARCHITECTURE.md](ARCHITECTURE.md)
- **Preguntas frecuentes**: Lee [FAQ.md](FAQ.md)
- **Documentación completa**: Lee [README.md](README.md)

## 🎨 Personalización Rápida

### Cambiar Colores

Edita `app/src/main/java/com/gastos/app/ui/theme/Color.kt`:

```kotlin
val Purple80 = Color(0xFFD0BCFF)  // Cambia a tu color
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)
```

### Agregar Categoría

Edita `app/src/main/java/com/gastos/app/data/model/Category.kt`:

```kotlin
enum class Category(val displayName: String, val emoji: String) {
    // ... categorías existentes ...
    MASCOTAS("Mascotas", "🐕"),  // Nueva
}
```

### Cambiar Moneda

En `HomeScreen.kt` y otros archivos, busca:

```kotlin
NumberFormat.getCurrencyInstance(Locale("es", "ES"))
```

Cambia a tu región:
```kotlin
Locale("en", "US")  // Dólares
Locale("es", "MX")  // Pesos MX
Locale("es", "AR")  // Pesos AR
```

## 🚀 Mejoras Recomendadas

Después de probar la app básica, considera implementar:

1. **Modo Oscuro** - Ya soportado por Material 3
2. **Gráficos** - Usa una librería de charts
3. **Exportar Datos** - CSV, PDF, Excel
4. **Modo Offline** - Room Database
5. **Widgets** - Widget de resumen en inicio
6. **Notificaciones** - Recordatorios de gastos
7. **Presupuestos** - Límites por categoría
8. **Multi-moneda** - Soporte para varias monedas

## 💡 Tips de Uso

- **Editar gasto**: Toca cualquier gasto en la lista
- **Eliminar gasto**: Icono de papelera en cada gasto
- **Cambiar mes**: Usa el filtro para ver meses anteriores
- **Total por categoría**: Ya calculado en `ExpenseViewModel.totalByCategory`

## 🎓 Aprender Más

Este proyecto es excelente para aprender:
- ✅ Kotlin avanzado
- ✅ Jetpack Compose
- ✅ Firebase (Auth + Firestore)
- ✅ MVVM Architecture
- ✅ Coroutines y Flow
- ✅ Material Design 3

## 🆘 Soporte

Si tienes problemas:
1. Revisa [FAQ.md](FAQ.md)
2. Revisa los logs en Logcat
3. Verifica la configuración de Firebase
4. Busca el error en Google/Stack Overflow

---

**¡Listo!** Ya puedes empezar a usar tu app de control de gastos. 💰✨

**Tiempo total de setup:** ~15-20 minutos

**Próximo paso:** Lee [README.md](README.md) para entender mejor la arquitectura y funcionalidades.

