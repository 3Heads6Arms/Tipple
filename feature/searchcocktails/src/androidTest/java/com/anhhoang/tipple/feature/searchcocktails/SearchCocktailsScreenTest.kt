package com.anhhoang.tipple.feature.searchcocktails

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.anhhoang.tipple.core.data.model.Cocktail
import com.anhhoang.tipple.feature.searchcocktails.SearchCocktailsScreenTestTags.EMPTY_LIST
import com.anhhoang.tipple.feature.searchcocktails.SearchCocktailsScreenTestTags.SEARCH_BAR
import com.anhhoang.tipple.feature.searchcocktails.SearchCocktailsScreenTestTags.SEARCH_ERROR
import com.anhhoang.tipple.feature.searchcocktails.SearchCocktailsScreenTestTags.SEARCH_LOADING
import com.anhhoang.tipple.feature.searchcocktails.SearchCocktailsScreenTestTags.SEARCH_RESULT
import com.anhhoang.tipple.feature.searchcocktails.SearchCocktailsScreenTestTags.SEARCH_RESULTS

import org.junit.Rule
import org.junit.Test

class SearchCocktailsScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    private var capturedAction: SearchCocktailsAction? = null


    private fun setUp(state: SearchCocktailsState) {
        composeRule.setContent {
            SearchCocktailsScreen(state) { capturedAction = it }
        }
    }

    @Test
    fun searchCocktailsScreen_expectSearchBarIsDisplayed() {
        setUp(SearchCocktailsState())

        composeRule.onNodeWithTag(SEARCH_BAR).assertIsDisplayed()
    }

    @Test
    fun searchCocktailsScreen_empty_expectEmptyListIsDisplayed() {
        setUp(SearchCocktailsState())

        composeRule.onNodeWithTag(EMPTY_LIST).assertIsDisplayed()
    }

    @Test
    fun searchCocktailsScreen_error_expectErrorIsDisplayed() {
        setUp(SearchCocktailsState(hasError = true))

        composeRule.onNodeWithTag(SEARCH_ERROR).assertIsDisplayed()
        composeRule.onNodeWithText("Retry").assertIsDisplayed()
    }

    @Test
    fun searchCocktailsScreen_loading_expectLoadingIsDisplayed() {
        setUp(SearchCocktailsState(isLoading = true))

        composeRule.onNodeWithTag(SEARCH_LOADING).assertIsDisplayed()
    }

    @Test
    fun searchCocktailsScreen_cocktails_expectCocktailsAreDisplayed() {
        setUp(
            SearchCocktailsState(
                cocktails = listOf(
                    Cocktail(
                        id = 1,
                        name = "Mojito",
                        instructions = "Mix all ingredients",
                        thumbnail = "",
                        generation = null,
                        servingGlass = "",
                        image = "",
                        ingredients = emptyList(),
                        category = "",
                        type = "",
                    ), Cocktail(
                        id = 1,
                        name = "Mojito 2",
                        instructions = "Mix all ingredients 2",
                        thumbnail = "",
                        generation = null,
                        servingGlass = "",
                        image = "",
                        ingredients = emptyList(),
                        category = "",
                        type = "",
                    )
                ),
            )
        )

        composeRule.onNodeWithTag(SEARCH_RESULTS).assertIsDisplayed()
        composeRule.onAllNodesWithTag(SEARCH_RESULT).assertCountEquals(2)
        composeRule.onNodeWithText("Mojito").assertIsDisplayed()
        composeRule.onNodeWithText("Mix all ingredients").assertIsDisplayed()
        composeRule.onNodeWithText("Mojito 2").assertIsDisplayed()
        composeRule.onNodeWithText("Mix all ingredients 2").assertIsDisplayed()
    }
}
