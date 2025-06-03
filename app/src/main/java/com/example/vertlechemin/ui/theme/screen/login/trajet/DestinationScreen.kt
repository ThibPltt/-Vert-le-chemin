package com.example.vertlechemin.ui.theme.screen.login.trajet

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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
    onStartRace: (Double, Double) -> Unit
) {
    val context = LocalContext.current

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasLocationPermission = granted }
    )

    // Demande la permission si non accordée
    LaunchedEffect(key1 = Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Configuration OSM
    LaunchedEffect(key1 = Unit) {
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
    var userLocation by remember { mutableStateOf<GeoPoint?>(null) }

    // Récupérer la dernière position connue dès que permission OK
    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            try {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                val location = fusedLocationClient.lastLocation.await()
                location?.let {
                    userLocation = GeoPoint(it.latitude, it.longitude)
                    mapView?.controller?.setCenter(userLocation)
                    mapView?.controller?.setZoom(15.0)
                }
            } catch (e: Exception) {
                // Gestion simple d'erreur : log, toast, ou rien
            }
        }
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

                                mapView?.let { mv ->
                                    mv.controller.setZoom(14.5)
                                    mv.controller.setCenter(city.location)
                                    mv.overlays.clear()

                                    val cityMarker = Marker(mv).apply {
                                        position = city.location
                                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                        title = city.name
                                    }
                                    mv.overlays.add(cityMarker)

                                    userLocation?.let { loc ->
                                        val userMarker = Marker(mv).apply {
                                            position = loc
                                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                            title = "Ma position"
                                        }
                                        mv.overlays.add(userMarker)
                                    }
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

                            val leMans = GeoPoint(48.0061, 0.1996)
                            controller.setZoom(14.5)
                            controller.setCenter(leMans)

                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            mapView = this
                        }
                    },
                    update = { mv ->
                        mv.overlays.clear()

                        selectedCity?.let { city ->
                            val cityMarker = Marker(mv).apply {
                                position = city.location
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                title = city.name
                            }
                            mv.overlays.add(cityMarker)
                        }

                        userLocation?.let { loc ->
                            val userMarker = Marker(mv).apply {
                                position = loc
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                title = "Ma position"
                            }
                            mv.overlays.add(userMarker)
                            mv.controller.setCenter(loc)
                            mv.controller.setZoom(15.0)
                        }

                        mv.invalidate()
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            ButtonCommencer(
                onStartRace = {
                    selectedCity?.let {
                        onStartRace(it.location.latitude, it.location.longitude)
                    }
                }
            )

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
