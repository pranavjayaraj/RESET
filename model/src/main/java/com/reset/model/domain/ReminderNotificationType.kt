package com.reset.model.domain

import com.reset.model.domain.model.EyeFact
import kotlin.random.Random

/**
 * The reminder variants the worker can post, mirroring vibely's `NotificationType`:
 * a typed catalog the notification builder switches on for content. Pure data — the
 * repository layer resolves each type to strings at show time.
 */
sealed interface ReminderNotificationType {

    /** The default nudge: "time to reset". */
    data object ResetNudge : ReminderNotificationType

    /** Protects an active streak: "don't break your N-day streak". */
    data class StreakGuard(val streakDays: Int) : ReminderNotificationType

    /** A gentler variant leading with an eye-science fact. */
    data class EyeFactNudge(val fact: EyeFact) : ReminderNotificationType
}

/** Picks which reminder variant to post at fire time. Pure — testable with a seeded [Random]. */
object ReminderTypeSelector {

    /**
     * Uniformly random among the eligible variants: the default nudge and the eye fact are
     * always eligible; the streak guard joins only while a streak is alive.
     */
    fun select(streakDays: Int, fact: EyeFact, random: Random): ReminderNotificationType {
        val eligible = buildList {
            add(ReminderNotificationType.ResetNudge)
            add(ReminderNotificationType.EyeFactNudge(fact))
            if (streakDays > 0) add(ReminderNotificationType.StreakGuard(streakDays))
        }
        return eligible.random(random)
    }
}
