package com.example.huariquehub_mobile.ui.screens.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huariquehub_mobile.data.model.UserRole
import com.example.huariquehub_mobile.data.remote.SessionManager
import com.example.huariquehub_mobile.data.remote.toUserMessage
import com.example.huariquehub_mobile.data.repository.AuthRepository
import kotlinx.coroutines.launch

/**
 * Gestión del perfil del usuario (CRUD de cuenta): ver el perfil, actualizar el
 * nombre visible y eliminar la cuenta. Consume los endpoints
 * `GET/PATCH/DELETE auth/users/{id}` del backend.
 */
class ProfileViewModel : ViewModel() {

    private val repo = AuthRepository()

    var name by mutableStateOf("")
        private set
    var email by mutableStateOf("")
        private set
    var role by mutableStateOf(UserRole.CONSUMER)
        private set

    var isLoading by mutableStateOf(false)
        private set
    var saved by mutableStateOf(false)
        private set
    var deleted by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    val isOwner: Boolean get() = role == UserRole.OWNER

    fun onNameChange(v: String) { name = v; saved = false }

    /** Carga el perfil desde el backend; usa la sesión en memoria como respaldo. */
    fun load() {
        val userId = SessionManager.userId ?: return
        // Respaldo inmediato con lo que ya hay en sesión (respuesta instantánea).
        SessionManager.session?.let {
            name = it.name
            email = it.email
            role = it.role
        }
        viewModelScope.launch {
            isLoading = true
            error = null
            runCatching { repo.getProfile(userId) }
                .onSuccess { u ->
                    name = u.name
                    email = u.email
                    role = u.role
                }
                .onFailure { error = it.toUserMessage() }
            isLoading = false
        }
    }

    /** Actualiza el nombre visible (PATCH auth/users/{id}). */
    fun save() {
        val userId = SessionManager.userId ?: return
        val trimmed = name.trim()
        if (trimmed.length < 2) {
            error = "El nombre debe tener al menos 2 caracteres."
            return
        }
        viewModelScope.launch {
            isLoading = true
            error = null
            saved = false
            runCatching { repo.updateName(userId, trimmed) }
                .onSuccess { name = it.name; saved = true }
                .onFailure { error = it.toUserMessage() }
            isLoading = false
        }
    }

    /** Elimina la cuenta (DELETE auth/users/{id}) y marca la sesión como cerrada. */
    fun deleteAccount() {
        val userId = SessionManager.userId ?: return
        viewModelScope.launch {
            isLoading = true
            error = null
            runCatching { repo.deleteAccount(userId) }
                .onSuccess { deleted = true }
                .onFailure { error = it.toUserMessage() }
            isLoading = false
        }
    }

    /** Cierra la sesión sin eliminar la cuenta. */
    fun logout() {
        SessionManager.clear()
        deleted = true
    }
}
