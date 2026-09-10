package com.example.huariquehub_mobile.data.remote

import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

/** Traduce excepciones de red/HTTP a mensajes claros para el usuario. */
fun Throwable.toUserMessage(): String = when (this) {
    is HttpException -> serverMessage() ?: when (code()) {
        400 -> "Datos inválidos. Revisa la información ingresada."
        401 -> "Correo o contraseña incorrectos."
        404 -> "No se encontró el recurso solicitado."
        409 -> "El recurso ya existe."
        in 500..599 -> "Error del servidor. Intenta nuevamente."
        else -> "Ocurrió un error (${code()}). Intenta de nuevo."
    }
    is IOException -> "Sin conexión con el servidor. Verifica tu internet."
    else -> "Ocurrió un error inesperado. Intenta de nuevo."
}

/** Extrae el campo "message" del cuerpo de error del backend (ErrorResource). */
private fun HttpException.serverMessage(): String? = runCatching {
    val body = response()?.errorBody()?.string()
    if (body.isNullOrBlank()) return null
    val msg = JSONObject(body).optString("message")
    msg.takeIf { it.isNotBlank() }
}.getOrNull()
