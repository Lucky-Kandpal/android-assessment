package com.example.androidassessment.view

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.ImageButton
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidassessment.R
import com.example.androidassessment.adapter.ScreenSessionAdapter
import com.example.androidassessment.viewmodel.ScreenOnOffViewModel
import com.example.androidassessment.utils.SharedPreferencesUtils

class SettingActivity : AppCompatActivity() {

    private lateinit var screenOnOffViewModel: ScreenOnOffViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ScreenSessionAdapter
    private lateinit var refreshButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_setting)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.setting)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.sessions_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ScreenSessionAdapter()
        recyclerView.adapter = adapter

        refreshButton = findViewById(R.id.refresh_button)
        refreshButton.setOnClickListener {
            loadScreenOnOffRecords()
        }

        screenOnOffViewModel = ViewModelProvider(this).get(ScreenOnOffViewModel::class.java)

        loadScreenOnOffRecords()
    }

    override fun onStart() {
        super.onStart()
        loadScreenOnOffRecords()
    }

    private fun loadScreenOnOffRecords() {
        val records = SharedPreferencesUtils.getAllScreenOnOffRecords(this)
        adapter.submitList(records)
    }
}
