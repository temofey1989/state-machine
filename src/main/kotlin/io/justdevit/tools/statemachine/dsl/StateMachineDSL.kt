package io.justdevit.tools.statemachine.dsl

import io.justdevit.tools.statemachine.DefaultStateMachine
import io.justdevit.tools.statemachine.StateMachine
import io.justdevit.tools.statemachine.StateMachineStartup
import io.justdevit.tools.statemachine.StateMachineStartup.AUTO
import io.justdevit.tools.statemachine.dsl.builder.StateMachineConfigurationBuilder

/**
 * Builds the State Machine.
 * In case of AUTO startup type, the State Machine will be automatically started.
 *
 * @param state Startup state (Only if startup type is AUTO).
 * @param startup Startup type of the State Machine.
 * @param configure Configure function of the State Machine.
 */
fun <S, E> stateMachine(
    state: S? = null,
    startup: StateMachineStartup = AUTO,
    configure: StateMachineConfigurationBuilder<S, E>.() -> Unit,
): StateMachine<S, E> {
    val configBuilder = StateMachineConfigurationBuilder<S, E>()
    configBuilder.configure()
    return DefaultStateMachine(
        config = configBuilder.build(),
    ).also {
        if (AUTO == startup) {
            it.start(state)
        }
    }
}
