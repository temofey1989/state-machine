package io.justdevit.tools.statemachine.dsl.builder

import io.justdevit.tools.statemachine.DefinedTransition
import io.justdevit.tools.statemachine.dsl.StateMachineDslMarker

/**
 * Transitions builder.
 *
 * @see StateMachineConfigurationBuilder.from()
 * @param sourceState Source state of the transition.
 */
@StateMachineDslMarker
data class TransitionsBuilder<S, E>(val sourceState: S) {
    private val transitions: MutableList<DefinedTransition<S, E>> = mutableListOf()

    /**
     * Creates source to target state pair.
     *
     * @return Source to target state pair.
     */
    fun to(targetState: S) = Pair(sourceState, targetState)

    /**
     * Register transition for event.
     *
     * @param event Event of the transition.
     * @param configure Configurer for transition.
     */
    fun Pair<S, S>.with(event: E, configure: (TransitionConfigurationBuilder<S, E>.() -> Unit)? = null) {
        transitions +=
            DefinedTransition(
                sourceState = first,
                targetState = second,
                event = event,
                config = TransitionConfigurationBuilder<S, E>()
                    .also {
                        configure?.let { invoke -> it.invoke() }
                    }.build(),
            )
    }

    /**
     * Builds the transition list.
     */
    fun build(): List<DefinedTransition<S, E>> = transitions
}
