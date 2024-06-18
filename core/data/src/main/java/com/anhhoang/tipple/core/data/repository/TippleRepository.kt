package com.anhhoang.tipple.core.data.repository

import com.anhhoang.tipple.core.data.model.Cocktail
import com.anhhoang.tipple.core.data.model.Resource
import kotlinx.coroutines.flow.Flow

/** Repository to handle data from network or local storage for the Tipple app. */
interface TippleRepository {
    /**
     * Search for cocktails by name.
     *
     * @param name The name to search for.
     * @return A list of cocktails that match the name.
     */
    suspend fun searchCocktails(name: String): Resource<List<Cocktail>>

    /**
     * Get a cocktail by its ID.
     *
     * @param id The ID of the cocktail to get.
     * @return The cocktail with the given ID.
     */
    suspend fun getCocktailById(id: Int): Resource<Cocktail>
}