package com.example.androidassessment.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.androidassessment.service.ScreenOnOffService
import com.example.androidassessment.utils.SharedPreferencesUtils

class ScreenOnOffViewModel(application: Application) : AndroidViewModel(application) {

    private val context: Context = application.applicationContext

    fun updateSwitchState(isTracking: Boolean) {
        viewModelScope.launch {
            if (isTracking) {
                startService()
            } else {
                stopService()
            }
            SharedPreferencesUtils.saveSwitchState(context, isTracking)

        }
    }

    fun getSwitchState(): Boolean {
        return SharedPreferencesUtils.getSwitchState(context)
    }


    private fun startService() {
        val intent = Intent(context, ScreenOnOffService::class.java)
        ContextCompat.startForegroundService(context, intent)
    }

    private fun stopService() {
        val intent = Intent(context, ScreenOnOffService::class.java)
        context.stopService(intent)
    }

}
