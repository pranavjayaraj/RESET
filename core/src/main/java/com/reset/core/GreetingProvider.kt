package com.reset.core

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Trivial provider injected across module boundaries to prove the Hilt + multi-module
 * wiring works. In a real app this would live behind a UseCase in `:domain:*`.
 */
@Singleton
class GreetingProvider @Inject constructor() {
    fun greeting(): String = "Hello, World — from RESET APP"
}
