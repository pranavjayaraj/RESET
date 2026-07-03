package com.reset.feature.settings

import androidx.lifecycle.SavedStateHandle
import com.reset.model.domain.model.HomePreferences
import com.reset.navigation.NavEvent
import com.reset.feature.settings.navigation.SettingsIntent
import com.reset.feature.settings.navigation.SettingsSideEffect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import org.orbitmvi.orbit.test.test

class SettingsViewModelTest {

    private fun viewModel(
        repository: FakeHomeRepository = FakeHomeRepository(),
        navigator: FakeNavigator = FakeNavigator(),
        scheduler: FakeReminderScheduler = FakeReminderScheduler(),
    ) = SettingsViewModel(SavedStateHandle(), repository, navigator, scheduler)

    @Test
    fun `loads persisted reminder preferences into state`() = runTest {
        val repo = FakeHomeRepository(
            HomePreferences(
                remindersEnabled = false,
                remindersEveryMin = 30,
                remindersStartHour = 8,
                remindersEndHour = 20,
            ),
        )

        viewModel(repo).test(this) {
            expectInitialState()
            runOnCreate()

            val loaded = awaitUntil { it.loaded }
            assertFalse(loaded.remindersEnabled)
            assertEquals(30, loaded.everyMin)
            assertEquals(8, loaded.startHour)
            assertEquals(20, loaded.endHour)

            cancelAndIgnoreRemainingItems()
        }
    }

    @Test
    fun `toggling reminders flips state and persists`() = runTest {
        val repo = FakeHomeRepository(HomePreferences(remindersEnabled = true))

        viewModel(repo).test(this) {
            expectInitialState()
            containerHost.handleSettingsIntent(SettingsIntent.ToggleReminders)

            val updated = awaitUntil { !it.remindersEnabled }
            assertFalse(updated.remindersEnabled)
            assertEquals(false, repo.lastRemindersEnabled)

            cancelAndIgnoreRemainingItems()
        }
    }

    @Test
    fun `selecting a cadence updates state and persists`() = runTest {
        val repo = FakeHomeRepository()

        viewModel(repo).test(this) {
            expectInitialState()
            containerHost.handleSettingsIntent(SettingsIntent.SelectEveryMin(120))

            val updated = awaitUntil { it.everyMin == 120 }
            assertEquals(120, updated.everyMin)
            assertEquals(120, repo.lastEveryMin)

            cancelAndIgnoreRemainingItems()
        }
    }

    @Test
    fun `start hour is clamped just below the end hour`() = runTest {
        // Defaults: start 9, end 18. A large nudge cannot push start past end - 1.
        val repo = FakeHomeRepository()

        viewModel(repo).test(this) {
            expectInitialState()
            containerHost.handleSettingsIntent(SettingsIntent.AdjustStartHour(50))

            val updated = awaitUntil { it.startHour != HomePreferences.DEFAULT_REMINDER_START_HOUR }
            assertEquals(HomePreferences.DEFAULT_REMINDER_END_HOUR - 1, updated.startHour)
            assertEquals(HomePreferences.DEFAULT_REMINDER_END_HOUR - 1, repo.lastStartHour)

            cancelAndIgnoreRemainingItems()
        }
    }

    @Test
    fun `end hour is clamped just above the start hour`() = runTest {
        val repo = FakeHomeRepository()

        viewModel(repo).test(this) {
            expectInitialState()
            containerHost.handleSettingsIntent(SettingsIntent.AdjustEndHour(-50))

            val updated = awaitUntil { it.endHour != HomePreferences.DEFAULT_REMINDER_END_HOUR }
            assertEquals(HomePreferences.DEFAULT_REMINDER_START_HOUR + 1, updated.endHour)
            assertEquals(HomePreferences.DEFAULT_REMINDER_START_HOUR + 1, repo.lastEndHour)

            cancelAndIgnoreRemainingItems()
        }
    }

    @Test
    fun `enabling reminders re-arms the schedule and requests notification permission`() = runTest {
        val repo = FakeHomeRepository(HomePreferences(remindersEnabled = false))
        val scheduler = FakeReminderScheduler()

        viewModel(repo, scheduler = scheduler).test(this) {
            expectInitialState()
            runOnCreate()
            awaitUntil { it.loaded && !it.remindersEnabled }

            containerHost.handleSettingsIntent(SettingsIntent.ToggleReminders)

            awaitUntil { it.remindersEnabled }
            assertEquals(SettingsSideEffect.RequestNotificationPermission, awaitSideEffect())
            assertEquals(1, scheduler.preferencesChangedCount)

            cancelAndIgnoreRemainingItems()
        }
    }

    @Test
    fun `changing the cadence re-arms the schedule`() = runTest {
        val scheduler = FakeReminderScheduler()

        viewModel(scheduler = scheduler).test(this) {
            expectInitialState()
            containerHost.handleSettingsIntent(SettingsIntent.SelectEveryMin(30))

            awaitUntil { it.everyMin == 30 }
            assertEquals(1, scheduler.preferencesChangedCount)

            cancelAndIgnoreRemainingItems()
        }
    }

    @Test
    fun `only a granted notification permission ensures a schedule exists`() = runTest {
        val scheduler = FakeReminderScheduler()

        viewModel(scheduler = scheduler).test(this) {
            expectInitialState()
            containerHost.handleSettingsIntent(SettingsIntent.UpdateNotificationPermission(granted = false))
            containerHost.handleSettingsIntent(SettingsIntent.UpdateNotificationPermission(granted = true))
            // Intents run in order — awaiting this trailing state change proves both
            // permission intents (which emit nothing) have completed.
            containerHost.handleSettingsIntent(SettingsIntent.SelectEveryMin(30))

            awaitUntil { it.everyMin == 30 }
            assertEquals(1, scheduler.ensureScheduledCount)

            cancelAndIgnoreRemainingItems()
        }
    }

    @Test
    fun `back press pops via the navigator`() = runTest {
        val navigator = FakeNavigator()

        viewModel(navigator = navigator).test(this) {
            expectInitialState()
            containerHost.handleSettingsIntent(SettingsIntent.HandleBackPress)

            assertEquals(NavEvent.Pop, navigator.events.first())

            cancelAndIgnoreRemainingItems()
        }
    }
}
