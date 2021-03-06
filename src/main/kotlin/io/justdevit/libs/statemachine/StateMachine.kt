package io.justdevit.libs.statemachine

import java.util.UUID

/**
 * Represents the State Machine.
 */
interface StateMachine<S, in E> {

    /**
     * ID of the state machine.
     */
    val id: UUID

    /**
     * Actual state of the State Machine.
     */
    val actualState: S

    /**
     * Defines if the State Machine is in the final state.
     */
    val finished: Boolean

    /**
     * Starts the State Machine.
     *
     * @param state State to be on start.
     */
    fun start(state: S? = null)

    /**
     * Resets state of the State Machine to the initial state or defined state if state .
     *
     * @param state State to be on reset.
     * @throws IllegalStateException In case of the State Machine is not started.
     */
    fun reset(state: S? = null)

    /**
     * Sends the event to the State Machine.
     *
     * @param event Event for the State Machine. Should be of generic type.
     * @param parameters Metadata parameter map.
     * @throws IllegalStateException In case of the State Machine is not started.
     */
    fun sendEvent(event: E, parameters: Map<String, Any> = emptyMap()): EventResult
}
