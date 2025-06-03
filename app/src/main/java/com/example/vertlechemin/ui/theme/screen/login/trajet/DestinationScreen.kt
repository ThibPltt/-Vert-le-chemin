package com.example.vertlechemin.ui.theme.screen.login.trajet

import android.content.Context
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

    // Configuration obligatoire osmdroid
    LaunchedEffect(Unit) {
        Configuration.getInstance().load(
            context,
            context.getSharedPreferences("osm_prefs", Context.MODE_PRIVATE)
        )
    }

    val cities = listOf(
        City("Paris", GeoPoint(48.8566, 2.3522)),
        City("Marseille", GeoPoint(43.2965, 5.3698)),
        City("Lyon", GeoPoint(45.7640, 4.8357)),
        City("Toulouse", GeoPoint(43.6047, 1.4442)),
        City("Nice", GeoPoint(43.7102, 7.2620)),
        City("Nantes", GeoPoint(47.2184, -1.5536)),
        City("Strasbourg", GeoPoint(48.5734, 7.7521)),
        City("Montpellier", GeoPoint(43.6108, 3.8767)),
        City("Bordeaux", GeoPoint(44.8378, -0.5792)),
        City("Lille", GeoPoint(50.6292, 3.0573)),
    )

    var searchQuery by remember { mutableStateOf("") }
    var selectedCity by remember { mutableStateOf<City?>(null) }
    var mapView by remember { mutableStateOf<MapView?>(null) }

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

            // Barre de recherche
            TextField(
                value = searchQuery,
                onValueChange = { newValue ->
                    searchQuery = newValue
                    selectedCity = null
                },
                placeholder = { Text("Rechercher une destination", color = Color.Black) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedTextColor = Color.Black,
                    focusedTextColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Liste filtrée des villes
            val filteredCities = if (searchQuery.isEmpty()) emptyList() else
                cities.filter {
                    it.name.lowercase().startsWith(searchQuery.lowercase())
                }.take(5)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
            ) {
                filteredCities.forEach { city ->
                    Text(
                        text = city.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedCity = city
                                searchQuery = city.name

                                // Centre la carte sur la ville choisie
                                mapView?.let { mv ->
                                    mv.controller.setZoom(14.5)
                                    mv.controller.setCenter(city.location)

                                    mv.overlays.clear()
                                    val marker = Marker(mv).apply {
                                        position = city.location
                                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                        title = city.name
                                    }
                                    mv.overlays.add(marker)
                                    mv.invalidate()
                                }
                            }
                            .padding(8.dp),
                        color = Color.Black,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Carte OpenStreetMap
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp, max = 500.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = {
                        MapView(it).apply {
                            setTileSource(TileSourceFactory.MAPNIK)
                            setMultiTouchControls(true)

                            // Centre initial sur Le Mans
                            val leMans = GeoPoint(48.0061, 0.1996)
                            controller.setZoom(14.5)
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
                            mapView = this
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            ButtonCommencer(onStartRace = onStartRace)

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
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

data class City(val name: String, val location: GeoPoint)
