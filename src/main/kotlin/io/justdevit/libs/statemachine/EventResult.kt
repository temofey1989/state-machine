package io.justdevit.libs.statemachine

/**
 * Represents the result of the event processing.
 */
sealed class EventResult

/**
 * Represents the success result of the event processing.
 */
data object SuccessResult : EventResult()

/**
 * Represents the rejected result of the event processing.
 * Usually by rejection from the guard.
 */
data class RejectedResult(
    /**
     * Reason of the rejection.
     */
    val reason: String,
) : EventResult()

/**
 * Represents the failure of the event processing.
 */
data class FailedResult(
    /**
     * The failure exception.
     */
    val exception: Throwable,
) : EventResult()

/**
 * Parameter represent successfulness of the event execution.
 */
val EventResult.successful: Boolean
    get() =
        when (this) {
            is SuccessResult -> true

            is RejectedResult,
            is FailedResult,
            -> false
        }

/**
 * Function provides action of successful event processing.
 *
 * @param action Action to be executed.
 */
inline fun EventResult.ifSuccess(action: () -> Unit) {
    if (this is SuccessResult) {
        action()
    }
}

/**
 * Function provides action of rejected event processing.
 *
 * @param action Action to be executed.
 */
inline fun EventResult.ifRejected(action: (String) -> Unit) {
    if (this is RejectedResult) {
        action(reason)
    }
}

/**
 * Function provides action of failed event processing.
 *
 * @param action Action to be executed.
 */
inline fun EventResult.ifFailed(action: (Throwable) -> Unit) {
    if (this is FailedResult) {
        action(exception)
    }
}
