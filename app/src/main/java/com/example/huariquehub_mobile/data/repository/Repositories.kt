package com.example.huariquehub_mobile.data.repository

import com.example.huariquehub_mobile.data.model.Category
import com.example.huariquehub_mobile.data.model.Huarique
import com.example.huariquehub_mobile.data.model.Plan
import com.example.huariquehub_mobile.data.model.Promo
import com.example.huariquehub_mobile.data.model.Review
import com.example.huariquehub_mobile.data.model.Subscription
import com.example.huariquehub_mobile.data.model.UserRole
import com.example.huariquehub_mobile.data.model.UserSession
import com.example.huariquehub_mobile.data.remote.ApiClient
import com.example.huariquehub_mobile.data.remote.dto.AuthResponseDto
import com.example.huariquehub_mobile.data.remote.dto.CreateHuariqueRequest
import com.example.huariquehub_mobile.data.remote.dto.CreatePromoRequest
import com.example.huariquehub_mobile.data.remote.dto.CreateReviewRequest
import com.example.huariquehub_mobile.data.remote.dto.CreateSubscriptionRequest
import com.example.huariquehub_mobile.data.remote.dto.CreateReportRequest
import com.example.huariquehub_mobile.data.remote.dto.CreateUserRequest
import com.example.huariquehub_mobile.data.remote.dto.ForgotPasswordRequest
import com.example.huariquehub_mobile.data.remote.dto.LoginRequest
import com.example.huariquehub_mobile.data.remote.dto.ResetPasswordRequest
import com.example.huariquehub_mobile.data.remote.dto.UpdatePreferenceRequest
import com.example.huariquehub_mobile.data.remote.dto.UpdateProfileRequest
import com.example.huariquehub_mobile.data.remote.dto.UserDto
import com.example.huariquehub_mobile.data.remote.SessionManager
import com.example.huariquehub_mobile.data.model.AppNotification
import com.example.huariquehub_mobile.data.model.Receipt
import com.example.huariquehub_mobile.data.model.UserPreferences
import com.example.huariquehub_mobile.data.remote.toModel
import okhttp3.MultipartBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

private val api get() = ApiClient.api

/** Acceso a endpoints de autenticación y recuperación de contraseña. */
class AuthRepository {

    suspend fun login(email: String, password: String): UserSession =
        api.login(LoginRequest(email.trim(), password)).toSession()

    suspend fun register(
        name: String,
        email: String,
        password: String,
        role: UserRole
    ): UserSession {
        val roleStr = if (role == UserRole.OWNER) "owner" else "consumer"
        api.createUser(CreateUserRequest(name.trim(), email.trim(), password, roleStr))
        // POST /users no devuelve token: iniciamos sesión para obtenerlo.
        return login(email, password)
    }

    /** Inicia la recuperación de contraseña (US16). */
    suspend fun forgotPassword(email: String): String =
        api.forgotPassword(ForgotPasswordRequest(email.trim())).message

    /** Restablece la contraseña (US16). */
    suspend fun resetPassword(email: String, newPassword: String): String =
        api.resetPassword(ResetPasswordRequest(email.trim(), newPassword)).message

    /** Obtiene el perfil del usuario (CRUD de cuenta). */
    suspend fun getProfile(id: Int): UserSession =
        api.getProfile(id).toSession()

    /** Actualiza el nombre visible del usuario y refresca la sesión en memoria. */
    suspend fun updateName(id: Int, name: String): UserSession {
        val updated = api.updateProfile(id, UpdateProfileRequest(name.trim())).toSession()
        // Conservamos el token vigente de la sesión activa.
        SessionManager.session?.let { SessionManager.update(it.copy(name = updated.name)) }
        return updated
    }

    /** Elimina la cuenta del usuario y cierra la sesión. */
    suspend fun deleteAccount(id: Int): String {
        val message = api.deleteAccount(id).message
        SessionManager.clear()
        return message
    }

