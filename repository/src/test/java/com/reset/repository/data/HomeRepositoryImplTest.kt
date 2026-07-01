package com.reset.repository.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.reset.repository.data.HomeRepositoryImpl
import com.reset.model.domain.model.HomePreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class HomeRepositoryImplTest {

    @get:Rule
    val tmp = TemporaryFolder()

    private fun TestScope.repository(): HomeRepositoryImpl {
        val store: DataStore<Preferences> = PreferenceDataStoreFactory.create(scope = backgroundScope) {
            File(tmp.root, "afk_${System.nanoTime()}.preferences_pb")
        }
        return HomeRepositoryImpl(store)
    }

    @Test
    fun `exposes defaults when nothing persisted`() = runTest {
        val repo = repository()

        val prefs = repo.preferences.first()
        assertEquals(HomePreferences.DEFAULT_DURATION_MIN, prefs.durationMin)
        assertTrue(prefs.remindersEnabled)

        val stats = repo.stats.first()
        assertEquals(0, stats.sessions)
        assertTrue(stats.isFirstSit)
    }

    @Test
    fun `persists duration and reminder preference`() = runTest {
        val repo = repository()

        repo.setDuration(3)
        repo.setRemindersEnabled(false)

        val prefs = repo.preferences.first()
        assertEquals(3, prefs.durationMin)
        assertFalse(prefs.remindersEnabled)
    }
}
