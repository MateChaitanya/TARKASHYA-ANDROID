package com.example.tarkashya.repository


import com.example.tarkashya.models.RegistrationRequest
import com.example.tarkashya.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrationRepository {
    fun sendRegistrationData(data: RegistrationRequest, onResult: (String?) -> Unit) {
        RetrofitClient.instance.registerUser(data).enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                if (response.isSuccessful) {
                    onResult(response.body()?.get("message"))
                } else {
                    onResult("Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                onResult("Failure: ${t.message}")
            }
        })
    }
}