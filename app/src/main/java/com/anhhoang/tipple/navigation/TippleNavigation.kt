package com.anhhoang.tipple.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

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

        }
        composable<DetailsScreen> {

        }
    }
}
