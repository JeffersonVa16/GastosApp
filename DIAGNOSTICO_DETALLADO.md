# 🔍 DIAGNÓSTICO DETALLADO - Problema de Carga Infinita

## 📋 **Estado Actual**

El botón de guardar se queda en estado de carga y no avanza ni guarda el gasto.

---

## ✅ **Cambios Implementados**

### 1. **Logs Agregados**

He agregado logs detallados en:
- ✅ `ExpenseViewModel.kt` - Para ver el flujo completo
- ✅ `ExpenseRepository.kt` - Para ver llamadas a Firebase
- ✅ `AddEditExpenseScreen.kt` - Toast con mensajes de error

### 2. **Mensajes Visibles**

Ahora verás:
- ✅ **Toast de éxito:** "✅ Gasto guardado exitosamente"
- ✅ **Toast de error:** "❌ Error: [mensaje]"
- ✅ **Card roja** con errores de validación

---

## 🚀 **PASOS PARA DIAGNOSTICAR**

### **Paso 1: Ver los Logs en Logcat** (IMPORTANTE)

1. **Abre Android Studio**

2. **Ve a la pestaña "Logcat"** (abajo)

3. **Configura los filtros:**
   - Haz clic en el dropdown que dice "Show only selected application"
   - Verifica que esté seleccionado: `com.gastos.app`

4. **Agrega un filtro de etiqueta:**
   - En el campo de búsqueda, escribe: `ExpenseViewModel|ExpenseRepository`
   - O filtra por: `tag:ExpenseViewModel|ExpenseRepository`

5. **Ejecuta la app y trata de guardar un gasto**

6. **Busca estos mensajes en orden:**

```
✅ Lo que DEBERÍAS ver si todo está bien:
D/ExpenseViewModel: addExpense llamado - name: Comida, amount: 50
D/ExpenseViewModel: Cambiando estado a Loading
D/ExpenseViewModel: Llamando a repository.addExpense
D/ExpenseRepository: addExpense - userId: [tu-user-id]
D/ExpenseRepository: Guardando gasto: {userId=..., name=Comida, amount=50.0, ...}
D/ExpenseRepository: Gasto guardado con ID: [document-id]
D/ExpenseViewModel: Gasto agregado exitosamente con ID: [document-id]
```

```
❌ Si ves este error - Usuario no autenticado:
E/ExpenseRepository: Usuario no autenticado

SOLUCIÓN: Cierra sesión y vuelve a iniciar sesión
```

```
❌ Si ves este error - Permission denied:
E/ExpenseRepository: Error al agregar gasto
  com.google.firebase.firestore.FirebaseFirestoreException: PERMISSION_DENIED

SOLUCIÓN: Las reglas de Firestore están bloqueando. Usa firestore.rules.testing
```

```
❌ Si ves este error - FAILED_PRECONDITION:
E/ExpenseRepository: Error al agregar gasto
  com.google.firebase.firestore.FirebaseFirestoreException: FAILED_PRECONDITION: The query requires an index

SOLUCIÓN: Firebase necesita crear un índice. El log te dará un link, ábrelo.
```

```
❌ Si NO ves ningún mensaje:
- El código no se está ejecutando
- Verifica que hayas sincronizado Gradle
- Reinstala la app
```

---

### **Paso 2: Verificar Firebase Console**

1. **Ve a:** https://console.firebase.google.com

2. **Selecciona tu proyecto:** `gastosapp-74686`

3. **Verifica Authentication:**
   ```
   - Ve a: Authentication → Users
   - Deberías ver tu usuario registrado
   - Copia el "User UID"
   ```

4. **Verifica Firestore:**
   ```
   - Ve a: Firestore Database
   - Deberías ver la colección "expenses" (puede estar vacía)
   - Si NO existe, Firebase la creará al guardar el primer gasto
   ```

5. **Verifica Reglas:**
   ```
   - Ve a: Firestore Database → Rules
   - Deberías ver las reglas del archivo firestore.rules
   ```

---

### **Paso 3: Prueba con Reglas Permisivas** ⚠️

**IMPORTANTE:** Esto es SOLO para diagnosticar. NO uses esto en producción.

1. **Ve a Firebase Console → Firestore Database → Rules**

2. **Reemplaza TODO el contenido con esto:**

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

3. **Haz clic en "Publish" (Publicar)**

4. **Intenta guardar un gasto en la app**

**Resultados:**

✅ **Si FUNCIONA:**
- El problema eran las reglas de Firestore
- Usa las reglas de `firestore.rules` (las originales) pero actualízalas en Firebase Console
- Las reglas originales son más seguras

❌ **Si NO FUNCIONA:**
- El problema NO son las reglas
- Continúa al Paso 4

---

### **Paso 4: Verificar Conectividad**

1. **Verifica que el dispositivo tenga Internet:**
   - Abre un navegador en el emulador/dispositivo
   - Ve a google.com
   - Debería cargar

