package com.reset.model.domain.model

/** User preferences persisted across launches. */
data class HomePreferences(
    val durationMin: Int = DEFAULT_DURATION_MIN,
    val remindersEnabled: Boolean = true,
    val remindersEveryMin: Int = DEFAULT_REMINDER_EVERY_MIN,
    val remindersStartHour: Int = DEFAULT_REMINDER_START_HOUR,
    val remindersEndHour: Int = DEFAULT_REMINDER_END_HOUR,
) {
    companion object {
        const val DEFAULT_DURATION_MIN = 5

        /** Durations offered by the Home segmented control, in minutes. */
        val DURATION_OPTIONS = listOf(3, 5)

        const val DEFAULT_REMINDER_EVERY_MIN = 60
        const val DEFAULT_REMINDER_START_HOUR = 9
        const val DEFAULT_REMINDER_END_HOUR = 18

        /** Reminder cadences offered by the Settings frequency grid, in minutes. */
        val REMINDER_EVERY_OPTIONS = listOf(30, 60, 90, 120)

        /** Earliest / latest hour a reminder window boundary may take (24h clock). */
        const val MIN_HOUR = 0
        const val MAX_HOUR = 24
    }
}

/** Aggregate sit history shown on Home (and, later, the Reset summary). */
data class SessionStats(
    val sessions: Int = 0,
    val totalMin: Int = 0,
    val streak: Int = 0,
) {
    /** No sits recorded yet — Home renders its "your first sit" empty state. */
    val isFirstSit: Boolean get() = sessions == 0
}

/** Which singing-bowl chime to play. */
enum class ChimeKind { Start, End }

/**
 * One "science of closing your eyes" card. The copy lives in string resources
 * ([com.reset.feature.home.R.array.afk_eye_fact_texts] / `_sources`); a card is
 * identified purely by its [index] so the domain layer stays free of UI strings.
 */
data class EyeFact(val index: Int) {
    companion object {
        /** Must match the size of the parallel eye-fact string arrays. */
        const val COUNT = 5
    }
}
