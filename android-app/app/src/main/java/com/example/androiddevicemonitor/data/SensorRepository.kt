package com.example.androiddevicemonitor.data

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SensorRepository(context: Context) : SensorEventListener {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val _reading = MutableStateFlow<LiveReading?>(null)

    val reading: StateFlow<LiveReading?> = _reading
    val isAvailable: Boolean get() = accelerometer != null
    var isMonitoring: Boolean = false
        private set

    fun start() {
        if (accelerometer == null || isMonitoring) return
        isMonitoring = sensorManager.registerListener(
            this,
            accelerometer,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    fun stop() {
        sensorManager.unregisterListener(this)
        isMonitoring = false
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type != Sensor.TYPE_ACCELEROMETER) return
        _reading.value = LiveReading(
            timestamp = event.timestamp,
            axisX = event.values[0],
            axisY = event.values[1],
            axisZ = event.values[2]
        )
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}
