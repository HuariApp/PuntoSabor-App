package com.example.huariquehub_mobile.data.model

data class Promo(
    val id: Int,
    val title: String,
    val note: String,
    val type: String = "otro",       // 2x1 | descuento | menu | happy-hour | otro
    val discount: Int = 0,
    val code: String? = null,
    val startDate: String? = null,   // ISO date string hasta conectar API
    val endDate: String? = null,
    val maxUses: Int? = null,
    val currentUses: Int = 0,
    val huariqueId: Int? = null,
    val isActive: Boolean = true
)

val samplePromos = listOf(
    Promo(id = 1, title = "2x1 Pollo Hoy",       note = "Locales seleccionados",      type = "2x1",       discount = 50, huariqueId = 1),
    Promo(id = 2, title = "Menú Marino S/20",    note = "Lun–Vie 12:00–16:00",        type = "menu",      discount = 0,  huariqueId = 2),
    Promo(id = 3, title = "Parrillada Familiar", note = "15% en fines de semana",     type = "descuento", discount = 15, huariqueId = 1),
    Promo(id = 4, title = "Descuento Criollo",   note = "Platos criollos -10%",       type = "descuento", discount = 10, huariqueId = 2)
)
