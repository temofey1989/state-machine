package io.justdevit.libs.statemachine.dsl.builder

import io.justdevit.libs.statemachine.action.TransitionAction
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class TransitionActionBuilderTest {

    @Test
    fun `Should be able to create empty action list`() {
        val builder = TransitionActionBuilder<Any, Any>()

        val result = builder.build()

        assertThat(result).isEmpty()
    }

    @Test
    fun `Should be able to create action list`() {
        val builder = TransitionActionBuilder<Any, Any>()
        val action = mockk<TransitionAction<Any, Any>>()
        builder.apply { +action }

        val result = builder.build()

        assertThat(result).containsExactly(action)
    }
}
