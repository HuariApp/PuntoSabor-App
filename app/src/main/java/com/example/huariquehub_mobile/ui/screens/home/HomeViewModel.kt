package com.example.huariquehub_mobile.ui.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huariquehub_mobile.data.model.Category
import com.example.huariquehub_mobile.data.model.Huarique
import com.example.huariquehub_mobile.data.remote.SessionManager
import com.example.huariquehub_mobile.data.remote.toUserMessage
import com.example.huariquehub_mobile.data.repository.FavoriteRepository
import com.example.huariquehub_mobile.data.repository.HuariqueRepository
import kotlinx.coroutines.launch

/** Carga huariques, sugerencias y permite filtrar por favoritos. */
class HomeViewModel : ViewModel() {

    private val repo = HuariqueRepository()
    private val favoritesRepo = FavoriteRepository()

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    var huariques by mutableStateOf<List<Huarique>>(emptyList())
        private set

    var categories by mutableStateOf<List<Category>>(listOf(Category(0, "Todas", "🍽️")))
        private set

    // Ids de huariques favoritos del usuario actual (US03).
    var favoriteIds by mutableStateOf<Set<Int>>(emptySet())
        private set

    // Filtro "Cerca de mí" (US19) — usa el parámetro near del backend.
    var nearbyOnly by mutableStateOf(false)
        private set

    // Filtro "Solo favoritos" (US03).
    var favoritesOnly by mutableStateOf(false)
        private set

    // Sugerencias personalizadas (US18).
    var suggestions by mutableStateOf<List<Huarique>>(emptyList())
        private set

    init {
        load()
    }

    /**
     * Carga la información principal del Home.
     * Si el filtro "Cerca de mí" está activo, trae huariques cercanos.
     * Caso contrario, carga la lista general de huariques.
     */
    fun load() {
        viewModelScope.launch {
            isLoading = true
            error = null

            runCatching {
                val huariquesResult =
                    if (nearbyOnly) repo.getNearbyHuariques() else repo.getHuariques()
                val categoriesResult = repo.getCategories()
                huariquesResult to categoriesResult
            }.onSuccess { (h, c) ->
                huariques = h
                categories = listOf(Category(0, "Todas", "🍽️")) + c
            }.onFailure {
                error = it.toUserMessage()
            }

            isLoading = false
            loadFavorites()
            loadSuggestions()
        }
    }

    /**
     * Obtiene los favoritos del usuario actual para marcar los huariques guardados.
     */
    private fun loadFavorites() {
        val userId = SessionManager.userId ?: return

        viewModelScope.launch {
            runCatching {
                favoritesRepo.getFavoriteIds(userId)
            }.onSuccess {
                favoriteIds = it
            }
        }
    }

    /**
     * Carga sugerencias personalizadas según el usuario autenticado.
     */
    private fun loadSuggestions() {
        val userId = SessionManager.userId ?: return

        viewModelScope.launch {
            runCatching {
                repo.getSuggestions(userId)
            }.onSuccess {
                suggestions = it
            }
        }
    }

    /** Activa/desactiva el filtro "Cerca de mí" y recarga la lista. */
    fun toggleNearby() {
        nearbyOnly = !nearbyOnly
        load()
    }

    /** Activa/desactiva el filtro "Solo favoritos". */
    fun toggleFavoritesOnly() {
        favoritesOnly = !favoritesOnly
    }

    /**
     * Restablece los filtros rápidos del Home y recarga la lista si era necesario.
     */
    fun clearFilters() {
        val shouldReload = nearbyOnly

        nearbyOnly = false
        favoritesOnly = false

        if (shouldReload) {
            load()
        }
    }

    /**
     * Marca/desmarca un huarique como favorito (US03). Actualiza el estado
     * local de inmediato y revierte si la petición al backend falla.
     */
    fun toggleFavorite(huariqueId: Int) {
        val userId = SessionManager.userId ?: return
        val wasFavorite = huariqueId in favoriteIds

        favoriteIds = if (wasFavorite) {
            favoriteIds - huariqueId
        } else {
            favoriteIds + huariqueId
        }

        viewModelScope.launch {
            val result = runCatching {
                if (wasFavorite) {
                    favoritesRepo.remove(userId, huariqueId)
                } else {
                    favoritesRepo.add(userId, huariqueId)
                }
            }

            if (result.isFailure) {
                // Revertir el cambio optimista ante un error de red.
                favoriteIds = if (wasFavorite) {
                    favoriteIds + huariqueId
                } else {
                    favoriteIds - huariqueId
                }
            }
        }
    }
}