package com.example.huariquehub_mobile.data.model

data class Subscription(
    val id: Int,
    val userId: Int,
    val planId: String,
    val planName: String?,
    val planPrice: Float?,
    val status: String,
    val isActive: Boolean,
    val startDate: String?,
    val endDate: String?
)

val sampleSubscriptions = listOf(
    Subscription(
        id = 1, userId = 2, planId = "premium",
        planName = "Premium", planPrice = 35f,
        status = "active", isActive = true,
        startDate = "2025-01-01", endDate = null
    ),
    Subscription(
        id = 2, userId = 1, planId = "basic",
        planName = "Básico", planPrice = 0f,
        status = "active", isActive = true,
        startDate = "2025-03-01", endDate = null
    )
)
