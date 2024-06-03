package com.anhhoang.tipple.core.data.repository

import com.anhhoang.tipple.core.data.model.Cocktail
import com.anhhoang.tipple.core.data.model.Resource
import com.anhhoang.tipple.core.network.TippleNetworkDataSource
import com.anhhoang.tipple.core.network.model.NetworkCocktail
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/** Tests for [TippleRepositoryImpl]. */
class TippleRepositoryImplTest {

    private val dataSource = mockk<TippleNetworkDataSource>()
    private lateinit var repository: TippleRepository

    @BeforeEach
    fun setUp() {
        repository = TippleRepositoryImpl(dataSource)
    }

    @Test
    fun searchCocktails_success_expectCocktailsSuccess() = runTest {
        val cocktails = listOf(testCocktail)
        coEvery { dataSource.searchCocktails(any()) } returns cocktails
        val expectedCocktail = Cocktail(
            id = testCocktail.id,
            name = testCocktail.name,
            instructions = testCocktail.instructions,
            servingGlass = testCocktail.glass,
            thumbnail = testCocktail.image,
            image = testCocktail.image,
            generation = testCocktail.generation,
            type = testCocktail.type,
            category = testCocktail.category,
            ingredients = listOf(
                "Ingredient 1",
                "Ingredient 2",
            )
        )

        val result = repository.searchCocktails("Cocktail 1")

        assertThat(result).isEqualTo(Resource.Success(listOf(expectedCocktail)))
    }

    @Test
    fun searchCocktails_failure_expectResourceError() = runTest {
        val exception = RuntimeException("Test")
        coEvery { dataSource.searchCocktails(any()) } throws exception

        val result = repository.searchCocktails("Cocktail 1")

        assertThat(result).isEqualTo(Resource.Error(exception))
    }

    @Test
    fun getCocktailById_success_expectCocktailsSuccess() = runTest {
        val cocktails = listOf(testCocktail)
        coEvery { dataSource.getCocktailsById(any()) } returns cocktails
        val expectedCocktail = Cocktail(
            id = testCocktail.id,
            name = testCocktail.name,
            instructions = testCocktail.instructions,
            servingGlass = testCocktail.glass,
            thumbnail = testCocktail.image,
            image = testCocktail.image,
            generation = testCocktail.generation,
            type = testCocktail.type,
            category = testCocktail.category,
            ingredients = listOf(
                "Ingredient 1",
                "Ingredient 2",
            )
        )

        val result = repository.getCocktailById(1)

        assertThat(result).isEqualTo(Resource.Success(expectedCocktail))
    }

    @Test
    fun getCocktailById_failure_expectResourceError() = runTest {
        val exception = RuntimeException("Test")
        coEvery { dataSource.getCocktailsById(any()) } throws exception

        val result = repository.getCocktailById(1)

        assertThat(result).isEqualTo(Resource.Error(exception))
    }

    companion object {
        private val testCocktail = NetworkCocktail(
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