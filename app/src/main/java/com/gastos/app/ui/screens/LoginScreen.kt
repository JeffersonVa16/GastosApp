package com.gastos.app.ui.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gastos.app.viewmodel.AuthState
import com.gastos.app.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var selectedCountryCode by remember { mutableStateOf("+1") }
    var showCountryCodePicker by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var isRegistering by remember { mutableStateOf(false) }
    
    val authState by authViewModel.authState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    // Launcher para el login con Google
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { intent ->
                authViewModel.signInWithGoogle(intent)
            }
        }
    }
    
    // Observar cambios en el estado de autenticación
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                onLoginSuccess()
            }
            else -> { }
        }
    }
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            // Logo/Título de la app con diseño mejorado y más abajo
            Card(
                modifier = Modifier
                    .padding(bottom = 24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "💰",
                        fontSize = 56.sp
                    )
                    Text(
                        text = "Control de Gastos",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            
            // Título de sección
            Text(
                text = if (isRegistering) "Crear cuenta nueva" else "Bienvenido de nuevo",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            
            Text(
                text = if (isRegistering) "Completa los datos para registrarte" else "Ingresa tus credenciales",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 20.dp)
            )
            
            // Campos de nombre y apellido (solo al registrarse)
            if (isRegistering) {
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("Nombre") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    singleLine = true,
                    placeholder = { Text("Ingresa tu nombre") }
                )
                
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Apellido") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    singleLine = true,
                    placeholder = { Text("Ingresa tu apellido") }
                )
                
                // Campo de teléfono con selector de código de área
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Selector de código de área
                    OutlinedCard(
                        onClick = { showCountryCodePicker = true },
                        modifier = Modifier
                            .weight(0.35f)
                            .height(56.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = selectedCountryCode,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Seleccionar código",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    // Campo de número de teléfono
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { 
                            if (it.all { char -> char.isDigit() }) {
                                phoneNumber = it
                            }
                        },
                        label = { Text("Teléfono") },
                        placeholder = { Text("123456789") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.weight(0.65f),
                        singleLine = true
                    )
                }
            }
            
            // Campo de email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                singleLine = true
            )
            
            // Campo de contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                singleLine = true
            )
            
            // Botón de login o registro con diseño mejorado
            Button(
                onClick = {
                    if (isRegistering) {
                        val fullName = "$firstName $lastName".trim()
                        authViewModel.registerWithEmail(email, password, fullName)
                    } else {
                        authViewModel.loginWithEmail(email, password)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = authState !is AuthState.Loading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        text = if (isRegistering) "Crear cuenta" else "Iniciar Sesión",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // Botón para cambiar entre login y registro
            TextButton(
                onClick = { 
                    isRegistering = !isRegistering
                    firstName = ""
                    lastName = ""
                    phoneNumber = ""
                    selectedCountryCode = "+1"
                    authViewModel.resetAuthState()
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    text = if (isRegistering) 
                        "¿Ya tienes cuenta? Inicia sesión" 
                    else 
                        "¿No tienes cuenta? Regístrate"
                )
            }
            
            // Divider
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(modifier = Modifier.weight(1f))
                Text(
                    text = "O",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Divider(modifier = Modifier.weight(1f))
            }
            
            // Botón de login con Google
            OutlinedButton(
                onClick = {
                    coroutineScope.launch {
                        val intentSender = authViewModel.beginGoogleSignIn()
                        intentSender?.let {
                            val request = IntentSenderRequest.Builder(it).build()
                            googleSignInLauncher.launch(request)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = authState !is AuthState.Loading
            ) {
                Text(
                    text = "🔐 Continuar con Google",
                    fontSize = 16.sp
                )
            }
            
            // Mensaje de error
            if (authState is AuthState.Error) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = (authState as AuthState.Error).message,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
        
        // Diálogo para seleccionar código de área
        if (showCountryCodePicker) {
            CountryCodePickerDialog(
                selectedCode = selectedCountryCode,
                onDismiss = { showCountryCodePicker = false },
                onCodeSelected = { code ->
                    selectedCountryCode = code
                    showCountryCodePicker = false
                }
            )
        }
    }
}

@Composable
fun CountryCodePickerDialog(
    selectedCode: String,
    onDismiss: () -> Unit,
    onCodeSelected: (String) -> Unit
) {
    val countryCodes = listOf(
        "🇺🇸 +1" to "+1",
        "🇲🇽 +52" to "+52",
        "🇪🇸 +34" to "+34",
        "🇦🇷 +54" to "+54",
        "🇨🇴 +57" to "+57",
        "🇨🇱 +56" to "+56",
        "🇵🇪 +51" to "+51",
        "🇻🇪 +58" to "+58",
        "🇪🇨 +593" to "+593",
        "🇬🇹 +502" to "+502",
        "🇨🇺 +53" to "+53",
        "🇩🇴 +1-809" to "+1-809",
        "🇵🇦 +507" to "+507",
        "🇨🇷 +506" to "+506",
        "🇺🇾 +598" to "+598",
        "🇵🇾 +595" to "+595",
        "🇧🇴 +591" to "+591",
        "🇭🇳 +504" to "+504",
        "🇳🇮 +505" to "+505",
        "🇸🇻 +503" to "+503"
    )
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Selecciona código de área",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(countryCodes) { (display, code) ->
                    TextButton(
                        onClick = { onCodeSelected(code) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = display,
                                fontSize = 16.sp,
                                color = if (code == selectedCode) 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    MaterialTheme.colorScheme.onSurface
                            )
                            if (code == selectedCode) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Seleccionado",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