2. **Verifica la configuración de Firebase:**
   - El archivo `app/google-services.json` debe existir
   - Debe tener el `project_id`: "gastosapp-74686"

3. **Sincroniza Gradle:**
   ```
   File → Sync Project with Gradle Files
   ```

4. **Limpia y reconstruye:**
   ```
   Build → Clean Project
   Build → Rebuild Project
   ```

---

### **Paso 5: Prueba Manual en Firebase Console**

Vamos a crear un documento manualmente para ver si Firebase funciona:

1. **Ve a Firebase Console → Firestore Database**

2. **Haz clic en "Start Collection" o "Add Collection"**

3. **Ingresa:**
   ```
   Collection ID: expenses
   ```

4. **Agrega un documento con estos campos:**
   ```
   Field         Type        Value
   ----------------------------------------
   userId        string      [tu-user-id del Paso 2]
   name          string      Test Manual
   amount        number      100
   category      string      COMIDA
   date          timestamp   [fecha actual]
   notes         string      Prueba
   createdAt     timestamp   [fecha actual]
   updatedAt     timestamp   [fecha actual]
   ```

5. **Guarda el documento**

6. **Ve a la app y verifica:**
   - ¿Aparece el gasto en la lista?
   - Si SÍ aparece: Firebase está funcionando, el problema es al CREAR
   - Si NO aparece: Problema de lectura, verifica autenticación

---

## 🐛 **ERRORES COMUNES Y SOLUCIONES**

### **Error 1: Usuario no autenticado**

**Síntoma en Logcat:**
```
E/ExpenseRepository: Usuario no autenticado
```

**Causa:** No hay sesión activa de Firebase Auth

**Solución:**
```
1. En la app, toca el menú (⋮)
2. Cierra sesión
3. Vuelve a iniciar sesión con tu email y contraseña
4. Intenta guardar de nuevo
```

---

### **Error 2: PERMISSION_DENIED**

**Síntoma en Logcat:**
```
E/ExpenseRepository: Error al agregar gasto
  com.google.firebase.firestore.FirebaseFirestoreException: PERMISSION_DENIED
```

**Causa:** Las reglas de Firestore están bloqueando la escritura

**Solución:**
```
1. Usa las reglas del Paso 3 temporalmente
2. Si funciona, actualiza las reglas de firestore.rules en Firebase Console
3. Las reglas correctas están en el archivo firestore.rules del proyecto
```

**Reglas correctas a usar:**
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /expenses/{expense} {
      allow read: if request.auth != null &&
                     resource.data.userId == request.auth.uid;
      
      allow create: if request.auth != null &&
                       request.resource.data.userId == request.auth.uid &&
                       request.resource.data.keys().hasAll(['userId', 'name', 'amount', 'category', 'date', 'createdAt']) &&
                       request.resource.data.name is string &&
                       request.resource.data.amount is number &&
                       request.resource.data.amount > 0 &&
                       request.resource.data.category is string &&
                       request.resource.data.date is timestamp &&
                       request.resource.data.createdAt is timestamp;
      
      allow update: if request.auth != null &&
                       resource.data.userId == request.auth.uid &&
                       request.resource.data.userId == request.auth.uid;
      
      allow delete: if request.auth != null &&
                       resource.data.userId == request.auth.uid;
    }
  }
}
```

---

### **Error 3: FAILED_PRECONDITION (Índice faltante)**

**Síntoma en Logcat:**
```
E/ExpenseRepository: Error al agregar gasto
  com.google.firebase.firestore.FirebaseFirestoreException: FAILED_PRECONDITION: 
  The query requires an index. You can create it here: https://...
```

**Causa:** Firebase necesita crear un índice compuesto

**Solución:**
```
1. Copia el link que aparece en el error
2. Pégalo en tu navegador
3. Firebase te llevará a crear el índice automáticamente
4. Haz clic en "Create Index"
5. Espera 2-3 minutos
6. Intenta guardar de nuevo
```

---

### **Error 4: No aparece nada en Logcat**

**Síntoma:** No ves ningún log de ExpenseViewModel o ExpenseRepository

**Causa:** El código no se está ejecutando o los logs no se muestran

**Solución:**
```
1. Verifica el filtro de Logcat (debe mostrar com.gastos.app)
2. Sincroniza Gradle: File → Sync Project with Gradle Files
3. Reinstala la app:
   - Desinstala del dispositivo/emulador
   - Run → Run 'app'
4. Verifica que no haya errores de compilación
```

---

### **Error 5: FirebaseApp not initialized**

**Síntoma en Logcat:**
```
E/FirebaseApp: FirebaseApp is not initialized
```

**Causa:** Firebase no se inicializó correctamente

**Solución:**
```
1. Verifica que google-services.json esté en: app/google-services.json
2. Verifica que el plugin de Google Services esté aplicado en app/build.gradle.kts:
   plugins {
       ...
       alias(libs.plugins.google.services)
   }
