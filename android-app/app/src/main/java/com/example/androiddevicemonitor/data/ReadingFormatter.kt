package com.example.androiddevicemonitor.data

import java.util.Locale

object ReadingFormatter {
    fun axis(value: Float): String = String.format(Locale.US, "%+.2f", value)

    fun timestamp(value: Long): String = if (value <= 0) "—" else value.toString()

    fun battery(value: Int?): String = value?.let { "$it%" } ?: "Indisponível"
}
