package com.anhhoang.tipple.feature.cocktaildetails

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.anhhoang.tipple.core.data.model.Cocktail
import com.anhhoang.tipple.feature.cocktaildetails.CocktailDetailsScreenTestTags.COCKTAIL_ADDITIONAL_INFORMATION
import com.anhhoang.tipple.feature.cocktaildetails.CocktailDetailsScreenTestTags.COCKTAIL_ERROR
import com.anhhoang.tipple.feature.cocktaildetails.CocktailDetailsScreenTestTags.COCKTAIL_IMAGE
import com.anhhoang.tipple.feature.cocktaildetails.CocktailDetailsScreenTestTags.COCKTAIL_INGREDIENTS
import com.anhhoang.tipple.feature.cocktaildetails.CocktailDetailsScreenTestTags.COCKTAIL_INSTRUCTIONS
import com.anhhoang.tipple.feature.cocktaildetails.CocktailDetailsScreenTestTags.COCKTAIL_LOADING
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Tests for [CocktailDetailsViewModel]. */
@RunWith(RobolectricTestRunner::class)
class CocktailDetailsScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    private var capturedAction: CocktailDetailsAction? = null

    @Test
    fun defaultState_expectLoadingIsDisplayed() {
        setUp(CocktailDetailsState())

        composeRule.onNodeWithTag(COCKTAIL_LOADING).assertIsDisplayed()
    }

    @Test
    fun hasError_expectErrorIsDisplayed() {
        setUp(CocktailDetailsState(isLoading = false, hasError = true))

        composeRule.onNodeWithTag(COCKTAIL_ERROR).assertIsDisplayed()
        composeRule.onNodeWithText("Retry").assertIsDisplayed()
    }

    @Test
    fun cocktail_expectCocktailDetailsAreDisplayed() {
        setUp(CocktailDetailsState(isLoading = false, cocktail = cocktail))

        composeRule.onNodeWithText("Mojito").assertIsDisplayed()
        composeRule.onNodeWithTag(COCKTAIL_IMAGE).assertIsDisplayed()
        composeRule.onNodeWithTag(COCKTAIL_INSTRUCTIONS).assertIsDisplayed()
        composeRule.onNodeWithTag(COCKTAIL_INGREDIENTS).assertExists()
        composeRule.onNodeWithTag(COCKTAIL_ADDITIONAL_INFORMATION).assertExists()
    }

    @Test
    fun retryClick_expectRetryAction() {
        setUp(CocktailDetailsState(isLoading = false, hasError = true))

        composeRule.onNodeWithText("Retry").performClick()

        assertThat(capturedAction).isEqualTo(CocktailDetailsAction.Retry)
    }

    @Test
    fun goBackClick_expectGoBackAction() {
        setUp(CocktailDetailsState(isLoading = false, cocktail = cocktail))

        composeRule.onNodeWithContentDescription("Go back").performClick()

        assertThat(capturedAction).isEqualTo(CocktailDetailsAction.GoBack)
    }

    @Test
    fun favouriteClick_expectFavouriteAction() {
        setUp(CocktailDetailsState(isLoading = false, cocktail = cocktail))

        composeRule.onNodeWithContentDescription("Add to favorites").performClick()

        assertThat(capturedAction).isEqualTo(CocktailDetailsAction.FavouriteToggle)
    }

    private fun setUp(state: CocktailDetailsState) {
        composeRule.setContent {
            CocktailDetailsScreen(state) { capturedAction = it }
        }
    }

    companion object {
        private val cocktail = Cocktail(
            id = 1,
            name = "Mojito",
            instructions = "Mix all ingredients",
            thumbnail = "",
            generation = null,
            servingGlass = "",
            image = "",
            ingredients = listOf("Ingredient 1", "Ingredient 2"),
            category = "",
            type = "",
        )
    }
}