3. Sincroniza Gradle
4. Limpia y reconstruye el proyecto
5. Reinstala la app
```

---

## 📊 **CHECKLIST DE VERIFICACIÓN**

Marca cada punto conforme lo verifiques:

### Antes de Probar:
- [ ] Sincronizaste Gradle
- [ ] Limpiaste y reconstruiste el proyecto
- [ ] Reinstalaste la app en el dispositivo
- [ ] El dispositivo tiene conexión a Internet
- [ ] Abriste Logcat y configuraste los filtros

### Durante la Prueba:
- [ ] Llenas los campos correctamente (nombre no vacío, monto válido)
- [ ] Tocas el botón "Guardar Gasto"
- [ ] El botón muestra el indicador de carga
- [ ] Observas Logcat mientras pruebas

### Verificar Logs:
- [ ] Ves: "addExpense llamado"
- [ ] Ves: "Cambiando estado a Loading"
- [ ] Ves: "addExpense - userId: [algo]"
- [ ] ¿Qué aparece después?

### Si hay Error:
- [ ] Copiaste el mensaje de error completo de Logcat
- [ ] Verificaste la autenticación en Firebase Console
- [ ] Probaste con reglas permisivas
- [ ] Verificaste el archivo google-services.json

### Si NO hay Logs:
- [ ] Verificaste que Logcat esté filtrando com.gastos.app
- [ ] Sincronizaste Gradle de nuevo
- [ ] Reinstalaste la app
- [ ] Hay errores de compilación?

---

## 📝 **INFORMACIÓN A REPORTAR**

Si después de estos pasos el problema persiste, necesito esta información:

### 1. Logs de Logcat

Copia TODO lo que aparezca en Logcat cuando intentes guardar:

```
[Pega aquí los logs de Logcat]
```

### 2. ¿Qué ves en la app?

- [ ] El botón se queda cargando infinitamente
- [ ] Aparece un Toast de error (¿qué dice?)
- [ ] Aparece una card roja de error (¿qué dice?)
- [ ] No pasa nada

### 3. Firebase Console

- [ ] ¿Tu usuario aparece en Authentication → Users?
- [ ] ¿Cuál es tu User UID? [copia aquí]
- [ ] ¿Qué reglas tienes en Firestore? [copia o captura de pantalla]
- [ ] ¿Puedes crear un documento manualmente en Firestore?

### 4. Conexión

- [ ] ¿El dispositivo tiene Internet?
- [ ] ¿Puedes abrir google.com en el navegador del dispositivo?

---

## 🎯 **SOLUCIONES RÁPIDAS**

### **Solución Rápida 1: Reinstalar Todo**

```bash
# En Android Studio:
1. Build → Clean Project (Espera a que termine)
2. File → Invalidate Caches / Restart → Invalidate and Restart
3. Cuando reinicie:
4. File → Sync Project with Gradle Files
5. Build → Rebuild Project
6. Desinstala la app del dispositivo manualmente
7. Run → Run 'app'
```

### **Solución Rápida 2: Reglas Permisivas (Temporal)**

En Firebase Console → Firestore → Rules, usa:
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

### **Solución Rápida 3: Verificar Autenticación**

```kotlin
// En MainActivity, agrega este log temporalmente:
setContent {
    val currentUser = FirebaseAuth.getInstance().currentUser
    Log.d("MainActivity", "Usuario actual: ${currentUser?.uid} - ${currentUser?.email}")
    
    // ... resto del código
}
```

---

## 🆘 **SIGUIENTE PASO**

**POR FAVOR, SIGUE ESTOS PASOS EN ORDEN:**

1. ✅ **Sincroniza Gradle**
2. ✅ **Reinstala la app**
3. ✅ **Abre Logcat**
4. ✅ **Configura filtros**: `tag:ExpenseViewModel|ExpenseRepository`
5. ✅ **Intenta guardar un gasto**
6. ✅ **Copia los logs que aparezcan**
7. ✅ **Dime qué Toast/mensaje aparece en la app**

---

## 💡 **NOTAS IMPORTANTES**

1. **Los logs son CRUCIALES** - Sin ellos no puedo diagnosticar
2. **Las reglas de Firestore** suelen ser la causa #1
3. **La autenticación** es la causa #2
4. **Los índices faltantes** son la causa #3

---

**Última actualización:** 25 de Octubre, 2025  
**Versión:** 3.0 - Con logs y diagnóstico completo

---

## 🚨 **ACCIÓN INMEDIATA**

**HAZ ESTO AHORA:**

1. Sincroniza Gradle
2. Reinstala la app
3. Abre Logcat (filtra por ExpenseViewModel)
4. Llena un gasto: Nombre="Test", Monto="50"
5. Toca "Guardar Gasto"
6. **COPIA Y PEGA TODO lo que salga en Logcat**
7. Dime qué mensaje apareció en la pantalla (Toast)

**Con esa información podré darte una solución exacta.** 🎯

