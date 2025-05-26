package com.example.vertlechemin.ui.theme.screen.login.trajet

import android.content.Context
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun DestinationScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToFavoris: () -> Unit,
    onNavigateToParameters: () -> Unit,
    onStartRace: () -> Unit
) {
    val context = LocalContext.current

    // Configuration obligatoire de osmdroid
    LaunchedEffect(Unit) {
        Configuration.getInstance().load(
            context,
            context.getSharedPreferences("osm_prefs", Context.MODE_PRIVATE)
        )
    }

    Scaffold(
        containerColor = Color(0xFF3F6634),
        bottomBar = {
            DestinationBottomNavigationBar(
                onNavigateToTrajet = onNavigateToHome,
                onNavigateToFavoris = onNavigateToFavoris,
                onNavigateToParameters = onNavigateToParameters
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Destination",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            SearchBarPlaceholder()

            Spacer(modifier = Modifier.height(12.dp))

            // Carte OpenStreetMap dans une Box bien limitée
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp, max = 500.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                OpenStreetMap()
            }

            Spacer(modifier = Modifier.height(20.dp))

            ButtonCommencer(onStartRace = onStartRace)

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun SearchBarPlaceholder() {
    TextField(
        value = "",
        onValueChange = {},
        placeholder = { Text("Rechercher une destination") },
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun OpenStreetMap() {
    val context = LocalContext.current

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = {
            MapView(it).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)

                // Position initiale sur Le Mans
                controller.setZoom(14.5)
                val leMans = GeoPoint(48.0061, 0.1996)
                controller.setCenter(leMans)

                // Marqueur Le Mans
                val marker = Marker(this).apply {
                    position = leMans
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    title = "Le Mans"
                }
                overlays.add(marker)

                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        }
    )
}

@Composable
fun ButtonCommencer(onStartRace: () -> Unit) {
    Button(
        onClick = onStartRace,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFDAB87C),
            contentColor = Color.Black
        )
    ) {
        Text("Commencer la course", style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
fun DestinationBottomNavigationBar(
    onNavigateToTrajet: () -> Unit,
    onNavigateToFavoris: () -> Unit,
    onNavigateToParameters: () -> Unit
) {
    NavigationBar(
        modifier = Modifier.clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
        containerColor = Color(0xFFDAB87C)
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
