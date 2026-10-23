package com.example.androiddevicemonitor.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accelerometer_readings")
data class AccelerometerReading(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val axisX: Float,
    val axisY: Float,
    val axisZ: Float
)

data class LiveReading(
    val timestamp: Long = 0,
    val axisX: Float = 0f,
    val axisY: Float = 0f,
    val axisZ: Float = 0f
)

fun LiveReading.toEntity(): AccelerometerReading = AccelerometerReading(
    timestamp = timestamp,
    axisX = axisX,
    axisY = axisY,
    axisZ = axisZ
)
