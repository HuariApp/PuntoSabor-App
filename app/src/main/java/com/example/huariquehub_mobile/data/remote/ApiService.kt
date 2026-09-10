package com.example.huariquehub_mobile.data.remote

import com.example.huariquehub_mobile.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*
import okhttp3.MultipartBody

/**
 * Definición de los endpoints del backend de PuntoSabor desplegado en Railway.
 * Las rutas coinciden con las documentadas en Swagger.
 */
interface ApiService {

    // ── Auth & Users ────────────────────────────────────────────────────────
    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): AuthResponseDto

    @POST("users")
    suspend fun createUser(@Body body: CreateUserRequest): UserDto

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body body: ForgotPasswordRequest): MessageDto

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body body: ResetPasswordRequest): MessageDto

    // ── Perfil de usuario (CRUD de cuenta) ───────────────────────────────────
    @GET("auth/users/{id}")
    suspend fun getProfile(@Path("id") id: Int): UserDto

    @PATCH("auth/users/{id}")
    suspend fun updateProfile(
        @Path("id") id: Int,
        @Body body: UpdateProfileRequest
    ): UserDto

    @DELETE("auth/users/{id}")
    suspend fun deleteAccount(@Path("id") id: Int): MessageDto

    // ── Huariques ───────────────────────────────────────────────────────────
    @GET("huariques")
    suspend fun getHuariques(
        @Query("q") q: String? = null,
        @Query("near") near: Boolean? = null,
        @Query("ownerId") ownerId: Int? = null
    ): List<HuariqueDto>

    @GET("huariques/{id}")
    suspend fun getHuarique(@Path("id") id: Int): HuariqueDto

    @GET("huariques/suggestions")
    suspend fun getSuggestions(@Query("userId") userId: Int): List<HuariqueDto>

    @POST("huariques")
    suspend fun createHuarique(@Body body: CreateHuariqueRequest): HuariqueDto

    @PATCH("huariques/{id}")
    suspend fun patchHuarique(
        @Path("id") id: Int,
        @Body patch: Map<String, @JvmSuppressWildcards Any?>
    ): HuariqueDto

    @DELETE("huariques/{id}")
    suspend fun deleteHuarique(@Path("id") id: Int): Response<Unit>

    @Multipart
    @POST("huariques/{id}/image")
    suspend fun uploadHuariqueImage(
        @Path("id") id: Int,
        @Part file: MultipartBody.Part
    ): HuariqueDto
    // ── Categories ──────────────────────────────────────────────────────────
    @GET("categories")
    suspend fun getCategories(): List<CategoryDto>

    // ── Favorites ───────────────────────────────────────────────────────────
    @GET("favorites")
    suspend fun getFavorites(@Query("userId") userId: Int): List<FavoriteDto>

    @POST("favorites/{huariqueId}")
    suspend fun addFavorite(
        @Path("huariqueId") huariqueId: Int,
        @Query("userId") userId: Int
    ): Response<FavoriteDto>

    @DELETE("favorites/{huariqueId}")
    suspend fun removeFavorite(
        @Path("huariqueId") huariqueId: Int,
        @Query("userId") userId: Int
    ): Response<Unit>

    // ── Reviews ─────────────────────────────────────────────────────────────
    @GET("reviews")
    suspend fun getReviews(@Query("huariqueId") huariqueId: Int): List<ReviewDto>

    @POST("reviews")
    suspend fun createReview(@Body body: CreateReviewRequest): ReviewDto

    // ── Plans ───────────────────────────────────────────────────────────────
    @GET("plans")
    suspend fun getPlans(): List<PlanDto>

    // ── Promos ──────────────────────────────────────────────────────────────
    @GET("promos")
    suspend fun getPromos(
        @Query("ownerId") ownerId: Int? = null,
        @Query("huariqueId") huariqueId: Int? = null
    ): List<PromoDto>

    @GET("promos/{id}")
    suspend fun getPromo(@Path("id") id: Int): PromoDto

    @POST("promos")
    suspend fun createPromo(@Body body: CreatePromoRequest): PromoDto

    @PATCH("promos/{id}")
    suspend fun patchPromo(
        @Path("id") id: Int,
        @Body patch: Map<String, @JvmSuppressWildcards Any?>
    ): PromoDto

    @DELETE("promos/{id}")
    suspend fun deletePromo(@Path("id") id: Int): Response<Unit>

    @POST("promos/{id}/use")
    suspend fun usePromo(@Path("id") id: Int): Response<PromoDto>

    // ── Subscriptions ───────────────────────────────────────────────────────
    @GET("subscriptions/active")
    suspend fun getActiveSubscription(@Query("userId") userId: Int): Response<SubscriptionDto>

    @POST("subscriptions")
    suspend fun subscribe(@Body body: CreateSubscriptionRequest): SubscriptionDto

    @GET("subscriptions/{id}/receipt")
    suspend fun getReceipt(@Path("id") id: Int): ReceiptDto

    @POST("subscriptions/{id}/cancel")
    suspend fun cancelSubscription(@Path("id") id: Int): Response<Unit>

    // ── Preferences (US17/US11) ─────────────────────────────────────────────
    @GET("preferences")
    suspend fun getPreferences(@Query("userId") userId: Int): PreferenceDto

    @PUT("preferences")
    suspend fun savePreferences(
        @Query("userId") userId: Int,
        @Body body: UpdatePreferenceRequest
    ): PreferenceDto

    // ── Reports (US21) ──────────────────────────────────────────────────────
    @POST("reports")
    suspend fun createReport(@Body body: CreateReportRequest): ReportDto

    // ── Notifications (US12) ────────────────────────────────────────────────
    @GET("notifications")
    suspend fun getNotifications(@Query("userId") userId: Int): List<NotificationDto>

    @PATCH("notifications/{id}/read")
    suspend fun markNotificationRead(@Path("id") id: Int): Response<Unit>
}
