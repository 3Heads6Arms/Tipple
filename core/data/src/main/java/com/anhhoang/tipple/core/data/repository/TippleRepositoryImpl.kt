package com.anhhoang.tipple.core.data.repository

import com.anhhoang.tipple.core.data.extensions.toCocktail
import com.anhhoang.tipple.core.data.model.Cocktail
import com.anhhoang.tipple.core.data.model.Resource
import com.anhhoang.tipple.core.database.TippleLocalDataSource
import com.anhhoang.tipple.core.database.model.CocktailOfTheDay
import com.anhhoang.tipple.core.network.TippleNetworkDataSource
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/** Implementation of [TippleRepository]. */
class TippleRepositoryImpl @Inject internal constructor(
    private val tippleNetworkDataSource: TippleNetworkDataSource,
    private val tippleLocalDataSource: TippleLocalDataSource,
) : TippleRepository {
    override suspend fun searchCocktails(name: String): Resource<List<Cocktail>> =
        handleNetworkOps { tippleNetworkDataSource.searchCocktails(name).map { it.toCocktail() } }

    override suspend fun getCocktailById(id: Int): Resource<Cocktail> =
        handleNetworkOps { tippleNetworkDataSource.getCocktailById(id).first().toCocktail() }

    override fun getCocktailOfTheDay(): Flow<CocktailOfTheDay?> =
        tippleLocalDataSource.getCocktailOfTheDay()

    override suspend fun saveCocktailOfTheDay(id: Int) = tippleLocalDataSource.saveCocktailOfTheDay(
        CocktailOfTheDay(
            id = id,
            date = LocalDate.now(),
        )
    )

    override suspend fun getRandomCocktail(): Resource<Cocktail> =
        handleNetworkOps { tippleNetworkDataSource.getRandomCocktail().first().toCocktail() }

    override fun getFavouriteCocktails(): Flow<List<Int>> =
        tippleLocalDataSource.getFavouriteCocktails()

    override fun getFavouriteCocktailById(id: Int): Flow<Int?> =
        tippleLocalDataSource.getFavouriteCocktailById(id)

    override suspend fun favouriteCocktail(id: Int) =
        tippleLocalDataSource.insertFavouriteCocktail(id)

    override suspend fun removeFavouriteCocktailById(id: Int) =
        tippleLocalDataSource.deleteFavouriteCocktailById(id)

    private suspend fun <T> handleNetworkOps(block: suspend () -> T): Resource<T> = try {
        val result = block()
        Resource.Success(result)
    } catch (e: Exception) {
        Resource.Error(e)
    }
}
