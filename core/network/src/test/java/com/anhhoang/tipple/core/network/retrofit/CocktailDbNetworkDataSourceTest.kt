package com.anhhoang.tipple.core.network.retrofit

import com.anhhoang.tipple.core.network.model.NetworkCocktail
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlin.test.assertFailsWith
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/** Tests for [CocktailDbNetworkDataSource]. */
class CocktailDbNetworkDataSourceTest {

    private val api = mockk<CocktailDbApi>()
    private lateinit var dataSource: CocktailDbNetworkDataSource

    @BeforeEach
    fun setUp() {
        dataSource = CocktailDbNetworkDataSource(api, StandardTestDispatcher())
    }

    @Test
    fun searchCocktails_success_expectExactCocktails() = runTest {
        coEvery { api.searchCocktails(any()) } returns SearchCocktailsResponse(
            listOf(testCocktail)
        )

        val result = dataSource.searchCocktails("Cocktail 1")

        assertThat(result).containsExactly(testCocktail)
    }

    @Test
    fun searchCocktails_failure_expectExceptionPropagated() = runTest {
        coEvery { api.searchCocktails(any()) } throws RuntimeException("Test")

        assertFailsWith<RuntimeException>(message = "test") { dataSource.searchCocktails("Cocktail 1") }
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
            ingredient3 = "Ingredient 3",
            measure3 = "Measure 3",
            ingredient4 = "Ingredient 4",
            measure4 = "Measure 4",
            ingredient5 = "Ingredient 5",
            measure5 = "Measure 5",
            ingredient6 = "Ingredient 6",
            measure6 = "Measure 6",
            ingredient7 = "Ingredient 7",
            measure7 = "Measure 7",
            ingredient8 = "Ingredient 8",
            measure8 = "Measure 8",
            ingredient9 = "Ingredient 9",
            measure9 = "Measure 9",
            ingredient10 = "Ingredient 10",
            measure10 = "Measure 10",
            ingredient11 = "Ingredient 11",
            measure11 = "Measure 11",
            ingredient12 = "Ingredient 12",
            measure12 = "Measure 12",
            ingredient13 = "Ingredient 13",
            measure13 = "Measure 13",
            ingredient14 = "Ingredient 14",
            measure14 = "Measure 14",
            ingredient15 = "Ingredient 15",
            measure15 = "Measure 15",
        )
    }
}
