package com.example.huariquehub_mobile.ui.screens.subscription

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huariquehub_mobile.data.model.Plan
import com.example.huariquehub_mobile.data.model.Receipt
import com.example.huariquehub_mobile.data.model.Subscription
import com.example.huariquehub_mobile.data.remote.toUserMessage
import com.example.huariquehub_mobile.data.repository.MembershipRepository
import kotlinx.coroutines.launch

class SubscriptionViewModel : ViewModel() {

    private val repo = MembershipRepository()

    var plans by mutableStateOf<List<Plan>>(emptyList())
        private set
    var activeSub by mutableStateOf<Subscription?>(null)
        private set
    var isLoading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    // Comprobante de pago (US25).
    var receipt by mutableStateOf<Receipt?>(null)
        private set
    var loadingReceipt by mutableStateOf(false)
        private set

    fun load(userId: Int) {
        viewModelScope.launch {
            isLoading = true
            error = null
            runCatching {
                val plansResult = repo.getPlans()
                val subResult = repo.getActiveSubscription(userId)
                plansResult to subResult
            }.onSuccess { (p, s) ->
                plans = p.sortedBy { it.price }
                activeSub = s
            }.onFailure { error = it.toUserMessage() }
            isLoading = false
        }
    }

    fun subscribe(userId: Int, planId: String, onDone: () -> Unit) {
        viewModelScope.launch {
            error = null
            runCatching { repo.subscribe(userId, planId) }
                .onSuccess {
                    activeSub = it
                    onDone()
                }
                .onFailure { error = it.toUserMessage() }
        }
    }

    /** Descarga el comprobante de la suscripción activa (US25). */
    fun loadReceipt() {
        val subId = activeSub?.id ?: return
        viewModelScope.launch {
            loadingReceipt = true
            error = null
            runCatching { repo.getReceipt(subId) }
                .onSuccess { receipt = it }
                .onFailure { error = it.toUserMessage() }
            loadingReceipt = false
        }
    }

    fun dismissReceipt() { receipt = null }

    /** Cancela la suscripción activa. */
    fun cancel(onDone: () -> Unit) {
        val subId = activeSub?.id ?: return
        viewModelScope.launch {
            error = null
            runCatching { repo.cancelSubscription(subId) }
                .onSuccess { activeSub = null; onDone() }
                .onFailure { error = it.toUserMessage() }
        }
    }
}
