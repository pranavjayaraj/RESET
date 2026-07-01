package com.reset.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringArrayResource

/** All timing, animation, and string resource constants for the AFK feature live here. */
object HomeConstants {

    /** Delay before the "time for a break?" reminder banner slides in on Home. */
    const val REMINDER_BANNER_DELAY_MS = 1_600L

    /** Duration of the bubble fling-away animation before advancing to the session. */
    const val LEAVE_ANIMATION_MS = 860L

    /** Interval between meditation countdown ticks. */
    const val SESSION_TICK_MS = 1_000L

    /** Formats a seconds count as `m:ss` for the meditation countdown. */
    fun formatMmSs(totalSeconds: Int): String {
        val safe = totalSeconds.coerceAtLeast(0)
        return "${safe / 60}:${(safe % 60).toString().padStart(2, '0')}"
    }

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
    fun getFactIndex(state: HomeState): Int {
        val texts = factTexts
        return remember(state.fact.index) { state.fact.index.coerceIn(0, texts.lastIndex) }
    }
}
