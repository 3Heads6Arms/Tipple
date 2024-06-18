package com.anhhoang.tipple.core.data.repository

import app.cash.turbine.test
import com.anhhoang.tipple.core.data.extensions.toCocktail
import com.anhhoang.tipple.core.data.model.Cocktail
import com.anhhoang.tipple.core.data.model.Resource
import com.anhhoang.tipple.core.database.TippleLocalDataSource
import com.anhhoang.tipple.core.database.model.FavouriteCocktailEntity
import com.anhhoang.tipple.core.network.TippleNetworkDataSource
import com.anhhoang.tipple.core.network.model.NetworkCocktail
import com.google.common.base.Verify.verify
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

/** Tests for [TippleRepositoryImpl]. */
class TippleRepositoryImplTest {

    private val testDispatcher = StandardTestDispatcher()
    private val networkDataSource = mockk<TippleNetworkDataSource>()
    private val localDataSource = mockk<TippleLocalDataSource>(relaxed = true)
    private val repository: TippleRepository =
        TippleRepositoryImpl(networkDataSource, localDataSource)

    @Test
    fun searchCocktails_success_expectCocktailsSuccess() = runTest(testDispatcher) {
        val cocktails = listOf(networkCocktail)
        coEvery { networkDataSource.searchCocktails(any()) } returns cocktails
        val expectedCocktail = Cocktail(
            id = networkCocktail.id,
            name = networkCocktail.name,
            instructions = networkCocktail.instructions,
            servingGlass = networkCocktail.glass,
            thumbnail = networkCocktail.image,
            image = networkCocktail.image,
            generation = networkCocktail.generation,
            type = networkCocktail.type,
            category = networkCocktail.category,
            ingredients = listOf(
                "Ingredient 1 Measure 1",
                "Ingredient 2 Measure 2",
            )
        )

        val result = repository.searchCocktails("Cocktail 1")

        assertThat(result).isEqualTo(Resource.Success(listOf(expectedCocktail)))
    }

    @Test
    fun searchCocktails_failure_expectResourceError() = runTest(testDispatcher) {
        val exception = RuntimeException("Test")
        coEvery { networkDataSource.searchCocktails(any()) } throws exception

        val result = repository.searchCocktails("Cocktail 1")

        assertThat(result).isEqualTo(Resource.Error(exception))
    }

    @Test
    fun getCocktailById_success_expectCocktailsSuccess() = runTest(testDispatcher) {
        val cocktails = listOf(networkCocktail)
        coEvery { networkDataSource.getCocktailsById(any()) } returns cocktails
        val expectedCocktail = Cocktail(
            id = networkCocktail.id,
            name = networkCocktail.name,
            instructions = networkCocktail.instructions,
            servingGlass = networkCocktail.glass,
            thumbnail = networkCocktail.image,
            image = networkCocktail.image,
            generation = networkCocktail.generation,
            type = networkCocktail.type,
            category = networkCocktail.category,
            ingredients = listOf(
                "Ingredient 1 Measure 1",
                "Ingredient 2 Measure 2",
            )
        )

        val result = repository.getCocktailById(1)

        assertThat(result).isEqualTo(Resource.Success(expectedCocktail))
    }

    @Test
    fun getCocktailById_failure_expectResourceError() = runTest(testDispatcher) {
        val exception = RuntimeException("Test")
        coEvery { networkDataSource.getCocktailsById(any()) } throws exception

        val result = repository.getCocktailById(1)

        assertThat(result).isEqualTo(Resource.Error(exception))
    }

    @Test
    fun getFavouriteCocktails_expectCocktails() = runTest(testDispatcher) {
        every { localDataSource.getFavouriteCocktails() } returns flowOf(listOf(1, 2))

        repository.getFavouriteCocktails().test {
            assertThat(awaitItem()).containsExactly(1, 2)
            awaitComplete()
        }
    }

    @Test
    fun getFavouriteCocktailById_expectCocktail() = runTest(testDispatcher) {
        every { localDataSource.getFavouriteCocktailById(any()) } returns flowOf(5)

        repository.getFavouriteCocktailById(5).test {
            assertThat(awaitItem()).isEqualTo(5)
            awaitComplete()
        }
    }

    @Test
    fun favouriteCocktail_success_expectLocalDataSourceInvoked() = runTest(testDispatcher) {
        repository.favouriteCocktail(3)

        coVerify { localDataSource.insertFavouriteCocktail(3) }
    }

    @Test
    fun removeFavouriteCocktail_expectLocalDataSourceInvoked() = runTest(testDispatcher) {
        repository.removeFavouriteCocktailById(1)

        coVerify { localDataSource.deleteFavouriteCocktailById(1) }
    }


    companion object {
        private val networkCocktail = NetworkCocktail(
            id = 1,
            name = "Cocktail 1",
            category = "Category 1",
            generation = "Generation 1",
            type = "Type 1",
            glass = "Glass 1",
            instructions = "Instructions 1",
            image = "Image 1",
            ingredient1 = "Ingredient 1",
            measure1 = "Measure 1",
            ingredient2 = "Ingredient 2",
            measure2 = "Measure 2",
            ingredient3 = null,
            measure3 = null,
            ingredient4 = null,
            measure4 = null,
            ingredient5 = null,
            measure5 = null,
            ingredient6 = null,
            measure6 = null,
            ingredient7 = null,
            measure7 = null,
            ingredient8 = null,
            measure8 = null,
            ingredient9 = null,
            measure9 = null,
            ingredient10 = null,
            measure10 = null,
            ingredient11 = null,
            measure11 = null,
            ingredient12 = null,
            measure12 = null,
            ingredient13 = null,
            measure13 = null,
            ingredient14 = null,
            measure14 = null,
            ingredient15 = null,
            measure15 = null,
        )
    }
}