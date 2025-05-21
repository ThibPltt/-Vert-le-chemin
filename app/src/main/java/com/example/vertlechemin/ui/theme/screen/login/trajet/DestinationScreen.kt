package com.example.vertlechemin.ui.theme.screen.login.trajet

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun DestinationScreen(
    onNavigateToHome: () -> Unit
) {
    // Un simple bouton pour retourner à l'écran Home via callback
    Button(onClick = { onNavigateToHome() }) {
        Text(text = "Retour à l'accueil")
    }
}
