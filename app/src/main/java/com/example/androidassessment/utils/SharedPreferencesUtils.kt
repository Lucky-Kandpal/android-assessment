package com.example.androidassessment.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.androidassessment.model.ScreenOnOffRecord

object SharedPreferencesUtils {

    private const val PREFS_NAME = "screen_time_prefs"
    private const val KEY_SWITCH_STATE = "switch_state"  // Add this key

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getAllScreenOnOffRecords(context: Context): List<ScreenOnOffRecord> {
        val sharedPreferences = context.getSharedPreferences("ScreenTimePrefs", Context.MODE_PRIVATE)
        val records = mutableListOf<ScreenOnOffRecord>()

        val allEntries = sharedPreferences.all
        for (entry in allEntries) {
            val key = entry.key
            if (key.endsWith("_duration")) {
                val startTimeKey = key.replace("_duration", "_startTime")
                val duration = sharedPreferences.getLong(key, 0L)
                val startTime = sharedPreferences.getLong(startTimeKey, 0L)
                val endTime = startTime + duration

                if (startTime != 0L && duration != 0L) {
                    records.add(ScreenOnOffRecord(startTime, endTime))
                }
            }
        }
        return records.sortedBy { it.startTime }
    }



    fun saveSwitchState(context: Context, isTracking: Boolean) {
        getSharedPreferences(context).edit().putBoolean(KEY_SWITCH_STATE, isTracking).apply()
    }

    fun getSwitchState(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(KEY_SWITCH_STATE, false)
    }
}
