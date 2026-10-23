package com.example.androiddevicemonitor.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.androiddevicemonitor.data.AppDatabase
import com.example.androiddevicemonitor.data.DeviceInfo
import com.example.androiddevicemonitor.data.DeviceInfoRepository
import com.example.androiddevicemonitor.data.LiveReading
import com.example.androiddevicemonitor.data.ReadingRepository
import com.example.androiddevicemonitor.data.RoomReadingRepository
import com.example.androiddevicemonitor.data.SensorRepository
import com.example.androiddevicemonitor.data.toEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MonitorViewModel(
    application: Application,
    private val sensorRepository: SensorRepository,
    private val deviceRepository: DeviceInfoRepository,
    private val readingRepository: ReadingRepository
) : AndroidViewModel(application) {

    constructor(application: Application) : this(
        application = application,
        sensorRepository = SensorRepository(application),
        deviceRepository = DeviceInfoRepository(application),
        readingRepository = RoomReadingRepository(AppDatabase.get(application).readingDao())
    )

    val liveReading: StateFlow<LiveReading?> = sensorRepository.reading
    val isSensorAvailable: Boolean get() = sensorRepository.isAvailable

    val history = readingRepository.observeAll().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    private val _deviceInfo = MutableStateFlow(deviceRepository.read())
    val deviceInfo: StateFlow<DeviceInfo> = _deviceInfo

    private val _isMonitoring = MutableStateFlow(false)
    val isMonitoring: StateFlow<Boolean> = _isMonitoring

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    fun startMonitoring() {
        if (!sensorRepository.isAvailable) {
            _message.value = "Acelerômetro indisponível neste dispositivo"
            return
        }
        val started = sensorRepository.start()
        _isMonitoring.value = started
        if (started) {
            _message.value = null
        } else {
            _message.value = "Não foi possível iniciar a captura"
        }
    }

    fun stopMonitoring() {
        sensorRepository.stop()
        _isMonitoring.value = false
    }

    fun saveCurrentReading() {
        val current = liveReading.value
        if (current == null) {
            _message.value = "Inicie a captura antes de salvar uma leitura"
            return
        }
        viewModelScope.launch {
            runCatching { readingRepository.insert(current.toEntity()) }
                .onSuccess { _message.value = "Leitura salva" }
                .onFailure { _message.value = "Não foi possível salvar a leitura" }
        }
    }

    fun deleteReading(id: Long) {
        viewModelScope.launch {
            history.value.firstOrNull { it.id == id }?.let {
                runCatching { readingRepository.delete(it) }
            }
        }
    }

    fun refreshDeviceInfo() {
        _deviceInfo.value = deviceRepository.read()
    }

    fun clearMessage() {
        _message.value = null
    }

    override fun onCleared() {
        sensorRepository.stop()
        super.onCleared()
    }
}
