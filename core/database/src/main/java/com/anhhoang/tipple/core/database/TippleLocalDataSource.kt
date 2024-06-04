package com.anhhoang.tipple.core.database

import kotlinx.coroutines.flow.Flow

/** Local data source for the Tipple app. */
interface TippleLocalDataSource {
    /** Get a list of favourite cocktails. */
    fun getFavouriteCocktails(): Flow<List<Int>>

    /** Get a favourite cocktail by its ID. */
    fun getFavouriteCocktailById(id: Int): Flow<Int?>

    /** Insert a favourite cocktail. */
    suspend fun insertFavouriteCocktail(id: Int)

    /** Delete a favourite cocktail by its ID. */
    suspend fun deleteFavouriteCocktailById(id: Int)
}