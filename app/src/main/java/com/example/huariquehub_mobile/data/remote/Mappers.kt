package com.example.huariquehub_mobile.data.remote

import com.example.huariquehub_mobile.data.model.Category
import com.example.huariquehub_mobile.data.model.Huarique
import com.example.huariquehub_mobile.data.model.Plan
import com.example.huariquehub_mobile.data.model.Promo
import com.example.huariquehub_mobile.data.model.Review
import com.example.huariquehub_mobile.data.model.Subscription
import com.example.huariquehub_mobile.data.model.AppNotification
import com.example.huariquehub_mobile.data.model.Receipt
import com.example.huariquehub_mobile.data.model.UserPreferences
import com.example.huariquehub_mobile.data.remote.dto.CategoryDto
import com.example.huariquehub_mobile.data.remote.dto.HuariqueDto
import com.example.huariquehub_mobile.data.remote.dto.NotificationDto
import com.example.huariquehub_mobile.data.remote.dto.PlanDto
import com.example.huariquehub_mobile.data.remote.dto.PreferenceDto
import com.example.huariquehub_mobile.data.remote.dto.PromoDto
import com.example.huariquehub_mobile.data.remote.dto.ReceiptDto
import com.example.huariquehub_mobile.data.remote.dto.ReviewDto
import com.example.huariquehub_mobile.data.remote.dto.SubscriptionDto
import com.example.huariquehub_mobile.data.remote.ApiClient

/** Convierte los DTOs del backend a los modelos de dominio usados por la UI. */

fun HuariqueDto.toModel(): Huarique = Huarique(
    id = id,
    name = name,
    category = category.orEmpty(),
    address = address.orEmpty(),
    district = district.orEmpty(),
    rating = (rating ?: 0.0).toFloat(),
    reviewCount = 0,
    description = description.orEmpty(),
    phone = phone.orEmpty(),
    hours = formatHours(openAt, closeAt),
    price = (price ?: 0.0).toFloat(),
    imageUrl = if (!imageUrl.isNullOrBlank()) imageUrl!!
    else "${ApiClient.BASE_URL}huariques/$id/image" +
            (updatedAt?.let { "?v=${it.hashCode()}" } ?: ""),
    isFavorite = false,
    latitude = latitude ?: 0.0,
    longitude = longitude ?: 0.0,
    ownerId = ownerId,
    deliveryAvailable = deliveryAvailable ?: false,
    takeawayAvailable = takeawayAvailable ?: false,
    dineInAvailable = dineInAvailable ?: true,
    openAt = openAt,
    closeAt = closeAt
)

fun CategoryDto.toModel(): Category = Category(
    id = id,
    name = name,
    icon = categoryIcon(name)
)

fun ReviewDto.toModel(): Review = Review(
    id = id,
    huariqueId = huariqueId,
    userName = "Usuario #$userId",
    rating = rating.toFloat(),
    comment = comment.orEmpty(),
    date = createdAt?.take(10).orEmpty()
)

fun PlanDto.toModel(): Plan = Plan(
    id = id,
    name = name,
    price = price.toFloat(),
    features = planFeatures(id)
)

fun PromoDto.toModel(): Promo = Promo(
    id = id,
    title = title,
    note = note.orEmpty(),
    type = type ?: "otro",
    discount = discount ?: 0,
    code = code,
    startDate = startDate,
    endDate = endDate,
    maxUses = maxUses,
    currentUses = currentUses ?: 0,
    huariqueId = huariqueId,
    isActive = isActive ?: true
)

fun SubscriptionDto.toModel(): Subscription = Subscription(
    id = id,
    userId = userId,
    planId = planId,
    planName = planName,
    planPrice = planPrice?.toFloat(),
    status = status,
    isActive = isActive,
    startDate = startDate,
    endDate = endDate
)

fun PreferenceDto.toModel(): UserPreferences = UserPreferences(
    userId = userId,
    preferredCategory = preferredCategory,
    maxBudget = maxBudget,
    preferredDistrict = preferredDistrict,
    notificationsEnabled = notificationsEnabled
)

fun NotificationDto.toModel(): AppNotification = AppNotification(
    id = id,
    title = title,
    body = body,
    isRead = isRead,
    date = createdAt?.take(10).orEmpty()
)

fun ReceiptDto.toModel(): Receipt = Receipt(
    receiptNumber = receiptNumber,
    subscriptionId = subscriptionId,
    planName = planName,
    amount = amount,
    currency = currency,
    status = status,
    issuedAt = issuedAt?.take(10).orEmpty(),
    periodStart = periodStart?.take(10).orEmpty(),
    periodEnd = periodEnd?.take(10)
)

private fun formatHours(openAt: String?, closeAt: String?): String =
    if (!openAt.isNullOrBlank() && !closeAt.isNullOrBlank()) "$openAt – $closeAt"
    else "Horario no disponible"

private fun categoryIcon(name: String): String = when (name.lowercase()) {
    "pollo" -> "🍗"
    "marina" -> "🐟"
    "criolla" -> "🥘"
    "chifa" -> "🥡"
    "postres" -> "🍰"
    "menú", "menu" -> "🍽️"
    "café", "cafe" -> "☕"
    "parrillas" -> "🍖"
    "todas" -> "🍽️"
    else -> "🍴"
}

private fun planFeatures(id: String): List<String> = when (id.lowercase()) {
    "basic" -> listOf(
        "Publica hasta 1 local",
        "Recibe reseñas de clientes",
        "Aparece en el buscador"
    )
    "premium" -> listOf(
        "Todo lo de Básico",
        "Hasta 5 locales",
        "Gestión de promociones",
        "Estadísticas básicas",
        "Soporte prioritario"
    )
    "exclusive" -> listOf(
        "Todo lo de Premium",
        "Locales ilimitados",
        "Analytics avanzados",
        "Promos destacadas en inicio",
        "Acceso a API pública"
    )
    else -> emptyList()
}
