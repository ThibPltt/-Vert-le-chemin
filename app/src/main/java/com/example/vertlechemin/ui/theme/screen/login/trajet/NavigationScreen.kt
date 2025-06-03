package com.example.vertlechemin.ui.theme.screen.login.trajet

import android.Manifest
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.delay
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline

// --- Data classes Valhalla API simulée ---
data class ValhallaRouteResponse(val trip: Trip)
data class Trip(val shape: String, val legs: List<Leg>)
data class Leg(val maneuvers: List<Maneuver>)
data class Maneuver(val instruction: String)

// --- Mock API Valhalla (simulé) ---
suspend fun getRouteFromValhalla(
    startLat: Double, startLon: Double,
    endLat: Double, endLon: Double
): ValhallaRouteResponse {
    delay(1000)
    val examplePolyline = "_p~iF~ps|U_ulLnnqC_mqNvxq`@"
    val exampleManeuvers = listOf(
        Maneuver("Tourner à droite"),
        Maneuver("Continuer tout droit 1 km"),
        Maneuver("Tourner à gauche"),
        Maneuver("Vous êtes arrivé à destination")
    )
    val exampleLeg = Leg(exampleManeuvers)
    return ValhallaRouteResponse(Trip(examplePolyline, listOf(exampleLeg)))
}

// --- Décodage Google Encoded Polyline en GeoPoint ---
fun decodePolyline(encoded: String): List<GeoPoint> {
    val poly = mutableListOf<GeoPoint>()
    var index = 0
    val len = encoded.length
    var lat = 0
    var lng = 0

    while (index < len) {
        var b: Int
        var shift = 0
        var result = 0
        do {
            b = encoded[index++].code - 63
            result = result or ((b and 0x1f) shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlat = if ((result and 1) != 0) (result shr 1).inv() else (result shr 1)
        lat += dlat

        shift = 0
        result = 0
        do {
            b = encoded[index++].code - 63
            result = result or ((b and 0x1f) shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlng = if ((result and 1) != 0) (result shr 1).inv() else (result shr 1)
        lng += dlng

        poly.add(GeoPoint(lat.toDouble() / 1E5, lng.toDouble() / 1E5))
    }
    return poly
}

// --- Fonction démarrage mise à jour GPS ---
private fun startLocationUpdates(
    locationManager: LocationManager,
    onLocationChanged: (Location) -> Unit
) {
    try {
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            2000L, // 2 secondes
            5f,    // 5 mètres
            object : LocationListener {
                override fun onLocationChanged(loc: Location) {
                    onLocationChanged(loc)
                }
                override fun onStatusChanged(provider: String?, status: Int, extras: android.os.Bundle?) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            },
            Looper.getMainLooper()
        )
    } catch (ex: SecurityException) {
        // Permission non accordée, rien à faire ici
    }
}

// --- Composable récupération GPS continu ---
@Composable
fun getCurrentLocationContinuous(): Location? {
    val context = LocalContext.current
    var location by remember { mutableStateOf<Location?>(null) }
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    // Launcher pour permission
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                startLocationUpdates(locationManager) { loc -> location = loc }
            }
        }
    )

    LaunchedEffect(Unit) {
        val permissionGranted = androidx.core.content.ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        if (!permissionGranted) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            startLocationUpdates(locationManager) { loc -> location = loc }
        }
    }

    return location
}

// --- Écran Navigation complet ---
@Composable
fun NavigationScreen(
    destinationLat: Double,
    destinationLon: Double,
    onNavigateToHome: () -> Unit,
    onNavigateToFavoris: () -> Unit,
    onNavigateToParameters: () -> Unit,
    onFinishScreen: () -> Unit,
) {
    val location = getCurrentLocationContinuous()

    var polylineEncoded by remember { mutableStateOf<String?>(null) }
    var instructions by remember { mutableStateOf<List<String>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(location, destinationLat, destinationLon) {
        if (location == null) {
            errorMessage = "Impossible de récupérer la position GPS"
            loading = false
            return@LaunchedEffect
        }

        try {
            loading = true
            val response = getRouteFromValhalla(
                location.latitude, location.longitude,
                destinationLat, destinationLon
            )
            polylineEncoded = response.trip.shape
            instructions = response.trip.legs.flatMap { leg ->
                leg.maneuvers.map { it.instruction }
            }
            loading = false

            delay(10_000)
            onFinishScreen()
        } catch (e: Exception) {
            errorMessage = "Erreur lors de la récupération de l'itinéraire : ${e.message}"
            loading = false
        }
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            } else if (errorMessage != null) {
                Text(
                    text = errorMessage ?: "Erreur inconnue",
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                MapWithRoute(polylineEncoded = polylineEncoded, modifier = Modifier.weight(1f))
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                        .padding(8.dp)
                ) {
                    items(instructions) { instruction ->
                        Text(
                            text = instruction,
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MapWithRoute(polylineEncoded: String?, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            MapView(context).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(14.0)
                controller.setCenter(GeoPoint(48.8566, 2.3522)) // Paris par défaut
            }
        },
        update = { mapView ->
            mapView.overlays.clear()
            polylineEncoded?.let {
                val points = decodePolyline(it)
                val polyline = Polyline().apply {
                    setPoints(points)
                    outlinePaint.color = android.graphics.Color.BLUE
                    outlinePaint.strokeWidth = 8f
                }
                mapView.overlays.add(polyline)
                if (points.isNotEmpty()) {
                    mapView.controller.setCenter(points.first())
                    mapView.controller.setZoom(14.0)
                }
            }
            mapView.invalidate()
        }
    )
}

@Composable
fun NavigationBottomNavigationBar(
    onNavigateToTrajet: () -> Unit,
    onNavigateToFavoris: () -> Unit,
    onNavigateToParameters: () -> Unit,
) {
    NavigationBar(
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
