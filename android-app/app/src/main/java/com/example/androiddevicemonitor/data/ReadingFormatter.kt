package com.example.androiddevicemonitor.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object ReadingFormatter {
    fun axis(value: Float): String = String.format(Locale.US, "%+.2f", value)

    fun timestamp(value: Long, timeZone: TimeZone = TimeZone.getDefault()): String {
        if (value <= 0) return "—"
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US)
        formatter.timeZone = timeZone
        return formatter.format(Date(value))
    }

    fun battery(value: Int?): String = value?.let { "$it%" } ?: "Indisponível"

    fun batteryStatus(status: BatteryStatus): String = status.label

    fun magnitude(x: Float, y: Float, z: Float): Float =
        kotlin.math.sqrt(x * x + y * y + z * z)

    fun magnitudeString(x: Float, y: Float, z: Float): String =
        String.format(Locale.US, "%.2f m/s²", magnitude(x, y, z))
}
