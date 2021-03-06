package io.justdevit.libs.statemachine

data class Transition<S, E>(
    val sourceState: S,
    val targetState: S,
    val event: E,
    val config: TransitionConfiguration<S, E> = TransitionConfiguration()
)
