package io.justdevit.tools.statemachine

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.util.UUID
import kotlin.coroutines.CoroutineContext

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
     * @param parameters Parameters for the transition.
     * @throws IllegalStateException In case of the State Machine is not started.
     */
    suspend fun sendEvent(event: E, parameters: TransitionParameters = TransitionParameters()): EventResult

    /**
     * Sends the event to the State Machine.
     *
     * @param event Event for the State Machine. Should be of generic type.
     * @param parameters Metadata parameter map.
     * @throws IllegalStateException In case of the State Machine is not started.
     */
    suspend fun sendEvent(event: E, parameters: Map<String, Any>): EventResult = sendEvent(event, TransitionParameters(parameters))

    /**
     * Sends the event to the State Machine with custom transition parameters.
     *
     * @param event Event for the State Machine. Should be of generic type.
     * @param parametersBuilder A lambda function that builds the parameters map using.
     * @return The result of the event processing as an [EventResult] object.
     * @throws IllegalStateException In case of the State Machine is not started.
     */
    suspend fun sendEvent(event: E, parametersBuilder: MutableMap<String, Any>.() -> Unit): EventResult {
        val parameters = mutableMapOf<String, Any>().apply(parametersBuilder).toMap()
        return sendEvent(event, parameters)
    }

    /**
     * Sends the event to the State Machine.
     *
     * @param event Event for the State Machine. Should be of generic type.
     * @param parameters Parameters for the transition.
     * @throws IllegalStateException In case of the State Machine is not started.
     */
    fun sendEventAndAwait(
        event: E,
        context: CoroutineContext = Dispatchers.Default,
        parameters: TransitionParameters = TransitionParameters(),
    ): EventResult = runBlocking(context) { sendEvent(event, parameters) }

    /**
     * Sends the event to the State Machine.
     *
     * @param event Event for the State Machine. Should be of generic type.
     * @param parameters Metadata parameter map.
     * @throws IllegalStateException In case of the State Machine is not started.
     */
    fun sendEventAndAwait(
        event: E,
        context: CoroutineContext = Dispatchers.Default,
        parameters: Map<String, Any>,
    ): EventResult = sendEventAndAwait(event, context, TransitionParameters(parameters))

    /**
     * Sends the event to the State Machine and waits for the result.
     *
     * @param event Event for the State Machine. Should be of generic type.
     * @param context The coroutine context to run the sendEvent method on. Defaults to Dispatchers.Default.
     * @param parametersBuilder A lambda function that builds the parameters map using.
     * @return The result of the event processing as an [EventResult] object.
     */
    fun sendEventAndAwait(
        event: E,
        context: CoroutineContext = Dispatchers.Default,
        parametersBuilder: MutableMap<String, Any>.() -> Unit,
    ): EventResult {
        val parameters = mutableMapOf<String, Any>().apply(parametersBuilder).toMap()
        return sendEventAndAwait(event, context, parameters)
    }
}
