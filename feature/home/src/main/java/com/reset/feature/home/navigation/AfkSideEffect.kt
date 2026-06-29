package com.reset.feature.home.navigation

import com.reset.model.domain.model.ChimeKind

/** One-shot effects (navigation, sound). Never held in [com.reset.feature.home.AfkState]. */
sealed class AfkSideEffect {
    data object ShowLoadingScreen : AfkSideEffect()
    data object ShowHomeScreen : AfkSideEffect()
    data object ShowSessionScreen : AfkSideEffect()
    data object ShowSettingsScreen : AfkSideEffect()
    data object ShowErrorScreen : AfkSideEffect()
    data object NavigateHome : AfkSideEffect()
    data object CloseActivity : AfkSideEffect()

    data class PlayChime(val kind: ChimeKind) : AfkSideEffect()
}
