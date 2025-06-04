package com.example.vertlechemin.ui.theme.screen.login.trajet.Valhalla

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline
import androidx.compose.ui.viewinterop.AndroidView

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