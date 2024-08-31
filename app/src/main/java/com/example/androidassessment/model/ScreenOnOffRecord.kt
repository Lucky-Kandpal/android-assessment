package com.example.androidassessment.model

data class ScreenOnOffRecord(
    val startTime: Long,
    val endTime: Long
) {
    val duration: Long
        get() = endTime - startTime
}
