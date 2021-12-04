package io.justdevit.libs.statemachine

import io.justdevit.libs.statemachine.action.TransitionAction
import io.justdevit.libs.statemachine.guard.TransitionGuard
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class DefaultStateMachineTest {

    private val globalAction = mockk<TransitionAction<String, String>> {
        justRun { beforeEntry(any()) }
        justRun { afterEntry(any()) }
        justRun { beforeExit(any()) }
        justRun { afterExit(any()) }
    }
    private val globalGuard = mockk<TransitionGuard<String, String>> {
        every { onEntry(any()) } returns true
        every { onExit(any()) } returns true
    }
    private val action = mockk<TransitionAction<String, String>> {
        justRun { beforeEntry(any()) }
        justRun { afterEntry(any()) }
        justRun { beforeExit(any()) }
        justRun { afterExit(any()) }
    }
    private val guard = mockk<TransitionGuard<String, String>> {
        every { onEntry(any()) } returns true
        every { onExit(any()) } returns true
    }

    private val config = StateMachineConfiguration(
        initialState = "S0",
        finalStates = setOf("S1", "S2"),
        globalActions = listOf(globalAction),
        globalGuards = listOf(globalGuard),
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
                    actions = listOf(action),
                    guards = listOf(guard)
                )
            ),
        )
    )

    @Nested
    inner class AttributeTests {

        @Test
        fun `Should throw exception for empty final states`() {
            val config = StateMachineConfiguration<String, String>(
                initialState = "S0",
                finalStates = setOf(),
                transitions = emptyList()
            )

            assertThrows<IllegalArgumentException> { DefaultStateMachine(config = config) }
        }

        @Test
        fun `Should be able to return ID`() {
            val stateMachine = DefaultStateMachine(config)

            assertThat(stateMachine.id).isEqualTo(config.id)
        }

        @Test
        fun `Should be able to return actual state`() {
            val stateMachine = DefaultStateMachine(config)

            assertThat(stateMachine.actualState).isEqualTo(config.initialState)
        }

        @Test
        fun `Should be able to return unfinished state`() {
            val stateMachine = DefaultStateMachine(config)

            assertThat(stateMachine.finished).isFalse
        }
    }

    @Nested
    inner class StartTests {

        @Test
        fun `Should be able to return finished state`() {
            val stateMachine = DefaultStateMachine(config)
            stateMachine.start()

            val result = stateMachine.sendEvent("E1")

            assertThat(result).isEqualTo(SuccessResult)
            assertThat(stateMachine.finished).isTrue
        }

        @Test
        fun `Should be able to start state machine on defined state`() {
            val stateMachine = DefaultStateMachine(config)
            stateMachine.start("S1")

            assertThat(stateMachine.actualState).isEqualTo("S1")
            assertThat(stateMachine.finished).isTrue
        }

        @Test
        fun `Should throw on second start`() {
            val stateMachine = DefaultStateMachine(config)
            stateMachine.start()

            assertThrows<IllegalStateException> { stateMachine.start() }
        }
    }

    @Nested
    inner class ResetTests {

        @Test
        fun `Should throw on reset for not started state machine`() {
            val stateMachine = DefaultStateMachine(config)

            assertThrows<IllegalStateException> { stateMachine.reset() }
        }

        @Test
        fun `Should be able to reset state to initial`() {
            val stateMachine = DefaultStateMachine(config)
            stateMachine.start("S1")

            stateMachine.reset()

            assertThat(stateMachine.actualState).isEqualTo(config.initialState)
        }

        @Test
        fun `Should be able to reset state to defined state`() {
            val stateMachine = DefaultStateMachine(config)
            stateMachine.start()

            stateMachine.reset("S1")

            assertThat(stateMachine.actualState).isEqualTo("S1")
        }
    }

    @Nested
    inner class SendingEventTests {

        @Test
        fun `Should throw on send event for not started state machine`() {
            val stateMachine = DefaultStateMachine(config)

            assertThrows<IllegalStateException> { stateMachine.sendEvent("E1") }
        }

        @Test
        fun `Should reject on no transition`() {
            val stateMachine = DefaultStateMachine(config)
            stateMachine.start()

            val result = stateMachine.sendEvent("TEST")

            assertThat(result).isInstanceOf(RejectedResult::class.java)
        }

        @Test
        fun `Should return failed result on exception`() {
            val stateMachine = DefaultStateMachine(config)
            stateMachine.start()
            val exception = Exception("TEST")
            every { globalAction.beforeExit(any()) } throws exception

            val result = stateMachine.sendEvent("E1")

            assertThat(result).isInstanceOf(FailedResult::class.java)
            with(result as FailedResult) {
                assertThat(this.exception).isEqualTo(exception)
            }
        }

        @Test
        fun `Should be able to transit on event`() {
            val stateMachine = DefaultStateMachine(config)
            stateMachine.start()

            val result = stateMachine.sendEvent("E1")

            assertThat(result).isEqualTo(SuccessResult)
            assertThat(stateMachine.actualState).isEqualTo("S1")
            val slot = slot<TransitionContext<String, String>>()
            verify { globalAction.beforeExit(capture(slot)) }
            with(slot.captured) {
                assertThat(this.stateMachine).isEqualTo(stateMachine)
                assertThat(event).isEqualTo("E1")
                assertThat(sourceState).isEqualTo(config.initialState)
                assertThat(targetState).isEqualTo("S1")
                assertThat(parameters).isEmpty()
            }
            verify { globalAction.afterExit(slot.captured) }
            verify { globalAction.beforeEntry(slot.captured) }
            verify { globalAction.afterEntry(slot.captured) }
            verify { globalGuard.onExit(slot.captured) }
            verify { globalGuard.onEntry(slot.captured) }
        }
    }

    @Nested
    inner class GuardsTests {

        private val stateMachine = DefaultStateMachine(config)

        init {
            stateMachine.start()
        }

        @Test
        fun `Should be rejected by global guard on exit`() {
            every { globalGuard.onExit(any()) } returns false

            val result = stateMachine.sendEvent("E1")

            assertThat(result).isInstanceOf(RejectedResult::class.java)
            verify { globalAction.beforeExit(any()) }
            verify { globalGuard.onExit(any()) }
            verify(exactly = 0) { globalAction.afterExit(any()) }
            verify(exactly = 0) { globalAction.beforeEntry(any()) }
            verify(exactly = 0) { globalGuard.onEntry(any()) }
            verify(exactly = 0) { globalAction.afterEntry(any()) }
        }

        @Test
        fun `Should be rejected by global guard on entry`() {
            every { globalGuard.onEntry(any()) } returns false

            val result = stateMachine.sendEvent("E1")

            assertThat(result).isInstanceOf(RejectedResult::class.java)
            verify { globalAction.beforeExit(any()) }
            verify { globalGuard.onExit(any()) }
            verify { globalAction.afterExit(any()) }
            verify { globalAction.beforeEntry(any()) }
            verify { globalGuard.onEntry(any()) }
            verify(exactly = 0) { globalAction.afterEntry(any()) }
        }

        @Test
        fun `Should be rejected by guard on exit`() {
            every { guard.onExit(any()) } returns false

            val result = stateMachine.sendEvent("E2")

            assertThat(result).isInstanceOf(RejectedResult::class.java)
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

        @Test
        fun `Should be rejected by guard on entry`() {
            every { guard.onEntry(any()) } returns false

            val result = stateMachine.sendEvent("E2")

            assertThat(result).isInstanceOf(RejectedResult::class.java)
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
