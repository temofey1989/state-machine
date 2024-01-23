package io.justdevit.libs.statemachine

import io.justdevit.libs.statemachine.action.TransitionAction
import io.justdevit.libs.statemachine.guard.TransitionGuard
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.instanceOf
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify

internal class DefaultStateMachineTest : FreeSpec({

    "Attribute tests" - {

        "Should throw exception for empty final states" {
            val corruptedConfig = StateMachineConfiguration<String, String>(
                initialState = "S0",
                finalStates = setOf(),
                transitions = emptyList()
            )

            shouldThrow<IllegalArgumentException> { DefaultStateMachine(config = corruptedConfig) }
        }

        "Should be able to return ID" {
            val context = buildConfig()
            val config = context.config

            val stateMachine = DefaultStateMachine(config)

            stateMachine.id shouldBe config.id
        }

        "Should be able to return actual state" {
            val context = buildConfig()
            val config = context.config

            val stateMachine = DefaultStateMachine(config)

            stateMachine.actualState shouldBe config.initialState
        }

        "Should be able to return unfinished state" {
            val context = buildConfig()
            val config = context.config

            val stateMachine = DefaultStateMachine(config)

            stateMachine.finished shouldBe false
        }
    }

    "Start tests" - {

        "Should be able to return finished state" {
            val context = buildConfig()
            val config = context.config

            val stateMachine = DefaultStateMachine(config)
            stateMachine.start()

            val result = stateMachine.sendEvent("E1")

            result shouldBe SuccessResult
            stateMachine.finished shouldBe true
        }

        "Should be able to start state machine on defined state" {
            val context = buildConfig()
            val config = context.config

            val stateMachine = DefaultStateMachine(config)
            stateMachine.start("S1")

            stateMachine.actualState shouldBe "S1"
            stateMachine.finished shouldBe true
        }

        "Should throw on second start" {
            val context = buildConfig()
            val config = context.config

            val stateMachine = DefaultStateMachine(config)
            stateMachine.start()

            shouldThrow<IllegalStateException> {
                stateMachine.start()
            }
        }
    }

    "Reset Ttsts" - {

        "Should throw on reset for not started state machine" {
            val context = buildConfig()
            val config = context.config

            val stateMachine = DefaultStateMachine(config)

            shouldThrow<IllegalStateException> {
                stateMachine.reset()
            }
        }

        "Should be able to reset state to initial" {
            val context = buildConfig()
            val config = context.config

            val stateMachine = DefaultStateMachine(config)
            stateMachine.start("S1")

            stateMachine.reset()

            stateMachine.actualState shouldBe config.initialState
        }

        "Should be able to reset state to defined state" {
            val context = buildConfig()
            val config = context.config

            val stateMachine = DefaultStateMachine(config)
            stateMachine.start()

            stateMachine.reset("S1")

            stateMachine.actualState shouldBe "S1"
        }
    }

    "Sending event tests" - {

        "Should throw on send event for not started state machine" {
            val context = buildConfig()
            val config = context.config

            val stateMachine = DefaultStateMachine(config)

            shouldThrow<IllegalStateException> {
                stateMachine.sendEvent("E1")
            }
        }

        "Should reject on no transition" {
            val context = buildConfig()
            val config = context.config

            val stateMachine = DefaultStateMachine(config)
            stateMachine.start()

            val result = stateMachine.sendEvent("TEST")

            result shouldBe instanceOf<RejectedResult>()
        }

        "Should return failed result on exception" {
            val context = buildConfig {
                val exception = Exception("TEST")
                every { globalAction.beforeExit(any()) } throws exception
            }
            val config = context.config

            val stateMachine = DefaultStateMachine(config)
            stateMachine.start()

            val result = stateMachine.sendEvent("E1")

            result shouldBe instanceOf<FailedResult>()
            with(result as FailedResult) {
                this.exception shouldBe exception
            }
        }

        "Should be able to transit on event" {
            val context = buildConfig()
            val config = context.config

            val stateMachine = DefaultStateMachine(config)
            stateMachine.start()

            val result = stateMachine.sendEvent("E1")

            result shouldBe SuccessResult
            stateMachine.actualState shouldBe "S1"
            val slot = slot<TransitionContext<String, String>>()
            with(context.mocks) {
                verify { globalAction.beforeExit(capture(slot)) }
                with(slot.captured) {
                    this.stateMachine shouldBe stateMachine
                    event shouldBe "E1"
                    sourceState shouldBe config.initialState
                    targetState shouldBe "S1"
                    parameters.shouldBeEmpty()
                }
                verify { globalAction.afterExit(slot.captured) }
                verify { globalAction.beforeEntry(slot.captured) }
                verify { globalAction.afterEntry(slot.captured) }
                verify { globalGuard.onExit(slot.captured) }
                verify { globalGuard.onEntry(slot.captured) }
            }
        }
    }

    "Guards tests" - {

        "Should be rejected by global guard on exit" {
            val context = buildConfig {
                every { globalGuard.onExit(any()) } returns false
            }
            val config = context.config
            val stateMachine = DefaultStateMachine(config)
            stateMachine.start()

            val result = stateMachine.sendEvent("E1")

            result shouldBe instanceOf<RejectedResult>()
            with(context.mocks) {
                verify { globalAction.beforeExit(any()) }
                verify { globalGuard.onExit(any()) }
                verify(exactly = 0) { globalAction.afterExit(any()) }
                verify(exactly = 0) { globalAction.beforeEntry(any()) }
                verify(exactly = 0) { globalGuard.onEntry(any()) }
                verify(exactly = 0) { globalAction.afterEntry(any()) }
            }
        }

        "Should be rejected by global guard on entry" {
            val context = buildConfig {
                every { globalGuard.onEntry(any()) } returns false
            }
            val config = context.config
            val stateMachine = DefaultStateMachine(config)
            stateMachine.start()

            val result = stateMachine.sendEvent("E1")

            result shouldBe instanceOf<RejectedResult>()
            with(context.mocks) {
                verify { globalAction.beforeExit(any()) }
                verify { globalGuard.onExit(any()) }
                verify { globalAction.afterExit(any()) }
                verify { globalAction.beforeEntry(any()) }
                verify { globalGuard.onEntry(any()) }
                verify(exactly = 0) { globalAction.afterEntry(any()) }
            }
        }

        "Should be rejected by guard on exit" {
            val context = buildConfig {
                every { guard.onExit(any()) } returns false
            }
            val config = context.config
            val stateMachine = DefaultStateMachine(config)
            stateMachine.start()

            val result = stateMachine.sendEvent("E2")

            result shouldBe instanceOf<RejectedResult>()
            with(context.mocks) {
                verify { globalAction.beforeExit(any()) }
                verify { action.beforeExit(any()) }
                verify { globalGuard.onExit(any()) }
                verify { guard.onExit(any()) }
                verify(exactly = 0) { globalAction.afterExit(any()) }
                verify(exactly = 0) { action.afterExit(any()) }
                verify(exactly = 0) { globalAction.beforeEntry(any()) }
                verify(exactly = 0) { action.beforeEntry(any()) }
                verify(exactly = 0) { globalGuard.onEntry(any()) }
                verify(exactly = 0) { guard.onEntry(any()) }
                verify(exactly = 0) { globalAction.afterEntry(any()) }
                verify(exactly = 0) { action.afterEntry(any()) }
            }
        }

        "Should be rejected by guard on entry" {
            val context = buildConfig {
                every { guard.onEntry(any()) } returns false
            }
            val config = context.config
            val stateMachine = DefaultStateMachine(config)
            stateMachine.start()

            val result = stateMachine.sendEvent("E2")

            result shouldBe instanceOf<RejectedResult>()
            with(context.mocks) {
                verify { globalAction.beforeExit(any()) }
                verify { action.beforeExit(any()) }
                verify { globalGuard.onExit(any()) }
                verify { guard.onExit(any()) }
                verify { globalAction.afterExit(any()) }
                verify { action.afterExit(any()) }
                verify { globalAction.beforeEntry(any()) }
                verify { action.beforeEntry(any()) }
                verify { globalGuard.onEntry(any()) }
                verify { guard.onEntry(any()) }
                verify(exactly = 0) { globalAction.afterEntry(any()) }
                verify(exactly = 0) { action.afterEntry(any()) }
            }
        }
    }
})

