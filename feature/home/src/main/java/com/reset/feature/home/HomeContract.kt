package com.reset.feature.home

data class HomeState(
    val title: String = "RESET APP",
    val message: String = "",
    val isLoading: Boolean = true,
)

sealed interface HomeSideEffect

sealed interface HomeEvent {
    data object Load : HomeEvent
}
