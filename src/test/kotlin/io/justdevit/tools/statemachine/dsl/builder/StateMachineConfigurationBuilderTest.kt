package io.justdevit.tools.statemachine.dsl.builder

import io.justdevit.tools.statemachine.Transition
import io.justdevit.tools.statemachine.TransitionConfiguration
import io.justdevit.tools.statemachine.action.TransitionAction
import io.justdevit.tools.statemachine.guard.FinalStateGuard
import io.justdevit.tools.statemachine.guard.TransitionGuard
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.instanceOf
import io.mockk.mockk
import org.junit.jupiter.api.assertThrows
import java.util.UUID.randomUUID

internal class StateMachineConfigurationBuilderTest :
    FreeSpec(
        {

            "Should reject on missed initial state" {
                val builder = StateMachineConfigurationBuilder<Any, Any>()
                builder.finalStates = setOf(2)

                assertThrows<IllegalStateException> { builder.build() }
            }

            "Should reject on missed final state" {
                val builder = StateMachineConfigurationBuilder<Any, Any>()
                builder.initialState = 1

                assertThrows<IllegalStateException> { builder.build() }
            }

            "Should be able to create state machine configuration" {
                val transitionAction = mockk<TransitionAction<String, String>>()
                val transitionGuard = mockk<TransitionGuard<String, String>>()
                val builder =
                    StateMachineConfigurationBuilder<String, String>().apply {
                        id = randomUUID()
                        initialState = "S1"
                        finalStates = setOf("S2")
                        from("S1") {
                            to("S2").with("TEST")
                        }
                        globalActions {
                            +transitionAction
                        }
                        globalGuards {
                            +transitionGuard
                        }
                    }

                val result = builder.build()

                with(result) {
                    id shouldBe builder.id
                    initialState shouldBe builder.initialState
                    finalStates shouldBe builder.finalStates
                    transitions shouldHaveSingleElement
                        Transition(
                            sourceState = "S1",
                            targetState = "S2",
                            event = "TEST",
                            config = TransitionConfiguration(),
                        )
                    globalActions shouldHaveSingleElement transitionAction
                    globalGuards shouldHaveSize 2
                    globalGuards[0] shouldBe instanceOf<FinalStateGuard<String, String>>()
                    globalGuards[1] shouldBe transitionGuard
                }
            }
        },
    )
