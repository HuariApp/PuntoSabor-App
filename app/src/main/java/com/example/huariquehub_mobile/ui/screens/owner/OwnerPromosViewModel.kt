package com.example.huariquehub_mobile.ui.screens.owner

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huariquehub_mobile.data.model.Promo
import com.example.huariquehub_mobile.data.remote.toUserMessage
import com.example.huariquehub_mobile.data.repository.MembershipRepository
import kotlinx.coroutines.launch

class OwnerPromosViewModel : ViewModel() {

    private val repo = MembershipRepository()

    var promos by mutableStateOf<List<Promo>>(emptyList())
        private set
    var isLoading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    fun load(ownerId: Int) {
        viewModelScope.launch {
            isLoading = true
            error = null
            runCatching { repo.getPromosByOwner(ownerId) }
                .onSuccess {
                    promos = it.sortedWith(
                        compareByDescending<Promo> { it.isActive }
                            .thenBy(String.CASE_INSENSITIVE_ORDER) { promo -> promo.title }
                    )
                }
                .onFailure { error = it.toUserMessage() }
            isLoading = false
        }
    }

    fun delete(id: Int) {
        viewModelScope.launch {
            runCatching { repo.deletePromo(id) }
                .onSuccess { promos = promos.filter { it.id != id } }
                .onFailure { error = it.toUserMessage() }
        }
    }
}
