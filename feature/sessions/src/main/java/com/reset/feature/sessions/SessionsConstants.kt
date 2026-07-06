package com.reset.feature.sessions

import androidx.compose.ui.unit.dp

/** Dimension/format constants for the Sessions feature. No magic values in UI. */
object SessionsConstants {

    /** One hour, in minutes — the divisor for rendering totals as hours + minutes. */
    const val MINUTES_PER_HOUR = 60

    val sectionSpacing = 18.dp
    val cardSpacing = 12.dp
    val cardPadding = 16.dp
    val statValueSpacing = 4.dp
    val emptyStateSpacing = 10.dp

    /** Renders a minute total as `Nm` under an hour, `Hh Mm` beyond it. */
    fun formatTotalMinutes(totalMin: Int): String {
        val hours = totalMin / MINUTES_PER_HOUR
        val minutes = totalMin % MINUTES_PER_HOUR
        return if (hours == 0) "${minutes}m" else "${hours}h ${minutes}m"
    }
}
