package com.reset.feature.home.navigation

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavController

interface HomeNavigationAction {
    fun openLoadingScreen()
    fun openHomeScreen()
    fun openSessionScreen()
    fun openSettingsScreen()
    fun openErrorScreen()
    fun backToHome()
}

internal class HomeNavigationActionImpl(
    private val navController: NavController,
) : HomeNavigationAction {

    override fun openLoadingScreen() {
        HomeNavigationRoutes.LoadingScreen.navigateSingleTop(navController)
    }

    /** Lands on Home and clears the loading/error entry so back exits the flow. */
    override fun openHomeScreen() {
        navController.navigate(HomeNavigationRoutes.HomeScreen.route) {
            popUpTo(navController.graph.startDestinationId) { inclusive = true }
            launchSingleTop = true
        }
    }

    override fun openSessionScreen() {
        HomeNavigationRoutes.SessionScreen.navigate(navController)
    }

    override fun openSettingsScreen() {
        HomeNavigationRoutes.SettingsScreen.navigate(navController)
    }

    override fun openErrorScreen() {
        navController.navigate(HomeNavigationRoutes.ErrorScreen.route) {
            popUpTo(navController.graph.startDestinationId) { inclusive = true }
            launchSingleTop = true
        }
    }

    override fun backToHome() {
        navController.popBackStack(HomeNavigationRoutes.HomeScreen.route, inclusive = false)
    }
}

val LocalHomeNavigationAction = staticCompositionLocalOf<HomeNavigationAction> {
    error("No HomeNavigationAction specified")
}
