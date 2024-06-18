package com.anhhoang.tipple.core.data.repository

import com.anhhoang.tipple.core.data.extensions.toCocktail
import com.anhhoang.tipple.core.data.model.Cocktail
import com.anhhoang.tipple.core.data.model.Resource
import com.anhhoang.tipple.core.database.TippleLocalDataSource
import com.anhhoang.tipple.core.network.TippleNetworkDataSource
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/** Implementation of [TippleRepository]. */
class TippleRepositoryImpl @Inject internal constructor(
    private val tippleNetworkDataSource: TippleNetworkDataSource,
    private val tippleLocalDataSource: TippleLocalDataSource,
) : TippleRepository {
    override suspend fun searchCocktails(name: String): Resource<List<Cocktail>> = try {
        val cocktails = tippleNetworkDataSource.searchCocktails(name).map { it.toCocktail() }
        Resource.Success(cocktails)
    } catch (e: Exception) {
        Resource.Error(e)
    }

    override suspend fun getCocktailById(id: Int): Resource<Cocktail> = try {
        val cocktail = tippleNetworkDataSource.getCocktailsById(id).first().toCocktail()
        Resource.Success(cocktail)
    } catch (e: Exception) {
        Resource.Error(e)
    }

    override fun getFavouriteCocktails(): Flow<List<Int>> =
        tippleLocalDataSource.getFavouriteCocktails()

    override fun getFavouriteCocktailById(id: Int): Flow<Int?> =
        tippleLocalDataSource.getFavouriteCocktailById(id)

    override suspend fun favouriteCocktail(id: Int) =
        tippleLocalDataSource.insertFavouriteCocktail(id)

    override suspend fun removeFavouriteCocktailById(id: Int) =
        tippleLocalDataSource.deleteFavouriteCocktailById(id)
}
