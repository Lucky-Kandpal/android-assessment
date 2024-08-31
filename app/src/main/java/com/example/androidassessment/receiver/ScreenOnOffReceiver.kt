package com.example.androidassessment.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class ScreenOnOffReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) return

        val sharedPreferences = context.getSharedPreferences("ScreenTimePrefs", Context.MODE_PRIVATE)

        when (intent?.action) {
            Intent.ACTION_SCREEN_ON -> {
                val screenOnTime = System.currentTimeMillis()
                Log.d("screenOnTimeACTION_SCREEN_ON", "screenOnTime:- $screenOnTime")

                with(sharedPreferences.edit()) {
                    putLong("screenOnTime", screenOnTime)
                    apply()
                }
            }
            Intent.ACTION_SCREEN_OFF -> {
                val screenOffTime = System.currentTimeMillis()
                Log.d("beforeACTION_SCREEN_OFF", "screenOffTime:- $screenOffTime")

                val screenOnTime = sharedPreferences.getLong("screenOnTime", 0L)
                Log.d("beforeACTION_SCREEN_ON", "screenOnTime:- $screenOnTime")

                if (screenOnTime != 0L) {
                    val duration = screenOffTime - screenOnTime
                    Log.d("duration", "Screen on duration: $duration")
                    saveScreenOnOffRecord(context, screenOnTime, duration)
                } else {
                    Log.d("ScreenOnOffReceiver", "Screen on time was not set; skipping duration calculation.")
                }
            }
        }
    }

    private fun saveScreenOnOffRecord(context: Context, screenOnTime: Long, duration: Long) {
        val sharedPreferences = context.getSharedPreferences("ScreenTimePrefs", Context.MODE_PRIVATE)

        val screenOnTimeId = screenOnTime.toString()

        with(sharedPreferences.edit()) {
            putLong("${screenOnTimeId}_duration", duration)
            putLong("${screenOnTimeId}_startTime", screenOnTime)
            apply()
        }

        Log.d("ScreenOnOffRecord", "Saved record with ID: $screenOnTimeId, Duration: $duration")
    }
}
