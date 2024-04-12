package io.justdevit.tools.statemachine

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.util.UUID
import kotlin.coroutines.CoroutineContext

/**
 * Represents the State Machine.
 */
interface StateMachine<S, E> {
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
    suspend fun sendEvent(event: E, parameters: TransitionParameters = TransitionParameters()): TransitionResult<S, E>

    /**
     * Sends the event to the State Machine.
     *
     * @param event Event for the State Machine. Should be of generic type.
     * @param parameters Metadata parameter map.
     * @throws IllegalStateException In case of the State Machine is not started.
     */
    suspend fun sendEvent(event: E, parameters: Map<String, Any>): TransitionResult<S, E> = sendEvent(event, TransitionParameters(parameters))

    /**
     * Sends the event to the State Machine with custom transition parameters.
     *
     * @param event Event for the State Machine. Should be of generic type.
     * @param parametersBuilder A lambda function that builds the parameters using a [TransitionParameters.Builder].
     * @return The result of the event processing as an [TransitionResult] object.
     * @throws IllegalStateException In case the State Machine is not started.
     */
    suspend fun sendEvent(event: E, parametersBuilder: TransitionParameters.Builder.() -> Unit): TransitionResult<S, E> {
        val parameters = TransitionParameters
            .Builder()
            .apply(parametersBuilder)
            .build()
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
    ): TransitionResult<S, E> = runBlocking(context) { sendEvent(event, parameters) }

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
    ): TransitionResult<S, E> = sendEventAndAwait(event, context, TransitionParameters(parameters))

    /**
     * Sends the event to the State Machine and waits for the result.
     *
     * @param event Event for the State Machine. Should be of generic type.
     * @param context The coroutine context to run the sendEvent method on. Defaults to Dispatchers.Default.
     * @param parametersBuilder A lambda function that builds the parameters using a [TransitionParameters.Builder].
     * @return The result of the event processing as an [TransitionResult] object.
     */
    fun sendEventAndAwait(
        event: E,
        context: CoroutineContext = Dispatchers.Default,
        parametersBuilder: TransitionParameters.Builder.() -> Unit,
    ): TransitionResult<S, E> {
        val parameters = TransitionParameters
            .Builder()
            .apply(parametersBuilder)
            .build()
        return sendEventAndAwait(event, context, parameters)
    }
}
