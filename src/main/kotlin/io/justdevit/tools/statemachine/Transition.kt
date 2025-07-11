package io.justdevit.tools.statemachine

/**
 * Represents a state transition in a state machine.
 *
 * @param S The type representing the states of the state machine.
 * @param E The type representing the events that trigger state transitions.
 */
sealed interface Transition<S : Any, E : Any> {
    val sourceState: S
    val targetState: S
    val config: TransitionConfiguration<S, E>
}

/**
 * Represents a transition in a state machine that is triggered by a specific event.
 *
 * @param S The type representing the states of the state machine.
 * @param E The type representing the events that trigger state transitions.
 * @property sourceState The state from which the transition originates.
 * @property targetState The state to which the transition leads.
 * @property config The configuration of the transition, which may include actions and guards.
 * @property event The event that triggers this transition.
 */
data class EventBasedTransition<S : Any, E : Any>(
    override val sourceState: S,
    override val targetState: S,
    override val config: TransitionConfiguration<S, E> = TransitionConfiguration(),
    val event: E,
) : Transition<S, E>

/**
 * Represents a state transition that is triggered by a specific event key.
 *
 * @param S The type representing the states of the state machine.
 * @param E The type representing the events triggering a state transition.
 * @property sourceState The state from which the transition originates.
 * @property targetState The state to which the transition leads.
 * @property config Configuration for the transition, including actions and guards.
 * @property eventKey The key identifying the event that triggers this transition.
 */
data class EventKeyBasedTransition<S : Any, E : Any>(
    override val sourceState: S,
    override val targetState: S,
    override val config: TransitionConfiguration<S, E> = TransitionConfiguration(),
    val eventKey: Any,
) : Transition<S, E>

/**
 * Represents a state transition based on a specific event type in a state machine.
 *
 * @param S The type representing the states of the state machine.
 * @param E The type representing the events that trigger state transitions.
 * @property sourceState The state from which the transition originates.
 * @property targetState The state to which the transition leads.
 * @property config The transition configuration, including actions and guards.
 * @property eventType The type of the event that triggers this transition.
 */
data class EventTypeBasedTransition<S : Any, E : Any>(
    override val sourceState: S,
    override val targetState: S,
    override val config: TransitionConfiguration<S, E> = TransitionConfiguration(),
    val eventType: Class<*>,
) : Transition<S, E>

/**
 * Represents a transition in a state machine that does not define a valid target state.
 *
 * @param S The type representing the states of the state machine.
 * @param E The type representing the events that trigger state transitions.
 * @property sourceState The source state of the transition.
 * @property event The event that triggered the transition.
 * @throws NoSuchElementException Thrown when attempting to access the target state, as it is undefined.
 */
data class UndefinedTransition<S : Any, E : Any>(override val sourceState: S, val event: E) : Transition<S, E> {
    override val targetState: S
        get() = throw NoSuchElementException("No target state defined.")
    override val config: TransitionConfiguration<S, E> = TransitionConfiguration()
}
