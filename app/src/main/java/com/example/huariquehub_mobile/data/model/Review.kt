package com.example.huariquehub_mobile.data.model

data class Review(
    val id: Int,
    val huariqueId: Int,
    val userName: String,
    val rating: Float,
    val comment: String,
    val date: String
)

val sampleReviews = listOf(
    Review(1, 1, "María G.", 5f, "¡Increíble! El mejor pollo a la brasa que he probado en Lima. El ambiente es sencillo pero la comida es de otro nivel.", "hace 2 días"),
    Review(2, 1, "Carlos M.", 4f, "Muy buena sazón, porciones generosas y precio justo. El caldo de pollo es espectacular.", "hace 5 días"),
    Review(3, 1, "Lucía P.", 5f, "Llevo años yendo y nunca me defrauda. Los anticuchos son perfectos.", "hace 1 semana"),
    Review(4, 2, "José R.", 5f, "El ceviche más fresco que he comido. La leche de tigre es de otro mundo.", "hace 3 días"),
    Review(5, 2, "Ana T.", 4f, "Excelente relación calidad-precio. El tiradito nikkei es mi favorito.", "hace 1 semana"),
    Review(6, 3, "Pedro S.", 5f, "La pachamanca es auténtica, igual que en la sierra. Me recuerda a mi tierra.", "hace 4 días"),
    Review(7, 4, "Valeria C.", 5f, "El mejor restaurante vegano del distrito. Los jugos son fresquísimos.", "hace 2 días"),
    Review(8, 5, "Roberto L.", 4f, "El arroz chaufa es el mejor de Barrios Altos. Siempre lleno pero vale la pena esperar.", "hace 6 días")
)

fun getReviewsForHuarique(huariqueId: Int) = sampleReviews.filter { it.huariqueId == huariqueId }
