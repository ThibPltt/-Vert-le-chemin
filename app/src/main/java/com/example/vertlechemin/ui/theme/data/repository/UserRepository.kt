package com.example.vertlechemin.ui.theme.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

sealed class AuthResult {
    object Success : AuthResult()
    object Loading : AuthResult()
    data class Error(val message: String) : AuthResult()
}

@Singleton
class UserRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {

    private val _currentUser = MutableStateFlow<FirebaseUser?>(firebaseAuth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser

    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        _currentUser.value = auth.currentUser
    }

    init {
        firebaseAuth.addAuthStateListener(authStateListener)
    }

    // Pense à retirer ce listener si tu as un cycle de vie adapté (optionnel)

    suspend fun login(email: String, password: String): AuthResult {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Erreur lors de la connexion")
        }
    }

    suspend fun register(email: String, password: String, displayName: String): AuthResult {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()

            result.user?.updateProfile(
                com.google.firebase.auth.UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build()
            )?.await()

            AuthResult.Success
        } catch (e: FirebaseAuthUserCollisionException) {
            AuthResult.Error("Un compte existe déjà avec cet email")
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Erreur lors de l'inscription")
        }
    }

    fun logout() {
        firebaseAuth.signOut()
    }

    fun isLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    fun currentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }
}
