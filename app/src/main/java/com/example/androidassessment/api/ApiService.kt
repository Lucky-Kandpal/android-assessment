package com.example.androidassessment.api

import com.example.androidassessment.model.Location
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("location")
    fun sendLocation(@Body location: Location): Call<Void>
}
