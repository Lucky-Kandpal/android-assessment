package com.example.androidassessment.view

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Switch
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.androidassessment.R
import com.example.androidassessment.viewmodel.LocationViewModel
import com.example.androidassessment.viewmodel.ScreenOnOffViewModel
import android.Manifest
import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission

class MainActivity : AppCompatActivity() {
    private lateinit var settingButton: ImageButton
    private lateinit var screenTimeSwitch: Switch
    private lateinit var locationViewModel: LocationViewModel
    private lateinit var screenOnOffViewModel: ScreenOnOffViewModel
    private lateinit var postButton: Button
    private lateinit var locationTextView: TextView
    private lateinit var locationPermissionRequest: ActivityResultLauncher<String>
    private lateinit var notificationPermissionRequest: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupNotificationPermissionRequest()

        checkAndRequestNotificationPermission()

        initializeViews()
        initializeViewModels()
        setupPermissionRequest()
        setupListeners()
        setupObservers()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationViewModel.fetchLocationData()
        } else {
            locationTextView.text = "Location permission not granted"
        }
        locationViewModel.locationSendResult.observe(this) { isSuccess ->
            if (isSuccess) {
                showAlertDialog("Success", "Location sent successfully!", "OK")
            } else {
                showAlertDialog("Failure", "Failed to send location.", "Retry")
            }
        }
    }
    private fun setupNotificationPermissionRequest() {
        notificationPermissionRequest = registerForActivityResult(RequestPermission()) { isGranted ->
            if (isGranted) {
            } else {
                showAlertDialog("Permission Denied", "Notification permission is required for the service to function properly.", "OK")
            }
        }
    }

    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(this, POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED -> {
                }
                shouldShowRequestPermissionRationale(POST_NOTIFICATIONS) -> {
                    showAlertDialog("Notification Permission", "This app requires notification permission to notify you of screen time updates.", "OK") {
                        notificationPermissionRequest.launch(POST_NOTIFICATIONS)
                    }
                }
                else -> {
                    notificationPermissionRequest.launch(POST_NOTIFICATIONS)
                }
            }
        }
    }

    private fun initializeViews() {
        settingButton = findViewById(R.id.setting_button)
        screenTimeSwitch = findViewById(R.id.screen_time_switch)
        postButton = findViewById(R.id.post_btn)
        locationTextView = findViewById(R.id.locationTextView)
    }

    private fun initializeViewModels() {
        locationViewModel = ViewModelProvider(this)[LocationViewModel::class.java]
        screenOnOffViewModel = ViewModelProvider(this)[ScreenOnOffViewModel::class.java]
    }

    private fun setupPermissionRequest() {
        locationPermissionRequest = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                locationViewModel.fetchLocationData()
            } else {
                locationTextView.text = "Location permission denied"
            }
        }
    }

    private fun setupListeners() {
        settingButton.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }

        screenTimeSwitch.setOnCheckedChangeListener { _, isChecked ->
            screenOnOffViewModel.updateSwitchState(isChecked)
            val message = if (isChecked) {
                "Tracking started. You'll receive updates in the settings activity after screen off/on."
            } else {
                "Tracking stopped."
            }
            showAlertDialog("Service Status", message, "OK")
        }

        postButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationViewModel.checkPermissionAndSendLocation()

            } else {
                locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun setupObservers() {
        screenTimeSwitch.setOnCheckedChangeListener(null)

        val isTracking = screenOnOffViewModel.getSwitchState()
        screenTimeSwitch.isChecked = isTracking

        screenTimeSwitch.setOnCheckedChangeListener { _, isChecked ->
            screenOnOffViewModel.updateSwitchState(isChecked)
            val message = if (isChecked) {
                "Tracking started. You'll receive updates in the settings activity after screen off/on."
            } else {
                "Tracking stopped."
            }
            showAlertDialog("Service Status", message, "OK")
        }

        locationViewModel.locationData.observe(this) { location ->
            Log.e("setupObserverslocation", location.toString())
            locationTextView.text = location ?: "Location not available"
        }
    }

    private fun showAlertDialog(title: String, message: String, positiveButtonText: String, onPositiveAction: (() -> Unit)? = null) {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButtonText) { dialog, _ ->
                dialog.dismiss()
                onPositiveAction?.invoke()
            }
            .create()

        alertDialog.show()

        val alertTitleId = this.resources.getIdentifier("alertTitle", "id", "android")
        alertDialog.findViewById<TextView>(alertTitleId)?.apply {
            setTextColor(getColorForCurrentMode())
        }

        alertDialog.findViewById<TextView>(android.R.id.message)?.apply {
            setTextColor(getColorForCurrentMode())
        }

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(getColorForCurrentMode())

        alertDialog.window?.setBackgroundDrawableResource(getBackgroundColorForCurrentMode())
    }


    private fun getColorForCurrentMode(): Int {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            ContextCompat.getColor(this, R.color.white)
        } else {
            ContextCompat.getColor(this, R.color.black)
        }
    }

    private fun getBackgroundColorForCurrentMode(): Int {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            R.color.black
        } else {
            R.color.white
        }
    }
}
