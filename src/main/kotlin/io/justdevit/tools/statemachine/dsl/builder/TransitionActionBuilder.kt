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
     * Register new action.
     */
    fun action(action: TransitionAction<S, E>) {
        actions += action
    }

    /**
     * Register new action.
     *
     * Syntax: +MyAction()
     */
    operator fun TransitionAction<S, E>.unaryPlus() {
        action(this)
    }

    fun beforeEntry(action: suspend TransitionContext<S, E>.() -> Unit) {
        actions += object : TransitionAction<S, E> {
            override suspend fun beforeEntry(context: TransitionContext<S, E>) = context.action()
        }
    }

    fun afterEntry(action: suspend TransitionContext<S, E>.() -> Unit) {
        actions += object : TransitionAction<S, E> {
            override suspend fun afterEntry(context: TransitionContext<S, E>) = context.action()
        }
    }

    fun beforeExit(action: suspend TransitionContext<S, E>.() -> Unit) {
        actions += object : TransitionAction<S, E> {
            override suspend fun beforeExit(context: TransitionContext<S, E>) = context.action()
        }
    }

    fun afterExit(action: suspend TransitionContext<S, E>.() -> Unit) {
        actions += object : TransitionAction<S, E> {
            override suspend fun afterExit(context: TransitionContext<S, E>) = context.action()
        }
    }

    /**
     * Creates list of transition actions.
     *
     * @return List of transition actions.
     */
    fun build(): List<TransitionAction<S, E>> = actions
}
