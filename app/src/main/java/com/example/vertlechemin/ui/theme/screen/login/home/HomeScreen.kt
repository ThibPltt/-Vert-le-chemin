package com.example.vertlechemin.ui.theme.screen.login.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vertlechemin.ui.theme.data.repository.UserRepository
import javax.inject.Inject


class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository
) : androidx.lifecycle.ViewModel() {

    val currentUser = userRepository.currentUser

    fun logout() {
        userRepository.logout()
    }
}

@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    onNavigateToTrajet: () -> Unit,
    onNavigateToFavoris: () -> Unit,
    onNavigateToParameters: () -> Unit
) {
    Scaffold(
        containerColor = Color(0xFF3F6634), // Couleur verte du background
        bottomBar = {
            BottomNavigationBar(
                onNavigateToTrajet = onNavigateToTrajet,
                onNavigateToFavoris = onNavigateToFavoris,
                onNavigateToParameters = onNavigateToParameters
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icone de profil en haut à gauche
            Box(modifier = Modifier.fillMaxWidth()) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profil",
                    modifier = Modifier
                        .size(60.dp)
                        .align(Alignment.TopStart),
                    tint = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(80.dp))

            // Titre
            Text(
                text = "Destination",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Barre de recherche
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Rechercher une destination", color = Color.Black) },
                trailingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black,
                    cursorColor = Color.Black
                )
            )
        }
    }
}

@Composable
fun BottomNavigationBar(
    onNavigateToTrajet: () -> Unit,
    onNavigateToFavoris: () -> Unit,
    onNavigateToParameters: () -> Unit
) {

    NavigationBar(
        modifier = Modifier
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
        containerColor = Color(0xFFDAB87C) // Beige/orangé

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
            icon = { Icon(Icons.Default.Settings, contentDescription = "Profil", tint = Color.Black) },
            label = { Text("Profil", color = Color.Black) },
            selected = false,
            onClick = onNavigateToParameters
        )
    }
}
