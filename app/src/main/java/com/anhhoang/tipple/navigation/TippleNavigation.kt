package com.anhhoang.tipple.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.anhhoang.tipple.feature.searchcocktails.SearchCocktailsScreen
import com.anhhoang.tipple.feature.searchcocktails.SearchCocktailsViewModel

/** Main navigation composable. */
@Composable
fun TippleNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = MainScreen
    ) {
        composable<MainScreen> {
            val viewModel = hiltViewModel<SearchCocktailsViewModel>()
            val state by viewModel.state.collectAsStateWithLifecycle()
            SearchCocktailsScreen(state, onAction = viewModel::onAction)
        }
        composable<DetailsScreen> {

        }
    }
}
