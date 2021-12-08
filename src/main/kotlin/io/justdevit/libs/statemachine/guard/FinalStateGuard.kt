package io.justdevit.libs.statemachine.guard

import io.justdevit.libs.statemachine.TransitionContext
import io.justdevit.libs.statemachine.dsl.builder.TransitionGuardBuilder

/**
 * Guard checks if the State Machine is in the final state.
 * If so, the guard rejects the transition.
 */
class FinalStateGuard<S, E> : TransitionGuard<S, E> {
    override fun onExit(context: TransitionContext<S, E>) =
        !context.stateMachine.finished
}

/**
 * Builder function for FinalStateGuard.
 *
 * @see FinalStateGuard
 */
fun <S, E> TransitionGuardBuilder<S, E>.finalStateGuard() {
    this.guard(FinalStateGuard())
}
