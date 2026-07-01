package com.reset.navigation

import kotlinx.coroutines.flow.Flow

/** One-shot navigation intents emitted by features and applied by the host. */
sealed interface NavEvent {
    data class Navigate(val screen: Screen) : NavEvent
    data object Pop : NavEvent
    data object Exit : NavEvent
}

/**
 * The navigation seam. Features (ViewModels) depend on this interface only — never on a
 * `NavController` or on each other. They emit events; a single host observes the [events]
 * stream (see `ObserveNavigation`) and performs the actual navigation.
 */
interface Navigator {
    val events: Flow<NavEvent>

    fun navigate(screen: Screen)
    fun pop()
    fun exit()
}
