package com.example.androiddevicemonitor.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import com.example.androiddevicemonitor.data.AccelerometerReading
import com.example.androiddevicemonitor.data.ReadingFormatter
import kotlin.math.abs
import kotlin.math.max

/**
 * Custom View that renders multi-axis accelerometer curves (X, Y, Z and Magnitude ||a||)
 * along with gravity and neutral reference lines.
 */
class AccelerometerChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var readings: List<AccelerometerReading> = emptyList()

    private val density = context.resources.displayMetrics.density

    private fun dp(value: Float): Float = value * density
    private fun sp(value: Float): Float = value * context.resources.displayMetrics.scaledDensity

    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#1E2D42")
        strokeWidth = dp(1f)
        style = Paint.Style.STROKE
    }

    private val dashedGridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#263852")
        strokeWidth = dp(1f)
        style = Paint.Style.STROKE
        pathEffect = DashPathEffect(floatArrayOf(dp(4f), dp(4f)), 0f)
    }

    private val zeroLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#485A75")
        strokeWidth = dp(1.5f)
        style = Paint.Style.STROKE
    }

    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#89929C")
        textSize = sp(9f)
        typeface = android.graphics.Typeface.MONOSPACE
    }

    private val textMsgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#89929C")
        textSize = sp(12f)
        textAlign = Paint.Align.CENTER
    }

    private val pathPaintX = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#78E0DB") // Cyan
        strokeWidth = dp(2.5f)
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val pathPaintY = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FF7138") // Orange
        strokeWidth = dp(2.5f)
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val pathPaintZ = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#EEF2EE") // Paper / White
        strokeWidth = dp(2.5f)
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val pathPaintMag = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#34D399") // Emerald
        strokeWidth = dp(2f)
        style = Paint.Style.STROKE
        pathEffect = DashPathEffect(floatArrayOf(dp(6f), dp(4f)), 0f)
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val pointPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val pathX = Path()
    private val pathY = Path()
    private val pathZ = Path()
    private val pathMag = Path()

    fun setReadings(data: List<AccelerometerReading>) {
        this.readings = data
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val leftPadding = dp(62f)
        val rightPadding = dp(14f)
        val topPadding = dp(16f)
        val bottomPadding = dp(16f)

        val chartWidth = width - leftPadding - rightPadding
        val chartHeight = height - topPadding - bottomPadding

        if (chartWidth <= 0 || chartHeight <= 0) return

        // Compute max scale range (at least 15 m/s² for ±10 m/s² gravity line)
        var maxVal = 15f
        if (readings.isNotEmpty()) {
            val highest = readings.maxOf { r ->
                val mag = ReadingFormatter.magnitude(r.axisX, r.axisY, r.axisZ)
                maxOf(abs(r.axisX), abs(r.axisY), abs(r.axisZ), mag)
            }
            if (highest > maxVal) {
                maxVal = highest * 1.15f
            }
        }

        val centerY = topPadding + (chartHeight / 2f)
        val halfHeight = chartHeight / 2f

        fun valToY(v: Float): Float {
            val clamped = v.coerceIn(-maxVal, maxVal)
            return centerY - (clamped / maxVal) * halfHeight
        }

        // Draw horizontal reference lines
        // 0 m/s²
        canvas.drawLine(leftPadding, centerY, width - rightPadding, centerY, zeroLinePaint)
        canvas.drawText("0 m/s²", dp(8f), centerY + dp(3f), labelPaint)

        // +10 m/s² (Gravity standard positive)
        val y10 = valToY(10f)
        canvas.drawLine(leftPadding, y10, width - rightPadding, y10, dashedGridPaint)
        canvas.drawText("+10 m/s²", dp(8f), y10 + dp(3f), labelPaint)

        // -10 m/s² (Gravity standard negative)
        val yMinus10 = valToY(-10f)
        canvas.drawLine(leftPadding, yMinus10, width - rightPadding, yMinus10, dashedGridPaint)
        canvas.drawText("-10 m/s²", dp(8f), yMinus10 + dp(3f), labelPaint)

        if (readings.isEmpty()) {
            canvas.drawText("Nenhuma amostra para traçar curva", width / 2f, centerY + dp(5f), textMsgPaint)
            return
        }

        val n = readings.size
        pathX.reset()
        pathY.reset()
        pathZ.reset()
        pathMag.reset()

        if (n == 1) {
            val r = readings[0]
            val px = leftPadding + (chartWidth / 2f)
            val pyX = valToY(r.axisX)
            val pyY = valToY(r.axisY)
            val pyZ = valToY(r.axisZ)
            val pyMag = valToY(ReadingFormatter.magnitude(r.axisX, r.axisY, r.axisZ))

            drawDataPoint(canvas, px, pyX, pathPaintX.color)
            drawDataPoint(canvas, px, pyY, pathPaintY.color)
            drawDataPoint(canvas, px, pyZ, pathPaintZ.color)
            drawDataPoint(canvas, px, pyMag, pathPaintMag.color)
            return
        }

        val stepX = chartWidth / (n - 1).toFloat()

        readings.forEachIndexed { i, r ->
            val px = leftPadding + (i * stepX)
            val pyX = valToY(r.axisX)
            val pyY = valToY(r.axisY)
            val pyZ = valToY(r.axisZ)
            val mag = ReadingFormatter.magnitude(r.axisX, r.axisY, r.axisZ)
            val pyMag = valToY(mag)

            if (i == 0) {
                pathX.moveTo(px, pyX)
                pathY.moveTo(px, pyY)
                pathZ.moveTo(px, pyZ)
                pathMag.moveTo(px, pyMag)
            } else {
                pathX.lineTo(px, pyX)
                pathY.lineTo(px, pyY)
                pathZ.lineTo(px, pyZ)
                pathMag.lineTo(px, pyMag)
            }
        }

        // Draw paths
        canvas.drawPath(pathX, pathPaintX)
        canvas.drawPath(pathY, pathPaintY)
        canvas.drawPath(pathZ, pathPaintZ)
        canvas.drawPath(pathMag, pathPaintMag)

        // Draw points on top of lines for clarity
        readings.forEachIndexed { i, r ->
            val px = leftPadding + (i * stepX)
            val mag = ReadingFormatter.magnitude(r.axisX, r.axisY, r.axisZ)
            drawDataPoint(canvas, px, valToY(r.axisX), pathPaintX.color)
            drawDataPoint(canvas, px, valToY(r.axisY), pathPaintY.color)
            drawDataPoint(canvas, px, valToY(r.axisZ), pathPaintZ.color)
            drawDataPoint(canvas, px, valToY(mag), pathPaintMag.color)
        }
    }

    private fun drawDataPoint(canvas: Canvas, x: Float, y: Float, color: Int) {
        pointPaint.color = color
        canvas.drawCircle(x, y, dp(3f), pointPaint)
    }
}
