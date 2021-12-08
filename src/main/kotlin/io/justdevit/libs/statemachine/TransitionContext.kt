package io.justdevit.libs.statemachine

/**
 * State Machine context.
 */
data class TransitionContext<S, E>(

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
     * Reference to instance of the State Machine.
     */
    val stateMachine: StateMachine<S, E>,

    /**
     * Parameters of the execution.
     */
    val parameters: Map<String, Any> = emptyMap()
)
