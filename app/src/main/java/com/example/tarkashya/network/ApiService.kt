package com.example.tarkashya.network

import com.example.tarkashya.models.RegistrationRequest
import com.example.tarkashya.models.UserDetailsModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    // --- Auth Endpoints ---
    @POST("api/auth/register")
    fun registerUser(@Body request: RegistrationRequest): Call<Map<String, String>>

    @POST("api/auth/login")
    fun loginUser(@Body loginRequest: Map<String, String>): Call<Map<String, Any>>

    // --- Emergency/Scanner Endpoints ---
    // This matches the fetchUserDetails call in your ScannerActivity
    @GET("api/v1/emergency/scan/{id}")
    fun getUserDetails(@Path("id") userId: String): Call<UserDetailsModel>
}