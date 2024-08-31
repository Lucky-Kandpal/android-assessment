package com.example.androidassessment.repository

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.androidassessment.api.ApiService
import com.example.androidassessment.model.Location as LocationModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LocationRepository(private val context: Context) {

    private val apiService: ApiService
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://123.253.10.229:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(ApiService::class.java)
    }

    fun getLocationData(callback: (String?) -> Unit) {
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                callback(location?.let { "Latitude: ${it.latitude}, Longitude: ${it.longitude}" })
                    ?: callback("Location not available")
            }
        } else {
            callback(null)
        }
    }

    fun checkAndSendLocation(callback: (Boolean) -> Unit) {
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                val locationData = location?.let { LocationModel("Latitude: ${it.latitude}, Longitude: ${it.longitude}") }
                if (locationData != null) {
                    Log.e("checkAndSendLocation","locationData:- $locationData")
                    apiService.sendLocation(locationData).enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            Log.e("checkAndSendLocation","onResponse: Success locationData:- $locationData")

                            callback(response.isSuccessful)
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Log.e("checkAndSendLocation","onFailure: Failure locationData:- $locationData")

                            callback(false)
                        }
                    })
                } else {
                    callback(false)
                }
            }
        } else {
            callback(false)
        }
    }
}
