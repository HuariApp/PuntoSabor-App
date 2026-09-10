package com.example.huariquehub_mobile.ui.screens.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huariquehub_mobile.data.model.UserRole
import com.example.huariquehub_mobile.data.model.UserSession
import com.example.huariquehub_mobile.data.remote.SessionManager
import com.example.huariquehub_mobile.data.remote.toUserMessage
import com.example.huariquehub_mobile.data.repository.AuthRepository
import kotlinx.coroutines.launch

/** Gestiona login, registro y recuperación de contraseña (US16). */
class AuthViewModel : ViewModel() {

    private val repo = AuthRepository()

    var isLoading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    /** Inicia sesión y guarda la sesión recibida para usar el token en próximas peticiones. */
    fun login(email: String, password: String, onSuccess: (UserSession) -> Unit) {
        viewModelScope.launch {
            isLoading = true
            error = null
            runCatching { repo.login(email, password) }
                .onSuccess {
                    SessionManager.update(it)
                    onSuccess(it)
                }
                .onFailure { error = it.toUserMessage() }
            isLoading = false
        }
    }

    /** Registra una cuenta nueva y actualiza la sesión si el backend responde correctamente. */
    fun register(
        name: String,
        email: String,
        password: String,
        role: UserRole,
        onSuccess: (UserSession) -> Unit
    ) {
        viewModelScope.launch {
            isLoading = true
            error = null
            runCatching { repo.register(name, email, password, role) }
                .onSuccess {
                    SessionManager.update(it)
                    onSuccess(it)
                }
                .onFailure { error = it.toUserMessage() }
            isLoading = false
        }
    }

    // Mensaje informativo (US16: recuperación de contraseña).
    var message by mutableStateOf<String?>(null)
        private set

    /** Solicita instrucciones de recuperación de contraseña (US16). */
    fun forgotPassword(email: String, onDone: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            error = null
            message = null
            runCatching { repo.forgotPassword(email) }
                .onSuccess { message = it; onDone() }
                .onFailure { error = it.toUserMessage() }
            isLoading = false
        }
    }

    /** Restablece la contraseña con una nueva (US16). */
    fun resetPassword(email: String, newPassword: String, onDone: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            error = null
            message = null
            runCatching { repo.resetPassword(email, newPassword) }
                .onSuccess { message = it; onDone() }
                .onFailure { error = it.toUserMessage() }
            isLoading = false
        }
    }

    /** Limpia mensajes de error e informativos tras mostrarlos en pantalla. */
    fun clearError() {
        error = null
        message = null
    }
}
