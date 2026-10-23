package com.example.androiddevicemonitor

import com.example.androiddevicemonitor.data.LiveReading
import com.example.androiddevicemonitor.data.toEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class ReadingModelTest {
    @Test
    fun convertsLiveReadingToEntityAccurately() {
        val live = LiveReading(
            timestamp = 1710000000000L,
            axisX = 1.25f,
            axisY = -9.81f,
            axisZ = 0.04f
        )
        val entity = live.toEntity()

        assertEquals(0L, entity.id)
        assertEquals(1710000000000L, entity.timestamp)
        assertEquals(1.25f, entity.axisX, 0.0001f)
        assertEquals(-9.81f, entity.axisY, 0.0001f)
        assertEquals(0.04f, entity.axisZ, 0.0001f)
    }

    @Test
    fun defaultLiveReadingHasZeroValues() {
        val defaultLive = LiveReading()
        assertEquals(0L, defaultLive.timestamp)
        assertEquals(0f, defaultLive.axisX, 0.0001f)
        assertEquals(0f, defaultLive.axisY, 0.0001f)
        assertEquals(0f, defaultLive.axisZ, 0.0001f)
    }
}
