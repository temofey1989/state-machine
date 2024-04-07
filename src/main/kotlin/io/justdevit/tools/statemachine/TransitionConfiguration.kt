package io.justdevit.tools.statemachine

import io.justdevit.tools.statemachine.action.TransitionAction
import io.justdevit.tools.statemachine.guard.TransitionGuard

data class TransitionConfiguration<S, E>(val actions: List<TransitionAction<S, E>> = emptyList(), val guards: List<TransitionGuard<S, E>> = emptyList())
