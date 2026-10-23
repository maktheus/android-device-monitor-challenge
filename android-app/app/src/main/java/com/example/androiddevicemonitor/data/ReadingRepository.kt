package com.example.androiddevicemonitor.data

import kotlinx.coroutines.flow.Flow

interface ReadingRepository {
    fun observeAll(): Flow<List<AccelerometerReading>>
    suspend fun insert(reading: AccelerometerReading)
    suspend fun delete(reading: AccelerometerReading)
}

class RoomReadingRepository(private val readingDao: ReadingDao) : ReadingRepository {
    override fun observeAll(): Flow<List<AccelerometerReading>> = readingDao.observeAll()

    override suspend fun insert(reading: AccelerometerReading) {
        readingDao.insert(reading)
    }

    override suspend fun delete(reading: AccelerometerReading) {
        readingDao.delete(reading)
    }
}
