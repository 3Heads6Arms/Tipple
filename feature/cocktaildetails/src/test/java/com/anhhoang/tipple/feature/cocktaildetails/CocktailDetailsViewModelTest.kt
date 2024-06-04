package com.anhhoang.tipple.feature.cocktaildetails

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.anhhoang.tipple.core.data.model.Cocktail
import com.anhhoang.tipple.core.data.model.Resource
import com.anhhoang.tipple.core.data.repository.TippleRepository
import com.anhhoang.tipple.feature.cocktaildetails.usecase.GetCocktailUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
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

/** Tests for [CocktailDetailsViewModel]. */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class CocktailDetailsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository = mockk<TippleRepository>(relaxed = true)
    private lateinit var viewModel: CocktailDetailsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun initialState_cocktailFound_expectStateWithCocktail() = runTest(testDispatcher) {
        coEvery { repository.getCocktailById(any()) } returns Resource.Success(cocktail)
        coEvery { repository.getFavouriteCocktailById(any()) } returns flowOf(null)
        setupViewModel()

        viewModel.state.test {
            awaitItem()

            assertThat(awaitItem()).isEqualTo(
                CocktailDetailsState(
                    isLoading = false,
                    cocktail = cocktail,
                )
            )
        }
    }

    @Test
    fun initialState_favouriteCocktailFound_expectStateWithCocktail() = runTest(testDispatcher) {
        coEvery { repository.getCocktailById(any()) } returns Resource.Success(cocktail)
        coEvery { repository.getFavouriteCocktailById(any()) } returns flowOf(1)
        setupViewModel()

        viewModel.state.test {
            awaitItem()

            assertThat(awaitItem()).isEqualTo(
                CocktailDetailsState(
                    isLoading = false,
                    cocktail = favouriteCocktail,
                )
            )
        }
    }

    @Test
    fun initialState_cocktailNotFound_expectErrorState() = runTest(testDispatcher) {
        coEvery { repository.getCocktailById(any()) } returns Resource.Error(RuntimeException())
        coEvery { repository.getFavouriteCocktailById(any()) } returns flowOf(null)
        setupViewModel()

        viewModel.state.test {
            assertThat(awaitItem()).isEqualTo(CocktailDetailsState(isLoading = true))
            assertThat(awaitItem()).isEqualTo(
                CocktailDetailsState(
                    isLoading = false,
                    hasError = true,
                )
            )
        }
    }

    @Test
    fun onAction_retry_expectStateWithCocktail() = runTest(testDispatcher) {
        coEvery { repository.getCocktailById(any()) } returns
                Resource.Error(RuntimeException()) andThen
                Resource.Success(cocktail)
        coEvery { repository.getFavouriteCocktailById(any()) } returns flowOf(null)
        setupViewModel()

        viewModel.state.test {
            // Unused states, already checked by other tests.
            awaitItem()
            awaitItem()

            viewModel.onAction(CocktailDetailsAction.Retry)
            awaitItem()

            assertThat(awaitItem()).isEqualTo(
                CocktailDetailsState(
                    isLoading = false,
                    cocktail = cocktail,
                )
            )
        }
    }

    @Test
    fun onAction_favourite_notFavourite_expectCallToFavourite() = runTest(testDispatcher) {
        coEvery { repository.getCocktailById(any()) } returns Resource.Success(cocktail)
        coEvery { repository.getFavouriteCocktailById(any()) } returns flowOf(null)
        setupViewModel()


        viewModel.state.test {
            awaitItem()
            awaitItem()

            viewModel.onAction(CocktailDetailsAction.FavouriteToggle)
            advanceUntilIdle()

            coVerify(exactly = 1) { repository.favouriteCocktail(1) }
        }
    }

    @Test
    fun onAction_favourite_isFavourite_expectCallToRemoveFavourite() = runTest(testDispatcher) {
        coEvery { repository.getCocktailById(any()) } returns Resource.Success(cocktail)
        coEvery { repository.getFavouriteCocktailById(any()) } returns flowOf(1)
        setupViewModel()

        viewModel.state.test {
            awaitItem()
            awaitItem()

            viewModel.onAction(CocktailDetailsAction.FavouriteToggle)
            advanceUntilIdle()

            coVerify(exactly = 1) { repository.removeFavouriteCocktailById(1) }
        }
    }

    private fun setupViewModel() {
        viewModel = CocktailDetailsViewModel(
            SavedStateHandle(mapOf("id" to 1)),
            GetCocktailUseCase(testDispatcher, repository),
            repository,
        )
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