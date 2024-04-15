package io.justdevit.tools.statemachine

import io.justdevit.tools.statemachine.action.TransitionAction
import io.justdevit.tools.statemachine.guard.TransitionGuard
import java.util.UUID

/**
 * Default implementation of the state machine.
 *
 * @param config State Machine configuration.
 * @see StateMachine
 */
open class DefaultStateMachine<S : Any, E : Any>(private val config: StateMachineConfiguration<S, E>) : StateMachine<S, E> {
    private val transitionMap = config.transitions.associateBy { Pair(it.sourceState, it.event) }
    private var state: S = config.initialState
    private var started: Boolean = false

    init {
        require(config.finalStates.isNotEmpty()) { "Final state(s) should be defined." }
    }

    override val id: UUID
        get() = config.id

    override val actualState: S
        get() = state

    override val finished: Boolean
        get() = actualState in config.finalStates

    override fun start(state: S?) {
        if (started) {
            throw IllegalStateException("State Machine ($id) is already started.")
        }
        this.state = state ?: config.initialState
        started = true
    }

    override fun reset(state: S?) {
        checkStateMachineStarted()
        this.state = state ?: config.initialState
    }

    override suspend fun sendEvent(event: E, parameters: TransitionParameters): TransitionResult<S, E> {
        checkStateMachineStarted()
        val transition =
            transitionMap[actualState to event]
                ?: return RejectedResult(
                    transition = UndefinedTransition(
                        sourceState = actualState,
                        event = event,
                    ),
                    reason = "No transition from $actualState with $event exists.",
                )
        val context =
            TransitionContext(
                sourceState = actualState,
                targetState = transition.targetState,
                event = event,
                stateMachine = this@DefaultStateMachine,
                parameters = parameters,
            )
        val guards = config.globalGuards + transition.config.guards
        val actions = config.globalActions + transition.config.actions
        try {
            actions.execBeforeExit(context)
            guards.ifAnyDeclinedOnExit(context) {
                return RejectedResult(
                    transition = transition,
                    reason = "${this@ifAnyDeclinedOnExit::class.simpleName} has declined exit on $event for state $actualState.",
                )
            }
            actions.execAfterExit(context)

            actions.execBeforeEntry(context)
            guards.ifAnyDeclinedOnEntry(context) {
                return RejectedResult(
                    transition = transition,
                    reason = "${this@ifAnyDeclinedOnEntry::class.simpleName} has declined entry on $event for state $actualState.",
                )
            }
            state = transition.targetState
            actions.execAfterEntry(context)

            return SuccessResult(transition = transition)
        } catch (throwable: Throwable) {
            return FailedResult(transition = transition, exception = throwable)
        }
    }

    private fun checkStateMachineStarted() {
        if (!started) {
            throw IllegalStateException("State Machine ($id) is not started yet.")
        }
    }

    private suspend fun List<TransitionAction<S, E>>.execBeforeEntry(context: TransitionContext<S, E>) =
        forEach {
            it.beforeEntry(context)
        }

    private suspend fun List<TransitionAction<S, E>>.execAfterEntry(context: TransitionContext<S, E>) =
        forEach {
            it.afterEntry(context)
        }

    private suspend fun List<TransitionAction<S, E>>.execBeforeExit(context: TransitionContext<S, E>) =
        forEach {
            it.beforeExit(context)
        }

    private suspend fun List<TransitionAction<S, E>>.execAfterExit(context: TransitionContext<S, E>) =
        forEach {
            it.afterExit(context)
        }

    private suspend inline fun List<TransitionGuard<S, E>>.ifAnyDeclinedOnExit(context: TransitionContext<S, E>, onReject: TransitionGuard<S, E>.() -> Unit) =
        firstOrNull { !it.onExit(context) }
            ?.onReject()

    private suspend inline fun List<TransitionGuard<S, E>>.ifAnyDeclinedOnEntry(context: TransitionContext<S, E>, onReject: TransitionGuard<S, E>.() -> Unit) =
        firstOrNull { !it.onEntry(context) }
            ?.onReject()
}
