package io.justdevit.tools.statemachine

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk

internal class TransitionResultTest :
    FreeSpec(
        {
            val transition = mockk<Transition<Any, Any>>()

            "Success result tests" - {

                val result: TransitionResult<Any, Any> = SuccessResult(transition = transition)

                "Should have correct extension attribute" {
                    result.successful shouldBe true
                }

                "Should be able to execute action on success" {
                    var variable = false

                    result.ifSuccess {
                        variable = true
                    }

                    variable shouldBe true
                }

                "Should be able to ignore execute action on rejection" {
                    var variable = false

                    result.ifRejected {
                        variable = true
                    }

                    variable shouldBe false
                }

                "Should be able to ignore execute action on failure" {
                    var variable = false

                    result.ifFailed {
                        variable = true
                    }

                    variable shouldBe false
                }
            }

            "Rejected result tests" - {

                val result: TransitionResult<Any, Any> = RejectedResult(transition = transition, reason = "TEST")

                "Should have correct extension attribute" {
                    result.successful shouldBe false
                }

                "Should be able to execute ignore action on success" {
                    var variable = false

                    result.ifSuccess {
                        variable = true
                    }

                    variable shouldBe false
                }

                "Should be able to execute action on rejection" {
                    var variable = false

                    result.ifRejected {
                        variable = true
                    }

                    variable shouldBe true
                }

                "Should be able to execute ignore action on failure" {
                    var variable = false

                    result.ifFailed {
                        variable = true
                    }

                    variable shouldBe false
                }
            }

            "Failed result tests" - {

                val result: TransitionResult<Any, Any> = FailedResult(transition = transition, exception = IllegalArgumentException("TEST"))

                "Should have correct extension attribute" {
                    result.successful shouldBe false
                }

                "Should be able to execute ignore action on success" {
                    var variable = false

                    result.ifSuccess {
                        variable = true
                    }

                    variable shouldBe false
                }

                "Should be able to execute ignore action on rejection" {
                    var variable = false

                    result.ifRejected {
                        variable = true
                    }

                    variable shouldBe false
                }

                "Should be able to execute action on failure" {
                    var variable = false

                    result.ifFailed {
                        variable = true
                    }

                    variable shouldBe true
                }
            }
        },
    )
