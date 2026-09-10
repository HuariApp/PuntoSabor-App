package com.example.huariquehub_mobile.data.model

data class Category(
    val id: Int,
    val name: String,
    val icon: String
)

val sampleCategories = listOf(
    Category(1, "Todas", "🍽️"),
    Category(2, "Criolla", "🍗"),
    Category(3, "Marina", "🐟"),
    Category(4, "Andina", "🥘"),
    Category(5, "Vegetariana", "🥗"),
    Category(6, "Chifa", "🥡"),
    Category(7, "Panadería", "🍞"),
    Category(8, "Jugos", "🥤")
)
