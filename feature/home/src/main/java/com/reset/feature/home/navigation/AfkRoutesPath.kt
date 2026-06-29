package com.reset.feature.home.navigation

import androidx.annotation.StringDef

@Retention(AnnotationRetention.SOURCE)
@StringDef(
    AfkRoutesPath.LOADING_SCREEN,
    AfkRoutesPath.SESSION_SCREEN,
    AfkRoutesPath.SETTINGS_SCREEN,
    AfkRoutesPath.ERROR_SCREEN,
)
annotation class AfkRoutesPath {
    companion object {
        const val LOADING_SCREEN = "loading_screen"
        const val HOME_SCREEN = "home_screen"
        const val SESSION_SCREEN = "session_screen"
        const val SETTINGS_SCREEN = "settings_screen"
        const val ERROR_SCREEN = "error_screen"
    }
}
