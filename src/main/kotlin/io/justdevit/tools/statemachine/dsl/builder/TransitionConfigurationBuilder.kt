package io.justdevit.tools.statemachine.dsl.builder

import io.justdevit.tools.statemachine.TransitionConfiguration
import io.justdevit.tools.statemachine.TransitionContext
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

    fun onEntry(action: suspend (TransitionContext<S, E>) -> Boolean) {
        guards += object : TransitionGuard<S, E> {
            override suspend fun onEntry(context: TransitionContext<S, E>) = action(context)
        }
    }

    fun onExit(action: suspend (TransitionContext<S, E>) -> Boolean) {
        guards += object : TransitionGuard<S, E> {
            override suspend fun onExit(context: TransitionContext<S, E>) = action(context)
        }
    }

    fun beforeEntry(action: suspend (TransitionContext<S, E>) -> Unit) {
        actions += object : TransitionAction<S, E> {
            override suspend fun beforeEntry(context: TransitionContext<S, E>) = action(context)
        }
    }

    fun afterEntry(action: suspend (TransitionContext<S, E>) -> Unit) {
        actions += object : TransitionAction<S, E> {
            override suspend fun afterEntry(context: TransitionContext<S, E>) = action(context)
        }
    }

    fun beforeExit(action: suspend (TransitionContext<S, E>) -> Unit) {
        actions += object : TransitionAction<S, E> {
            override suspend fun beforeExit(context: TransitionContext<S, E>) = action(context)
        }
    }

    fun afterExit(action: suspend (TransitionContext<S, E>) -> Unit) {
        actions += object : TransitionAction<S, E> {
            override suspend fun afterExit(context: TransitionContext<S, E>) = action(context)
        }
    }

    fun build() =
        TransitionConfiguration(
            guards = guards,
            actions = actions,
        )
}
