package com.example.huariquehub_mobile.data.remote.dto

/**
 * DTOs que representan las respuestas JSON del backend (PuntoSabor API).
 * Los nombres de campo coinciden con el JSON (camelCase), por lo que Moshi
 * los mapea directamente. Los mappers a modelos de dominio están en Mappers.kt.
 */

data class AuthResponseDto(
    val id: Int,
    val name: String,
    val email: String,
    val role: String,
    val token: String?
)

data class UserDto(
    val id: Int,
    val name: String,
    val email: String,
    val role: String
)

data class HuariqueDto(
    val id: Int,
    val name: String,
    val category: String?,
    val categoryId: Int?,
    val price: Double?,
    val rating: Double?,
    val district: String?,
    val near: Boolean?,
    val latitude: Double?,
    val longitude: Double?,
    val ownerId: Int?,
    val address: String?,
    val phone: String?,
    val description: String?,
    val imageUrl: String?,
    val openAt: String?,
    val closeAt: String?,
    val deliveryAvailable: Boolean?,
    val takeawayAvailable: Boolean?,
    val dineInAvailable: Boolean?,
    val updatedAt: String?
)

data class CategoryDto(
    val id: Int,
    val name: String
)

data class FavoriteDto(
    val id: Int,
    val userId: Int,
    val huariqueId: Int,
    val createdAt: String?
)

data class PreferenceDto(
    val userId: Int,
    val preferredCategory: String?,
    val maxBudget: Double?,
    val preferredDistrict: String?,
    val notificationsEnabled: Boolean
)

data class ReportDto(
    val id: Int,
    val huariqueId: Int,
    val userId: Int,
    val reason: String,
    val status: String,
    val createdAt: String?
)

data class NotificationDto(
    val id: Int,
    val userId: Int,
    val title: String,
    val body: String,
    val isRead: Boolean,
    val createdAt: String?
)

data class MessageDto(val message: String)

data class ReceiptDto(
    val receiptNumber: String,
    val subscriptionId: Int,
    val userId: Int,
    val planId: String,
    val planName: String,
    val amount: Double,
    val currency: String,
    val status: String,
    val issuedAt: String?,
    val periodStart: String?,
    val periodEnd: String?
)

data class ReviewDto(
    val id: Int,
    val huariqueId: Int,
    val userId: Int,
    val rating: Int,
    val comment: String?,
    val createdAt: String?
)

data class PlanDto(
    val id: String,
    val name: String,
    val price: Double
)

data class PromoDto(
    val id: Int,
    val title: String,
    val note: String?,
    val type: String?,
    val discount: Int?,
    val code: String?,
    val startDate: String?,
    val endDate: String?,
    val maxUses: Int?,
    val currentUses: Int?,
    val huariqueId: Int?,
    val imageUrl: String?,
    val isActive: Boolean?
)

data class SubscriptionDto(
    val id: Int,
    val userId: Int,
    val planId: String,
    val planName: String?,
    val planPrice: Double?,
    val status: String,
    val isActive: Boolean,
    val startDate: String?,
    val endDate: String?
)
