package io.justdevit.tools.statemachine.dsl.builder

import io.justdevit.tools.statemachine.DefaultStateMachine
import io.justdevit.tools.statemachine.DefinedTransition
import io.justdevit.tools.statemachine.StateMachineConfiguration
import io.justdevit.tools.statemachine.action.TransitionAction
import io.justdevit.tools.statemachine.dsl.StateMachineDslMarker
import io.justdevit.tools.statemachine.guard.FinalStateGuard
import io.justdevit.tools.statemachine.guard.TransitionGuard
import java.util.UUID
import java.util.UUID.randomUUID

/**
 * State Machine configuration builder.
 *
 * @see io.justdevit.tools.statemachine.StateMachineConfiguration
 */
@StateMachineDslMarker
class StateMachineConfigurationBuilder<S : Any, E : Any> {
    /**
     * ID of the State Machine.
     */
    var id: UUID = randomUUID()

    /**
     * Initial state.
     */
    var initialState: S? = null

    /**
     * Final states of the State Machine.
     */
    var finalStates: Set<S> = emptySet()

    /**
     * Guards for each transition.
     */
    private val globalGuards = mutableListOf<TransitionGuard<S, E>>(
        FinalStateGuard(),
    )

    /**
     * Actions for each transition.
     */
    private val globalActions = mutableListOf<TransitionAction<S, E>>()

    /**
     * List of transitions.
     */
    private val transitions = mutableListOf<DefinedTransition<S, E>>()

    /**
     * Register list of transitions from state.
     *
     * @param sourceState Source state for transitions.
     * @param prepareTransitions Transitions builder for source state.
     */
    fun from(sourceState: S, prepareTransitions: TransitionsBuilder<S, E>.() -> Unit) {
        val builder = TransitionsBuilder<S, E>(sourceState)
        builder.prepareTransitions()
        transitions += builder.build()
    }

    /**
     * Register global actions for the State Machine.
     *
     * @param prepareActions Actions preparation function.
     */
    fun globalActions(prepareActions: TransitionActionBuilder<S, E>.() -> Unit) {
        val builder = TransitionActionBuilder<S, E>()
        builder.prepareActions()
        globalActions += builder.build()
    }

    /**
     * Register global guards for the State Machine.
     *
     * @param prepareGuards Guards preparation function.
     */
    fun globalGuards(prepareGuards: TransitionGuardBuilder<S, E>.() -> Unit) {
        val builder = TransitionGuardBuilder<S, E>()
        builder.prepareGuards()
        globalGuards += builder.build()
    }

    /**
     * Creates the State Machine based on configuration.
     *
     * @return Instance of the State Machine configuration.
     * @see StateMachineConfiguration
     * @see DefaultStateMachine
     */
    fun build() =
        StateMachineConfiguration(
            id = id,
            initialState = initialState ?: throw IllegalStateException("Initial state should be declared."),
            finalStates = finalStates.apply {
                if (finalStates.isEmpty()) {
                    throw IllegalStateException("Final state(s) should be defined.")
                }
            },
            transitions = transitions,
            globalGuards = globalGuards,
            globalActions = globalActions,
        )
}
