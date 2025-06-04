package com.example.vertlechemin.ui.theme.screen.login.trajet.Valhalla

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitInstance {
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    val api: ValhallaApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://valhalla1.openstreetmap.de/") // Serveur public de test
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(ValhallaApiService::class.java)
    }
}