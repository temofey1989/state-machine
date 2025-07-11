package io.justdevit.tools.statemachine

/**
 * Represents the result of the transition.
 */
sealed interface TransitionResult<S : Any, E : Any> {
    val transition: Transition<S, E>
}

/**
 * Represents the success result of the transition.
 */
data class SuccessResult<S : Any, E : Any>(override val transition: Transition<S, E>) : TransitionResult<S, E>

/**
 * Represents the rejected result of the transition.
 * Usually by rejection from the guard.
 */
data class RejectedResult<S : Any, E : Any>(
    override val transition: Transition<S, E>,
    /**
     * Reason of the rejection.
     */
    val reason: String,
) : TransitionResult<S, E>

/**
 * Represents the failure of the transition.
 */
data class FailedResult<S : Any, E : Any>(
    override val transition: Transition<S, E>,
    /**
     * The failure exception.
     */
    val exception: Throwable,
) : TransitionResult<S, E>

/**
 * Parameter represents the successfulness of the event execution.
 */
val <S : Any, E : Any> TransitionResult<S, E>.successful: Boolean
    get() =
        when (this) {
            is SuccessResult -> true
            is RejectedResult, is FailedResult -> false
        }

/**
 * Function provides action of successful transition.
 *
 * @param action Action to be executed.
 */
inline fun <S : Any, E : Any> TransitionResult<S, E>.ifSuccess(action: (SuccessResult<S, E>) -> Unit) {
    if (this is SuccessResult) {
        action(this)
    }
}

/**
 * Function provides action of rejected transition.
 *
 * @param action Action to be executed.
 */
inline fun <S : Any, E : Any> TransitionResult<S, E>.ifRejected(action: (RejectedResult<S, E>) -> Unit) {
    if (this is RejectedResult) {
        action(this)
    }
}

/**
 * Function provides action of failed transition.
 *
 * @param action Action to be executed.
 */
inline fun <S : Any, E : Any> TransitionResult<S, E>.ifFailed(action: (FailedResult<S, E>) -> Unit) {
    if (this is FailedResult) {
        action(this)
    }
}

/**
 * Function provides action of failed transition.
 *
 * @param action Action to be executed.
 */
inline fun <S : Any, E : Any> TransitionResult<S, E>.ifFailedOrRejected(action: (TransitionResult<S, E>) -> Unit) {
    if (this is FailedResult) {
        action(this)
    }
}
