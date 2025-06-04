package com.example.vertlechemin.ui.theme.screen.login.trajet.Valhalla

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ValhallaApiService {
    @Headers("Content-Type: application/json")
    @POST("route")
    suspend fun getRoute(@Body request: ValhallaRequest): ValhallaResponse
}