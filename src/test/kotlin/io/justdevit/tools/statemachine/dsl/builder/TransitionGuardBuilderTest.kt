package io.justdevit.tools.statemachine.dsl.builder

import io.justdevit.tools.statemachine.guard.TransitionGuard
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.mockk.mockk

internal class TransitionGuardBuilderTest :
    FreeSpec(
        {

            "Should be able to create empty guard list" {
                val builder = TransitionGuardBuilder<Any, Any>()

                val result = builder.build()

                result.shouldBeEmpty()
            }

            "Should be able to create guard list" {
                val builder = TransitionGuardBuilder<Any, Any>()
                val guard = mockk<TransitionGuard<Any, Any>>()
                builder.apply { +guard }

                val result = builder.build()

                result shouldHaveSingleElement guard
            }
        },
    )
