package io.justdevit.libs.statemachine.dsl.builder

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.function.Consumer

internal class TransitionsBuilderTest {

    @Test
    fun `Should be able to create transition`() {
        val builder = TransitionsBuilder<String, String>("0").apply {
            to("1").with("E1")
        }

        val result = builder.build()

        assertThat(result).singleElement().satisfies(Consumer {
            assertThat(it.sourceState).isEqualTo("0")
            assertThat(it.targetState).isEqualTo("1")
            assertThat(it.event).isEqualTo("E1")
            assertThat(it.config.actions).isEmpty()
            assertThat(it.config.guards).isEmpty()
        })
    }
}