    private fun UserDto.toSession() = UserSession(
        id = id,
        name = name,
        email = email,
        role = if (role.equals("owner", ignoreCase = true)) UserRole.OWNER else UserRole.CONSUMER,
        token = SessionManager.token.orEmpty()
    )

    private fun AuthResponseDto.toSession() = UserSession(
        id = id,
        name = name,
        email = email,
        role = if (role.equals("owner", ignoreCase = true)) UserRole.OWNER else UserRole.CONSUMER,
        token = token.orEmpty()
    )
}

/** Preferencias del usuario (US17) y notificaciones (US11). */
class PreferenceRepository {
    suspend fun getPreferences(userId: Int): UserPreferences =
        api.getPreferences(userId).toModel()

    suspend fun savePreferences(
        userId: Int,
        preferredCategory: String?,
        maxBudget: Double?,
        preferredDistrict: String?,
        notificationsEnabled: Boolean
    ): UserPreferences = api.savePreferences(
        userId,
        UpdatePreferenceRequest(preferredCategory, maxBudget, preferredDistrict, notificationsEnabled)
    ).toModel()
}

/** Reportes de información incorrecta (US21). */
class ReportRepository {
    suspend fun createReport(huariqueId: Int, userId: Int, reason: String) {
        api.createReport(CreateReportRequest(huariqueId, userId, reason.trim()))
    }
}

/** Notificaciones del usuario (US12). */
class NotificationRepository {
    suspend fun getNotifications(userId: Int): List<AppNotification> =
        api.getNotifications(userId).map { it.toModel() }

    suspend fun markAsRead(id: Int) {
        api.markNotificationRead(id)
    }
}

/** Consultas y mutaciones de huariques, reseñas y promos asociadas. */
class HuariqueRepository {

    suspend fun getHuariques(): List<Huarique> =
        api.getHuariques().map { it.toModel() }

    suspend fun getHuariquesByOwner(ownerId: Int): List<Huarique> =
        api.getHuariques(ownerId = ownerId).map { it.toModel() }

    /**
     * Huariques marcados como cercanos por el backend (US19 "Cerca de mí").
     * El backend responde 404 cuando un filtro no arroja resultados; eso se
     * traduce a una lista vacía en lugar de un error.
     */
    suspend fun getNearbyHuariques(): List<Huarique> =
        try {
            api.getHuariques(near = true).map { it.toModel() }
        } catch (e: retrofit2.HttpException) {
            if (e.code() == 404) emptyList() else throw e
        }

    suspend fun getHuarique(id: Int): Huarique =
        api.getHuarique(id).toModel()

    /** Sugerencias personalizadas para el usuario (US18). */
    suspend fun getSuggestions(userId: Int): List<Huarique> =
        try {
            api.getSuggestions(userId).map { it.toModel() }
        } catch (e: retrofit2.HttpException) {
            if (e.code() == 404) emptyList() else throw e
        }

    suspend fun getCategories(): List<Category> =
        api.getCategories().map { it.toModel() }

    suspend fun createHuarique(
        name: String,
        category: String,
        categoryId: Int,
        price: Double,
        district: String,
        address: String?,
        phone: String?,
        description: String?,
        ownerId: Int?,
        openAt: String?,
        closeAt: String?,
        deliveryAvailable: Boolean,
        takeawayAvailable: Boolean,
        dineInAvailable: Boolean
    ): Huarique = api.createHuarique(
        CreateHuariqueRequest(
            name = name,
            category = category,
            categoryId = categoryId,
            price = price,
            district = district,
            address = address,
            phone = phone,
            description = description,
            ownerId = ownerId,
            openAt = openAt,
            closeAt = closeAt,
            deliveryAvailable = deliveryAvailable,
            takeawayAvailable = takeawayAvailable,
            dineInAvailable = dineInAvailable
        )
    ).toModel()

    suspend fun updateHuarique(id: Int, patch: Map<String, Any?>): Huarique =
        api.patchHuarique(id, patch).toModel()

