package com.reset.model.domain

import com.reset.model.domain.model.EyeFact
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class ReminderTypeSelectorTest {

    private val fact = EyeFact(index = 2)

    @Test
    fun `without a streak only the nudge and the fact are eligible`() {
        val seen = (0 until 200).map { seed ->
            ReminderTypeSelector.select(streakDays = 0, fact = fact, random = Random(seed))
        }.toSet()

        assertEquals(
            setOf(
                ReminderNotificationType.ResetNudge,
                ReminderNotificationType.EyeFactNudge(fact),
            ),
            seen,
        )
    }

    @Test
    fun `an active streak makes the streak guard eligible with its day count`() {
        val seen = (0 until 200).map { seed ->
            ReminderTypeSelector.select(streakDays = 4, fact = fact, random = Random(seed))
        }.toSet()

        assertTrue(seen.contains(ReminderNotificationType.StreakGuard(streakDays = 4)))
        assertEquals(3, seen.size)
    }

    @Test
    fun `selection is deterministic for a given seed`() {
        val first = ReminderTypeSelector.select(streakDays = 4, fact = fact, random = Random(42))
        val second = ReminderTypeSelector.select(streakDays = 4, fact = fact, random = Random(42))

        assertEquals(first, second)
    }

    @Test
    fun `eye fact variant carries the provided fact`() {
        val seen = (0 until 200).mapNotNull { seed ->
            ReminderTypeSelector.select(streakDays = 0, fact = EyeFact(index = 4), random = Random(seed))
                as? ReminderNotificationType.EyeFactNudge
        }

        assertTrue(seen.isNotEmpty())
        assertTrue(seen.all { it.fact == EyeFact(index = 4) })
    }
}
