package com.reset.navigation

/** A navigable destination, identified by its [route]. */
interface Screen {
    val route: String
}

/**
 * The app's cross-feature destination catalog. It lives in `:navigation` (a leaf module)
 * so any feature can target another feature's destination without depending on it — the
 * feature emits `navigator.navigate(AppDestination.X)` and the app-owned host resolves it.
 */
sealed interface AppDestination : Screen {
    data object Home : AppDestination {
        override val route = "home"
    }

    data object Settings : AppDestination {
        override val route = "settings"
    }
}
