package io.justdevit.tools.statemachine.guard

import io.justdevit.tools.statemachine.TransitionContext

/**
 * Guard checks if the State Machine is in the final state.
 * If so, the guard rejects the transition.
 */
class FinalStateGuard<S : Any, E : Any> : TransitionGuard<S, E> {
    override suspend fun onExit(context: TransitionContext<S, E>) = !context.stateMachine.finished
}
