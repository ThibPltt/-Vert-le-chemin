package com.example.vertlechemin.ui.theme.data.repository

import com.example.vertlechemin.ui.theme.data.repository.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

// Résultat de l'authentification avec message d'erreur si besoin
sealed class AuthResult {
    object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
    object Loading : AuthResult()
}

@Singleton
class UserRepository @Inject constructor() {

    // Dans une vraie app, vous utiliseriez Firebase Auth, votre API ou autre
    // Ici nous simulons une base d'utilisateurs localement
    private val users = mutableListOf(
        User(id = "1", email = "test@example.com", displayName = "Utilisateur Test", password = "password123")
    )

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    suspend fun login(email: String, password: String): AuthResult {
        return withContext(Dispatchers.IO) {
            try {
                // Simuler un délai réseau
                delay(1000)

                val user = users.find { it.email == email && it.password == password }

                if (user != null) {
                    _currentUser.value = user
                    AuthResult.Success
                } else {
                    AuthResult.Error("Email ou mot de passe incorrect")
                }
            } catch (e: Exception) {
                AuthResult.Error(e.message ?: "Une erreur inconnue s'est produite")
            }
        }
    }

    suspend fun register(email: String, password: String, displayName: String): AuthResult {
        return withContext(Dispatchers.IO) {
            try {
                // Simuler un délai réseau
                delay(1000)

                // Vérifier si l'utilisateur existe déjà
                if (users.any { it.email == email }) {
                    return@withContext AuthResult.Error("Un compte existe déjà avec cet email")
                }

                // Créer un nouvel utilisateur
                val newUser = User(
                    id = (users.size + 1).toString(),
                    email = email,
                    password = password,
                    displayName = displayName
                )

                users.add(newUser)
                _currentUser.value = newUser

                AuthResult.Success
            } catch (e: Exception) {
                AuthResult.Error(e.message ?: "Une erreur inconnue s'est produite")
            }
        }
    }

    fun logout() {
        _currentUser.value = null
    }

    fun isLoggedIn(): Boolean {
        return _currentUser.value != null
    }
}