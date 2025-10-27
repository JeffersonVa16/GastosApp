package com.gastos.app.viewmodel

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gastos.app.data.model.UserProfile
import com.gastos.app.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: UserProfile) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(context: Context) : ViewModel() {
    private val authRepository = AuthRepository(context)
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()
    
    init {
        checkCurrentUser()
    }
    
    private fun checkCurrentUser() {
        _currentUser.value = authRepository.getCurrentUserProfile()
        _currentUser.value?.let {
            _authState.value = AuthState.Success(it)
        }
    }
    
    fun loginWithEmail(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("El email y la contraseña son obligatorios")
            return
        }
        
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.loginWithEmail(email, password)
            
            _authState.value = result.fold(
                onSuccess = { user ->
                    _currentUser.value = user
                    AuthState.Success(user)
                },
                onFailure = { exception ->
                    AuthState.Error(exception.message ?: "Error al iniciar sesión")
                }
            )
        }
    }
    
    fun registerWithEmail(email: String, password: String, displayName: String = "") {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("El email y la contraseña son obligatorios")
            return
        }
        
        if (displayName.isBlank()) {
            _authState.value = AuthState.Error("El nombre y apellido son obligatorios")
            return
        }
        
        if (password.length < 6) {
            _authState.value = AuthState.Error("La contraseña debe tener al menos 6 caracteres")
            return
        }
        
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.registerWithEmail(email, password, displayName)
            
            _authState.value = result.fold(
                onSuccess = { user ->
                    _currentUser.value = user
                    AuthState.Success(user)
                },
                onFailure = { exception ->
                    AuthState.Error(exception.message ?: "Error al registrar usuario")
                }
            )
        }
    }
    
    suspend fun beginGoogleSignIn(): IntentSender? {
        _authState.value = AuthState.Loading
        return authRepository.beginGoogleSignIn()
    }
    
    fun signInWithGoogle(intent: Intent) {
        viewModelScope.launch {
            val result = authRepository.signInWithGoogle(intent)
            
            _authState.value = result.fold(
                onSuccess = { user ->
                    _currentUser.value = user
                    AuthState.Success(user)
                },
                onFailure = { exception ->
                    AuthState.Error(exception.message ?: "Error al iniciar sesión con Google")
                }
            )
        }
    }
    
    fun logout() {
        authRepository.logout()
        _currentUser.value = null
        _authState.value = AuthState.Idle
    }
    
    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }
    
    fun isUserLoggedIn(): Boolean = authRepository.isUserLoggedIn()
    
    // Actualizar nombre de usuario
    fun updateDisplayName(newName: String) {
        if (newName.isBlank()) {
            _authState.value = AuthState.Error("El nombre no puede estar vacío")
            return
        }
        
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.updateDisplayName(newName)
            
            _authState.value = result.fold(
                onSuccess = {
                    // Actualizar el usuario actual
                    _currentUser.value = authRepository.getCurrentUserProfile()
                    AuthState.Success(_currentUser.value!!)
                },
                onFailure = { exception ->
                    AuthState.Error(exception.message ?: "Error al actualizar nombre")
                }
            )
        }
    }
    
    // Cambiar contraseña
    fun changePassword(currentPassword: String, newPassword: String) {
        if (currentPassword.isBlank() || newPassword.isBlank()) {
            _authState.value = AuthState.Error("Las contraseñas son obligatorias")
            return
        }
        
        if (newPassword.length < 6) {
            _authState.value = AuthState.Error("La nueva contraseña debe tener al menos 6 caracteres")
            return
        }
        
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.updatePassword(currentPassword, newPassword)
            
            _authState.value = result.fold(
                onSuccess = {
                    AuthState.Success(_currentUser.value!!)
                },
                onFailure = { exception ->
                    AuthState.Error(exception.message ?: "Error al cambiar contraseña")
                }
            )
        }
    }
    
    // Eliminar cuenta
    fun deleteAccount(password: String?) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.deleteAccount(password)
            
            _authState.value = result.fold(
                onSuccess = {
                    _currentUser.value = null
                    AuthState.Idle
                },
                onFailure = { exception ->
                    AuthState.Error(exception.message ?: "Error al eliminar cuenta")
                }
            )
        }
    }
}

