package com.example.androiddevicemonitor.data

enum class BatteryStatus(val label: String) {
    CHARGING("Carregando"),
    DISCHARGING("Descarregando"),
    FULL("Carga completa"),
    NOT_CHARGING("Não carregando"),
    UNAVAILABLE("Indisponível")
}
