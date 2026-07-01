package com.reset.feature.home.navigation

import android.os.Bundle
import androidx.navigation.NavController

sealed class HomeNavigationRoutes(val route: String) {

    object LoadingScreen : HomeNavigationRoutes(HomeRoutesPath.LOADING_SCREEN) {
        override fun getNavigationRoute(arguments: Bundle?): String = route
    }

    object HomeScreen : HomeNavigationRoutes(HomeRoutesPath.HOME_SCREEN) {
        override fun getNavigationRoute(arguments: Bundle?): String = route
    }

    object SessionScreen : HomeNavigationRoutes(HomeRoutesPath.SESSION_SCREEN) {
        override fun getNavigationRoute(arguments: Bundle?): String = route
    }

    object SettingsScreen : HomeNavigationRoutes(HomeRoutesPath.SETTINGS_SCREEN) {
        override fun getNavigationRoute(arguments: Bundle?): String = route
    }

    object ErrorScreen : HomeNavigationRoutes(HomeRoutesPath.ERROR_SCREEN) {
        override fun getNavigationRoute(arguments: Bundle?): String = route
    }

    abstract fun getNavigationRoute(arguments: Bundle? = null): String

    fun navigate(navController: NavController, arguments: Bundle? = null) {
        navController.navigate(getNavigationRoute(arguments))
    }

    fun navigateSingleTop(navController: NavController, arguments: Bundle? = null) {
        navController.navigate(getNavigationRoute(arguments)) {
            launchSingleTop = true
        }
    }
}
