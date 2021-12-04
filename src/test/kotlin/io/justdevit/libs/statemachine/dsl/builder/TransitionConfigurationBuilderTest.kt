package io.justdevit.libs.statemachine.dsl.builder

import io.justdevit.libs.statemachine.action.TransitionAction
import io.justdevit.libs.statemachine.guard.TransitionGuard
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class TransitionConfigurationBuilderTest {

    @Test
    fun `Should be able to create empty config`() {
        val builder = TransitionConfigurationBuilder<Any, Any>()

        val result = builder.build()

        assertThat(result.actions).isEmpty()
        assertThat(result.guards).isEmpty()
    }

    @Test
    fun `Should be able to create config with action`() {
        val builder = TransitionConfigurationBuilder<Any, Any>()
        val action = mockk<TransitionAction<Any, Any>>()
        builder.add(action)

        val result = builder.build()

        assertThat(result.actions).containsExactly(action)
    }

    @Test
    fun `Should be able to create config with guard`() {
        val builder = TransitionConfigurationBuilder<Any, Any>()
        val guard = mockk<TransitionGuard<Any, Any>>()
        builder.add(guard)

        val result = builder.build()

        assertThat(result.guards).containsExactly(guard)
    }

    @Test
    fun `Should be able to create config with action and guard`() {
        val builder = TransitionConfigurationBuilder<Any, Any>()
        val action = mockk<TransitionAction<Any, Any>>()
        builder.add(action)
        val guard = mockk<TransitionGuard<Any, Any>>()
        builder.add(guard)

        val result = builder.build()

        assertThat(result.actions).containsExactly(action)
        assertThat(result.guards).containsExactly(guard)
    }
}
