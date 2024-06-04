package com.anhhoang.tipple.core.database.room

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.anhhoang.tipple.core.database.TippleLocalDataSource
import com.anhhoang.tipple.core.database.model.CocktailOfTheDay
import com.google.common.truth.Truth.assertThat
import java.time.LocalDate
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Test implementation of the Local data source for the Tipple app. */
@RunWith(RobolectricTestRunner::class)
class TippleLocalDataSourceImplTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val testDispatcher = StandardTestDispatcher()
    private val database = Room.inMemoryDatabaseBuilder(
        context,
        TippleDatabase::class.java,
    ).allowMainThreadQueries().build()
    private val testDataStore: DataStore<Preferences> = PreferenceDataStoreFactory.create(
        scope = TestScope(testDispatcher),
        produceFile = { context.preferencesDataStoreFile("test_preferences") },
    )

    private val tippleLocalDataSource: TippleLocalDataSource =
        TippleLocalDataSourceImpl(
            coroutineContext = testDispatcher,
            favouriteCocktailDao = database.favouriteCocktailDao(),
            dataStore = testDataStore,
        )

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

    @Test
    fun getCocktailOfTheDay_defaults_expectNull() = runTest(testDispatcher) {
        tippleLocalDataSource.getCocktailOfTheDay().test {
            assertThat(awaitItem()).isNull()
        }
    }

    @Test
    fun saveCocktailOfTheDay_expectValueSaved() = runTest(testDispatcher) {
        val cocktailOfTheDay = CocktailOfTheDay(1, LocalDate.now())
        tippleLocalDataSource.saveCocktailOfTheDay(cocktailOfTheDay)

        tippleLocalDataSource.getCocktailOfTheDay().test {
            assertThat(awaitItem()).isEqualTo(cocktailOfTheDay)
        }
    }
}
