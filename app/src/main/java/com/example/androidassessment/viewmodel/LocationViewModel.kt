package com.example.androidassessment.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.androidassessment.repository.LocationRepository
import kotlinx.coroutines.launch

class LocationViewModel(application: Application) : AndroidViewModel(application) {

    private val locationRepository: LocationRepository =
        LocationRepository(application.applicationContext)

    private val _locationData = MutableLiveData<String?>()
    val locationData: LiveData<String?> = _locationData

    private val _locationSendResult = MutableLiveData<Boolean>()
    val locationSendResult: LiveData<Boolean> = _locationSendResult

    fun fetchLocationData() {
        viewModelScope.launch {
            locationRepository.getLocationData { location ->
                _locationData.postValue(location ?: "Location permission not granted or location not available")
            }
        }
    }

    fun checkPermissionAndSendLocation() {
        viewModelScope.launch {
            locationRepository.checkAndSendLocation { isSuccess ->
                _locationSendResult.postValue(isSuccess)
            }
        }
    }
}
