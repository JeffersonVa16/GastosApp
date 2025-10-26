package com.gastos.app.data.model

data class UserProfile(
    val uid: String = "",
    val email: String? = null,
    val displayName: String? = null,
    val photoUrl: String? = null
) {
    companion object {
        fun fromFirebaseUser(user: com.google.firebase.auth.FirebaseUser): UserProfile {
            return UserProfile(
                uid = user.uid,
                email = user.email,
                displayName = user.displayName,
                photoUrl = user.photoUrl?.toString()
            )
        }
    }
}

