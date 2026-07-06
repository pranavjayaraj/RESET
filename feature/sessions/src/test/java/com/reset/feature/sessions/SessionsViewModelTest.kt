package com.reset.feature.sessions

import androidx.lifecycle.SavedStateHandle
import com.reset.feature.sessions.navigation.SessionsIntent
import com.reset.model.domain.model.SessionStats
import com.reset.navigation.NavEvent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.orbitmvi.orbit.test.test

class SessionsViewModelTest {

    private fun viewModel(
        repository: FakeHomeRepository = FakeHomeRepository(),
        navigator: FakeNavigator = FakeNavigator(),
    ) = SessionsViewModel(SavedStateHandle(), repository, navigator)

    @Test
    fun `loads sit history into state`() = runTest {
        val repo = FakeHomeRepository(SessionStats(sessions = 4, totalMin = 20, streak = 3))

        viewModel(repo).test(this) {
            expectInitialState()
            runOnCreate()

            val loaded = awaitUntil { it.status == SessionsStatus.Content }
            assertEquals(4, loaded.stats.sessions)
            assertEquals(20, loaded.stats.totalMin)
            assertEquals(3, loaded.stats.streak)

            cancelAndIgnoreRemainingItems()
        }
    }

    @Test
    fun `stats stream stays hot so later sits appear`() = runTest {
        val repo = FakeHomeRepository(SessionStats(sessions = 1, totalMin = 5, streak = 1))

        viewModel(repo).test(this) {
            expectInitialState()
            runOnCreate()
            awaitUntil { it.status == SessionsStatus.Content }

            repo.statsFlow.value = SessionStats(sessions = 2, totalMin = 10, streak = 1)

            val updated = awaitUntil { it.stats.sessions == 2 }
            assertEquals(10, updated.stats.totalMin)

            cancelAndIgnoreRemainingItems()
        }
    }

    @Test
    fun `a failed load surfaces the error state`() = runTest {
        val repo = FakeHomeRepository(statsError = IllegalStateException("datastore down"))

        viewModel(repo).test(this) {
            expectInitialState()
            runOnCreate()

            val failed = awaitUntil { it.status is SessionsStatus.Error }
            assertEquals("datastore down", (failed.status as SessionsStatus.Error).message)

            cancelAndIgnoreRemainingItems()
        }
    }

    @Test
    fun `back press pops via the navigator`() = runTest {
        val navigator = FakeNavigator()

        viewModel(navigator = navigator).test(this) {
            expectInitialState()
            containerHost.handleSessionsIntent(SessionsIntent.HandleBackPress)

            assertEquals(NavEvent.Pop, navigator.events.first())
            assertTrue(navigator.popped)

            cancelAndIgnoreRemainingItems()
        }
    }
}
