package io.justdevit.libs.statemachine.guard

import io.justdevit.libs.statemachine.StateMachine
import io.justdevit.libs.statemachine.TransitionContext
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class FinalStateGuardTest {

    @Test
    fun `Should return false on state machine is finished`() {
        val stateMachine = mockk<StateMachine<String, String>>()
        every { stateMachine.finished } returns true
        val guard = FinalStateGuard<String, String>()

        val result = guard.onExit(
            TransitionContext(
                sourceState = "S1",
                targetState = "S2",
                event = "TEST",
                stateMachine = stateMachine
            )
        )

        assertThat(result).isFalse
    }

    @Test
    fun `Should return true on state machine is not finished`() {
        val stateMachine = mockk<StateMachine<String, String>>()
        every { stateMachine.finished } returns false
        val guard = FinalStateGuard<String, String>()

        val result = guard.onExit(
            TransitionContext(
                sourceState = "S1",
                targetState = "S2",
                event = "TEST",
                stateMachine = stateMachine
            )
        )

        assertThat(result).isTrue
    }
}
