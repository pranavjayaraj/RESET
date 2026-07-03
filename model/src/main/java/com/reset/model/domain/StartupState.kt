package com.reset.model.domain

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * App-launch readiness signal: the host keeps the system splash on screen until the first
 * feature screen has real content to show, so the user never sees an empty backdrop.
 *
 * The *meaning* of ready is owned by the feature that renders first (Home marks it when its
 * load settles — Content or Error alike, so a failed load releases the splash to the error
 * screen instead of pinning it forever); the host only observes. Latching (never reset) on
 * purpose: readiness is a once-per-process fact, not a per-screen one.
 */
@Singleton
class StartupState @Inject constructor() {

    private val _contentReady = MutableStateFlow(false)
    val contentReady: StateFlow<Boolean> = _contentReady.asStateFlow()

    fun markContentReady() {
        _contentReady.value = true
    }
}
