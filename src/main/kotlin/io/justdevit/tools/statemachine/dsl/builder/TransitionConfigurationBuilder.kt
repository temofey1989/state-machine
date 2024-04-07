package io.justdevit.tools.statemachine.dsl.builder

import io.justdevit.tools.statemachine.TransitionConfiguration
import io.justdevit.tools.statemachine.action.TransitionAction
import io.justdevit.tools.statemachine.guard.TransitionGuard

/**
 * Transition configuration builder.
 */
class TransitionConfigurationBuilder<S, E> {
    private val guards: MutableList<TransitionGuard<S, E>> = mutableListOf()
    private val actions: MutableList<TransitionAction<S, E>> = mutableListOf()

    fun add(guard: TransitionGuard<S, E>) {
        guards += guard
    }

    operator fun TransitionGuard<S, E>.unaryPlus() {
        add(this)
    }

    fun add(action: TransitionAction<S, E>) {
        actions += action
    }

    operator fun TransitionAction<S, E>.unaryPlus() {
        add(this)
    }

    fun build() =
        TransitionConfiguration(
            guards = guards,
            actions = actions,
        )
}
