package com.example.huariquehub_mobile.ui.screens.owner

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huariquehub_mobile.data.model.Huarique
import com.example.huariquehub_mobile.data.remote.SessionManager
import com.example.huariquehub_mobile.data.remote.toUserMessage
import com.example.huariquehub_mobile.data.repository.HuariqueRepository
import kotlinx.coroutines.launch

class CreateEditHuariqueViewModel : ViewModel() {

    private val repo = HuariqueRepository()

    // Respaldo nombre → id alineado con las categorías sembradas en el backend.
    private val fallbackCategories = linkedMapOf(
        "Pollo" to 1, "Marina" to 2, "Criolla" to 3, "Chifa" to 4,
        "Postres" to 5, "Menú" to 6, "Café" to 7, "Parrillas" to 8
    )

    var existing by mutableStateOf<Huarique?>(null)
        private set
    var categoryIds by mutableStateOf<Map<String, Int>>(fallbackCategories)
        private set
    var isSaving by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    val categoryNames: List<String> get() = categoryIds.keys.toList()

    private var initialized = false

    fun init(id: Int?) {
        if (initialized) return
        initialized = true
        viewModelScope.launch {
            runCatching { repo.getCategories() }.onSuccess { cats ->
                if (cats.isNotEmpty()) categoryIds = cats.associate { it.name to it.id }
            }
            if (id != null) {
                runCatching { repo.getHuarique(id) }
                    .onSuccess { existing = it }
                    .onFailure { error = it.toUserMessage() }
            }
        }
    }

    fun save(
        id: Int?,
        name: String,
        category: String,
        district: String,
        address: String,
        phone: String,
        priceText: String,
        openAt: String,
        closeAt: String,
        description: String,
        delivery: Boolean,
        takeaway: Boolean,
        dineIn: Boolean,
        imageBytes: ByteArray?,
        imageMime: String?,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            isSaving = true
            error = null
            val price = priceText.replace(",", ".").toDoubleOrNull() ?: 0.0
            val categoryId = categoryIds[category] ?: fallbackCategories[category] ?: 0
            runCatching {
                val huariqueId = if (id == null) {
                    val created = repo.createHuarique(
                        name = name,
                        category = category,
                        categoryId = categoryId,
                        price = price,
                        district = district,
                        address = address.ifBlank { null },
                        phone = phone.ifBlank { null },
                        description = description.ifBlank { null },
                        ownerId = SessionManager.userId,
                        openAt = openAt.ifBlank { null },
                        closeAt = closeAt.ifBlank { null },
                        deliveryAvailable = delivery,
                        takeawayAvailable = takeaway,
                        dineInAvailable = dineIn
                    )
                    created.id
                } else {
                    val patch = mutableMapOf<String, Any?>(
                        "name" to name,
                        "category" to category,
                        "categoryId" to categoryId,
                        "district" to district,
                        "price" to price,
                        "phone" to phone.ifBlank { null },
                        "address" to address.ifBlank { null },
                        "description" to description.ifBlank { null },
                        "openAt" to openAt.ifBlank { null },
                        "closeAt" to closeAt.ifBlank { null },
                        "deliveryAvailable" to delivery,
                        "takeawayAvailable" to takeaway,
                        "dineInAvailable" to dineIn
                    )
                    repo.updateHuarique(id, patch)
                    id
                }
                // Sube la foto solo si el dueño eligió una nueva.
                if (imageBytes != null) {
                    repo.uploadHuariqueImage(huariqueId, imageBytes, imageMime ?: "image/jpeg")
                }
            }.onSuccess { onDone() }
                .onFailure { error = it.toUserMessage() }
            isSaving = false
        }
    }
}
