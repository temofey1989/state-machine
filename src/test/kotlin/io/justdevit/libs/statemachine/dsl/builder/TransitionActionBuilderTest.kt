package io.justdevit.libs.statemachine.dsl.builder

import io.justdevit.libs.statemachine.action.TransitionAction
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.mockk.mockk

internal class TransitionActionBuilderTest :
    FreeSpec(
        {

            "Should be able to create empty action list" {
                val builder = TransitionActionBuilder<Any, Any>()

                val result = builder.build()

                result.shouldBeEmpty()
            }

            "Should be able to create action list" {
                val builder = TransitionActionBuilder<Any, Any>()
                val action = mockk<TransitionAction<Any, Any>>()
                builder.apply { +action }

                val result = builder.build()

                result shouldHaveSingleElement action
            }
        },
    )
