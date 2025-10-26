# ✅ Lista de Verificación - Gastos App

Usa esta lista para asegurarte de que todo está configurado correctamente.

---

## 📋 Antes de Empezar

### Requisitos del Sistema
- [ ] Android Studio instalado (versión más reciente)
- [ ] JDK 11 o superior instalado
- [ ] Cuenta de Google/Gmail activa
- [ ] Conexión a Internet estable

---

## 🔥 Configuración de Firebase

### Crear Proyecto
- [ ] He creado un proyecto en [Firebase Console](https://console.firebase.google.com/)
- [ ] El proyecto tiene un nombre apropiado
- [ ] He aceptado los términos y condiciones

### Authentication
- [ ] He habilitado **Email/Password** en Authentication
- [ ] He habilitado **Google** en Authentication
- [ ] He copiado el **Web Client ID** de Google Sign-In
- [ ] He guardado el Web Client ID en un lugar seguro

### Firestore Database
- [ ] He creado una base de datos de Firestore
- [ ] He seleccionado una ubicación (región)
- [ ] He configurado en "modo de prueba" inicialmente
- [ ] He actualizado las reglas de seguridad con las reglas del archivo `firestore.rules`

### Registrar App Android
- [ ] He registrado mi app Android en Firebase
- [ ] He usado el package name correcto: `com.gastos.app`
- [ ] He descargado `google-services.json`
- [ ] He copiado `google-services.json` a la carpeta `app/` del proyecto
- [ ] He obtenido el SHA-1 de debug
- [ ] He agregado el SHA-1 en Firebase Console

---

## 💻 Configuración del Proyecto

### Archivos de Configuración
- [ ] El archivo `google-services.json` está en `app/`
- [ ] He abierto `AuthRepository.kt`
- [ ] He reemplazado `TU_WEB_CLIENT_ID.apps.googleusercontent.com` con mi Web Client ID real
- [ ] He guardado todos los cambios

### Sincronización
- [ ] He abierto el proyecto en Android Studio
- [ ] He hecho clic en "Sync Project with Gradle Files"
- [ ] La sincronización completó sin errores
- [ ] No hay errores de compilación visibles

### Build del Proyecto
- [ ] He hecho "Clean Project" (Build > Clean Project)
- [ ] He hecho "Rebuild Project" (Build > Rebuild Project)
- [ ] El build completó exitosamente
- [ ] No hay errores en la pestaña "Build"

---

## 📱 Pruebas de la Aplicación

### Preparación del Dispositivo
- [ ] He conectado un dispositivo físico O iniciado un emulador
- [ ] El dispositivo tiene Android 7.0 (API 24) o superior
- [ ] El dispositivo tiene conexión a Internet
- [ ] Los Play Services están actualizados (para Google Sign-In)

### Ejecutar la App
- [ ] He hecho clic en el botón "Run" (▶️)
- [ ] La app se instaló correctamente
- [ ] La app se abrió sin crashes
- [ ] Veo la pantalla de Login

---

## 🧪 Pruebas Funcionales

### Autenticación - Email/Password
- [ ] Puedo hacer clic en "Registrarse"
- [ ] Puedo ingresar un email y contraseña
- [ ] El botón "Registrarse" funciona
- [ ] Se crea la cuenta exitosamente
- [ ] Navego a la pantalla principal (Home)
- [ ] Puedo cerrar sesión
- [ ] Puedo volver a iniciar sesión con las mismas credenciales

### Autenticación - Google Sign-In
- [ ] Veo el botón "Continuar con Google"
- [ ] Al hacer clic, se abre el selector de cuentas de Google
- [ ] Puedo seleccionar mi cuenta de Google
- [ ] Se completa el login exitosamente
- [ ] Navego a la pantalla principal
- [ ] Veo mi email en la barra superior

### Gestión de Gastos
- [ ] Veo el botón flotante "+" en la pantalla principal
- [ ] Al hacer clic, voy a la pantalla de agregar gasto
- [ ] Puedo ingresar un nombre de gasto
- [ ] Puedo ingresar un monto
- [ ] Puedo seleccionar una categoría
- [ ] Puedo seleccionar una fecha con el DatePicker
- [ ] Al hacer clic en "Guardar", el gasto se guarda
- [ ] Vuelvo a la pantalla principal automáticamente
- [ ] Veo mi gasto en la lista
- [ ] El total mensual se actualiza correctamente

### Edición de Gastos
- [ ] Puedo hacer clic en un gasto de la lista
- [ ] Se abre la pantalla de edición con los datos pre-llenados
- [ ] Puedo modificar el nombre
- [ ] Puedo modificar el monto
- [ ] Puedo cambiar la categoría
- [ ] Puedo cambiar la fecha
- [ ] Al hacer clic en "Actualizar", los cambios se guardan
- [ ] Vuelvo a la lista y veo los cambios reflejados

### Eliminación de Gastos
- [ ] Veo el icono de papelera en cada gasto
- [ ] Al hacer clic, aparece un diálogo de confirmación
- [ ] Puedo cancelar la eliminación
- [ ] Puedo confirmar la eliminación
- [ ] El gasto desaparece de la lista
- [ ] El total mensual se actualiza

### Filtros
- [ ] Veo el icono de filtro (⚙️) en la barra superior
- [ ] Al hacer clic, se abre el diálogo de filtros
- [ ] Puedo filtrar por categoría
- [ ] Puedo filtrar por mes/año
- [ ] Al aplicar filtros, la lista se actualiza
- [ ] Veo los filtros activos debajo del resumen
- [ ] Puedo limpiar los filtros
- [ ] La lista vuelve a mostrar todos los gastos

---

## 🔍 Verificación en Firebase Console

### Authentication
- [ ] Veo mi usuario en Authentication > Users
- [ ] El método de autenticación es correcto (Email o Google)
- [ ] El estado del usuario es "Enabled"

### Firestore Database
- [ ] Veo la colección "expenses" en Firestore
- [ ] Cada documento de gasto tiene los campos correctos:
  - [ ] userId
  - [ ] name
  - [ ] amount
  - [ ] category
  - [ ] date
  - [ ] createdAt
- [ ] El userId coincide con mi UID de usuario
- [ ] Los datos son correctos

### Reglas de Seguridad
- [ ] Las reglas de Firestore están configuradas
- [ ] Las reglas protegen los datos por usuario
- [ ] Puedo leer solo mis gastos
- [ ] No puedo leer gastos de otros usuarios

---

## 📚 Documentación

### He Leído
- [ ] README.md - Documentación general
- [ ] QUICK_START.md - Guía de inicio rápido
- [ ] FIREBASE_SETUP.md - Configuración de Firebase
- [ ] ARCHITECTURE.md - Arquitectura del proyecto
- [ ] FAQ.md - Preguntas frecuentes
- [ ] RESUMEN_PROYECTO.md - Resumen del proyecto

### Comprendo
- [ ] Cómo funciona la arquitectura MVVM
- [ ] Cómo se comunica la app con Firebase
- [ ] Cómo agregar nuevas categorías
- [ ] Cómo personalizar colores y temas
- [ ] Cómo solucionar problemas comunes

---

## 🎨 Personalización (Opcional)

### Colores
- [ ] He revisado los colores en `ui/theme/Color.kt`
- [ ] He personalizado los colores según mis preferencias
- [ ] He probado los cambios en la app

### Categorías
- [ ] He revisado las categorías en `data/model/Category.kt`
- [ ] He agregado/modificado categorías según mis necesidades
- [ ] Las nuevas categorías aparecen en la app

### Moneda
- [ ] He verificado el formato de moneda en `HomeScreen.kt`
- [ ] He cambiado el locale si es necesario
- [ ] La moneda se muestra correctamente

### Strings
- [ ] He revisado `res/values/strings.xml`
- [ ] He personalizado los textos si es necesario

---

## 🐛 Resolución de Problemas

Si encuentras problemas, verifica:

### Google Sign-In no funciona
- [ ] He verificado el Web Client ID en `AuthRepository.kt`
- [ ] He agregado el SHA-1 en Firebase Console
- [ ] He esperado 10 minutos después de la configuración
- [ ] He reiniciado la app

### No se sincronizan los datos
- [ ] Tengo conexión a Internet
- [ ] El usuario está autenticado
- [ ] Las reglas de Firestore están correctas
- [ ] He verificado los logs en Logcat

### Errores de compilación
- [ ] He sincronizado con Gradle
- [ ] He limpiado el proyecto
- [ ] He reconstruido el proyecto
- [ ] He invalidado caché (File > Invalidate Caches)

---

## 🚀 Próximos Pasos

### Después de Verificar Todo
- [ ] He probado todas las funcionalidades
- [ ] La app funciona correctamente
- [ ] Estoy satisfecho con el resultado
- [ ] He personalizado según mis necesidades

### Mejoras Futuras
- [ ] He leído la sección de mejoras en README.md
- [ ] He identificado qué funcionalidades quiero agregar
- [ ] He planificado mi roadmap de desarrollo

---

## ✅ Proyecto Listo

Cuando hayas marcado todos los elementos anteriores, ¡tu aplicación estará completamente funcional y lista para usar!

**Fecha de verificación:** __________________

**Notas adicionales:**
```
_______________________________________________________________
_______________________________________________________________
_______________________________________________________________
```

---

## 🎉 ¡Felicitaciones!

Has configurado exitosamente tu aplicación de Control de Gastos.

### Recursos Adicionales
- [Firebase Documentation](https://firebase.google.com/docs)
- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)

### Comunidad
- [r/androiddev](https://reddit.com/r/androiddev)
- [Stack Overflow - Android](https://stackoverflow.com/questions/tagged/android)
- [Android Developers](https://developer.android.com/)

---

**¿Tienes alguna pregunta?** Consulta [FAQ.md](FAQ.md)

**¿Encontraste un bug?** Revisa los logs en Logcat y consulta la sección de Troubleshooting en README.md

---

¡Disfruta de tu app! 💰✨

