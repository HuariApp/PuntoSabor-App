package com.example.huariquehub_mobile.data.remote

import com.example.huariquehub_mobile.data.model.UserSession

/**
 * Mantiene en memoria la sesión del usuario autenticado (incluido el JWT).
 * El [AuthInterceptor] lee el token desde aquí para adjuntarlo a las peticiones,
 * y los ViewModels obtienen el id/rol del usuario actual.
 */
object SessionManager {
    @Volatile
    var session: UserSession? = null
        private set

    val token: String? get() = session?.token?.takeIf { it.isNotBlank() }
    val userId: Int? get() = session?.id?.takeIf { it > 0 }
    val isOwner: Boolean get() = session?.isOwner == true

    fun update(session: UserSession) {
        this.session = session
    }

    fun clear() {
        session = null
    }
}
