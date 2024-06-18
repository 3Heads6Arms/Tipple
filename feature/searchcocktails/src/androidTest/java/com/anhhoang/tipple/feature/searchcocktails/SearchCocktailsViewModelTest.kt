package com.anhhoang.tipple.feature.searchcocktails

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.anhhoang.tipple.core.data.model.Cocktail
import com.anhhoang.tipple.core.data.model.Ingredient
import com.anhhoang.tipple.core.data.model.Resource
import com.anhhoang.tipple.core.data.repository.TippleRepository
import com.anhhoang.tipple.feature.searchcocktails.usecase.SearchCocktailsUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith

/** Tests for [SearchCocktailsViewModel]. */
@RunWith(AndroidJUnit4::class)
class SearchCocktailsViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private val repository = mockk<TippleRepository>()
    private val searchCocktailsUseCase = SearchCocktailsUseCase(testDispatcher, repository)
    private val viewModel = SearchCocktailsViewModel(searchCocktailsUseCase)

    @Test
    fun getState_initial_isEqualsToSearchCocktailsStateDefault() {
        assertThat(viewModel.state.value).isEqualTo(SearchCocktailsState())
    }

    @Test
    fun onAction_search_success_expectStateWithCocktails() = runTest(testDispatcher) {
        coEvery { repository.searchCocktails(any()) } returns Resource.Success(listOf(cocktail))

        viewModel.onAction(SearchCocktailsAction.Search("Cocktail 1"))

        viewModel.state.test {
            assertThat(awaitItem()).isEqualTo(
                SearchCocktailsState(
                    searchQuery = "Cocktail 1",
                    isLoading = true,
                )
            )
            assertThat(awaitItem()).isEqualTo(
                SearchCocktailsState(
                    searchQuery = "Cocktail 1",
                    cocktails = listOf(cocktail),
                )
            )
        }
    }

    @Test
    fun onAction_search_failure_expectStateWithError() = runTest(testDispatcher) {
        coEvery { repository.searchCocktails(any()) } returns Resource.Error(RuntimeException())

        viewModel.onAction(SearchCocktailsAction.Search("Cocktail 1"))

        viewModel.state.test {
            // Ignore loading state, already checked by other test.
            awaitItem()

            assertThat(awaitItem()).isEqualTo(
                SearchCocktailsState(
                    searchQuery = "Cocktail 1",
                    hasError = true,
                )
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun onAction_retry_success_expectStateWithCocktails() = runTest(testDispatcher) {
        coEvery { repository.searchCocktails(any()) } returns
                Resource.Error(RuntimeException()) andThen
                Resource.Success(listOf(cocktail))

        viewModel.state.test {
            viewModel.onAction(SearchCocktailsAction.Search("Cocktail 1"))
            advanceTimeBy(510.milliseconds)
            runCurrent()
            viewModel.onAction(SearchCocktailsAction.Retry)

            // Unused states, already checked by other tests.
            awaitItem()
            awaitItem()
            awaitItem()

            assertThat(awaitItem()).isEqualTo(
                SearchCocktailsState(
                    searchQuery = "Cocktail 1",
                    cocktails = listOf(cocktail),
                )
            )
        }
    }

    companion object {
        private val cocktail = Cocktail(
            id = 1,
            name = "Cocktail 1",
            instructions = "Instructions 1",
            ingredients = listOf(
                Ingredient("Ingredient 1", "Measure 1"),
                Ingredient("Ingredient 2", "Measure 2"),
            ),
            servingGlass = "Glass 1",
            thumbnail = "Image 1",
            image = "Image 1",
            generation = "Generation 1",
            type = "Type 1",
            category = "Category 1",
        )
    }
}