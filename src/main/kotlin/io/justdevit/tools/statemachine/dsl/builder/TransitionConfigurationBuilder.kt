package io.justdevit.tools.statemachine.dsl.builder

import io.justdevit.tools.statemachine.TransitionConfiguration
import io.justdevit.tools.statemachine.TransitionContext
import io.justdevit.tools.statemachine.action.TransitionAction
import io.justdevit.tools.statemachine.guard.TransitionGuard

/**
 * Builder class for constructing the configuration of a state machine transition,
 * including guards and actions.
 *
 * @param S The type representing the state in the state machine.
 * @param E The type representing the event in the state machine.
 */
class TransitionConfigurationBuilder<S : Any, E : Any> {
    private val guards: MutableList<TransitionGuard<S, E>> = mutableListOf()
    private val actions: MutableList<TransitionAction<S, E>> = mutableListOf()

    /**
     * Adds a transition guard to the configuration.
     *
     * @param guard The guard to be added, used to evaluate transition conditions.
     */
    fun add(guard: TransitionGuard<S, E>) {
        guards += guard
    }

    /**
     * Operator extension to add a transition guard to the configuration.
     */
    operator fun TransitionGuard<S, E>.unaryPlus() {
        add(this)
    }

    /**
     * Adds a transition action to the configuration.
     *
     * @param action The action to be added, defining the behavior during the transition.
     */
    fun add(action: TransitionAction<S, E>) {
        actions += action
    }

    /**
     * Operator extension to add a transition action to the configuration.
     */
    operator fun TransitionAction<S, E>.unaryPlus() {
        add(this)
    }

    /**
     * Specifies an on-entry guard for the transition.
     *
     * @param action A suspending function applied within the transition context that evaluates
     * to `true` if the transition can proceed or `false` otherwise.
     */
    fun onEntry(action: suspend TransitionContext<S, E>.() -> Boolean) {
        guards += object : TransitionGuard<S, E> {
            override suspend fun onEntry(context: TransitionContext<S, E>) = context.action()
        }
    }

    /**
     * Specifies an on-exit guard for the transition.
     *
     * @param action A suspending function executed within the transition context that determines
     * whether the transition can proceed. Should return `true` if the transition is allowed or
     * `false` otherwise.
     */
    fun onExit(action: suspend TransitionContext<S, E>.() -> Boolean) {
        guards += object : TransitionGuard<S, E> {
            override suspend fun onExit(context: TransitionContext<S, E>) = context.action()
        }
    }

    /**
     * Specifies an action to be executed before entering the target state during a transition.
     *
     * @param action A suspending function applied within the transition context that defines
     * the behavior to be executed before transitioning to the target state.
     */
    fun beforeEntry(action: suspend TransitionContext<S, E>.() -> Unit) {
        actions += object : TransitionAction<S, E> {
            override suspend fun beforeEntry(context: TransitionContext<S, E>) = context.action()
        }
    }

    /**
     * Specifies an action to be executed after entering the target state during a transition.
     *
     * @param action A suspending function applied within the transition context that defines
     * the behavior to be executed after transitioning to the target state.
     */
    fun afterEntry(action: suspend TransitionContext<S, E>.() -> Unit) {
        actions += object : TransitionAction<S, E> {
            override suspend fun afterEntry(context: TransitionContext<S, E>) = context.action()
        }
    }

    /**
     * Specifies an action to be executed before exiting the current state during a transition.
     *
     * @param action A suspending function applied within the transition context that defines
     * the behavior to be executed before transitioning out of the current state.
     */
    fun beforeExit(action: suspend TransitionContext<S, E>.() -> Unit) {
        actions += object : TransitionAction<S, E> {
            override suspend fun beforeExit(context: TransitionContext<S, E>) = context.action()
        }
    }

    /**
     * Specifies an action to be executed after exiting the current state during a transition.
     *
     * @param action A suspending function applied within the transition context
     * that defines the behavior to be executed after transitioning out of the current state.
     */
    fun afterExit(action: suspend TransitionContext<S, E>.() -> Unit) {
        actions += object : TransitionAction<S, E> {
            override suspend fun afterExit(context: TransitionContext<S, E>) = context.action()
        }
    }

    /**
     * Builds the transition configuration containing the defined guards and actions.
     *
     * @return A new instance of [TransitionConfiguration] with the guards and actions defined in the builder.
     */
    fun build() =
        TransitionConfiguration(
            guards = guards,
            actions = actions,
        )
}
