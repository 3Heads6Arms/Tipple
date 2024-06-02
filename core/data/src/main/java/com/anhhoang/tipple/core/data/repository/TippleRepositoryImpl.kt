package com.anhhoang.tipple.core.data.repository

import com.anhhoang.tipple.core.data.extensions.toCocktail
import com.anhhoang.tipple.core.data.model.Cocktail
import com.anhhoang.tipple.core.data.model.Resource
import com.anhhoang.tipple.core.network.TippleNetworkDataSource
import javax.inject.Inject

/** Implementation of [TippleRepository]. */
class TippleRepositoryImpl @Inject internal constructor(
    private val tippleNetworkDataSource: TippleNetworkDataSource,
) : TippleRepository {
    override suspend fun searchCocktails(name: String): Resource<List<Cocktail>> {
        try {
            val cocktails = tippleNetworkDataSource.searchCocktails(name).map { it.toCocktail() }
            return Resource.Success(cocktails)
        } catch (e: Exception) {
            return Resource.Error(e)
        }
    }
}
