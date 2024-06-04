package com.anhhoang.tipple.core.database.room

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.anhhoang.tipple.core.database.TippleLocalDataSource
import com.anhhoang.tipple.core.database.model.FavouriteCocktailEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Test implementation of the Local data source for the Tipple app. */
@RunWith(RobolectricTestRunner::class)
class TippleLocalDataSourceImplTest {

    private val testDispatcher = StandardTestDispatcher()
    private val database = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(), TippleDatabase::class.java,
    ).allowMainThreadQueries().build()

    private val tippleLocalDataSource: TippleLocalDataSource =
        TippleLocalDataSourceImpl(testDispatcher, database.favouriteCocktailDao())

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun getFavouriteCocktails_defaults_expectEmpty() = runTest(testDispatcher) {
        tippleLocalDataSource.getFavouriteCocktails().test {
            assertThat(awaitItem()).isEmpty()
        }
    }

    @Test
    fun getFavouriteCocktailById_defaults_expectNull() = runTest(testDispatcher) {
        tippleLocalDataSource.getFavouriteCocktailById(1).test {
            assertThat(awaitItem()).isNull()
        }
    }

    @Test
    fun insertFavouriteCocktail() = runTest(testDispatcher) {
        tippleLocalDataSource.insertFavouriteCocktail(2)

        tippleLocalDataSource.getFavouriteCocktailById(2).test {
            assertThat(awaitItem()).isEqualTo(2)
        }
    }

    @Test
    fun deleteFavouriteCocktailById() = runTest(testDispatcher) {
        tippleLocalDataSource.insertFavouriteCocktail(1)
        tippleLocalDataSource.getFavouriteCocktailById(1).test {
            tippleLocalDataSource.deleteFavouriteCocktailById(1)

            assertThat(awaitItem()).isNull()
        }
    }
}