package com.example.androiddevicemonitor.data

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build

data class DeviceInfo(
    val manufacturer: String,
    val model: String,
    val androidVersion: String,
    val apiLevel: Int,
    val batteryPercent: Int?,
    val batteryStatus: BatteryStatus,
    val powerSource: String?
) {
    val isCharging: Boolean
        get() = batteryStatus == BatteryStatus.CHARGING || batteryStatus == BatteryStatus.FULL
}

class DeviceInfoRepository(private val context: Context) {
    fun read(): DeviceInfo {
        val battery = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = battery?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = battery?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val percent = if (level >= 0 && scale > 0) (level * 100 / scale) else null

        val statusExtra = battery?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val status = parseBatteryStatus(statusExtra)

        val plugged = battery?.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) ?: 0
        val source = when (plugged) {
            BatteryManager.BATTERY_PLUGGED_AC -> "AC"
            BatteryManager.BATTERY_PLUGGED_USB -> "USB"
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Sem fio"
            else -> null
        }

        return DeviceInfo(
            manufacturer = Build.MANUFACTURER,
            model = Build.MODEL,
            androidVersion = Build.VERSION.RELEASE,
            apiLevel = Build.VERSION.SDK_INT,
            batteryPercent = percent,
            batteryStatus = status,
            powerSource = source
        )
    }

    companion object {
        fun parseBatteryStatus(statusCode: Int): BatteryStatus = when (statusCode) {
            BatteryManager.BATTERY_STATUS_CHARGING -> BatteryStatus.CHARGING
            BatteryManager.BATTERY_STATUS_DISCHARGING -> BatteryStatus.DISCHARGING
            BatteryManager.BATTERY_STATUS_FULL -> BatteryStatus.FULL
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> BatteryStatus.NOT_CHARGING
            else -> BatteryStatus.UNAVAILABLE
        }
    }
}
