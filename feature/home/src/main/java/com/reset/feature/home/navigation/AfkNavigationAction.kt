package com.reset.feature.home.navigation

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavController

interface AfkNavigationAction {
    fun openLoadingScreen()
    fun openHomeScreen()
    fun openSessionScreen()
    fun openSettingsScreen()
    fun openErrorScreen()
    fun backToHome()
}

internal class AfkNavigationActionImpl(
    private val navController: NavController,
) : AfkNavigationAction {

    override fun openLoadingScreen() {
        AfkNavigationRoutes.LoadingScreen.navigateSingleTop(navController)
    }

    /** Lands on Home and clears the loading/error entry so back exits the flow. */
    override fun openHomeScreen() {
        navController.navigate(AfkNavigationRoutes.HomeScreen.route) {
            popUpTo(navController.graph.startDestinationId) { inclusive = true }
            launchSingleTop = true
        }
    }

    override fun openSessionScreen() {
        AfkNavigationRoutes.SessionScreen.navigate(navController)
    }

    override fun openSettingsScreen() {
        AfkNavigationRoutes.SettingsScreen.navigate(navController)
    }

    override fun openErrorScreen() {
        navController.navigate(AfkNavigationRoutes.ErrorScreen.route) {
            popUpTo(navController.graph.startDestinationId) { inclusive = true }
            launchSingleTop = true
        }
    }

    override fun backToHome() {
        navController.popBackStack(AfkNavigationRoutes.HomeScreen.route, inclusive = false)
    }
}

val LocalAfkNavigationAction = staticCompositionLocalOf<AfkNavigationAction> {
    error("No AfkNavigationAction specified")
}