    suspend fun deleteHuarique(id: Int) {
        api.deleteHuarique(id)
    }

    suspend fun getReviews(huariqueId: Int): List<Review> =
        api.getReviews(huariqueId).map { it.toModel() }

    suspend fun addReview(huariqueId: Int, userId: Int, rating: Int, comment: String): Review =
        api.createReview(CreateReviewRequest(huariqueId, userId, rating, comment)).toModel()

    /** Sube la foto del huarique al backend (se guarda como bytes en la db). */
    suspend fun uploadHuariqueImage(id: Int, bytes: ByteArray, mimeType: String): Huarique {
        val body = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("file", "huarique_$id", body)
        return api.uploadHuariqueImage(id, part).toModel()
    }
}

/**
 * Repositorio de favoritos (US03). Se apoya en los endpoints REST
 * `/favorites` del backend desplegado: GET por usuario, POST y DELETE.
 */
class FavoriteRepository {

    /** Ids de huariques marcados como favoritos por el usuario. */
    suspend fun getFavoriteIds(userId: Int): Set<Int> =
        api.getFavorites(userId).map { it.huariqueId }.toSet()

    /** Agrega a favoritos. Tolera el 409 (ya existía) como éxito idempotente. */
    suspend fun add(userId: Int, huariqueId: Int) {
        val response = api.addFavorite(huariqueId, userId)
        if (!response.isSuccessful && response.code() != 409) {
            throw retrofit2.HttpException(response)
        }
    }

    /** Quita de favoritos. Tolera el 404 (ya no existía) como éxito idempotente. */
    suspend fun remove(userId: Int, huariqueId: Int) {
        val response = api.removeFavorite(huariqueId, userId)
        if (!response.isSuccessful && response.code() != 404) {
            throw retrofit2.HttpException(response)
        }
    }
}

class MembershipRepository {

    suspend fun getPlans(): List<Plan> =
        api.getPlans().map { it.toModel() }

    suspend fun getActiveSubscription(userId: Int): Subscription? {
        val response = api.getActiveSubscription(userId)
        return if (response.isSuccessful) response.body()?.toModel() else null
    }

    suspend fun subscribe(userId: Int, planId: String): Subscription =
        api.subscribe(CreateSubscriptionRequest(userId, planId)).toModel()

    /** Comprobante de pago de una suscripción (US25). */
    suspend fun getReceipt(subscriptionId: Int): Receipt =
        api.getReceipt(subscriptionId).toModel()

    /** Cancela una suscripción activa. */
    suspend fun cancelSubscription(id: Int) {
        val response = api.cancelSubscription(id)
        if (!response.isSuccessful) throw retrofit2.HttpException(response)
    }

    suspend fun getPromosByOwner(ownerId: Int): List<Promo> =
        api.getPromos(ownerId = ownerId).map { it.toModel() }

    /** Promos activas de un huarique para mostrar al cliente (US26). */
    suspend fun getPromosByHuarique(huariqueId: Int): List<Promo> =
        api.getPromos(huariqueId = huariqueId).map { it.toModel() }.filter { it.isActive }

    /** Canjea una promoción incrementando su contador de usos. */
    suspend fun usePromo(id: Int) {
        val response = api.usePromo(id)
        if (!response.isSuccessful) throw retrofit2.HttpException(response)
    }

    suspend fun getPromo(id: Int): Promo =
        api.getPromo(id).toModel()

    suspend fun createPromo(
        title: String,
        note: String,
        huariqueId: Int?,
        type: String,
        discount: Int
    ): Promo = api.createPromo(
        CreatePromoRequest(
            title = title,
            note = note,
            huariqueId = huariqueId,
            type = type,
            discount = discount
        )
    ).toModel()

    suspend fun updatePromo(id: Int, patch: Map<String, Any?>): Promo =
        api.patchPromo(id, patch).toModel()

    suspend fun deletePromo(id: Int) {
        api.deletePromo(id)
    }
}
