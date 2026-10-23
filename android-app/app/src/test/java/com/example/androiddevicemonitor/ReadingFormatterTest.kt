package com.example.androiddevicemonitor

import com.example.androiddevicemonitor.data.ReadingFormatter
import org.junit.Assert.assertEquals
import org.junit.Test

class ReadingFormatterTest {
    @Test
    fun formatsAxisWithSignAndTwoDecimalPlaces() {
        assertEquals("+0.42", ReadingFormatter.axis(0.42f))
        assertEquals("-9.71", ReadingFormatter.axis(-9.71f))
    }

    @Test
    fun formatsUnavailableBatteryExplicitly() {
        assertEquals("Indisponível", ReadingFormatter.battery(null))
        assertEquals("87%", ReadingFormatter.battery(87))
    }
}
