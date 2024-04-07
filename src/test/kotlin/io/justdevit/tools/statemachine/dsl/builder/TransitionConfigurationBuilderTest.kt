package io.justdevit.tools.statemachine.dsl.builder

import io.justdevit.tools.statemachine.action.TransitionAction
import io.justdevit.tools.statemachine.guard.TransitionGuard
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.mockk.mockk

internal class TransitionConfigurationBuilderTest :
    FreeSpec(
        {

            "Should be able to create empty config" {
                val builder = TransitionConfigurationBuilder<Any, Any>()

                val result = builder.build()

                result.actions.shouldBeEmpty()
                result.guards.shouldBeEmpty()
            }

            "Should be able to create config with action" {
                val builder = TransitionConfigurationBuilder<Any, Any>()
                val action = mockk<TransitionAction<Any, Any>>()
                builder.add(action)

                val result = builder.build()

                result.actions shouldHaveSingleElement action
            }

            "Should be able to create config with guard" {
                val builder = TransitionConfigurationBuilder<Any, Any>()
                val guard = mockk<TransitionGuard<Any, Any>>()
                builder.add(guard)

                val result = builder.build()

                result.guards shouldHaveSingleElement guard
            }

            "Should be able to create config with action and guard" {
                val builder = TransitionConfigurationBuilder<Any, Any>()
                val action = mockk<TransitionAction<Any, Any>>()
                builder.add(action)
                val guard = mockk<TransitionGuard<Any, Any>>()
                builder.add(guard)

                val result = builder.build()

                result.actions shouldHaveSingleElement action
                result.guards shouldHaveSingleElement guard
            }
        },
    )
