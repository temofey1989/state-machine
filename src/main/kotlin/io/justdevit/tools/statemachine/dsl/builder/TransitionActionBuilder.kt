package io.justdevit.tools.statemachine.dsl.builder

import io.justdevit.tools.statemachine.TransitionContext
import io.justdevit.tools.statemachine.action.TransitionAction

/**
 * Transition action list builder.
 *
 * @see StateMachineConfigurationBuilder.globalActions()
 */
class TransitionActionBuilder<S : Any, E : Any> {
    private val actions = mutableListOf<TransitionAction<S, E>>()

    /**
     * Adds a transition action to the list of actions.
     *
     * @param action The transition action to be added.
     */
    fun action(action: TransitionAction<S, E>) {
        actions += action
    }

    /**
     * Adds the given transition action to the current list of actions
     * maintained within the builder.
     */
    operator fun TransitionAction<S, E>.unaryPlus() {
        action(this)
    }

    /**
     * Registers an action to be executed before entering a state.
     *
     * @param action A suspending lambda that defines the action to be performed within the context of the provided transition.
     */
    fun beforeEntry(action: suspend TransitionContext<S, E>.() -> Unit) {
        actions += object : TransitionAction<S, E> {
            override suspend fun beforeEntry(context: TransitionContext<S, E>) = context.action()
        }
    }

    /**
     * Registers an action to be executed after entering a state.
     *
     * @param action A suspending lambda that defines the action to be performed within the context of the provided transition.
     */
    fun afterEntry(action: suspend TransitionContext<S, E>.() -> Unit) {
        actions += object : TransitionAction<S, E> {
            override suspend fun afterEntry(context: TransitionContext<S, E>) = context.action()
        }
    }

    /**
     * Registers an action to be executed before exiting a state.
     *
     * @param action A suspending lambda that defines the action to be performed within the context of the provided transition.
     */
    fun beforeExit(action: suspend TransitionContext<S, E>.() -> Unit) {
        actions += object : TransitionAction<S, E> {
            override suspend fun beforeExit(context: TransitionContext<S, E>) = context.action()
        }
    }

    /**
     * Registers an action to be executed after exiting a state.
     *
     * @param action A suspending lambda that defines the action to be performed within the context of the provided transition.
     */
    fun afterExit(action: suspend TransitionContext<S, E>.() -> Unit) {
        actions += object : TransitionAction<S, E> {
            override suspend fun afterExit(context: TransitionContext<S, E>) = context.action()
        }
    }

    /**
     * Creates a list of transition actions.
     *
     * @return List of transition actions.
     */
    fun build(): List<TransitionAction<S, E>> = actions
}
