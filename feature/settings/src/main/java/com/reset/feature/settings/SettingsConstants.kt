package com.reset.feature.settings

import androidx.compose.ui.unit.dp

/** Timing/format/dimension constants for the Settings feature. No magic values in UI. */
object SettingsConstants {

    /** One hour, in minutes — the divisor for rendering cadences as whole hours. */
    const val MINUTES_PER_HOUR = 60

    val cardCornerExtra = 4.dp
    val sectionSpacing = 18.dp
    val rowSpacing = 10.dp
    val disabledAlpha = 0.4f

    /**
     * Formats a 24h clock hour as a friendly `h:00 am`/`h:00 pm`, matching the design.
     * Hour 0 and 24 both read as `12:00 am`.
     */
    fun formatHour(hour: Int): String {
        val meridiem = if (hour < 12 || hour == 24) "am" else "pm"
        val twelve = (hour % 12).let { if (it == 0) 12 else it }
        return "$twelve:00 $meridiem"
    }
}
