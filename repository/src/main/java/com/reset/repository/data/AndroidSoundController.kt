package com.reset.repository.data

import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log
import com.reset.model.domain.SoundController
import com.reset.model.domain.model.ChimeKind
import javax.inject.Inject

/**
 * Minimal stand-in for the design's synthesized singing-bowl chimes. Plays a soft,
 * low-volume system tone so the audio wiring is real and audible; intentionally
 * lighter than the reference Web-Audio synthesis and easy to replace later.
 */
class AndroidSoundController @Inject constructor() : SoundController {

    override fun playChime(kind: ChimeKind) {
        runCatching {
            val tone = when (kind) {
                ChimeKind.Start -> ToneGenerator.TONE_PROP_BEEP
                ChimeKind.End -> ToneGenerator.TONE_PROP_ACK
            }
            // Construct per-call and release after the tone so we never hold the
            // single global ToneGenerator slot open between chimes.
            val generator = ToneGenerator(AudioManager.STREAM_MUSIC, CHIME_VOLUME)
            generator.startTone(tone, CHIME_DURATION_MS)
            generator.release()
        }.onFailure { Log.w(TAG, "Unable to play chime $kind", it) }
    }

    private companion object {
        const val TAG = "AfkSoundController"
        const val CHIME_VOLUME = 40 // 0..100, kept gentle
        const val CHIME_DURATION_MS = 300
    }
}