data class Mocks(
    val globalAction: TransitionAction<String, String> = mockk<TransitionAction<String, String>> {
        justRun { beforeEntry(any()) }
        justRun { afterEntry(any()) }
        justRun { beforeExit(any()) }
        justRun { afterExit(any()) }
    },
    val globalGuard: TransitionGuard<String, String> = mockk<TransitionGuard<String, String>> {
        every { onEntry(any()) } returns true
        every { onExit(any()) } returns true
    },
    val action: TransitionAction<String, String> = mockk<TransitionAction<String, String>> {
        justRun { beforeEntry(any()) }
        justRun { afterEntry(any()) }
        justRun { beforeExit(any()) }
        justRun { afterExit(any()) }
    },
    val guard: TransitionGuard<String, String> = mockk<TransitionGuard<String, String>> {
        every { onEntry(any()) } returns true
        every { onExit(any()) } returns true
    },
)

data class TestContext(
    val config: StateMachineConfiguration<String, String>,
    val mocks: Mocks,
)

fun buildConfig(configurer: Mocks.() -> Unit = {}): TestContext {
    val mocks = Mocks()
    mocks.configurer()
    return TestContext(
        config = StateMachineConfiguration(
            initialState = "S0",
            finalStates = setOf("S1", "S2"),
            globalActions = listOf(mocks.globalAction),
            globalGuards = listOf(mocks.globalGuard),
            transitions = listOf(
                Transition(
                    sourceState = "S0",
                    targetState = "S1",
                    event = "E1"
                ),
                Transition(
                    sourceState = "S0",
                    targetState = "S2",
                    event = "E2",
                    config = TransitionConfiguration(
                        actions = listOf(mocks.action),
                        guards = listOf(mocks.guard)
                    )
                ),
            )
        ),
        mocks = mocks,
    )
}
