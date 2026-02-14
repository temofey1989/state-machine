package io.justdevit.tools.statemachine.dsl

import io.justdevit.tools.statemachine.DefaultStateMachine
import io.justdevit.tools.statemachine.StateMachine
import io.justdevit.tools.statemachine.dsl.builder.StateMachineConfigurationBuilder
import kotlin.DeprecationLevel.ERROR

/**
 * Creates and configures a state machine.
 *
 * @param S The type representing the states of the state machine.
 * @param E The type representing the events that trigger state transitions.
 * @param state The initial state of the state machine. Defaults to `null`.
 * @param autoStartup Whether the state machine should automatically start after creation. Defaults to `true`.
 * @param configure A lambda to configure the state machine using the [StateMachineConfigurationBuilder].
 * @return The configured [StateMachine] instance.
 */
fun <S : Any, E : Any> stateMachine(
    state: S? = null,
    autoStartup: Boolean = true,
    configure: StateMachineConfigurationBuilder<S, E>.() -> Unit,
): StateMachine<S, E> {
    val configBuilder = StateMachineConfigurationBuilder<S, E>()
    configBuilder.configure()
    return DefaultStateMachine(
        config = configBuilder.build(),
    ).also {
        if (autoStartup) {
            it.start(state)
        }
    }
}

@Suppress("UnusedReceiverParameter", "unused")
@Deprecated("Nested state machine is not allowed.", level = ERROR)
fun StateMachineConfigurationBuilder<*, *>.stateMachine(
    state: Any? = null,
    autoStartup: Boolean = true,
    configure: StateMachineConfigurationBuilder<*, *>.() -> Unit,
): Nothing = error("Nested state machine is not allowed.")
