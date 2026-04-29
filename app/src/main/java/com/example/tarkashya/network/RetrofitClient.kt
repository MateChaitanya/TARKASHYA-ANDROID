package com.example.tarkashya.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // CRITICAL: This IP must be your Laptop's current IPv4 address
    // Ensure the phone is connected to the SAME Wi-Fi
    private const val BASE_URL = "http://192.168.1.8:8080/"

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}