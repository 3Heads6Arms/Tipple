package com.anhhoang.tipple.core.database.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.anhhoang.tipple.core.database.model.FavouriteCocktailEntity
import kotlinx.coroutines.flow.Flow

/** DAO for Favourited cocktails in the Tipple database. */
@Dao
internal interface FavouriteCocktailDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavouriteCocktail(cocktail: FavouriteCocktailEntity)

    @Query("DELETE FROM favourite_cocktails WHERE id = :id")
    suspend fun deleteFavouriteCocktailById(id: Int)

    @Query("SELECT id FROM favourite_cocktails")
    fun getFavouriteCocktails(): Flow<List<Int>>

    @Query("SELECT id FROM favourite_cocktails WHERE id = :id")
    fun getFavouriteCocktailById(id: Int): Flow<Int?>
}
