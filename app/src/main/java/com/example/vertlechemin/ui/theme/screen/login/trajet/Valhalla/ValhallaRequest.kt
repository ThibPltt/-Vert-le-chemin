package com.example.vertlechemin.ui.theme.screen.login.trajet.Valhalla

data class ValhallaRequest(
    val locations: List<LocationReq>,
    val costing: String = "auto"
)

data class LocationReq(
    val lat: Double,
    val lon: Double
)
