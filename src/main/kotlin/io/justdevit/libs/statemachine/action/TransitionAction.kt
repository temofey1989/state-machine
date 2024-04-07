package io.justdevit.libs.statemachine.action

import io.justdevit.libs.statemachine.TransitionContext
import io.justdevit.libs.statemachine.dsl.StateMachineDslMarker

/**
 * Represents the action(s) on transition.
 */
@StateMachineDslMarker
interface TransitionAction<S, E> {
    /**
     * Step before entry to the state.
     * Executes before guards checks.
     *
     * @param context Context of the transition.
     */
    suspend fun beforeEntry(context: TransitionContext<S, E>) {
        // noop
    }

    /**
     * Step after entry to the state.
     * Executes after guards checks.
     *
     * @param context Context of the transition.
     */
    suspend fun afterEntry(context: TransitionContext<S, E>) {
        // noop
    }

    /**
     * Step before exit from the state.
     * Executes before guard checks.
     *
     * @param context Context of the transition.
     */
    suspend fun beforeExit(context: TransitionContext<S, E>) {
        // noop
    }

    /**
     * Step after exit from the state.
     * Executes before guard checks.
     *
     * @param context Context of the transition.
     */
    suspend fun afterExit(context: TransitionContext<S, E>) {
        // noop
    }
}
