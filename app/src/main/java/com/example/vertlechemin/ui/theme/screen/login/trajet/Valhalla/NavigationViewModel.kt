package com.example.vertlechemin.ui.theme.screen.login.trajet.Valhalla

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vertlechemin.ui.theme.screen.login.trajet.Valhalla.LocationReq
import com.example.vertlechemin.ui.theme.screen.login.trajet.Valhalla.RetrofitInstance
import com.example.vertlechemin.ui.theme.screen.login.trajet.Valhalla.ValhallaRequest
import kotlinx.coroutines.launch

class NavigationViewModel : ViewModel() {
    var polylineEncoded = mutableStateOf<String?>(null)
        private set

    var instructions = mutableStateOf<List<String>>(emptyList())
        private set

    var errorMessage = mutableStateOf<String?>(null)
        private set

    var loading = mutableStateOf(false)
        private set

    fun fetchRoute(
        startLat: Double,
        startLon: Double,
        endLat: Double,
        endLon: Double
    ) {
        viewModelScope.launch {
            loading.value = true
            errorMessage.value = null
            try {
                val request = ValhallaRequest(
                    locations = listOf(
                        LocationReq(startLat, startLon),
                        LocationReq(endLat, endLon)
                    )
                )
                val response = RetrofitInstance.api.getRoute(request)
                polylineEncoded.value = response.trip.shape
                instructions.value = response.trip.legs.flatMap { leg ->
                    leg.maneuvers.map { it.instruction }
                }
            } catch (e: Exception) {
                errorMessage.value = e.message ?: "Erreur inconnue"
            } finally {
                loading.value = false
            }
        }
    }
}