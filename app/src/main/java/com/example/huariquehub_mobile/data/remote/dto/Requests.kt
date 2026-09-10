package com.example.huariquehub_mobile.data.remote.dto

/** Cuerpos de petición (request bodies) para la API de PuntoSabor. */

data class LoginRequest(
    val email: String,
    val password: String
)

data class CreateUserRequest(
    val name: String,
    val email: String,
    val password: String,
    val role: String
)

data class CreateHuariqueRequest(
    val name: String,
    val category: String,
    val categoryId: Int,
    val price: Double,
    val district: String,
    val address: String? = null,
    val phone: String? = null,
    val description: String? = null,
    val ownerId: Int? = null,
    val imageUrl: String? = null,
    val openAt: String? = null,
    val closeAt: String? = null,
    val deliveryAvailable: Boolean? = null,
    val takeawayAvailable: Boolean? = null,
    val dineInAvailable: Boolean? = null
)

data class CreateReviewRequest(
    val huariqueId: Int,
    val userId: Int,
    val rating: Int,
    val comment: String
)

data class CreatePromoRequest(
    val title: String,
    val note: String,
    val huariqueId: Int? = null,
    val type: String = "otro",
    val discount: Int = 0,
    val code: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val maxUses: Int? = null,
    val imageUrl: String? = null
)

data class CreateSubscriptionRequest(
    val userId: Int,
    val planId: String,
    val endDate: String? = null
)

data class UpdatePreferenceRequest(
    val preferredCategory: String?,
    val maxBudget: Double?,
    val preferredDistrict: String?,
    val notificationsEnabled: Boolean
)

data class CreateReportRequest(
    val huariqueId: Int,
    val userId: Int,
    val reason: String
)

data class ForgotPasswordRequest(val email: String)

data class ResetPasswordRequest(val email: String, val newPassword: String)

/** Actualización del nombre de perfil del usuario (CRUD de cuenta). */
data class UpdateProfileRequest(val name: String)
