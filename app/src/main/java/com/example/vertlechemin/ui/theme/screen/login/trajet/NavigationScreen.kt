package com.example.vertlechemin.ui.theme.screen.login.trajet

import android.widget.ImageView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.media3.exoplayer.image.ImageDecoder
import kotlinx.coroutines.delay

@Composable
fun NavigationScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToFavoris: () -> Unit,
    onNavigateToParameters: () -> Unit,
    onFinishScreen: () -> Unit,
) {
    // Timer 10 secondes
    LaunchedEffect(Unit) {
        delay(10_000)
        onFinishScreen()
    }

    Scaffold(
        containerColor = Color(0xFF3F6634),
        bottomBar = {
            NavigationBottomNavigationBar(
                onNavigateToTrajet = onNavigateToHome,
                onNavigateToFavoris = onNavigateToFavoris,
                onNavigateToParameters = onNavigateToParameters,
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize())
    }
}

@Composable
fun NavigationBottomNavigationBar(
    onNavigateToTrajet: () -> Unit,
    onNavigateToFavoris: () -> Unit,
    onNavigateToParameters: () -> Unit,
) {
    NavigationBar(
        modifier = Modifier,
        containerColor = Color(0xFFDAB87C),
        tonalElevation = 4.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Accueil", tint = Color.Black) },
            label = { Text("Accueil", color = Color.Black) },
            selected = true,
            onClick = onNavigateToTrajet
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.FavoriteBorder, contentDescription = "Favoris", tint = Color.Black) },
            label = { Text("Favoris", color = Color.Black) },
            selected = false,
            onClick = onNavigateToFavoris
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = "Paramètres", tint = Color.Black) },
            label = { Text("Paramètres", color = Color.Black) },
            selected = false,
            onClick = onNavigateToParameters
        )
    }
}
