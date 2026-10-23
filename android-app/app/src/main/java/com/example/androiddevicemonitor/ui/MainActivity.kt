package com.example.androiddevicemonitor.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.androiddevicemonitor.R
import com.example.androiddevicemonitor.data.AccelerometerReading
import com.example.androiddevicemonitor.data.ReadingFormatter
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val viewModel: MonitorViewModel by viewModels()
    private lateinit var monitorPanel: View
    private lateinit var devicePanel: View
    private lateinit var historyPanel: View
    private lateinit var monitorStatus: TextView
    private lateinit var monitorMessage: TextView
    private lateinit var toggleButton: Button
    private lateinit var historyList: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindViews()
        bindActions()
        collectState()
        showPanel(monitorPanel)
    }

    override fun onPause() {
        viewModel.stopMonitoring()
        super.onPause()
    }

    private fun bindViews() {
        monitorPanel = findViewById(R.id.monitor_panel)
        devicePanel = findViewById(R.id.device_panel)
        historyPanel = findViewById(R.id.history_panel)
        monitorStatus = findViewById(R.id.monitor_status)
        monitorMessage = findViewById(R.id.monitor_message)
        toggleButton = findViewById(R.id.toggle_monitoring)
        historyList = findViewById(R.id.history_list)
    }

    private fun bindActions() {
        findViewById<Button>(R.id.tab_monitor).setOnClickListener { showPanel(monitorPanel) }
        findViewById<Button>(R.id.tab_device).setOnClickListener {
            viewModel.refreshDeviceInfo()
            showPanel(devicePanel)
        }
        findViewById<Button>(R.id.tab_history).setOnClickListener { showPanel(historyPanel) }
        toggleButton.setOnClickListener {
            if (viewModel.isMonitoring.value) viewModel.stopMonitoring() else viewModel.startMonitoring()
        }
        findViewById<Button>(R.id.save_reading).setOnClickListener { viewModel.saveCurrentReading() }
    }

    private fun collectState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.liveReading.collect { renderReading(it) } }
                launch { viewModel.isMonitoring.collect { renderMonitoringState(it) } }
                launch { viewModel.deviceInfo.collect { renderDevice(it) } }
                launch { viewModel.history.collect { renderHistory(it) } }
                launch {
                    viewModel.message.collect { message ->
                        monitorMessage.text = message.orEmpty()
                        if (message != null) Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                        viewModel.clearMessage()
                    }
                }
            }
        }
    }

    private fun renderReading(reading: com.example.androiddevicemonitor.data.LiveReading?) {
        val current = reading ?: return
        findViewById<TextView>(R.id.value_x).text = "X  ${ReadingFormatter.axis(current.axisX)}"
        findViewById<TextView>(R.id.value_y).text = "Y  ${ReadingFormatter.axis(current.axisY)}"
        findViewById<TextView>(R.id.value_z).text = "Z  ${ReadingFormatter.axis(current.axisZ)}"
        findViewById<TextView>(R.id.reading_timestamp).text = "Timestamp: ${ReadingFormatter.timestamp(current.timestamp)}"
    }

    private fun renderMonitoringState(active: Boolean) {
        monitorStatus.text = getString(if (active) R.string.capture_running else R.string.capture_stopped)
        toggleButton.text = getString(if (active) R.string.stop_capture else R.string.start_capture)
    }

    private fun renderDevice(info: com.example.androiddevicemonitor.data.DeviceInfo) {
        findViewById<TextView>(R.id.device_values).text = buildString {
            append("${getString(R.string.manufacturer)}: ${info.manufacturer}\n")
            append("${getString(R.string.model)}: ${info.model}\n")
            append("${getString(R.string.android_version)}: ${info.androidVersion}\n")
            append("${getString(R.string.api_level)}: ${info.apiLevel}")
        }
        findViewById<TextView>(R.id.battery_values).text = buildString {
            append("Percentual: ${ReadingFormatter.battery(info.batteryPercent)}\n")
            append("Estado: ${if (info.isCharging) getString(R.string.charging) else getString(R.string.not_charging)}\n")
            append("${getString(R.string.power_source)}: ${info.powerSource ?: getString(R.string.battery_unknown)}")
        }
    }

    private fun renderHistory(readings: List<AccelerometerReading>) {
        historyList.removeAllViews()
        if (readings.isEmpty()) {
            historyList.addView(TextView(this).apply {
                text = getString(R.string.history_empty)
                setTextColor(getColor(R.color.muted))
                textSize = 16f
            })
            return
        }
        readings.forEach { reading ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(0, 14, 0, 14)
            }
            val details = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                text = "${ReadingFormatter.axis(reading.axisX)}  ${ReadingFormatter.axis(reading.axisY)}  ${ReadingFormatter.axis(reading.axisZ)}\n${ReadingFormatter.timestamp(reading.timestamp)}"
                setTextColor(getColor(R.color.paper))
                textSize = 14f
            }
            val delete = Button(this).apply {
                text = getString(R.string.delete)
                isAllCaps = false
                setOnClickListener { viewModel.deleteReading(reading.id) }
            }
            row.addView(details)
            row.addView(delete)
            historyList.addView(row)
        }
    }

    private fun showPanel(selected: View) {
        monitorPanel.visibility = if (selected == monitorPanel) View.VISIBLE else View.GONE
        devicePanel.visibility = if (selected == devicePanel) View.VISIBLE else View.GONE
        historyPanel.visibility = if (selected == historyPanel) View.VISIBLE else View.GONE
    }
}
