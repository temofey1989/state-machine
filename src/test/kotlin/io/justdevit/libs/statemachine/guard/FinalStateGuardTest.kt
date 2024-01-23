package io.justdevit.libs.statemachine.guard

import io.justdevit.libs.statemachine.StateMachine
import io.justdevit.libs.statemachine.TransitionContext
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

internal class FinalStateGuardTest : FreeSpec({

    "Should return false on state machine is finished" {
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

        result shouldBe false
    }

    "Should return true on state machine is not finished" {
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

        result shouldBe true
    }
})
