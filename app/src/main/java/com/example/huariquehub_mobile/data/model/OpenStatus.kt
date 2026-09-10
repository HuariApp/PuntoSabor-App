package com.example.huariquehub_mobile.data.model

import java.util.Calendar

/**
 * Estado operativo de un huarique (US20/US22).
 *
 * Se calcula en el cliente a partir de los campos [Huarique.openAt] y
 * [Huarique.closeAt] que expone el backend (formato "HH:mm"). Cuando el
 * horario no está disponible o no se puede interpretar, el estado es
 * [UNKNOWN] ("Estado no confirmado").
 *
 * Se usa aritmética simple de minutos en lugar de java.time para mantener
 * compatibilidad con minSdk 24 (java.time requiere API 26 o desugaring).
 */
enum class OpenStatus {
    OPEN,     // Abierto ahora
    CLOSED,   // Cerrado
    UNKNOWN;  // Estado no confirmado

    val label: String
        get() = when (this) {
            OPEN -> "Abierto ahora"
            CLOSED -> "Cerrado"
            UNKNOWN -> "Estado no confirmado"
        }
}

/**
 * Determina si el huarique está abierto comparando la hora actual con el
 * rango [openAt, closeAt]. Soporta rangos que cruzan medianoche
 * (p. ej. 20:00–02:00).
 */
fun Huarique.openStatus(nowMinutes: Int = currentMinutesOfDay()): OpenStatus {
    val open = parseMinutes(openAt) ?: return OpenStatus.UNKNOWN
    val close = parseMinutes(closeAt) ?: return OpenStatus.UNKNOWN

    val isOpen = if (close > open) {
        // Mismo día: 09:00–18:00
        nowMinutes in open until close
    } else {
        // Cruza medianoche: 20:00–02:00
        nowMinutes >= open || nowMinutes < close
    }
    return if (isOpen) OpenStatus.OPEN else OpenStatus.CLOSED
}

private fun currentMinutesOfDay(): Int {
    val cal = Calendar.getInstance()
    return cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
}

/** Convierte "HH:mm" (o "H:mm", "HH:mm:ss") a minutos desde medianoche. */
private fun parseMinutes(value: String?): Int? {
    val raw = value?.trim().orEmpty()
    if (raw.isEmpty()) return null
    val parts = raw.split(":")
    val hour = parts.getOrNull(0)?.toIntOrNull() ?: return null
    val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0
    if (hour !in 0..23 || minute !in 0..59) return null
    return hour * 60 + minute
}
