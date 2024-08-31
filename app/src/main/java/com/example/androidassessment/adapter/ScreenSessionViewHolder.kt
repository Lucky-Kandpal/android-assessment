package com.example.androidassessment.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.androidassessment.R
import com.example.androidassessment.model.ScreenOnOffRecord
import java.text.SimpleDateFormat
import java.util.Locale

class ScreenSessionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val startTimeTextView: TextView = itemView.findViewById(R.id.start_time_text)
    private val durationTextView: TextView = itemView.findViewById(R.id.duration_text)

    fun bind(record: ScreenOnOffRecord) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        startTimeTextView.text = "Start Time: ${dateFormat.format(record.startTime)}"
        durationTextView.text = "Duration: ${formatDuration(record.duration)}"
    }

    private fun formatDuration(durationMs: Long): String {
        val seconds = (durationMs / 1000) % 60
        val minutes = (durationMs / (1000 * 60)) % 60
        val hours = (durationMs / (1000 * 60 * 60)) % 24
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}


