package com.anhhoang.tipple.feature.searchcocktails

import app.cash.turbine.test
import com.anhhoang.tipple.core.data.model.Cocktail
import com.anhhoang.tipple.core.data.model.Resource
import com.anhhoang.tipple.core.data.repository.TippleRepository
import com.anhhoang.tipple.feature.searchcocktails.usecase.GetCocktailOfTheDayUseCase
import com.anhhoang.tipple.feature.searchcocktails.usecase.SearchCocktailsUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Tests for [SearchCocktailsViewModel]. */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class SearchCocktailsViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private val repository = mockk<TippleRepository>(relaxed = true)
    private val searchCocktailsUseCase = SearchCocktailsUseCase(testDispatcher, repository)
    private val getCocktailOfTheDayUseCase = GetCocktailOfTheDayUseCase(testDispatcher, repository)
    private lateinit var viewModel: SearchCocktailsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun getState_initial_isEqualsToSearchCocktailsStateDefault() {
        setupViewModel()
        assertThat(viewModel.state.value).isEqualTo(SearchCocktailsState(isLoading = true))
    }

    @Test
    fun onAction_search_success_expectStateWithCocktails() = runTest(testDispatcher) {
        every { repository.getCocktailOfTheDay() } returns flowOf(null)
        coEvery { repository.getRandomCocktail() } returns Resource.Success(cocktail)
        coEvery { repository.searchCocktails(any()) } returns Resource.Success(listOf(cocktail))
        coEvery { repository.getFavouriteCocktails() } returns flowOf(emptyList())
        setupViewModel()

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
                    cocktailOfTheDay = cocktail,
                )
            )
            assertThat(awaitItem()).isEqualTo(
                SearchCocktailsState(
                    searchQuery = "Cocktail 1",
                    cocktails = listOf(cocktail),
                    cocktailOfTheDay = cocktail,
                )
            )
        }
    }

    @Test
    fun onAction_search_successWithFavourite_expectStateWithFavouriteCocktails() =
        runTest(testDispatcher) {
            coEvery { repository.searchCocktails(any()) } returns Resource.Success(listOf(cocktail))
            coEvery { repository.getFavouriteCocktails() } returns flowOf(listOf(1))
            setupViewModel()

            viewModel.onAction(SearchCocktailsAction.Search("Cocktail 1"))

            viewModel.state.test {
                awaitItem()
                awaitItem()

                assertThat(awaitItem()).isEqualTo(
                    SearchCocktailsState(
                        searchQuery = "Cocktail 1",
                        cocktails = listOf(favouriteCocktail),
                        hasCocktailOfTheDayError = true,
                    )
                )
            }
        }

    @Test
    fun onAction_search_failure_expectStateWithError() = runTest(testDispatcher) {
        coEvery { repository.searchCocktails(any()) } returns Resource.Error(RuntimeException())
        every { repository.getFavouriteCocktails() } returns flowOf(emptyList())
        setupViewModel()

        viewModel.onAction(SearchCocktailsAction.Search("Cocktail 1"))

        viewModel.state.test {
            // Ignore loading state, already checked by other test.
            awaitItem()
            awaitItem()

            assertThat(awaitItem()).isEqualTo(
                SearchCocktailsState(
                    searchQuery = "Cocktail 1",
                    hasCocktailsError = true,
                    hasCocktailOfTheDayError = true,
                )
            )
        }
    }

    @Test
    fun onAction_retry_success_expectStateWithCocktails() = runTest(testDispatcher) {
        coEvery { repository.searchCocktails(any()) } returns Resource.Error(RuntimeException()) andThen Resource.Success(
            listOf(cocktail)
        )
        every { repository.getFavouriteCocktails() } returns flowOf(emptyList())
        setupViewModel()

        viewModel.onAction(SearchCocktailsAction.Search("Cocktail 1"))

        viewModel.state.test {
            // Unused states, already checked by other tests.
            awaitItem()
            awaitItem()
            awaitItem()

            viewModel.onAction(SearchCocktailsAction.Retry)
            awaitItem()
            awaitItem()

            assertThat(awaitItem()).isEqualTo(
                SearchCocktailsState(
                    searchQuery = "Cocktail 1",
                    cocktails = listOf(cocktail),
                    hasCocktailOfTheDayError = true,
                )
            )
        }
    }

    @Test
    fun onAction_favourite_notFavourite_expectCallToFavourite() = runTest(testDispatcher) {
        coEvery { repository.searchCocktails(any()) } returns Resource.Success(listOf(cocktail))
        coEvery { repository.getFavouriteCocktails() } returns flowOf(listOf(1))
        setupViewModel()

        viewModel.onAction(SearchCocktailsAction.Search("Cocktail 1"))

        viewModel.state.test {
            awaitItem()
            awaitItem()
            awaitItem()

            viewModel.onAction(SearchCocktailsAction.FavouriteToggle(1))
            advanceUntilIdle()

            coVerify(exactly = 1) { repository.removeFavouriteCocktailById(1) }
        }
    }

    @Test
    fun onAction_favourite_isFavourite_expectCallToRemoveFavourite() = runTest(testDispatcher) {
        coEvery { repository.searchCocktails(any()) } returns Resource.Success(listOf(cocktail))
        coEvery { repository.getFavouriteCocktails() } returns flowOf(emptyList())
        setupViewModel()

        viewModel.onAction(SearchCocktailsAction.Search("Cocktail 1"))

        viewModel.state.test {
            awaitItem()
            awaitItem()
            awaitItem()

            viewModel.onAction(SearchCocktailsAction.FavouriteToggle(1))
            advanceUntilIdle()

            coVerify(exactly = 1) { repository.favouriteCocktail(1) }
        }
    }

    private fun setupViewModel() {
        viewModel =
            SearchCocktailsViewModel(searchCocktailsUseCase, getCocktailOfTheDayUseCase, repository)
    }

    companion object {
        private val cocktail = Cocktail(
            id = 1,
            name = "Cocktail 1",
            instructions = "Instructions 1",
            ingredients = listOf(
                "Ingredient 1",
                "Ingredient 2",
            ),
            servingGlass = "Glass 1",
            thumbnail = "Image 1",
            image = "Image 1",
            generation = "Generation 1",
            type = "Type 1",
            category = "Category 1",
        )

        private val favouriteCocktail = cocktail.copy(isFavourite = true)
    }
}