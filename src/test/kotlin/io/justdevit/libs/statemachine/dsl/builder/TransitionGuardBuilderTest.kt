package io.justdevit.libs.statemachine.dsl.builder

import io.justdevit.libs.statemachine.guard.TransitionGuard
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class TransitionGuardBuilderTest {

    @Test
    fun `Should be able to create empty guard list`() {
        val builder = TransitionGuardBuilder<Any, Any>()

        val result = builder.build()

        assertThat(result).isEmpty()
    }

    @Test
    fun `Should be able to create guard list`() {
        val builder = TransitionGuardBuilder<Any, Any>()
        val guard = mockk<TransitionGuard<Any, Any>>()
        builder.apply { +guard }

        val result = builder.build()

        assertThat(result).containsExactly(guard)
    }
}
