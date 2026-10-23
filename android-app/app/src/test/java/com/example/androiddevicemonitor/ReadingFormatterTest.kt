package com.example.androiddevicemonitor

import com.example.androiddevicemonitor.data.BatteryStatus
import com.example.androiddevicemonitor.data.ReadingFormatter
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.TimeZone

class ReadingFormatterTest {
    @Test
    fun formatsAxisWithSignAndTwoDecimalPlaces() {
        assertEquals("+0.42", ReadingFormatter.axis(0.42f))
        assertEquals("-9.71", ReadingFormatter.axis(-9.71f))
        assertEquals("+0.00", ReadingFormatter.axis(0.0001f))
    }

    @Test
    fun formatsUnavailableBatteryExplicitly() {
        assertEquals("Indisponível", ReadingFormatter.battery(null))
        assertEquals("87%", ReadingFormatter.battery(87))
        assertEquals("0%", ReadingFormatter.battery(0))
        assertEquals("100%", ReadingFormatter.battery(100))
    }

    @Test
    fun formatsBatteryStatusLabelsCorrectly() {
        assertEquals("Carregando", ReadingFormatter.batteryStatus(BatteryStatus.CHARGING))
        assertEquals("Descarregando", ReadingFormatter.batteryStatus(BatteryStatus.DISCHARGING))
        assertEquals("Carga completa", ReadingFormatter.batteryStatus(BatteryStatus.FULL))
        assertEquals("Não carregando", ReadingFormatter.batteryStatus(BatteryStatus.NOT_CHARGING))
        assertEquals("Indisponível", ReadingFormatter.batteryStatus(BatteryStatus.UNAVAILABLE))
    }

    @Test
    fun formatsTimestampCorrectly() {
        assertEquals("—", ReadingFormatter.timestamp(0))
        assertEquals("—", ReadingFormatter.timestamp(-100))

        val utc = TimeZone.getTimeZone("UTC")
        // 1700000000000L = 2023-11-14 22:13:20 UTC
        assertEquals("14/11/2023 22:13:20", ReadingFormatter.timestamp(1700000000000L, utc))
    }
}
