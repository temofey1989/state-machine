package io.justdevit.tools.statemachine.action

import io.justdevit.tools.statemachine.TransitionContext
import io.justdevit.tools.statemachine.dsl.StateMachineDslMarker

/**
 * Represents the action(s) on transition.
 */
@StateMachineDslMarker
interface TransitionAction<S : Any, E : Any> {
    /**
     * Step before entry to the state.
     * Executes before guards checks.
     *
     * @param context Context of the transition.
     */
    suspend fun beforeEntry(context: TransitionContext<S, E>) = Unit

    /**
     * Step after entry to the state.
     * Executes after guards checks.
     *
     * @param context Context of the transition.
     */
    suspend fun afterEntry(context: TransitionContext<S, E>) = Unit

    /**
     * Step before exit from the state.
     * Executes before guard checks.
     *
     * @param context Context of the transition.
     */
    suspend fun beforeExit(context: TransitionContext<S, E>) = Unit

    /**
     * Step after exit from the state.
     * Executes before guard checks.
     *
     * @param context Context of the transition.
     */
    suspend fun afterExit(context: TransitionContext<S, E>) = Unit
}
