package com.reset.feature.home

import androidx.lifecycle.ViewModel
import com.reset.core.GreetingProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val greetingProvider: GreetingProvider,
) : ViewModel(), ContainerHost<HomeState, HomeSideEffect> {

    override val container = container<HomeState, HomeSideEffect>(HomeState())

    fun onEvent(event: HomeEvent) = intent {
        when (event) {
            HomeEvent.Load -> reduce {
                state.copy(message = greetingProvider.greeting(), isLoading = false)
            }
        }
    }
}
