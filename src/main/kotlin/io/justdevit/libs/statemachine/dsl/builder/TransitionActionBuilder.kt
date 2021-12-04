package io.justdevit.libs.statemachine.dsl.builder

import io.justdevit.libs.statemachine.action.TransitionAction

/**
 * Transition action list builder.
 *
 * @see StateMachineConfigurationBuilder.globalActions()
 */
class TransitionActionBuilder<S, E> {

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

    /**
     * Creates list of transition actions.
     *
     * @return List of transition actions.
     */
    fun build(): List<TransitionAction<S, E>> = actions

}
