package com.example.vertlechemin.ui.theme.screen.login.trajet

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
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
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
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

// --- Composable récupération GPS continue avec FusedLocationProvider ---
@Composable
fun getCurrentLocationContinuous(): Location? {
    val context = LocalContext.current
    var location by remember { mutableStateOf<Location?>(null) }
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                try {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (location == null) {
                        startLocationUpdates(locationManager) { loc -> location = loc }
                    }
                } catch (e: SecurityException) {
                    e.printStackTrace()
                }
            }
        }
    )

    LaunchedEffect(Unit) {
        val permissionGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!permissionGranted) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            try {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (location == null) {
                    startLocationUpdates(locationManager) { loc -> location = loc }
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }

    LaunchedEffect(Unit) {
        val granted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!granted) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            val fusedClient = LocationServices.getFusedLocationProviderClient(context)

            try {
                fusedClient.lastLocation.addOnSuccessListener { loc ->
                    if (loc != null) {
                        location = loc
                    }
                }

                val locationRequest = LocationRequest.create().apply {
                    interval = 5000
                    fastestInterval = 2000
                    priority = Priority.PRIORITY_HIGH_ACCURACY
                }

                fusedClient.requestLocationUpdates(
                    locationRequest,
                    object : LocationCallback() {
                        override fun onLocationResult(result: LocationResult) {
                            location = result.lastLocation
                        }
                    },
                    Looper.getMainLooper()
                )
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }

    return location
}

private fun startLocationUpdates(locationManager: LocationManager, onLocationChanged: (Location) -> Unit) {
    try {
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            5000L,
            5f,
            { location -> onLocationChanged(location) }
        )
    } catch (e: SecurityException) {
        e.printStackTrace()
    }
}

// --- Écran Navigation complet corrigé ---
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

    var lastUsedLocation by remember { mutableStateOf<Location?>(null) }
    var polylineEncoded by remember { mutableStateOf<String?>(null) }
    var instructions by remember { mutableStateOf<List<String>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // On ne relance la requête Valhalla que si on bouge assez loin (ex: 50m)
    LaunchedEffect(location) {
        location?.let { currentLocation ->
            val lastLoc = lastUsedLocation
            val distance = lastLoc?.distanceTo(currentLocation) ?: Float.MAX_VALUE

            if (distance > 50f) {
                try {
                    loading = true
                    errorMessage = null

                    val response = getRouteFromValhalla(
                        currentLocation.latitude, currentLocation.longitude,
                        destinationLat, destinationLon
                    )
                    polylineEncoded = response.trip.shape
                    instructions = response.trip.legs.flatMap { leg -> leg.maneuvers.map { it.instruction } }

                    lastUsedLocation = currentLocation
                    loading = false
                } catch (e: Exception) {
                    errorMessage = "Erreur lors de la récupération de l'itinéraire : ${e.message}"
                    loading = false
                }
            }
        } ?: run {
            errorMessage = "Impossible de récupérer la position GPS"
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
            when {
                loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
                errorMessage != null -> {
                    Text(
                        text = errorMessage ?: "Erreur inconnue",
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                else -> {
                    MapWithRoute(
                        polylineEncoded = polylineEncoded,
                        userLocation = location,
                        modifier = Modifier.weight(1f)
                    )
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
}

@Composable
fun MapWithRoute(
    polylineEncoded: String?,
    userLocation: Location? = null,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            MapView(context).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(14.0)
                // Centrer par défaut sur Paris
                controller.setCenter(GeoPoint(48.8566, 2.3522))
            }
        },
        update = { mapView ->
            userLocation?.let {
                val userGeoPoint = GeoPoint(it.latitude, it.longitude)
                mapView.controller.setCenter(userGeoPoint)
            }

            mapView.overlays.clear()

            // Ajouter la ligne du trajet
            polylineEncoded?.let { encoded ->
                val geoPoints = decodePolyline(encoded)
                val polyline = Polyline().apply {
                    setPoints(geoPoints)
                    outlinePaint.color = android.graphics.Color.GREEN
                    outlinePaint.strokeWidth = 10f
                }
                mapView.overlays.add(polyline)
            }

            // Ajouter le marker position utilisateur
            userLocation?.let {
                val overlay = org.osmdroid.views.overlay.Marker(mapView).apply {
                    position = GeoPoint(it.latitude, it.longitude)
                    title = "Votre position"
                }
                mapView.overlays.add(overlay)
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
    NavigationBar(containerColor = Color(0xFF1B3614)) {
        NavigationBarItem(
            selected = false,
            onClick = onNavigateToTrajet,
            icon = { Icon(Icons.Default.Home, contentDescription = "Accueil") },
            label = { Text("Accueil") }
        )
        NavigationBarItem(
            selected = false,
            onClick = onNavigateToFavoris,
            icon = { Icon(Icons.Default.FavoriteBorder, contentDescription = "Favoris") },
            label = { Text("Favoris") }
        )
        NavigationBarItem(
            selected = false,
            onClick = onNavigateToParameters,
            icon = { Icon(Icons.Default.Settings, contentDescription = "Paramètres") },
            label = { Text("Paramètres") }
        )
    }
}
