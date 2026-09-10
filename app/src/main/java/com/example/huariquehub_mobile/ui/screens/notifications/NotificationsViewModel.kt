package com.example.huariquehub_mobile.ui.screens.notifications

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huariquehub_mobile.data.model.AppNotification
import com.example.huariquehub_mobile.data.remote.SessionManager
import com.example.huariquehub_mobile.data.remote.toUserMessage
import com.example.huariquehub_mobile.data.repository.NotificationRepository
import kotlinx.coroutines.launch

/** Lista y marca como leídas las notificaciones del usuario actual. */
class NotificationsViewModel : ViewModel() {

    private val repo = NotificationRepository()

    var notifications by mutableStateOf<List<AppNotification>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    // Cantidad de notificaciones pendientes de lectura.
    val unreadCount: Int
        get() = notifications.count { !it.isRead }

    /** Carga las notificaciones y muestra primero las pendientes de lectura. */
    fun load() {
        val userId = SessionManager.userId ?: return

        viewModelScope.launch {
            isLoading = true
            error = null

            runCatching {
                repo.getNotifications(userId)
            }.onSuccess {
                notifications = it.sortedWith(
                    compareBy<AppNotification> { it.isRead }.thenByDescending { it.id }
                )
            }.onFailure {
                error = it.toUserMessage()
            }

            isLoading = false
        }
    }

    /** Marca una notificación como leída y actualiza el estado en pantalla. */
    fun markAsRead(id: Int) {
        viewModelScope.launch {
            runCatching {
                repo.markAsRead(id)
            }.onSuccess {
                notifications = notifications.map {
                    if (it.id == id) {
                        it.copy(isRead = true)
                    } else {
                        it
                    }
                }
            }
        }
    }
}