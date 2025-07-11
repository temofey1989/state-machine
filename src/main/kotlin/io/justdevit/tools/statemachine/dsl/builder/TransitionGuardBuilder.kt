package io.justdevit.tools.statemachine.dsl.builder

import io.justdevit.tools.statemachine.TransitionContext
import io.justdevit.tools.statemachine.guard.TransitionGuard

/**
 * Builder class for constructing a list of transition guards in a state machine.
 *
 * @param S The type representing states in the state machine.
 * @param E The type representing events in the state machine.
 */
class TransitionGuardBuilder<S : Any, E : Any> {
    private val guards = mutableListOf<TransitionGuard<S, E>>()

    /**
     * Adds the specified transition guard to the list of guards.
     *
     * @param guard The transition guard to be added.
     */
    fun guard(guard: TransitionGuard<S, E>) {
        guards += guard
    }

    /**
     * Operator function to add a transition guard to the list of guards.
     */
    operator fun TransitionGuard<S, E>.unaryPlus() {
        guard(this)
    }

    /**
     * Registers a guard action to be evaluated during the entry phase of a state transition.
     *
     * @param action A suspending lambda that defines the guard logic to determine whether the entry transition is allowed.
     */
    fun onEntry(action: suspend TransitionContext<S, E>.() -> Boolean) {
        guards += object : TransitionGuard<S, E> {
            override suspend fun onEntry(context: TransitionContext<S, E>) = context.action()
        }
    }

    /**
     * Registers a guard action to be evaluated during the exit phase of a state transition.
     *
     * @param action A suspending lambda that represents the logic to determine whether the exit transition is allowed.
     */
    fun onExit(action: suspend TransitionContext<S, E>.() -> Boolean) {
        guards += object : TransitionGuard<S, E> {
            override suspend fun onExit(context: TransitionContext<S, E>) = context.action()
        }
    }

    /**
     * Builds and retrieves the list of transition guards configured within the builder.
     *
     * @return List of transition guards.
     */
    fun build(): List<TransitionGuard<S, E>> = guards
}
