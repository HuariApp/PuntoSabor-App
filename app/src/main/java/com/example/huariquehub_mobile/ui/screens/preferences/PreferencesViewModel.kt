package com.example.huariquehub_mobile.ui.screens.preferences

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huariquehub_mobile.data.remote.SessionManager
import com.example.huariquehub_mobile.data.remote.toUserMessage
import com.example.huariquehub_mobile.data.repository.PreferenceRepository
import kotlinx.coroutines.launch

/** Carga y persiste las preferencias de categoría y presupuesto del usuario. */
class PreferencesViewModel : ViewModel() {

    private val repo = PreferenceRepository()

    var preferredCategory by mutableStateOf("")
        private set

    var maxBudget by mutableStateOf("")
        private set

    var preferredDistrict by mutableStateOf("")
        private set

    var notificationsEnabled by mutableStateOf(true)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var saved by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    fun onCategoryChange(v: String) {
        preferredCategory = v.take(60)
        saved = false
    }

    /** Permite ingresar solo números y punto decimal para el presupuesto. */
    fun onBudgetChange(v: String) {
        maxBudget = v.filter { it.isDigit() || it == '.' }.take(10)
        saved = false
    }

    fun onDistrictChange(v: String) {
        preferredDistrict = v.take(60)
        saved = false
    }

    fun onNotificationsChange(v: Boolean) {
        notificationsEnabled = v
        saved = false
    }

    /**
     * Limpia los campos del formulario sin guardar cambios en el backend.
     * Para aplicar estos valores, el usuario debe presionar "Guardar preferencias".
     */
    fun clearInputs() {
        preferredCategory = ""
        maxBudget = ""
        preferredDistrict = ""
        notificationsEnabled = true
        saved = false
        error = null
    }

    /** Recupera las preferencias guardadas del usuario autenticado. */
    fun load() {
        val userId = SessionManager.userId ?: return

        viewModelScope.launch {
            isLoading = true
            error = null

            runCatching {
                repo.getPreferences(userId)
            }.onSuccess { p ->
                preferredCategory = p.preferredCategory.orEmpty()
                maxBudget = p.maxBudget
                    ?.let {
                        if (it % 1.0 == 0.0) {
                            it.toInt().toString()
                        } else {
                            it.toString()
                        }
                    }
                    .orEmpty()
                preferredDistrict = p.preferredDistrict.orEmpty()
                notificationsEnabled = p.notificationsEnabled
            }.onFailure {
                error = it.toUserMessage()
            }

            isLoading = false
        }
    }

    /** Guarda las preferencias seleccionadas por el usuario. */
    fun save() {
        val userId = SessionManager.userId ?: return

        viewModelScope.launch {
            isLoading = true
            error = null
            saved = false

            runCatching {
                repo.savePreferences(
                    userId = userId,
                    preferredCategory = preferredCategory.trim().ifBlank { null },
                    maxBudget = maxBudget.toDoubleOrNull(),
                    preferredDistrict = preferredDistrict.trim().ifBlank { null },
                    notificationsEnabled = notificationsEnabled
                )
            }.onSuccess {
                saved = true
            }.onFailure {
                error = it.toUserMessage()
            }

            isLoading = false
        }
    }
}