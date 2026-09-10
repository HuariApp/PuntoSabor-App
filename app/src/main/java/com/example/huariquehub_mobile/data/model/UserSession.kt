package com.example.huariquehub_mobile.data.model

enum class UserRole { CONSUMER, OWNER }

data class UserSession(
    val id: Int = 0,
    val name: String = "",
    val email: String = "",
    val role: UserRole = UserRole.CONSUMER,
    val token: String = ""
) {
    val isOwner get() = role == UserRole.OWNER
}
