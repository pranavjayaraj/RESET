package com.reset.feature.home.navigation

import com.reset.model.domain.model.ChimeKind

/** One-shot effects (navigation, sound). Never held in [com.reset.feature.home.HomeState]. */
sealed class HomeSideEffect {
    data object ShowLoadingScreen : HomeSideEffect()
    data object ShowHomeScreen : HomeSideEffect()
    data object ShowSessionScreen : HomeSideEffect()
    data object ShowSettingsScreen : HomeSideEffect()
    data object ShowErrorScreen : HomeSideEffect()
    data object NavigateHome : HomeSideEffect()
    data object CloseActivity : HomeSideEffect()

    data class PlayChime(val kind: ChimeKind) : HomeSideEffect()
}
