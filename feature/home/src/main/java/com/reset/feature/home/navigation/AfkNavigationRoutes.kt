package com.reset.feature.home.navigation

import android.os.Bundle
import androidx.navigation.NavController

sealed class AfkNavigationRoutes(val route: String) {

    object LoadingScreen : AfkNavigationRoutes(AfkRoutesPath.LOADING_SCREEN) {
        override fun getNavigationRoute(arguments: Bundle?): String = route
    }

    object HomeScreen : AfkNavigationRoutes(AfkRoutesPath.HOME_SCREEN) {
        override fun getNavigationRoute(arguments: Bundle?): String = route
    }

    object SessionScreen : AfkNavigationRoutes(AfkRoutesPath.SESSION_SCREEN) {
        override fun getNavigationRoute(arguments: Bundle?): String = route
    }

    object SettingsScreen : AfkNavigationRoutes(AfkRoutesPath.SETTINGS_SCREEN) {
        override fun getNavigationRoute(arguments: Bundle?): String = route
    }

    object ErrorScreen : AfkNavigationRoutes(AfkRoutesPath.ERROR_SCREEN) {
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
