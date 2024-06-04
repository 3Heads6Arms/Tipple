package com.anhhoang.tipple.core.database.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.anhhoang.tipple.core.database.model.FavouriteCocktailEntity
import com.anhhoang.tipple.core.database.model.StringListConverter

/** Room database for the Tipple app. */
@Database(entities = [FavouriteCocktailEntity::class], version = 1)
internal abstract class TippleDatabase: RoomDatabase() {
    abstract fun favouriteCocktailDao(): FavouriteCocktailDao
}
