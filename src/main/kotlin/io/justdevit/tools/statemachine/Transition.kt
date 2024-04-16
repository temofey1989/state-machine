package io.justdevit.tools.statemachine

sealed interface Transition<S : Any, E : Any> {
    val sourceState: S
    val targetState: S
    val config: TransitionConfiguration<S, E>
}

data class EventBasedTransition<S : Any, E : Any>(
    override val sourceState: S,
    override val targetState: S,
    override val config: TransitionConfiguration<S, E> = TransitionConfiguration(),
    val event: E,
) : Transition<S, E>

data class EventKeyBasedTransition<S : Any, E : Any>(
    override val sourceState: S,
    override val targetState: S,
    override val config: TransitionConfiguration<S, E> = TransitionConfiguration(),
    val eventKey: Any,
) : Transition<S, E>

data class EventTypeBasedTransition<S : Any, E : Any>(
    override val sourceState: S,
    override val targetState: S,
    override val config: TransitionConfiguration<S, E> = TransitionConfiguration(),
    val eventType: Class<*>,
) : Transition<S, E>

data class UndefinedTransition<S : Any, E : Any>(override val sourceState: S, val event: E) : Transition<S, E> {
    override val targetState: S
        get() = throw NoSuchElementException("No target state defined.")
    override val config: TransitionConfiguration<S, E> = TransitionConfiguration()
}
