package io.justdevit.tools.statemachine

sealed interface Transition<S : Any, E : Any> {
    val sourceState: S
    val targetState: S
    val event: E
}

data class DefinedTransition<S : Any, E : Any>(
    override val sourceState: S,
    override val targetState: S,
    override val event: E,
    val config: TransitionConfiguration<S, E> = TransitionConfiguration(),
) : Transition<S, E>

data class UndefinedTransition<S : Any, E : Any>(override val sourceState: S, override val event: E) : Transition<S, E> {
    override val targetState: S
        get() = throw NoSuchElementException("No target state defined.")
}
