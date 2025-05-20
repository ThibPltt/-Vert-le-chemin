package com.example.vertlechemin.ui.theme.data.repository.utils

import android.util.Patterns
import java.util.regex.Pattern

object ValidationUtils {
    fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        // Au moins 6 caractères
        return password.length >= 6
    }

    fun getPasswordErrorMessage(password: String): String? {
        return when {
            password.isEmpty() -> "Le mot de passe ne peut pas être vide"
            password.length < 6 -> "Le mot de passe doit avoir au moins 6 caractères"
            else -> null
        }
    }

    fun getEmailErrorMessage(email: String): String? {
        return when {
            email.isEmpty() -> "L'email ne peut pas être vide"
            !isValidEmail(email) -> "Format d'email invalide"
            else -> null
        }
    }
}