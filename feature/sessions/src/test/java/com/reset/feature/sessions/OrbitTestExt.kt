package com.reset.feature.sessions

import org.orbitmvi.orbit.test.OrbitTestContext

/**
 * Awaits successive states until [predicate] holds, returning that state — lets tests
 * ignore intermediate emissions without depending on how many reductions occurred.
 */
suspend fun OrbitTestContext<SessionsState, *, *>.awaitUntil(
    predicate: (SessionsState) -> Boolean,
): SessionsState {
    var state = awaitState()
    while (!predicate(state)) {
        state = awaitState()
    }
    return state
}
