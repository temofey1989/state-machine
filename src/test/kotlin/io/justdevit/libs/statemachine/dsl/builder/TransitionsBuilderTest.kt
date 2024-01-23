package io.justdevit.libs.statemachine.dsl.builder

import io.justdevit.libs.statemachine.Transition
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldHaveSingleElement

internal class TransitionsBuilderTest : FreeSpec({

    "Should be able to create transition" {
        val builder = TransitionsBuilder<String, String>("0").apply {
            to("1").with("E1")
        }

        val result = builder.build()

        result shouldHaveSingleElement Transition(
            sourceState = "0",
            targetState = "1",
            event = "E1",
        )
    }
})
