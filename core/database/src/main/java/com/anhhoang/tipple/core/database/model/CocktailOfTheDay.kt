package com.anhhoang.tipple.core.database.model

import java.time.Instant
import java.time.LocalDate

/** Object to keep track of the cocktail of the day. */
data class CocktailOfTheDay(
    val id: Int,
    val date: Instant,
)
