package com.reset.navigation

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * App-scoped [Navigator]. Events go through a buffered [Channel] so a feature can emit
 * before the host starts collecting (e.g. during a fast launch) without losing the event.
 */
@Singleton
class NavigatorImpl @Inject constructor() : Navigator {

    private val _events = Channel<NavEvent>(Channel.BUFFERED)
    override val events: Flow<NavEvent> = _events.receiveAsFlow()

    override fun navigate(screen: Screen) {
        _events.trySend(NavEvent.Navigate(screen))
    }

    override fun switchTab(screen: Screen) {
        _events.trySend(NavEvent.SwitchTab(screen))
    }

    override fun pop() {
        _events.trySend(NavEvent.Pop)
    }

    override fun popTo(screen: Screen) {
        _events.trySend(NavEvent.PopTo(screen))
    }

    override fun exit() {
        _events.trySend(NavEvent.Exit)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class NavigatorModule {
    @Binds
    abstract fun bindNavigator(impl: NavigatorImpl): Navigator
}
