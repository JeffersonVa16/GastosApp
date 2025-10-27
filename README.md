# 💰 Gastos App - Aplicación de Control de Gastos

Aplicación móvil Android desarrollada con Kotlin y Jetpack Compose que permite gestionar gastos personales con Firebase Authentication y Firestore.

## 🚀 Características

- ✅ Autenticación con email/contraseña y Google Sign-In
- ✅ Registro y gestión de gastos (nombre, monto, categoría, fecha)
- ✅ Historial completo de gastos
- ✅ Resumen mensual automático
- ✅ 10 categorías predefinidas con emojis
- ✅ Filtros por categoría y mes
- ✅ Interfaz moderna con Material Design 3
- ✅ Sincronización en tiempo real con Firebase

## 📋 Requisitos Previos

- Android Studio (versión más reciente)
- JDK 11 o superior
- Cuenta de Firebase
- SDK de Android 24 o superior

## 🔧 Configuración de Firebase

### 1. Crear Proyecto en Firebase

1. Ve a [Firebase Console](https://console.firebase.google.com/)
2. Haz clic en "Agregar proyecto"
3. Sigue los pasos para crear un nuevo proyecto

### 2. Configurar Firebase Authentication

1. En la consola de Firebase, ve a **Authentication**
2. Haz clic en "Comenzar"
3. Habilita los siguientes métodos de inicio de sesión:
   - **Correo electrónico/Contraseña**
   - **Google**

### 3. Configurar Firestore Database

1. En la consola de Firebase, ve a **Firestore Database**
2. Haz clic en "Crear base de datos"
3. Selecciona "Comenzar en modo de prueba" (o configura las reglas de seguridad según tus necesidades)

### 4. Configurar la App Android

1. En la consola de Firebase, ve a **Configuración del proyecto** (⚙️)
2. En la sección "Tus apps", haz clic en el icono de Android
3. Registra tu app con el nombre del paquete: `com.gastos.app`
4. Descarga el archivo `google-services.json`
5. **Reemplaza** el archivo `app/google-services.json` del proyecto con el archivo descargado

### 5. Configurar Google Sign-In

1. En Firebase Console, ve a **Authentication** > **Sign-in method** > **Google**
2. Copia el **Web client ID** (Client ID web)
3. Abre el archivo `app/src/main/java/com/gastos/app/data/repository/AuthRepository.kt`
4. Busca la línea que dice `"TU_WEB_CLIENT_ID.apps.googleusercontent.com"`
5. Reemplázalo con tu Web Client ID real

```kotlin
.setServerClientId("TU_WEB_CLIENT_ID_AQUÍ.apps.googleusercontent.com")
```

## 🛠️ Instalación y Ejecución

1. **Clona el repositorio** (o abre el proyecto en Android Studio)

2. **Asegúrate de haber completado la configuración de Firebase** (pasos anteriores)

3. **Sincroniza el proyecto con Gradle**:
   - En Android Studio, haz clic en "Sync Project with Gradle Files"

4. **Ejecuta la aplicación**:
   - Conecta un dispositivo Android o inicia un emulador
   - Haz clic en el botón "Run" (▶️)

## 📱 Uso de la Aplicación

### Inicio de Sesión
- **Registro**: Crea una cuenta con correo electrónico y contraseña (mínimo 6 caracteres)
- **Login**: Inicia sesión con tu cuenta o usa Google Sign-In

### Gestión de Gastos
- **Agregar Gasto**: Toca el botón flotante "+" en la pantalla principal
- **Editar Gasto**: Toca cualquier gasto en la lista para editarlo
- **Eliminar Gasto**: Toca el icono de papelera en cada gasto

### Filtros y Resumen
- **Ver Resumen Mensual**: Por defecto, la app muestra los gastos del mes actual y el total
- **Filtrar**: Toca el icono de filtro (⚙️) para filtrar por categoría o mes
- **Limpiar Filtros**: Usa el botón "Limpiar" cuando hay filtros activos

## 🗂️ Estructura del Proyecto

```
app/src/main/java/com/gastos/app/
├── data/
│   ├── model/              # Modelos de datos (Expense, Category, UserProfile)
│   └── repository/         # Repositorios (AuthRepository, ExpenseRepository)
├── viewmodel/              # ViewModels (AuthViewModel, ExpenseViewModel)
├── ui/
│   ├── screens/           # Pantallas Compose
│   │   ├── LoginScreen.kt
│   │   ├── HomeScreen.kt
│   │   └── AddEditExpenseScreen.kt
│   ├── navigation/        # Navegación
│   └── theme/             # Temas y estilos
└── MainActivity.kt        # Actividad principal
```

## 🎨 Categorías de Gastos

La app incluye 10 categorías predefinidas:

- 🍔 Comida
- 🚗 Transporte
- 🎬 Entretenimiento
- 🏥 Salud
- 📚 Educación
- 🏠 Vivienda
- 💡 Servicios
- 👕 Ropa
- 💻 Tecnología
- 📦 Otros

## 🔒 Seguridad

### Reglas de Firestore Recomendadas

Configura las siguientes reglas en Firebase Firestore para mayor seguridad:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /expenses/{expense} {
      // Solo el usuario propietario puede leer y escribir sus gastos
      allow read, write: if request.auth != null && 
                           request.auth.uid == resource.data.userId;
      allow create: if request.auth != null;
    }
  }
}
```

## 📚 Tecnologías Utilizadas

- **Kotlin** - Lenguaje de programación
- **Jetpack Compose** - UI moderna y declarativa
- **Firebase Authentication** - Autenticación de usuarios
- **Firebase Firestore** - Base de datos en tiempo real
- **Material Design 3** - Diseño de interfaz
- **Navigation Compose** - Navegación entre pantallas
- **Coroutines & Flow** - Programación asíncrona

## ⚠️ Notas Importantes

1. **Archivo google-services.json**: Es crucial reemplazar el archivo placeholder con tu archivo real de Firebase
2. **Web Client ID**: Necesario para que funcione Google Sign-In
3. **Reglas de Firestore**: Configura reglas de seguridad antes de producción
4. **Conexión a Internet**: La app requiere conexión a Internet para funcionar

## 🐛 Solución de Problemas

### Google Sign-In no funciona
- Verifica que hayas configurado correctamente el Web Client ID
- Asegúrate de que la huella digital SHA-1 esté registrada en Firebase Console

### No se sincronizan los datos
- Verifica tu conexión a Internet
- Revisa las reglas de seguridad de Firestore
- Asegúrate de que el usuario esté autenticado

### Error al compilar
- Sincroniza el proyecto con Gradle
- Limpia y reconstruye el proyecto: Build > Clean Project > Rebuild Project

## 📄 Licencia

Este proyecto fue desarrollado como material educativo para aprender Firebase y Jetpack Compose.

## 👨‍💻 Autor

Desarrollado como proyecto de aprendizaje de Android, Kotlin y Firebase.

---

¡Gracias por usar Gastos App! 💰✨

