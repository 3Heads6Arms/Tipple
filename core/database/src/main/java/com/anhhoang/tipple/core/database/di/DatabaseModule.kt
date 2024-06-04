package com.anhhoang.tipple.core.database.di

import android.content.Context
import androidx.room.Room
import com.anhhoang.tipple.core.database.room.TippleDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal class DatabaseModule {
    @Provides
    @Singleton
    internal fun provideTippleDatabase(@ApplicationContext context: Context): TippleDatabase =
        Room.databaseBuilder(context, TippleDatabase::class.java, "tipple_database").build()

    @Provides
    @Singleton
    internal fun provideFavouriteCocktailDao(tippleDataBase: TippleDatabase) =
        tippleDataBase.favouriteCocktailDao()
}