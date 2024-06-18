package com.anhhoang.tipple.core.database.room

import com.anhhoang.tipple.core.coroutines.BlockingContext
import com.anhhoang.tipple.core.database.TippleLocalDataSource
import com.anhhoang.tipple.core.database.model.FavouriteCocktailEntity
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

/** Room implementation of the Local data source for the Tipple app. */
@Singleton
class TippleLocalDataSourceImpl @Inject internal constructor(
    @BlockingContext private val coroutineContext: CoroutineContext,
    private val favouriteCocktailDao: FavouriteCocktailDao
) : TippleLocalDataSource {
    override fun getFavouriteCocktails(): Flow<List<Int>> =
        favouriteCocktailDao.getFavouriteCocktails().flowOn(coroutineContext)


    override fun getFavouriteCocktailById(id: Int): Flow<Int?> =
        favouriteCocktailDao.getFavouriteCocktailById(id).flowOn(coroutineContext)

    override suspend fun insertFavouriteCocktail(id: Int) = withContext(coroutineContext) {
        favouriteCocktailDao.insertFavouriteCocktail(FavouriteCocktailEntity(id))
    }

    override suspend fun deleteFavouriteCocktailById(id: Int) = withContext(coroutineContext) {
        favouriteCocktailDao.deleteFavouriteCocktailById(id)
    }
}