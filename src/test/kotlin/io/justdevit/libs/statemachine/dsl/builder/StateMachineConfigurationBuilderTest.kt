package io.justdevit.libs.statemachine.dsl.builder

import io.justdevit.libs.statemachine.action.TransitionAction
import io.justdevit.libs.statemachine.guard.TransitionGuard
import io.mockk.isMockKMock
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID.randomUUID
import java.util.function.Consumer

internal class StateMachineConfigurationBuilderTest {

    @Test
    fun `Should reject on missed initial state`() {
        val builder = StateMachineConfigurationBuilder<Any, Any>()
        builder.finalStates = setOf(2)

        assertThrows<IllegalStateException> { builder.build() }
    }

    @Test
    fun `Should reject on missed final state`() {
        val builder = StateMachineConfigurationBuilder<Any, Any>()
        builder.initialState = 1

        assertThrows<IllegalStateException> { builder.build() }
    }

    @Test
    fun `Should be able to create state machine configuration`() {
        val builder = StateMachineConfigurationBuilder<String, String>()
        builder.id = randomUUID()
        builder.initialState = "S1"
        builder.finalStates = setOf("S2")
        builder.from("S1") {
            to("S2").with("TEST")
        }
        builder.globalActions {
            +mockk<TransitionAction<String, String>>()
        }
        builder.globalGuards {
            +mockk<TransitionGuard<String, String>>()
        }

        val result = builder.build()

        with(result) {
            assertThat(id).isEqualTo(builder.id)
            assertThat(initialState).isEqualTo(builder.initialState)
            assertThat(finalStates).isEqualTo(builder.finalStates)
            assertThat(transitions).singleElement().satisfies(Consumer {
                assertThat(it.sourceState).isEqualTo("S1")
                assertThat(it.targetState).isEqualTo("S2")
                assertThat(it.event).isEqualTo("TEST")
                assertThat(it.config.actions).isEmpty()
                assertThat(it.config.guards).isEmpty()
            })
            assertThat(globalActions).singleElement().satisfies(Consumer {
                isMockKMock(it)
            })
            assertThat(globalGuards).singleElement().satisfies(Consumer {
                isMockKMock(it)
            })
        }
    }
}
