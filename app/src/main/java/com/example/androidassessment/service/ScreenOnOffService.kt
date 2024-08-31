

package com.example.androidassessment.service

import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import com.example.androidassessment.receiver.ScreenOnOffReceiver

import android.content.IntentFilter
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

class ScreenOnOffService : Service() {

    private lateinit var screenOnOffReceiver: ScreenOnOffReceiver

    override fun onCreate() {
        super.onCreate()
        screenOnOffReceiver = ScreenOnOffReceiver()
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android U
            ContextCompat.registerReceiver(
                this,
                screenOnOffReceiver,
                filter,
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
        } else {
            registerReceiver(screenOnOffReceiver, filter)
        }

        startForegroundService()
    }

    private fun startForegroundService() {
        val channelId = "ScreenOnOffServiceChannel"
        val notificationManager = getSystemService(NotificationManager::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Screen On/Off Tracking Service",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Screen On/Off Tracker")
            .setContentText("Tracking screen on/off time")
            .setSmallIcon(R.drawable.ic_notification_overlay)
            .build()

        startForeground(1, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(screenOnOffReceiver)
    }

    override fun onBind(intent: Intent?) = null
}
