package com.reset.feature.home

import androidx.lifecycle.SavedStateHandle
import com.reset.model.domain.EyeFactProvider
import com.reset.model.domain.ReminderAction
import com.reset.model.domain.model.HomePreferences
import com.reset.model.domain.model.ChimeKind
import com.reset.model.domain.model.EyeFact
import com.reset.model.domain.model.SessionStats
import com.reset.feature.settings.api.SettingsDestination
import com.reset.navigation.NavEvent
import com.reset.feature.home.navigation.HomeIntent
import com.reset.feature.home.navigation.HomeSideEffect
import kotlinx.coroutines.flow.first
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
        navigator: FakeNavigator = FakeNavigator(),
        random: Random = Random(SEED),
        reminderActionStore: FakeReminderActionStore = FakeReminderActionStore(),
    ) = HomeViewModel(SavedStateHandle(), repository, EyeFactProvider(random), navigator, reminderActionStore)

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
            assertEquals(HomeStep.Home, leaving.screen)

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

            val session = awaitUntil { it.screen == HomeStep.Session }
            assertFalse(session.leaving)
            assertEquals(5 * 60, session.remainingSeconds)

            cancelAndIgnoreRemainingItems()
        }
    }

    @Test
    fun `opening settings navigates via the navigator and stays on home`() = runTest {
        val repo = FakeHomeRepository(HomePreferences(remindersEnabled = false))
        val navigator = FakeNavigator()

        viewModel(repo, navigator).test(this) {
            expectInitialState()
            containerHost.handleHomeIntent(HomeIntent.Load)
            awaitUntil { it.status is HomeStatus.Content }

            containerHost.handleHomeIntent(HomeIntent.OpenSettings)

            // Settings is a separate feature — Home stays put and emits a nav event.
            assertEquals(NavEvent.Navigate(SettingsDestination), navigator.events.first())
            assertEquals(HomeStep.Home, containerHost.stateFlow().value.screen)

            cancelAndIgnoreRemainingItems()
        }
    }

    @Test
    fun `reminder StartReset action enters the session directly with the persisted duration`() = runTest {
        val repo = FakeHomeRepository(HomePreferences(durationMin = 3, remindersEnabled = true))
        val store = FakeReminderActionStore()
        // Dispatched before the ViewModel exists — the buffered store must hold it
        // (cold start from the notification's Reset button).
        store.dispatch(ReminderAction.StartReset)

        viewModel(repo, reminderActionStore = store).test(this) {
            expectInitialState()
            // runOnCreate triggers initData(), which starts load() and the reminder
            // action collector — a bare Load intent would leave the store uncollected.
            runOnCreate()
            awaitUntil { it.status is HomeStatus.Content }
            assertEquals(HomeSideEffect.PlayChime(ChimeKind.Start), awaitSideEffect())

            val session = awaitUntil { it.screen == HomeStep.Session }
            assertFalse(session.leaving)
            assertEquals(3 * 60, session.remainingSeconds)

            cancelAndIgnoreRemainingItems()
        }
    }

    @Test
    fun `reminder StartReset action is ignored while a session is already running`() = runTest {
        val repo = FakeHomeRepository(HomePreferences(durationMin = 5, remindersEnabled = false))
        val store = FakeReminderActionStore()

        viewModel(repo, reminderActionStore = store).test(this) {
            expectInitialState()
            // runOnCreate so the reminder action collector is live — otherwise the
            // dispatch below is never observed and the test passes vacuously.
            runOnCreate()
            awaitUntil { it.status is HomeStatus.Content }

            containerHost.handleHomeIntent(HomeIntent.TapReset)
            awaitUntil { it.leaving }
            assertEquals(HomeSideEffect.PlayChime(ChimeKind.Start), awaitSideEffect())
            containerHost.handleHomeIntent(HomeIntent.LeaveAnimationFinished)
            val session = awaitUntil { it.screen == HomeStep.Session }

            store.dispatch(ReminderAction.StartReset)

            // No restart: the countdown keeps its remaining time and no new chime plays.
            assertEquals(5 * 60, session.remainingSeconds)
            assertEquals(HomeStep.Session, containerHost.stateFlow().value.screen)

            cancelAndIgnoreRemainingItems()
        }
    }

    private companion object {
        const val SEED = 1234
    }
}
