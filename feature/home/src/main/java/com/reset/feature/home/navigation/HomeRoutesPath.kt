package com.reset.feature.home.navigation

import androidx.annotation.StringDef

@Retention(AnnotationRetention.SOURCE)
@StringDef(
    HomeRoutesPath.LOADING_SCREEN,
    HomeRoutesPath.SESSION_SCREEN,
    HomeRoutesPath.SETTINGS_SCREEN,
    HomeRoutesPath.ERROR_SCREEN,
)
annotation class HomeRoutesPath {
    companion object {
        const val LOADING_SCREEN = "loading_screen"
        const val HOME_SCREEN = "home_screen"
        const val SESSION_SCREEN = "session_screen"
        const val SETTINGS_SCREEN = "settings_screen"
        const val ERROR_SCREEN = "error_screen"
    }
}
