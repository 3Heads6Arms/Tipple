package com.anhhoang.tipple.core.network

import com.anhhoang.tipple.core.network.model.NetworkCocktail

/** Interface for the network data source for Tipple app. */
interface TippleNetworkDataSource {

    /**
     * Find cocktails by name.
     *
     * @param name The name of the cocktail.
     * @return A list of cocktails that contain the name.
     */
    suspend fun searchCocktails(name: String): List<NetworkCocktail>

    /**
     * Get a cocktail by id.
     *
     * @param id The id of the cocktail.
     * @return A list of cocktails with the given id. (It's just the way API is designed ¯\_(ツ)_/¯)
     */
    suspend fun getCocktailsById(id: Int): List<NetworkCocktail>
}