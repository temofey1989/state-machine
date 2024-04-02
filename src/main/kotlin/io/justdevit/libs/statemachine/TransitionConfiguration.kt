package io.justdevit.libs.statemachine

import io.justdevit.libs.statemachine.action.TransitionAction
import io.justdevit.libs.statemachine.guard.TransitionGuard

data class TransitionConfiguration<S, E>(
    val actions: List<TransitionAction<S, E>> = emptyList(),
    val guards: List<TransitionGuard<S, E>> = emptyList(),
)
