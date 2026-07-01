package com.reset.feature.home.navigation

import com.reset.model.domain.model.ChimeKind

/**
 * One-shot effects for Home. Navigation is not here — it goes through the injected
 * [com.reset.navigation.Navigator]; the only remaining effect is the audio chime.
 */
sealed class HomeSideEffect {
    data class PlayChime(val kind: ChimeKind) : HomeSideEffect()
}
