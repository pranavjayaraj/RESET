package com.reset.feature.home

import org.orbitmvi.orbit.test.OrbitTestContext

/**
 * Awaits successive states until [predicate] holds, returning that state. Lets
 * tests ignore intermediate emissions (e.g. the transient Loading state) without
 * depending on exactly how many reductions occurred.
 */
suspend fun OrbitTestContext<HomeState, *, *>.awaitUntil(
    predicate: (HomeState) -> Boolean,
): HomeState {
    var state = awaitState()
    while (!predicate(state)) {
        state = awaitState()
    }
    return state
}
