package com.anhhoang.tipple.feature.cocktaildetails

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.anhhoang.tipple.core.data.model.Cocktail
import com.anhhoang.tipple.core.data.model.Resource
import com.anhhoang.tipple.core.data.repository.TippleRepository
import com.anhhoang.tipple.feature.cocktaildetails.usecase.GetCocktailUseCase
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
import org.robolectric.RobolectricTestRunner

/** Tests for [CocktailDetailsViewModel]. */
@RunWith(RobolectricTestRunner::class)
class CocktailDetailsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository = mockk<TippleRepository>()
    private lateinit var viewModel: CocktailDetailsViewModel

    @Test
    fun initialState_cocktailFound_expectStateWithCocktail() = runTest(testDispatcher) {
        coEvery { repository.getCocktailById(any()) } returns Resource.Success(cocktail)
        setUp()

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
    fun initialState_cocktailNotFound_expectErrorState() = runTest(testDispatcher) {
        coEvery { repository.getCocktailById(any()) } returns Resource.Error(RuntimeException())
        setUp()

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

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun onAction_retry_expectStateWithCocktail() = runTest(testDispatcher) {
        coEvery { repository.getCocktailById(any()) } returns
                Resource.Error(RuntimeException()) andThen
                Resource.Success(cocktail)
        setUp()

        viewModel.state.test {
            // Unused states, already checked by other tests.
            awaitItem()
            awaitItem()
            advanceTimeBy(510.milliseconds)
            runCurrent()

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

    private fun setUp() {
        viewModel = CocktailDetailsViewModel(
            SavedStateHandle(mapOf("id" to 1)),
            GetCocktailUseCase(testDispatcher, repository),
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
    }
}