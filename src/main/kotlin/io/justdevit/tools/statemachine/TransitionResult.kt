package io.justdevit.tools.statemachine

/**
 * Represents the result of the transition.
 */
sealed interface TransitionResult<S, E> {
    val transition: Transition<S, E>
}

/**
 * Represents the success result of the transition.
 */
data class SuccessResult<S, E>(override val transition: Transition<S, E>) : TransitionResult<S, E>

/**
 * Represents the rejected result of the transition.
 * Usually by rejection from the guard.
 */
data class RejectedResult<S, E>(
    override val transition: Transition<S, E>,
    /**
     * Reason of the rejection.
     */
    val reason: String,
) : TransitionResult<S, E>

/**
 * Represents the failure of the transition.
 */
data class FailedResult<S, E>(
    override val transition: Transition<S, E>,
    /**
     * The failure exception.
     */
    val exception: Throwable,
) : TransitionResult<S, E>

/**
 * Parameter represent successfulness of the event execution.
 */
val <S, E> TransitionResult<S, E>.successful: Boolean
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
inline fun <S, E> TransitionResult<S, E>.ifSuccess(action: (SuccessResult<S, E>) -> Unit) {
    if (this is SuccessResult) {
        action(this)
    }
}

/**
 * Function provides action of rejected transition.
 *
 * @param action Action to be executed.
 */
inline fun <S, E> TransitionResult<S, E>.ifRejected(action: (RejectedResult<S, E>) -> Unit) {
    if (this is RejectedResult) {
        action(this)
    }
}

/**
 * Function provides action of failed transition.
 *
 * @param action Action to be executed.
 */
inline fun <S, E> TransitionResult<S, E>.ifFailed(action: (FailedResult<S, E>) -> Unit) {
    if (this is FailedResult) {
        action(this)
    }
}

/**
 * Function provides action of failed transition.
 *
 * @param action Action to be executed.
 */
inline fun <S, E> TransitionResult<S, E>.ifFailedOrRejected(action: (TransitionResult<S, E>) -> Unit) {
    if (this is FailedResult) {
        action(this)
    }
}
