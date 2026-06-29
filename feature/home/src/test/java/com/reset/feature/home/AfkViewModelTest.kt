package com.reset.feature.home

import androidx.lifecycle.SavedStateHandle
import com.reset.model.domain.EyeFactProvider
import com.reset.model.domain.model.AfkPreferences
import com.reset.model.domain.model.ChimeKind
import com.reset.model.domain.model.EyeFact
import com.reset.model.domain.model.SessionStats
import com.reset.feature.home.navigation.AfkIntent
import com.reset.feature.home.navigation.AfkSideEffect
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.orbitmvi.orbit.test.test
import kotlin.random.Random

class AfkViewModelTest {

    private fun viewModel(
        repository: FakeAfkRepository = FakeAfkRepository(),
        random: Random = Random(SEED),
    ) = AfkViewModel(SavedStateHandle(), repository, EyeFactProvider(random))

    @Test
    fun `load surfaces persisted prefs and stats as Content`() = runTest {
        val repo = FakeAfkRepository(
            preferences = AfkPreferences(durationMin = 3, remindersEnabled = false),
            stats = SessionStats(sessions = 4, totalMin = 20, streak = 2),
        )

        viewModel(repo).test(this) {
            expectInitialState()
            containerHost.handleAfkIntent(AfkIntent.Load)

            val content = awaitUntil { it.status is AfkStatus.Content }
            assertEquals(3, content.durationMin)
            assertFalse(content.remindersEnabled)
            assertEquals(4, content.stats.sessions)
            assertEquals(Random(SEED).nextInt(EyeFact.COUNT), content.fact.index)

            cancelAndIgnoreRemainingItems()
        }
    }

    @Test
    fun `load failure surfaces Error status`() = runTest {
        viewModel(FakeAfkRepository(failPreferences = true)).test(this) {
            expectInitialState()
            containerHost.handleAfkIntent(AfkIntent.Load)

            val error = awaitUntil { it.status is AfkStatus.Error }
            assertTrue(error.status is AfkStatus.Error)

            cancelAndIgnoreRemainingItems()
        }
    }

    @Test
    fun `selecting a duration updates state and persists`() = runTest {
        val repo = FakeAfkRepository(AfkPreferences(durationMin = 5, remindersEnabled = false))

        viewModel(repo).test(this) {
            expectInitialState()
            containerHost.handleAfkIntent(AfkIntent.Load)
            awaitUntil { it.status is AfkStatus.Content }

            assertEquals(AfkSideEffect.ShowHomeScreen, awaitSideEffect())

            containerHost.handleAfkIntent(AfkIntent.SelectDuration(3))
            val updated = awaitUntil { it.durationMin == 3 }

            assertEquals(3, updated.durationMin)
            assertEquals(3, repo.lastDuration)

            cancelAndIgnoreRemainingItems()
        }
    }

    @Test
    fun `tapping reset flings away, plays start chime, then advances to session`() = runTest {
        val repo = FakeAfkRepository(AfkPreferences(remindersEnabled = false))

        viewModel(repo).test(this) {
            expectInitialState()
            containerHost.handleAfkIntent(AfkIntent.Load)
            awaitUntil { it.status is AfkStatus.Content }
            assertEquals(AfkSideEffect.ShowHomeScreen, awaitSideEffect())

            containerHost.handleAfkIntent(AfkIntent.TapReset)

            val leaving = awaitUntil { it.leaving }
            assertTrue(leaving.leaving)

            assertEquals(AfkSideEffect.PlayChime(ChimeKind.Start), awaitSideEffect())

            val session = awaitUntil { it.screen == AfkScreen.Session }
            assertFalse(session.leaving)

            cancelAndIgnoreRemainingItems()
        }
    }

    @Test
    fun `opening settings navigates to the settings screen`() = runTest {
        val repo = FakeAfkRepository(AfkPreferences(remindersEnabled = false))

        viewModel(repo).test(this) {
            expectInitialState()
            containerHost.handleAfkIntent(AfkIntent.Load)
            awaitUntil { it.status is AfkStatus.Content }
            assertEquals(AfkSideEffect.ShowHomeScreen, awaitSideEffect())

            containerHost.handleAfkIntent(AfkIntent.OpenSettings)
            assertEquals(AfkScreen.Settings, awaitUntil { it.screen == AfkScreen.Settings }.screen)
            assertEquals(AfkSideEffect.ShowSettingsScreen, awaitSideEffect())

            cancelAndIgnoreRemainingItems()
        }
    }

    @Test
    fun `reminder banner appears after delay and can be dismissed`() = runTest {
        val repo = FakeAfkRepository(AfkPreferences(remindersEnabled = true))

        viewModel(repo).test(this) {
            expectInitialState()
            containerHost.handleAfkIntent(AfkIntent.Load)
            awaitUntil { it.status is AfkStatus.Content }
            assertEquals(AfkSideEffect.ShowHomeScreen, awaitSideEffect())

            val shown = awaitUntil { it.showReminderBanner }
            assertTrue(shown.showReminderBanner)

            containerHost.handleAfkIntent(AfkIntent.DismissReminderBanner)
            val dismissed = awaitUntil { !it.showReminderBanner }
            assertFalse(dismissed.showReminderBanner)

            cancelAndIgnoreRemainingItems()
        }
    }

    private companion object {
        const val SEED = 1234
    }
}
