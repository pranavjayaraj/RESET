package com.reset.model.domain

import com.reset.model.domain.model.ChimeKind

/**
 * Plays the soft "singing bowl" chimes. One-shot audio is triggered from the UI
 * layer in response to [com.reset.feature.home.AfkSideEffect]s, never held in State.
 *
 * The shipped implementation is intentionally minimal (see [com.reset.repository.data.AndroidSoundController]):
 * it stands in for the design's Web-Audio bowl synthesis and is swappable.
 */
interface SoundController {
    fun playChime(kind: ChimeKind)
}
