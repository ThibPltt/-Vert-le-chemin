package com.example.vertlechemin.ui.theme.screen.login.parameters

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.vertlechemin.ui.theme.data.repository.navigation.Screen

enum class PopupType {
    NONE,
    PARAMETERS,
    LANGUAGE
}

@Composable
fun ParametersScreen(
    onNavigateToTrajet: () -> Unit,
    onNavigateToFavoris: () -> Unit,
    onNavigateToParameters: () -> Unit,
    navController: NavController
) {
    val context = LocalContext.current
    var currentPopup by remember { mutableStateOf(PopupType.NONE) }

    Scaffold(
        containerColor = Color(0xFF3F6634),
        bottomBar = {
            BottomNavigationBar(
                onNavigateToTrajet = onNavigateToTrajet,
                onNavigateToFavoris = onNavigateToFavoris,
                onNavigateToParameters = onNavigateToParameters
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Titre
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Paramètres",
                    color = Color.Black,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Supprimer mes données",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(24.dp))

            // "Informations personnelles"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { currentPopup = PopupType.PARAMETERS }
                    .padding(vertical = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Informations personnelles",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text("Informations personnelles", color = Color.White)
            }
            Divider(color = Color.Black, thickness = 1.dp)

            // Notifications
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                            }
                        } else {
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.parse("package:" + context.packageName)
                            }
                        }
                        context.startActivity(intent)
                    }
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Notifications",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Divider(color = Color.Black, thickness = 1.dp)

            // "Langue"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { currentPopup = PopupType.LANGUAGE }
                    .padding(vertical = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = "Langue",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text("Langue", color = Color.White)
            }
            Divider(color = Color.Black, thickness = 1.dp)

            // Déconnexion
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                    .padding(vertical = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PowerSettingsNew,
                    contentDescription = "Se déconnecter",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text("Se déconnecter", color = Color.White)
            }
            Divider(color = Color.Black, thickness = 1.dp)
        }
    }

    // Gestion des popups
    when (currentPopup) {
        PopupType.PARAMETERS -> ParametersPopupDialog(
            onDismiss = { currentPopup = PopupType.NONE },
        )
        PopupType.LANGUAGE -> LanguagePopupDialog(
            onDismiss = { currentPopup = PopupType.NONE },
            onLanguageSelected = { languageCode ->
                // TODO: Appliquer la langue choisie (languageCode : "fr", "en", "es", ...)
                currentPopup = PopupType.NONE
            }
        )
        else -> { /* rien à afficher */ }
    }
}

@Composable
fun BottomNavigationBar(
    onNavigateToTrajet: () -> Unit,
    onNavigateToFavoris: () -> Unit,
    onNavigateToParameters: () -> Unit
) {
    NavigationBar(
        modifier = Modifier.clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
        containerColor = Color(0xFFDAB87C)
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.DirectionsCar, contentDescription = "Trajet", tint = Color.Black) },
            label = { Text("Trajet", color = Color.Black) },
            selected = false,
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
            selected = true,
            onClick = onNavigateToParameters
        )
    }
}
