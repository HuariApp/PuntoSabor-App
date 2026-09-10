package com.example.huariquehub_mobile.data.model

data class Huarique(
    val id: Int,
    val name: String,
    val category: String,
    val address: String,
    val district: String,
    val rating: Float,
    val reviewCount: Int,
    val description: String,
    val phone: String,
    val hours: String,
    val price: Float = 0f,
    val imageUrl: String = "",
    val isFavorite: Boolean = false,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val ownerId: Int? = null,
    val deliveryAvailable: Boolean = false,
    val takeawayAvailable: Boolean = false,
    val dineInAvailable: Boolean = true,
    // Horario crudo (HH:mm) usado para calcular el estado Abierto/Cerrado (US20/US22).
    val openAt: String? = null,
    val closeAt: String? = null
)

val sampleHuariques = listOf(
    Huarique(
        id = 1,
        name = "El Rincón Criollo",
        category = "Criolla",
        address = "Jr. Ucayali 342",
        district = "Cercado de Lima",
        rating = 4.8f,
        reviewCount = 124,
        description = "El mejor pollo a la brasa y anticuchos de Lima. Tradición familiar de más de 20 años sirviendo la auténtica comida criolla peruana.",
        phone = "+51 963 179 684",
        hours = "Lun–Sáb: 12:00–21:00",
        latitude = -12.0464,
        longitude = -77.0428
    ),
    Huarique(
        id = 2,
        name = "Marisquería Don Pepe",
        category = "Marina",
        address = "Av. La Marina 856",
        district = "San Miguel",
        rating = 4.6f,
        reviewCount = 89,
        description = "Ceviche fresco preparado diario con los mejores ingredientes del mar. Especialidad en leche de tigre y tiradito al estilo nikkei.",
        phone = "+51 985 432 100",
        hours = "Lun–Dom: 10:00–17:00",
        latitude = -12.0781,
        longitude = -77.0836
    ),
    Huarique(
        id = 3,
        name = "Sabor Serrano",
        category = "Andina",
        address = "Calle Los Pinos 12",
        district = "Ate",
        rating = 4.5f,
        reviewCount = 67,
        description = "Auténtica cocina andina: pachamanca, caldo de gallina y rocoto relleno. Ingredientes traídos directamente de los Andes.",
        phone = "+51 971 234 567",
        hours = "Mar–Dom: 08:00–18:00",
        latitude = -12.0266,
        longitude = -76.9271
    ),
    Huarique(
        id = 4,
        name = "Verde & Fresco",
        category = "Vegetariana",
        address = "Av. Benavides 1450",
        district = "Miraflores",
        rating = 4.7f,
        reviewCount = 203,
        description = "Menú vegetariano y vegano con ingredientes orgánicos. Jugos naturales, ensaladas y platos de temporada.",
        phone = "+51 946 789 012",
        hours = "Lun–Sáb: 08:00–20:00",
        latitude = -12.1245,
        longitude = -77.0196
    ),
    Huarique(
        id = 5,
        name = "La Wok de Chen",
        category = "Chifa",
        address = "Jr. Paruro 678",
        district = "Barrios Altos",
        rating = 4.4f,
        reviewCount = 156,
        description = "Chifa auténtico con más de 30 años de tradición. Arroz chaufa, lomo saltado y wantán frito preparados al momento.",
        phone = "+51 932 456 789",
        hours = "Lun–Dom: 11:00–22:00",
        latitude = -12.0542,
        longitude = -77.0311
    )
)
