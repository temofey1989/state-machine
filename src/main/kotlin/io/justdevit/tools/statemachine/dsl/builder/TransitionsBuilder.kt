package io.justdevit.tools.statemachine.dsl.builder

import io.justdevit.tools.statemachine.EventBasedTransition
import io.justdevit.tools.statemachine.EventKeyBasedTransition
import io.justdevit.tools.statemachine.EventTypeBasedTransition
import io.justdevit.tools.statemachine.Transition
import io.justdevit.tools.statemachine.dsl.StateMachineDslMarker

/**
 * Builder class for defining transitions between states in a state machine using a DSL syntax.
 *
 * @param S The type representing the state in the state machine.
 * @param E The type representing the event triggering transitions in the state machine.
 * @property sourceState The source state for which transitions are being defined.
 */
@StateMachineDslMarker
data class TransitionsBuilder<S : Any, E : Any>(val sourceState: S) {
    private val transitions: MutableList<Transition<S, E>> = mutableListOf()

    /**
     * Specifies the target state for a transition.
     *
     * @param targetState The state to which the transition should occur.
     * @return A pair of the source state and the target state.
     */
    fun to(targetState: S) = Pair(sourceState, targetState)

    /**
     * Registers a transition between the source and target states in the pair, triggered by a specific event.
     *
     * @param event The event triggering the transition between the source and target states.
     * @param configure An optional configuration block to define guards, actions, or other properties of the transition.
     * @return The pair representing the source and target states of the transition.
     */
    fun Pair<S, S>.with(event: E, configure: (TransitionConfigurationBuilder<S, E>.() -> Unit)? = null): Pair<S, S> {
        transitions +=
            EventBasedTransition(
                sourceState = first,
                targetState = second,
                event = event,
                config = TransitionConfigurationBuilder<S, E>()
                    .also {
                        configure?.let { invoke -> it.invoke() }
                    }.build(),
            )
        return this
    }

    /**
     * Registers a transition between the source and target states in the pair, triggered by a specific event type.
     *
     * @param eventType The type of the event that triggers the transition.
     * @param configure An optional configuration block for defining guards, actions, or other properties of the transition.
     * @return The pair representing the source and target states of the transition.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : E> Pair<S, S>.withType(eventType: Class<T>, configure: (TransitionConfigurationBuilder<S, T>.() -> Unit)? = null): Pair<S, S> {
        transitions +=
            EventTypeBasedTransition(
                sourceState = first,
                targetState = second,
                eventType = eventType,
                config = TransitionConfigurationBuilder<S, T>()
                    .also {
                        configure?.let { invoke -> it.invoke() }
                    }.build(),
            ) as Transition<S, E>
        return this
    }

    /**
     * Registers a transition between the source and target states in the pair, triggered by a reified event type.
     *
     * @param configure An optional configuration block to define guards, actions, or other properties of the transition.
     * @return The pair representing the source and target states of the transition.
     */
    inline fun <reified T : E> Pair<S, S>.with(noinline configure: (TransitionConfigurationBuilder<S, T>.() -> Unit)? = null): Pair<S, S> = withType(T::class.java, configure)

    /**
     * Registers a transition between the source and target states in the pair, triggered by a specific event key.
     *
     * @param eventKey The key representing the event that triggers the transition.
     * @param configure An optional configuration block to define guards, actions, or other properties of the transition.
     * @return The pair representing the source and target states of the transition.
     */
    fun Pair<S, S>.withKey(eventKey: Any, configure: (TransitionConfigurationBuilder<S, E>.() -> Unit)? = null): Pair<S, S> {
        transitions +=
            EventKeyBasedTransition(
                sourceState = first,
                targetState = second,
                eventKey = eventKey,
                config = TransitionConfigurationBuilder<S, E>()
                    .also {
                        configure?.let { invoke -> it.invoke() }
                    }.build(),
            )
        return this
    }

    /**
     * Adds a transition to the list of transitions in the state machine.
     *
     * @param transition The transition to be added, representing a state change triggered by an event.
     */
    fun add(transition: Transition<S, E>) {
        transitions += transition
    }

    /**
     * Constructs and retrieves the list of transitions defined within the builder.
     *
     * @return List of transitions, each representing a state change triggered by a specific event.
     */
    fun build(): List<Transition<S, E>> = transitions
}
