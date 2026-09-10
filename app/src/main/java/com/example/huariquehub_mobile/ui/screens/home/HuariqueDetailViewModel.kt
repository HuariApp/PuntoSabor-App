package com.example.huariquehub_mobile.ui.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huariquehub_mobile.data.model.Huarique
import com.example.huariquehub_mobile.data.model.Review
import com.example.huariquehub_mobile.data.remote.SessionManager
import com.example.huariquehub_mobile.data.remote.toUserMessage
import com.example.huariquehub_mobile.data.model.Promo
import com.example.huariquehub_mobile.data.repository.FavoriteRepository
import com.example.huariquehub_mobile.data.repository.HuariqueRepository
import com.example.huariquehub_mobile.data.repository.MembershipRepository
import com.example.huariquehub_mobile.data.repository.ReportRepository
import kotlinx.coroutines.launch

class HuariqueDetailViewModel : ViewModel() {

    private val repo = HuariqueRepository()
    private val favoritesRepo = FavoriteRepository()
    private val reportRepo = ReportRepository()
    private val membershipRepo = MembershipRepository()

    var promos by mutableStateOf<List<Promo>>(emptyList())
        private set

    var huarique by mutableStateOf<Huarique?>(null)
        private set
    var reviews by mutableStateOf<List<Review>>(emptyList())
        private set
    var isLoading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set
    var isSubmitting by mutableStateOf(false)
        private set
    var isFavorite by mutableStateOf(false)
        private set

    fun load(id: Int) {
        viewModelScope.launch {
            isLoading = true
            error = null
            runCatching {
                val h = repo.getHuarique(id)
                val r = runCatching { repo.getReviews(id) }.getOrDefault(emptyList())
                h to r
            }.onSuccess { (h, r) ->
                huarique = h
                reviews = r
            }.onFailure { error = it.toUserMessage() }
            isLoading = false
            loadFavoriteState(id)
            promos = runCatching { membershipRepo.getPromosByHuarique(id) }
                .getOrDefault(emptyList())
                .sortedWith(compareByDescending<Promo> { it.isActive }.thenBy { it.title })
        }
    }

    /** Canjea una promoción del huarique (US26). */
    fun usePromo(promo: Promo) {
        viewModelScope.launch {
            runCatching { membershipRepo.usePromo(promo.id) }
                .onSuccess { feedback = "¡Promoción \"${promo.title}\" canjeada!" }
                .onFailure { feedback = it.toUserMessage() }
        }
    }

    private fun loadFavoriteState(huariqueId: Int) {
        val userId = SessionManager.userId ?: return
        viewModelScope.launch {
            runCatching { favoritesRepo.getFavoriteIds(userId) }
                .onSuccess { isFavorite = huariqueId in it }
        }
    }

    /** Marca/desmarca el huarique como favorito (US03), con reversión ante error. */
    fun toggleFavorite(huariqueId: Int) {
        val userId = SessionManager.userId ?: return
        val wasFavorite = isFavorite
        isFavorite = !wasFavorite
        viewModelScope.launch {
            val result = runCatching {
                if (wasFavorite) favoritesRepo.remove(userId, huariqueId)
                else favoritesRepo.add(userId, huariqueId)
            }
            if (result.isFailure) isFavorite = wasFavorite
        }
    }

    // Mensaje efímero para Snackbar (moderación de reseñas US08, reporte US21).
    var feedback by mutableStateOf<String?>(null)
        private set
    var reportSubmitting by mutableStateOf(false)
        private set

    fun consumeFeedback() { feedback = null }

    fun submitReview(huariqueId: Int, rating: Int, comment: String, onDone: () -> Unit) {
        val userId = SessionManager.userId ?: return
        viewModelScope.launch {
            isSubmitting = true
            runCatching { repo.addReview(huariqueId, userId, rating, comment.trim()) }
                .onSuccess {
                    reviews = runCatching { repo.getReviews(huariqueId) }.getOrDefault(reviews + it)
                    onDone()
                }
                .onFailure {
                    // Incluye el mensaje de moderación del backend cuando aplica (US08).
                    feedback = it.toUserMessage()
                }
            isSubmitting = false
        }
    }

    /** Reporta información incorrecta del huarique (US21). */
    fun reportHuarique(huariqueId: Int, reason: String, onDone: () -> Unit) {
        val userId = SessionManager.userId ?: return
        viewModelScope.launch {
            reportSubmitting = true
            runCatching { reportRepo.createReport(huariqueId, userId, reason) }
                .onSuccess {
                    feedback = "Gracias, registramos tu reporte para revisión."
                    onDone()
                }
                .onFailure { feedback = it.toUserMessage() }
            reportSubmitting = false
        }
    }
}
