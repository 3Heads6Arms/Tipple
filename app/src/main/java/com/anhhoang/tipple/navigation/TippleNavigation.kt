package com.anhhoang.tipple.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.anhhoang.tipple.feature.cocktaildetails.CocktailDetailsAction
import com.anhhoang.tipple.feature.cocktaildetails.CocktailDetailsScreen
import com.anhhoang.tipple.feature.cocktaildetails.CocktailDetailsViewModel
import com.anhhoang.tipple.feature.searchcocktails.SearchCocktailsAction
import com.anhhoang.tipple.feature.searchcocktails.SearchCocktailsScreen
import com.anhhoang.tipple.feature.searchcocktails.SearchCocktailsViewModel

/** Main navigation composable. */
@Composable
fun TippleNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController, startDestination = MainScreen
    ) {
        composable<MainScreen> {
            val viewModel = hiltViewModel<SearchCocktailsViewModel>()
            val state by viewModel.state.collectAsStateWithLifecycle()
            SearchCocktailsScreen(state) {
                when (it) {
                    is SearchCocktailsAction.OpenCocktail -> {
                        navController.navigate(DetailsScreen(it.id))
                    }

                    else -> viewModel.onAction(it)
                }
            }
        }
        composable<DetailsScreen> {
            val viewModel = hiltViewModel<CocktailDetailsViewModel>()
            val state by viewModel.state.collectAsStateWithLifecycle()
            CocktailDetailsScreen(state) {
                when (it) {
                    CocktailDetailsAction.GoBack -> navController.popBackStack()
                    else -> viewModel.onAction(it)
                }
            }
        }
    }
}
