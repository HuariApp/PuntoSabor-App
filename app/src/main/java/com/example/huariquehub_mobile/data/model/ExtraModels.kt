package com.example.huariquehub_mobile.data.model

/** Preferencias del usuario (US17) + notificaciones (US11). */
data class UserPreferences(
    val userId: Int,
    val preferredCategory: String?,
    val maxBudget: Double?,
    val preferredDistrict: String?,
    val notificationsEnabled: Boolean
)

/** Notificación del usuario (US12). */
data class AppNotification(
    val id: Int,
    val title: String,
    val body: String,
    val isRead: Boolean,
    val date: String
)

/** Comprobante de pago de una suscripción (US25). */
data class Receipt(
    val receiptNumber: String,
    val subscriptionId: Int,
    val planName: String,
    val amount: Double,
    val currency: String,
    val status: String,
    val issuedAt: String,
    val periodStart: String,
    val periodEnd: String?
)
