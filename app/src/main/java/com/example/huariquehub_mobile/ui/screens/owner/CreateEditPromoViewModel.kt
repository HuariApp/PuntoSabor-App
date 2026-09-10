package com.example.huariquehub_mobile.ui.screens.owner

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huariquehub_mobile.data.model.Huarique
import com.example.huariquehub_mobile.data.model.Promo
import com.example.huariquehub_mobile.data.remote.toUserMessage
import com.example.huariquehub_mobile.data.repository.HuariqueRepository
import com.example.huariquehub_mobile.data.repository.MembershipRepository
import kotlinx.coroutines.launch

class CreateEditPromoViewModel : ViewModel() {

    private val membership = MembershipRepository()
    private val huariqueRepo = HuariqueRepository()

    var existing by mutableStateOf<Promo?>(null)
        private set
    var ownerHuariques by mutableStateOf<List<Huarique>>(emptyList())
        private set
    var isSaving by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    private var initialized = false

    fun init(promoId: Int?, ownerId: Int) {
        if (initialized) return
        initialized = true
        viewModelScope.launch {
            runCatching { huariqueRepo.getHuariquesByOwner(ownerId) }
                .onSuccess { ownerHuariques = it }
            if (promoId != null) {
                runCatching { membership.getPromo(promoId) }
                    .onSuccess { existing = it }
                    .onFailure { error = it.toUserMessage() }
            }
        }
    }

    fun save(
        id: Int?,
        title: String,
        note: String,
        typeKey: String,
        discountText: String,
        huariqueId: Int?,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            isSaving = true
            error = null
            val discount = discountText.toIntOrNull() ?: 0
            runCatching {
                if (id == null) {
                    membership.createPromo(title, note, huariqueId, typeKey, discount)
                } else {
                    val patch = mutableMapOf<String, Any?>(
                        "title" to title,
                        "note" to note,
                        "type" to typeKey,
                        "discount" to discount
                    )
                    if (huariqueId != null) patch["huariqueId"] = huariqueId
                    membership.updatePromo(id, patch)
                }
            }.onSuccess { onDone() }
                .onFailure { error = it.toUserMessage() }
            isSaving = false
        }
    }
}
