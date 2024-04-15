package io.justdevit.tools.statemachine

import io.justdevit.tools.statemachine.action.TransitionAction
import io.justdevit.tools.statemachine.dsl.StateMachineDslMarker
import io.justdevit.tools.statemachine.guard.TransitionGuard
import java.util.UUID
import java.util.UUID.randomUUID

/**
 * State Machine configuration
 *
 * @see DefaultStateMachine
 */
@StateMachineDslMarker
data class StateMachineConfiguration<S : Any, E : Any>(
    /**
     * ID of the State Machine (default: random UUID).
     */
    val id: UUID = randomUUID(),
    /**
     * Initial State of the State Machine.
     */
    val initialState: S,
    /**
     * Final states of the State Machine.
     */
    val finalStates: Set<S>,
    /**
     * List of global guards (default: empty list).
     */
    val globalGuards: List<TransitionGuard<S, E>> = emptyList(),
    /**
     * List of global actions (default: empty list).
     */
    val globalActions: List<TransitionAction<S, E>> = emptyList(),
    /**
     * List of transitions.
     */
    val transitions: List<DefinedTransition<S, E>>,
)
