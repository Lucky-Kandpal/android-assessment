package com.example.androidassessment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.example.androidassessment.R
import com.example.androidassessment.model.ScreenOnOffRecord

class ScreenSessionAdapter : ListAdapter<ScreenOnOffRecord, ScreenSessionViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScreenSessionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_screen_session, parent, false)
        return ScreenSessionViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScreenSessionViewHolder, position: Int) {
        val record = getItem(position)
        holder.bind(record)
    }

    private class DiffCallback : DiffUtil.ItemCallback<ScreenOnOffRecord>() {
        override fun areItemsTheSame(oldItem: ScreenOnOffRecord, newItem: ScreenOnOffRecord): Boolean {
            return oldItem.startTime == newItem.startTime
        }

        override fun areContentsTheSame(oldItem: ScreenOnOffRecord, newItem: ScreenOnOffRecord): Boolean {
            return oldItem == newItem
        }
    }
}
