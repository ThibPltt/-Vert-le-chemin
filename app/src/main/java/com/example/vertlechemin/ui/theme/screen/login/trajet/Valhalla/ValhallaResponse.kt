package com.example.vertlechemin.ui.theme.screen.login.trajet.Valhalla

data class ValhallaResponse(
    val trip: Trip
)

data class Trip(
    val shape: String,
    val legs: List<Leg>
)

data class Leg(
    val maneuvers: List<Maneuver>
)

data class Maneuver(
    val instruction: String
)