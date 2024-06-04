package com.anhhoang.tipple.core.database.room

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import com.anhhoang.tipple.core.coroutines.BlockingContext
import com.anhhoang.tipple.core.database.TippleLocalDataSource
import com.anhhoang.tipple.core.database.model.CocktailOfTheDay
import com.anhhoang.tipple.core.database.model.FavouriteCocktailEntity
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/** Room implementation of the Local data source for the Tipple app. */
@Singleton
class TippleLocalDataSourceImpl @Inject internal constructor(
    @BlockingContext private val coroutineContext: CoroutineContext,
    private val favouriteCocktailDao: FavouriteCocktailDao,
    private val dataStore: DataStore<Preferences>
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

    override suspend fun saveCocktailOfTheDay(cocktailOfTheDay: CocktailOfTheDay) {
        dataStore.edit {
            it[COCKTAIL_ID_KEY] = cocktailOfTheDay.id
            it[COCKTAIL_DATE_KEY] =
                cocktailOfTheDay.date.atStartOfDay(ZoneId.systemDefault()).toInstant()
                    .toEpochMilli()
        }
    }

    override fun getCocktailOfTheDay(): Flow<CocktailOfTheDay?> =
        dataStore.data.map {
            val id = it[COCKTAIL_ID_KEY]
            val date = it[COCKTAIL_DATE_KEY]
            if (id != null && date != null) {
                CocktailOfTheDay(
                    id = id,
                    date = Instant.ofEpochMilli(date).atZone(ZoneId.systemDefault()).toLocalDate()
                )
            } else {
                null
            }
        }

    companion object {
        private val COCKTAIL_ID_KEY = intPreferencesKey("cockTailId")
        private val COCKTAIL_DATE_KEY = longPreferencesKey("cocktailDate")
    }
}
