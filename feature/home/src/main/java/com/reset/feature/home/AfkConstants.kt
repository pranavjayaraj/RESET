package com.reset.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringArrayResource

/** All timing, animation, and string resource constants for the AFK feature live here. */
object AfkConstants {

    /** Delay before the "time for a break?" reminder banner slides in on Home. */
    const val REMINDER_BANNER_DELAY_MS = 1_600L

    /** Duration of the bubble fling-away animation before advancing to the session. */
    const val LEAVE_ANIMATION_MS = 860L

    /** List of facts shown on the eye quotes card. */
    val factTexts: Array<String>
        @Composable
        get() {
            val res = stringArrayResource(R.array.afk_eye_fact_texts)
            return remember { res }
        }

    /** Sources matching the facts array. */
    val factSources: Array<String>
        @Composable
        get() {
            val res = stringArrayResource(R.array.afk_eye_fact_sources)
            return remember { res }
        }

    /** Words that orbit the reset bubble. */
    val resetWords: List<String>
        @Composable
        get() {
            val res = stringArrayResource(R.array.afk_reset_words)
            return remember { res.toList() }
        }

    /** Coerced index of the current fact to show. */
    @Composable
    fun getFactIndex(state: AfkState): Int {
        val texts = factTexts
        return remember(state.fact.index) { state.fact.index.coerceIn(0, texts.lastIndex) }
    }
}
