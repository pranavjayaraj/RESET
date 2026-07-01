package com.reset.feature.home

import androidx.lifecycle.SavedStateHandle
import com.reset.model.domain.EyeFactProvider
import com.reset.model.domain.model.HomePreferences
import com.reset.model.domain.model.ChimeKind
import com.reset.model.domain.model.EyeFact
import com.reset.model.domain.model.SessionStats
import com.reset.feature.home.navigation.HomeIntent
import com.reset.feature.home.navigation.HomeSideEffect
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.orbitmvi.orbit.test.test
import kotlin.random.Random

class HomeViewModelTest {

    private fun viewModel(
        repository: FakeHomeRepository = FakeHomeRepository(),
        random: Random = Random(SEED),
    ) = HomeViewModel(SavedStateHandle(), repository, EyeFactProvider(random))

    @Test
    fun `load surfaces persisted prefs and stats as Content`() = runTest {
        val repo = FakeHomeRepository(
            preferences = HomePreferences(durationMin = 3, remindersEnabled = false),
            stats = SessionStats(sessions = 4, totalMin = 20, streak = 2),
        )

        viewModel(repo).test(this) {
            expectInitialState()
            containerHost.handleHomeIntent(HomeIntent.Load)

            val content = awaitUntil { it.status is HomeStatus.Content }
            assertEquals(3, content.durationMin)
            assertFalse(content.remindersEnabled)
            assertEquals(4, content.stats.sessions)
            assertEquals(Random(SEED).nextInt(EyeFact.COUNT), content.fact.index)

            cancelAndIgnoreRemainingItems()
        }
    }

    @Test
    fun `load failure surfaces Error status`() = runTest {
        viewModel(FakeHomeRepository(failPreferences = true)).test(this) {
            expectInitialState()
            containerHost.handleHomeIntent(HomeIntent.Load)

            val error = awaitUntil { it.status is HomeStatus.Error }
            assertTrue(error.status is HomeStatus.Error)

            cancelAndIgnoreRemainingItems()
        }
    }

    @Test
    fun `selecting a duration updates state and persists`() = runTest {
        val repo = FakeHomeRepository(HomePreferences(durationMin = 5, remindersEnabled = false))

        viewModel(repo).test(this) {
            expectInitialState()
            containerHost.handleHomeIntent(HomeIntent.Load)
            awaitUntil { it.status is HomeStatus.Content }

            containerHost.handleHomeIntent(HomeIntent.SelectDuration(3))
            val updated = awaitUntil { it.durationMin == 3 }

            assertEquals(3, updated.durationMin)
            assertEquals(3, repo.lastDuration)

            cancelAndIgnoreRemainingItems()
        }
    }

    @Test
    fun `tapping reset starts the leave and plays the start chime`() = runTest {
        val repo = FakeHomeRepository(HomePreferences(durationMin = 5, remindersEnabled = false))

        viewModel(repo).test(this) {
            expectInitialState()
            containerHost.handleHomeIntent(HomeIntent.Load)
            awaitUntil { it.status is HomeStatus.Content }

            containerHost.handleHomeIntent(HomeIntent.TapReset)

            val leaving = awaitUntil { it.leaving }
            assertTrue(leaving.leaving)
            assertEquals(HomeScreen.Home, leaving.screen)

            assertEquals(HomeSideEffect.PlayChime(ChimeKind.Start), awaitSideEffect())

            cancelAndIgnoreRemainingItems()
        }
    }

    @Test
    fun `finishing the leave animation advances to the meditation session`() = runTest {
        val repo = FakeHomeRepository(HomePreferences(durationMin = 5, remindersEnabled = false))

        viewModel(repo).test(this) {
            expectInitialState()
            containerHost.handleHomeIntent(HomeIntent.Load)
            awaitUntil { it.status is HomeStatus.Content }

            containerHost.handleHomeIntent(HomeIntent.TapReset)
            awaitUntil { it.leaving }
            assertEquals(HomeSideEffect.PlayChime(ChimeKind.Start), awaitSideEffect())

            containerHost.handleHomeIntent(HomeIntent.LeaveAnimationFinished)

            val session = awaitUntil { it.screen == HomeScreen.Session }
            assertFalse(session.leaving)
            assertEquals(5 * 60, session.remainingSeconds)
            assertEquals(HomeSideEffect.ShowSessionScreen, awaitSideEffect())

            cancelAndIgnoreRemainingItems()
        }
    }

    @Test
    fun `opening settings navigates to the settings screen`() = runTest {
        val repo = FakeHomeRepository(HomePreferences(remindersEnabled = false))

        viewModel(repo).test(this) {
            expectInitialState()
            containerHost.handleHomeIntent(HomeIntent.Load)
            awaitUntil { it.status is HomeStatus.Content }

            containerHost.handleHomeIntent(HomeIntent.OpenSettings)
            assertEquals(HomeScreen.Settings, awaitUntil { it.screen == HomeScreen.Settings }.screen)
            assertEquals(HomeSideEffect.ShowSettingsScreen, awaitSideEffect())

            cancelAndIgnoreRemainingItems()
        }
    }

    @Test
    fun `reminder banner appears after delay and can be dismissed`() = runTest {
        val repo = FakeHomeRepository(HomePreferences(remindersEnabled = true))

        viewModel(repo).test(this) {
            expectInitialState()
            containerHost.handleHomeIntent(HomeIntent.Load)
            awaitUntil { it.status is HomeStatus.Content }

            val shown = awaitUntil { it.showReminderBanner }
            assertTrue(shown.showReminderBanner)

            containerHost.handleHomeIntent(HomeIntent.DismissReminderBanner)
            val dismissed = awaitUntil { !it.showReminderBanner }
            assertFalse(dismissed.showReminderBanner)

            cancelAndIgnoreRemainingItems()
        }
    }

    private companion object {
        const val SEED = 1234
    }
}
