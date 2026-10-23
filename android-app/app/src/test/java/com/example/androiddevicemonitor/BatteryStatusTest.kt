package com.example.androiddevicemonitor

import android.os.BatteryManager
import com.example.androiddevicemonitor.data.BatteryStatus
import com.example.androiddevicemonitor.data.DeviceInfoRepository
import org.junit.Assert.assertEquals
import org.junit.Test

class BatteryStatusTest {
    @Test
    fun parsesStandardAndroidBatteryStatuses() {
        assertEquals(BatteryStatus.CHARGING, DeviceInfoRepository.parseBatteryStatus(BatteryManager.BATTERY_STATUS_CHARGING))
        assertEquals(BatteryStatus.DISCHARGING, DeviceInfoRepository.parseBatteryStatus(BatteryManager.BATTERY_STATUS_DISCHARGING))
        assertEquals(BatteryStatus.FULL, DeviceInfoRepository.parseBatteryStatus(BatteryManager.BATTERY_STATUS_FULL))
        assertEquals(BatteryStatus.NOT_CHARGING, DeviceInfoRepository.parseBatteryStatus(BatteryManager.BATTERY_STATUS_NOT_CHARGING))
    }

    @Test
    fun parsesUnknownOrInvalidStatusAsUnavailable() {
        assertEquals(BatteryStatus.UNAVAILABLE, DeviceInfoRepository.parseBatteryStatus(-1))
        assertEquals(BatteryStatus.UNAVAILABLE, DeviceInfoRepository.parseBatteryStatus(999))
    }
}
