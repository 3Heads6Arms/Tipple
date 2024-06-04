package com.anhhoang.tipple.core.data.model

/** Domain model for the Tipple app. */
data class Cocktail(
    val id: Int,
    val name: String,
    val ingredients: List<String>,
    val instructions: String,
    val thumbnail: String,
    val image: String,
    val generation: String?,
    val type: String,
    val servingGlass: String,
    val category: String,
    val isFavourite: Boolean = false
)