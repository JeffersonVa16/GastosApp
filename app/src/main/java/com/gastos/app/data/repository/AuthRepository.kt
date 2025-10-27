package com.gastos.app.data.repository

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.gastos.app.data.model.UserProfile
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class AuthRepository(private val context: Context) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val oneTapClient: SignInClient = Identity.getSignInClient(context)
    
    // Obtener usuario actual
    fun getCurrentUser(): FirebaseUser? = auth.currentUser
    
    fun getCurrentUserProfile(): UserProfile? {
        return getCurrentUser()?.let { UserProfile.fromFirebaseUser(it) }
    }
    
    // Login con email y contraseña
    suspend fun loginWithEmail(email: String, password: String): Result<UserProfile> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) {
                Result.success(UserProfile.fromFirebaseUser(user))
            } else {
                Result.failure(Exception("Error al iniciar sesión"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Registro con email y contraseña
    suspend fun registerWithEmail(email: String, password: String, displayName: String = ""): Result<UserProfile> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) {
                // Si se proporcionó un nombre, actualizarlo en el perfil
                if (displayName.isNotBlank()) {
                    val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                        .setDisplayName(displayName)
                        .build()
                    user.updateProfile(profileUpdates).await()
                }
                Result.success(UserProfile.fromFirebaseUser(user))
            } else {
                Result.failure(Exception("Error al registrar usuario"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Iniciar proceso de login con Google (One Tap)
    suspend fun beginGoogleSignIn(): IntentSender? {
        return try {
            val request = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(
                    BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId("271969868917-udr7550amsvtfm58pabcqlaf1vcktcqj.apps.googleusercontent.com")
                        .setFilterByAuthorizedAccounts(false)
                        .build()
                )
                .setAutoSelectEnabled(false)
                .build()
            
            val result = oneTapClient.beginSignIn(request).await()
            result.pendingIntent.intentSender
        } catch (e: Exception) {
            null
        }
    }
    
    // Completar login con Google usando el resultado del intent
    suspend fun signInWithGoogle(intent: Intent): Result<UserProfile> {
        return try {
            val credential = oneTapClient.getSignInCredentialFromIntent(intent)
            val googleIdToken = credential.googleIdToken
            
            if (googleIdToken != null) {
                val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                val result = auth.signInWithCredential(firebaseCredential).await()
                val user = result.user
                
                if (user != null) {
                    Result.success(UserProfile.fromFirebaseUser(user))
                } else {
                    Result.failure(Exception("Error al iniciar sesión con Google"))
                }
            } else {
                Result.failure(Exception("No se obtuvo el token de Google"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Cerrar sesión
    fun logout() {
        auth.signOut()
        oneTapClient.signOut()
    }
    
    // Verificar si hay un usuario autenticado
    fun isUserLoggedIn(): Boolean = getCurrentUser() != null
    
    // Actualizar nombre de usuario
    suspend fun updateDisplayName(newName: String): Result<Unit> {
        return try {
            val user = getCurrentUser() ?: return Result.failure(Exception("Usuario no autenticado"))
            val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                .setDisplayName(newName)
                .build()
            user.updateProfile(profileUpdates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Cambiar contraseña
    suspend fun updatePassword(currentPassword: String, newPassword: String): Result<Unit> {
        return try {
            val user = getCurrentUser() ?: return Result.failure(Exception("Usuario no autenticado"))
            val email = user.email ?: return Result.failure(Exception("Email no disponible"))
            
            // Reautenticar usuario
            val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(email, currentPassword)
            user.reauthenticate(credential).await()
            
            // Actualizar contraseña
            user.updatePassword(newPassword).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Eliminar cuenta
    suspend fun deleteAccount(password: String?): Result<Unit> {
        return try {
            val user = getCurrentUser() ?: return Result.failure(Exception("Usuario no autenticado"))
            
            // Si el usuario tiene contraseña, reautenticar
            if (password != null && user.email != null) {
                val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(user.email!!, password)
                user.reauthenticate(credential).await()
            }
            
            // Eliminar cuenta
            user.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

