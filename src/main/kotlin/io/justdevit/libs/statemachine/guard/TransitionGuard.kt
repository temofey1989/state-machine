package io.justdevit.libs.statemachine.guard

import io.justdevit.libs.statemachine.TransitionContext

/**
 * Represents guard (check) on transition action.
 */
interface TransitionGuard<S, E> {
    /**
     * Checks if it is possible to entry to the target state.
     *
     * @param context Context of the transition.
     * @return true - if the transition is possible. Otherwise, returns false.
     */
    suspend fun onEntry(context: TransitionContext<S, E>): Boolean = true

    /**
     * Checks if it is possible to leave actual state.
     *
     * @param context Context of the transition.
     * @return true - if the transition is possible. Otherwise, returns false.
     */
    suspend fun onExit(context: TransitionContext<S, E>): Boolean = true
}
