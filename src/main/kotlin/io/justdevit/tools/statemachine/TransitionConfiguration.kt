package io.justdevit.tools.statemachine

import io.justdevit.tools.statemachine.action.TransitionAction
import io.justdevit.tools.statemachine.guard.TransitionGuard

/**
 * Represents the configuration for a transition in a state machine.
 *
 * @param S The type representing the states of the state machine.
 * @param E The type representing the events triggering state transitions.
 * @property actions The list of actions to be executed during the transition lifecycle. Defaults to an empty list.
 * @property guards The list of guards to be evaluated for the transition. Defaults to an empty list.
 */
data class TransitionConfiguration<S : Any, E : Any>(val actions: List<TransitionAction<S, E>> = emptyList(), val guards: List<TransitionGuard<S, E>> = emptyList())
