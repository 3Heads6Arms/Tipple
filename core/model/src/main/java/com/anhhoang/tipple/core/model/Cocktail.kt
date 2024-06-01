package com.anhhoang.tipple.core.model

/** Domain model for the Tipple app. */
data class Cocktail(
    val id: Int,
    val name: String,
    val ingredients: List<Ingredient>,
    val instructions: String,
    val thumbnail: String,
    val image: String,
    val generation: String?,
    val type: String,
    val servingGlass: String,
    val category: String,
)