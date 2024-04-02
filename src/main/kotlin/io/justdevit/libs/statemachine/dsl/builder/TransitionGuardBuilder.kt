package io.justdevit.libs.statemachine.dsl.builder

import io.justdevit.libs.statemachine.guard.TransitionGuard

/**
 * Transition guard list builder.
 *
 * @see StateMachineConfigurationBuilder.globalGuards()
 */
class TransitionGuardBuilder<S, E> {
    private val guards = mutableListOf<TransitionGuard<S, E>>()

    /**
     * Register new guard.
     */
    fun guard(guard: TransitionGuard<S, E>) {
        guards += guard
    }

    /**
     * Register new action.
     *
     * Syntax: +MyGuard()
     */
    operator fun TransitionGuard<S, E>.unaryPlus() {
        guard(this)
    }

    /**
     * Creates list of transition guards.
     *
     * @return List of transition guards.
     */
    fun build(): List<TransitionGuard<S, E>> = guards
}
