package com.example.huariquehub_mobile.data.model

data class Plan(
    val id: String,
    val name: String,
    val price: Float,
    val features: List<String>
)

val samplePlans = listOf(
    Plan(
        id = "basic",
        name = "Básico",
        price = 0f,
        features = listOf(
            "Publica hasta 1 local",
            "Recibe reseñas de clientes",
            "Aparece en el buscador"
        )
    ),
    Plan(
        id = "premium",
        name = "Premium",
        price = 35f,
        features = listOf(
            "Todo lo de Básico",
            "Hasta 5 locales",
            "Gestión de promociones",
            "Estadísticas básicas",
            "Soporte prioritario"
        )
    ),
    Plan(
        id = "exclusive",
        name = "Exclusive",
        price = 50f,
        features = listOf(
            "Todo lo de Premium",
            "Locales ilimitados",
            "Analytics avanzados",
            "Promos destacadas en inicio",
            "Acceso a API pública"
        )
    )
)
