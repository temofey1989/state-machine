package io.justdevit.tools.statemachine

/**
 * Enum describes startup mode for state machine.
 */
enum class StateMachineStartup {
    /**
     * State Machine starts automatically on created.
     */
    AUTO,

    /**
     * State Machine should be started manually.
     */
    LAZY,
}
