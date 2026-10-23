package com.example.androiddevicemonitor.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingDao {
    @Query("SELECT * FROM accelerometer_readings ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<AccelerometerReading>>

    @Insert
    suspend fun insert(reading: AccelerometerReading)

    @Delete
    suspend fun delete(reading: AccelerometerReading)

    @Query("DELETE FROM accelerometer_readings")
    suspend fun clearAll()
}
