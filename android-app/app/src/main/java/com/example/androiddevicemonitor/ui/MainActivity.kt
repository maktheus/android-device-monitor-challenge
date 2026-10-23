package com.example.androiddevicemonitor.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.androiddevicemonitor.R
import com.example.androiddevicemonitor.data.AccelerometerReading
import com.example.androiddevicemonitor.data.DeviceInfo
import com.example.androiddevicemonitor.data.LiveReading
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
    private lateinit var saveButton: Button
    private lateinit var historyList: LinearLayout
    private lateinit var tabMonitor: Button
    private lateinit var tabDevice: Button
    private lateinit var tabHistory: Button

    private lateinit var historyChart: AccelerometerChartView
    private lateinit var historyCountBadge: TextView
    private lateinit var statMeanX: TextView
    private lateinit var statMeanY: TextView
    private lateinit var statMeanZ: TextView
    private lateinit var statMeanMag: TextView
    private lateinit var btnExportCsv: Button
    private lateinit var btnClearHistory: Button
    private var currentHistoryReadings: List<AccelerometerReading> = emptyList()

    private lateinit var backCallback: OnBackPressedCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindViews()
        setupBackNavigation()
        bindActions()
        collectState()
        showPanel(monitorPanel)
        checkSensorAvailability()
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
        saveButton = findViewById(R.id.save_reading)
        historyList = findViewById(R.id.history_list)
        tabMonitor = findViewById(R.id.tab_monitor)
        tabDevice = findViewById(R.id.tab_device)
        tabHistory = findViewById(R.id.tab_history)

        historyChart = findViewById(R.id.history_chart)
        historyCountBadge = findViewById(R.id.history_count_badge)
        statMeanX = findViewById(R.id.stat_mean_x)
        statMeanY = findViewById(R.id.stat_mean_y)
        statMeanZ = findViewById(R.id.stat_mean_z)
        statMeanMag = findViewById(R.id.stat_mean_mag)
        btnExportCsv = findViewById(R.id.btn_export_csv)
        btnClearHistory = findViewById(R.id.btn_clear_history)
    }

    private fun setupBackNavigation() {
        backCallback = object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                showPanel(monitorPanel)
            }
        }
        onBackPressedDispatcher.addCallback(this, backCallback)
    }

    private fun checkSensorAvailability() {
        if (!viewModel.isSensorAvailable) {
            monitorStatus.text = getString(R.string.sensor_unavailable)
            toggleButton.isEnabled = false
            saveButton.isEnabled = false
        }
    }

    private fun bindActions() {
        tabMonitor.setOnClickListener { showPanel(monitorPanel) }
        tabDevice.setOnClickListener {
            viewModel.refreshDeviceInfo()
            showPanel(devicePanel)
        }
        tabHistory.setOnClickListener { showPanel(historyPanel) }

        toggleButton.setOnClickListener {
            if (viewModel.isMonitoring.value) {
                viewModel.stopMonitoring()
            } else {
                viewModel.startMonitoring()
            }
        }

        saveButton.setOnClickListener { viewModel.saveCurrentReading() }

        btnExportCsv.setOnClickListener {
            exportHistoryCsv(currentHistoryReadings)
        }
        btnClearHistory.setOnClickListener {
            viewModel.clearHistory()
        }
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
                        if (message != null) {
                            Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                            viewModel.clearMessage()
                        }
                    }
                }
            }
        }
    }

    private fun renderReading(reading: LiveReading?) {
        val current = reading ?: return
        findViewById<TextView>(R.id.value_x).text = "X  ${ReadingFormatter.axis(current.axisX)}"
        findViewById<TextView>(R.id.value_y).text = "Y  ${ReadingFormatter.axis(current.axisY)}"
        findViewById<TextView>(R.id.value_z).text = "Z  ${ReadingFormatter.axis(current.axisZ)}"
        findViewById<TextView>(R.id.reading_timestamp).text =
            "Timestamp: ${ReadingFormatter.timestamp(current.timestamp)}"
    }

    private fun renderMonitoringState(active: Boolean) {
        if (!viewModel.isSensorAvailable) {
            monitorStatus.text = getString(R.string.sensor_unavailable)
            toggleButton.isEnabled = false
            saveButton.isEnabled = false
            return
        }
        monitorStatus.text = getString(if (active) R.string.capture_running else R.string.capture_stopped)
        toggleButton.text = getString(if (active) R.string.stop_capture else R.string.start_capture)
    }

    private fun renderDevice(info: DeviceInfo) {
        findViewById<TextView>(R.id.device_values).text = buildString {
            append("${getString(R.string.manufacturer)}: ${info.manufacturer}\n")
            append("${getString(R.string.model)}: ${info.model}\n")
            append("${getString(R.string.android_version)}: ${info.androidVersion}\n")
            append("${getString(R.string.api_level)}: ${info.apiLevel}")
        }
        findViewById<TextView>(R.id.battery_values).text = buildString {
            append("Percentual: ${ReadingFormatter.battery(info.batteryPercent)}\n")
            append("Estado: ${ReadingFormatter.batteryStatus(info.batteryStatus)}\n")
            append("${getString(R.string.power_source)}: ${info.powerSource ?: getString(R.string.battery_unknown)}")
        }
    }

    private fun renderHistory(readings: List<AccelerometerReading>) {
        currentHistoryReadings = readings
        historyCountBadge.text = "${readings.size} Amostras"
        historyChart.setReadings(readings)

        historyList.removeAllViews()

        if (readings.isEmpty()) {
            statMeanX.text = "+0.00"
            statMeanY.text = "+0.00"
            statMeanZ.text = "+0.00"
            statMeanMag.text = "0.00"
            btnExportCsv.isEnabled = false
            btnClearHistory.isEnabled = false

            historyList.addView(TextView(this).apply {
                text = getString(R.string.history_empty)
                setTextColor(getColor(R.color.muted))
                textSize = 15f
                setPadding(0, 16, 0, 16)
            })
            return
        }

        btnExportCsv.isEnabled = true
        btnClearHistory.isEnabled = true

        val meanX = readings.map { it.axisX }.average().toFloat()
        val meanY = readings.map { it.axisY }.average().toFloat()
        val meanZ = readings.map { it.axisZ }.average().toFloat()
        val meanMag = readings.map { ReadingFormatter.magnitude(it.axisX, it.axisY, it.axisZ) }.average().toFloat()

        statMeanX.text = ReadingFormatter.axis(meanX)
        statMeanY.text = ReadingFormatter.axis(meanY)
        statMeanZ.text = ReadingFormatter.axis(meanZ)
        statMeanMag.text = String.format(java.util.Locale.US, "%.2f", meanMag)

        // Cards for each reading
        readings.forEach { reading ->
            val mag = ReadingFormatter.magnitude(reading.axisX, reading.axisY, reading.axisZ)
            val card = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setBackgroundColor(getColor(R.color.navy_900))
                setPadding(28, 22, 28, 22)
                val lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 0, 20)
                }
                layoutParams = lp
            }

            // Header: Timestamp + Magnitude Badge
            val headerRow = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
            }
            val timestampView = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                text = ReadingFormatter.timestamp(reading.timestamp)
                setTextColor(getColor(R.color.muted))
                textSize = 12f
            }
            val magView = TextView(this).apply {
                text = String.format(java.util.Locale.US, "||a|| = %.2f m/s²", mag)
                setTextColor(getColor(R.color.cyan_400))
                textSize = 13f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }
            headerRow.addView(timestampView)
            headerRow.addView(magView)
            card.addView(headerRow)

            // Visual Bars for X, Y, Z
            val axesRow = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(0, 18, 0, 14)
            }
            axesRow.addView(createAxisVisual("X", reading.axisX, getColor(R.color.cyan_400)))
            axesRow.addView(createAxisVisual("Y", reading.axisY, getColor(R.color.orange_500)))
            axesRow.addView(createAxisVisual("Z", reading.axisZ, getColor(R.color.paper)))
            card.addView(axesRow)

            // Bottom action: Delete button
            val footerRow = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = android.view.Gravity.END
            }
            val deleteBtn = Button(this).apply {
                text = getString(R.string.delete)
                isAllCaps = false
                textSize = 12f
                setOnClickListener { viewModel.deleteReading(reading.id) }
            }
            footerRow.addView(deleteBtn)
            card.addView(footerRow)

            historyList.addView(card)
        }
    }

    private fun exportHistoryCsv(readings: List<AccelerometerReading>) {
        if (readings.isEmpty()) return
        val csv = buildString {
            append("ID,Timestamp,Data_Hora,Eixo_X,Eixo_Y,Eixo_Z,Magnitude\n")
            readings.forEach { r ->
                val mag = ReadingFormatter.magnitude(r.axisX, r.axisY, r.axisZ)
                val dt = ReadingFormatter.timestamp(r.timestamp)
                append("${r.id},${r.timestamp},\"$dt\",${r.axisX},${r.axisY},${r.axisZ},${String.format(java.util.Locale.US, "%.2f", mag)}\n")
            }
        }
        val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(android.content.Intent.EXTRA_SUBJECT, "Leituras Acelerômetro - Android Device Monitor")
            putExtra(android.content.Intent.EXTRA_TEXT, csv)
        }
        startActivity(android.content.Intent.createChooser(intent, "Exportar Leituras (CSV)"))
    }

    private fun createAxisVisual(label: String, value: Float, color: Int): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                setMargins(6, 0, 6, 0)
            }
            val text = TextView(this@MainActivity).apply {
                text = "$label ${ReadingFormatter.axis(value)}"
                setTextColor(color)
                textSize = 13f
            }
            val barBg = LinearLayout(this@MainActivity).apply {
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 8).apply {
                    topMargin = 8
                }
                setBackgroundColor(getColor(R.color.line))
            }
            val ratio = (kotlin.math.abs(value) / 15f).coerceIn(0.05f, 1f)
            val barFill = View(this@MainActivity).apply {
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, ratio)
                setBackgroundColor(color)
            }
            val barSpacer = View(this@MainActivity).apply {
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f - ratio)
            }
            barBg.addView(barFill)
            barBg.addView(barSpacer)

            addView(text)
            addView(barBg)
        }
    }

    private fun showPanel(selected: View) {
        monitorPanel.visibility = if (selected == monitorPanel) View.VISIBLE else View.GONE
        devicePanel.visibility = if (selected == devicePanel) View.VISIBLE else View.GONE
        historyPanel.visibility = if (selected == historyPanel) View.VISIBLE else View.GONE

        backCallback.isEnabled = (selected != monitorPanel)

        tabMonitor.alpha = if (selected == monitorPanel) 1.0f else 0.65f
        tabDevice.alpha = if (selected == devicePanel) 1.0f else 0.65f
        tabHistory.alpha = if (selected == historyPanel) 1.0f else 0.65f
    }
}
