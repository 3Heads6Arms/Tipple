package com.anhhoang.tipple.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// Ideally we should support offline first model and save Cocktails to DB, this can be a field of it
@Entity(tableName = "favourite_cocktails")
data class FavouriteCocktailEntity(
    @PrimaryKey
    val id: Int,
)
