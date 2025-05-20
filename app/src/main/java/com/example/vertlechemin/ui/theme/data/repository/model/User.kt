package com.example.vertlechemin.ui.theme.data.repository.model

data class User(
    val id: String = "",
    val email: String = "",
    val displayName: String = "",
    // Ne stockez pas le mot de passe en clair dans la production
    val password: String = ""
)