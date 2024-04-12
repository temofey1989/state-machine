package io.justdevit.tools.statemachine

sealed interface Transition<S, E> {
    val sourceState: S
    val targetState: S
    val event: E
}

data class DefinedTransition<S, E>(
    override val sourceState: S,
    override val targetState: S,
    override val event: E,
    val config: TransitionConfiguration<S, E> = TransitionConfiguration(),
) : Transition<S, E>

data class UndefinedTransition<S, E>(override val sourceState: S, override val event: E) : Transition<S, E> {
    override val targetState: S
        get() = throw NoSuchElementException("No target state defined.")
}
