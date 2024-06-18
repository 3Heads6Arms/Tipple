package com.anhhoang.tipple.feature.searchcocktails

import androidx.compose.ui.test.junit4.createComposeRule

import org.junit.Rule
import org.junit.Test

class SearchCocktailsScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private var capturedAction: SearchCocktailsAction? = null


    private fun setUp(state: SearchCocktailsState) {
        composeTestRule.setContent {
            SearchCocktailsScreen(state) { capturedAction = it }
        }
    }

    @Test
    fun searchCocktailsScreen_emptyState() {
    }
}