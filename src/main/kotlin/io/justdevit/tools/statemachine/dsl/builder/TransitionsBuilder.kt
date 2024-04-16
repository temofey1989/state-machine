package io.justdevit.tools.statemachine.dsl.builder

import io.justdevit.tools.statemachine.EventBasedTransition
import io.justdevit.tools.statemachine.EventKeyBasedTransition
import io.justdevit.tools.statemachine.EventTypeBasedTransition
import io.justdevit.tools.statemachine.Transition
import io.justdevit.tools.statemachine.dsl.StateMachineDslMarker

/**
 * Transitions builder.
 *
 * @see StateMachineConfigurationBuilder.from()
 * @param sourceState Source state of the transition.
 */
@StateMachineDslMarker
data class TransitionsBuilder<S : Any, E : Any>(val sourceState: S) {
    private val transitions: MutableList<Transition<S, E>> = mutableListOf()

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
     * Register transition for event type.
     *
     * @param eventType Type of event.
     * @param configure Configurer for transition.
     */
    fun <T : E> Pair<S, S>.withType(eventType: Class<T>, configure: (TransitionConfigurationBuilder<S, E>.() -> Unit)? = null): Pair<S, S> {
        transitions +=
            EventTypeBasedTransition(
                sourceState = first,
                targetState = second,
                eventType = eventType,
                config = TransitionConfigurationBuilder<S, E>()
                    .also {
                        configure?.let { invoke -> it.invoke() }
                    }.build(),
            )
        return this
    }

    /**
     * Register transition for event type.
     *
     * @param configure Configurer for transition.
     */
    inline fun <reified T : E> Pair<S, S>.with(noinline configure: (TransitionConfigurationBuilder<S, E>.() -> Unit)? = null): Pair<S, S> = withType(T::class.java, configure)

    /**
     * Register transition for event key.
     *
     * @param eventKey Event key of the transition.
     * @param configure Configurer for transition.
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

    fun add(transition: Transition<S, E>) {
        transitions += transition
    }

    /**
     * Builds the transition list.
     */
    fun build(): List<Transition<S, E>> = transitions
}
