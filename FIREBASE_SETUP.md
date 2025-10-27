# 🔥 Guía Detallada de Configuración de Firebase

Esta guía te llevará paso a paso por el proceso de configuración de Firebase para la aplicación de Control de Gastos.

## 📋 Tabla de Contenidos

1. [Crear Proyecto en Firebase](#1-crear-proyecto-en-firebase)
2. [Configurar Authentication](#2-configurar-authentication)
3. [Configurar Firestore Database](#3-configurar-firestore-database)
4. [Registrar la App Android](#4-registrar-la-app-android)
5. [Configurar Google Sign-In](#5-configurar-google-sign-in)
6. [Configurar Reglas de Seguridad](#6-configurar-reglas-de-seguridad)
7. [Verificación Final](#7-verificación-final)

---

## 1. Crear Proyecto en Firebase

### Paso 1.1: Acceder a Firebase Console

1. Ve a [Firebase Console](https://console.firebase.google.com/)
2. Inicia sesión con tu cuenta de Google

### Paso 1.2: Crear Nuevo Proyecto

1. Haz clic en **"Agregar proyecto"** o **"Add project"**
2. Ingresa el nombre del proyecto: `Gastos App` (o el nombre que prefieras)
3. Haz clic en **"Continuar"**
4. (Opcional) Desactiva Google Analytics si no lo necesitas
5. Haz clic en **"Crear proyecto"**
6. Espera a que se complete la configuración (puede tomar 1-2 minutos)
7. Haz clic en **"Continuar"** cuando esté listo

---

## 2. Configurar Authentication

### Paso 2.1: Habilitar Authentication

1. En el menú lateral izquierdo, busca y haz clic en **"Authentication"** (Autenticación)
2. Haz clic en **"Get started"** o **"Comenzar"**

### Paso 2.2: Habilitar Email/Password

1. En la pestaña **"Sign-in method"** (Método de inicio de sesión)
2. Busca **"Email/Password"** (Correo electrónico/Contraseña)
3. Haz clic en el nombre
4. Activa el interruptor **"Enable"** (Habilitar)
5. Haz clic en **"Save"** (Guardar)

### Paso 2.3: Habilitar Google Sign-In

1. En la misma pestaña **"Sign-in method"**
2. Busca **"Google"**
3. Haz clic en el nombre
4. Activa el interruptor **"Enable"** (Habilitar)
5. Ingresa un correo electrónico de soporte del proyecto (tu correo de Gmail)
6. Haz clic en **"Save"** (Guardar)
7. **IMPORTANTE**: Copia el **"Web client ID"** que aparece (lo necesitarás más adelante)

---

## 3. Configurar Firestore Database

### Paso 3.1: Crear Firestore Database

1. En el menú lateral izquierdo, busca **"Firestore Database"**
2. Haz clic en **"Create database"** (Crear base de datos)

### Paso 3.2: Configurar el Modo de Seguridad

1. Selecciona **"Start in test mode"** (Comenzar en modo de prueba)
   - Esto permite acceso de lectura/escritura sin autenticación por 30 días
   - Más adelante cambiaremos esto por reglas de seguridad adecuadas
2. Haz clic en **"Next"** (Siguiente)

### Paso 3.3: Seleccionar Ubicación

1. Selecciona la ubicación del servidor más cercana a ti o a tus usuarios
   - Para España/Europa: `europe-west1` o `europe-west3`
   - Para América: `us-central1` o `southamerica-east1`
2. Haz clic en **"Enable"** (Habilitar)
3. Espera a que se complete la creación (puede tomar 1-2 minutos)

---

## 4. Registrar la App Android

### Paso 4.1: Agregar App Android al Proyecto

1. En la página principal de Firebase Console, busca el icono de Android
2. Haz clic en el icono de Android (🤖) o en **"Add app"** > **"Android"**

### Paso 4.2: Configurar Detalles de la App

1. **Android package name**: Ingresa `com.gastos.app`
   - ⚠️ Este debe coincidir exactamente con el package name del proyecto
2. **App nickname** (opcional): `Gastos App`
3. **Debug signing certificate SHA-1** (opcional, pero recomendado para Google Sign-In):
   
   Para obtener el SHA-1:
   
   **En Windows (PowerShell):**
   ```powershell
   keytool -list -v -keystore "$env:USERPROFILE\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android
   ```
   
   **En macOS/Linux:**
   ```bash
   keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
   ```
   
   Copia el valor de **SHA1** que aparece
   
4. Haz clic en **"Register app"** (Registrar app)

### Paso 4.3: Descargar google-services.json

1. Haz clic en **"Download google-services.json"**
2. Guarda el archivo en tu computadora
3. **MUY IMPORTANTE**: Copia este archivo a la carpeta `app/` de tu proyecto Android
   - Ruta completa: `GastosApp/app/google-services.json`
   - **REEMPLAZA** el archivo placeholder existente
4. Haz clic en **"Next"** (Siguiente)
5. Haz clic en **"Continue to console"** (Continuar a la consola)

---

## 5. Configurar Google Sign-In

### Paso 5.1: Obtener Web Client ID

1. Ve a **"Project settings"** (Configuración del proyecto) - icono ⚙️ en el menú lateral
2. Desplázate hasta la sección **"Your apps"** (Tus apps)
3. Busca **"Web client"** o **"SDK Configuration"**
4. Copia el **"Web client ID"** (algo como: `123456789-abcdefg.apps.googleusercontent.com`)

### Paso 5.2: Actualizar el Código de la App

1. Abre el archivo: `app/src/main/java/com/gastos/app/data/repository/AuthRepository.kt`
2. Busca la línea 41 (aproximadamente) que dice:
   ```kotlin
   .setServerClientId("TU_WEB_CLIENT_ID.apps.googleusercontent.com")
   ```
3. Reemplaza `TU_WEB_CLIENT_ID.apps.googleusercontent.com` con tu Web Client ID real
4. Guarda el archivo

**Ejemplo:**
```kotlin
// Antes:
.setServerClientId("TU_WEB_CLIENT_ID.apps.googleusercontent.com")

// Después (con tu ID real):
.setServerClientId("123456789-abcdefg.apps.googleusercontent.com")
```

---

## 6. Configurar Reglas de Seguridad

### Paso 6.1: Actualizar Reglas de Firestore

1. Ve a **"Firestore Database"** en el menú lateral
2. Haz clic en la pestaña **"Rules"** (Reglas)
3. Reemplaza todo el contenido con el siguiente código:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    
    // Reglas para la colección de gastos
    match /expenses/{expense} {
      // Permitir lectura solo si el usuario está autenticado y es el propietario
      allow read: if request.auth != null && 
                     resource.data.userId == request.auth.uid;
      
      // Permitir creación solo si el usuario está autenticado
      allow create: if request.auth != null && 
                       request.resource.data.userId == request.auth.uid &&
                       request.resource.data.keys().hasAll(['userId', 'name', 'amount', 'category', 'date', 'createdAt']) &&
                       request.resource.data.name is string &&
                       request.resource.data.amount is number &&
                       request.resource.data.amount > 0 &&
                       request.resource.data.category is string &&
                       request.resource.data.date is timestamp &&
                       request.resource.data.createdAt is timestamp;
      
      // Permitir actualización solo si el usuario está autenticado y es el propietario
      allow update: if request.auth != null && 
                       resource.data.userId == request.auth.uid &&
                       request.resource.data.userId == request.auth.uid;
      
      // Permitir eliminación solo si el usuario está autenticado y es el propietario
      allow delete: if request.auth != null && 
                       resource.data.userId == request.auth.uid;
    }
  }
}
```

4. Haz clic en **"Publish"** (Publicar)

### Paso 6.2: Verificar Reglas

1. Haz clic en la pestaña **"Rules playground"** (Simulador de reglas) si está disponible
2. Prueba diferentes escenarios para asegurarte de que las reglas funcionan correctamente

---

## 7. Verificación Final

### ✅ Checklist de Configuración

Marca cada elemento cuando lo hayas completado:

- [ ] Proyecto de Firebase creado
- [ ] Authentication habilitado (Email y Google)
- [ ] Firestore Database creado
- [ ] App Android registrada en Firebase
- [ ] Archivo `google-services.json` descargado y colocado en `app/`
- [ ] Web Client ID copiado y actualizado en `AuthRepository.kt`
- [ ] SHA-1 agregado a Firebase (para Google Sign-In)
- [ ] Reglas de seguridad de Firestore configuradas
- [ ] Proyecto sincronizado con Gradle en Android Studio

### 🧪 Probar la Configuración

1. Abre el proyecto en Android Studio
2. Sincroniza el proyecto con Gradle: **File** > **Sync Project with Gradle Files**
3. Ejecuta la aplicación en un emulador o dispositivo real
4. Intenta registrarte con un correo electrónico
5. Intenta iniciar sesión con Google
6. Agrega algunos gastos de prueba
7. Verifica que los datos aparezcan en Firestore Console

---

## 🆘 Solución de Problemas Comunes

### Error: "Default FirebaseApp is not initialized"

**Solución:**
- Verifica que `google-services.json` esté en la carpeta `app/`
- Sincroniza el proyecto con Gradle
- Limpia y reconstruye el proyecto

### Google Sign-In no funciona / Error 10

**Solución:**
1. Verifica que el Web Client ID esté correctamente configurado en `AuthRepository.kt`
2. Asegúrate de haber agregado el SHA-1 en Firebase Console
3. Verifica que el package name sea exactamente `com.gastos.app`
4. Espera unos minutos después de configurar (los cambios pueden tardar en propagarse)

### "Permission denied" al leer/escribir en Firestore

**Solución:**
- Verifica que las reglas de seguridad estén correctamente configuradas
- Asegúrate de que el usuario esté autenticado antes de intentar leer/escribir datos
- Verifica que el campo `userId` se esté guardando correctamente en los documentos

### No aparecen los datos en la app

**Solución:**
- Verifica tu conexión a Internet
- Revisa los logs de Android Studio (Logcat) para ver errores
- Verifica que el usuario esté autenticado
- Comprueba que los datos existan en Firestore Console

---

## 📚 Recursos Adicionales

- [Documentación oficial de Firebase](https://firebase.google.com/docs)
- [Firebase Authentication Docs](https://firebase.google.com/docs/auth)
- [Cloud Firestore Docs](https://firebase.google.com/docs/firestore)
- [Guía de reglas de seguridad de Firestore](https://firebase.google.com/docs/firestore/security/get-started)

---

## 🎉 ¡Configuración Completada!

Si has seguido todos los pasos, tu aplicación de Control de Gastos debería estar completamente configurada y funcional con Firebase.

**Próximos pasos:**
1. Personaliza la interfaz según tus preferencias
2. Agrega más categorías de gastos si lo deseas
3. Implementa funcionalidades adicionales como estadísticas o gráficos
4. Prepara la app para producción actualizando las reglas de seguridad

¡Disfruta de tu app de control de gastos! 💰✨

