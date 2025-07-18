package io.justdevit.tools.statemachine

/**
 * State Machine context.
 */
data class TransitionContext<S : Any, E : Any>(
    /**
     * Source state.
     */
    val sourceState: S,
    /**
     * Target state.
     */
    val targetState: S,
    /**
     * Event which triggers the transition.
     */
    val event: E,
    /**
     * Reference to an instance of the State Machine.
     */
    val stateMachine: StateMachine<S, E>,
    /**
     * Parameters of the execution.
     */
    val parameters: TransitionParameters = TransitionParameters(),
)
