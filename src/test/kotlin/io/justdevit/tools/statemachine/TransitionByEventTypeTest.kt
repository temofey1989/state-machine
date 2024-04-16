package io.justdevit.tools.statemachine

import io.justdevit.tools.statemachine.TransitionByEventTypeTest.Event.Started
import io.justdevit.tools.statemachine.TransitionByEventTypeTest.State.FINISHED
import io.justdevit.tools.statemachine.TransitionByEventTypeTest.State.NEW
import io.justdevit.tools.statemachine.TransitionByEventTypeTest.State.STARTED
import io.justdevit.tools.statemachine.dsl.stateMachine
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class TransitionByEventTypeTest :
    FreeSpec(
        {

            "Should be able to provide transition by event type" {
                val stateMachine = stateMachine<State, Event> {
                    initialState = NEW
                    finalStates = setOf(FINISHED)

                    from(NEW) {
                        to(STARTED).with<Started> {
                            afterExit {
                                println("Message: ${event.message}")
                            }
                        }
                    }
                }

                stateMachine.sendEvent(Started("TEST"))

                stateMachine.actualState shouldBe STARTED
            }
        },
    ) {

    enum class State {
        NEW,
        STARTED,
        FINISHED,
    }

    sealed interface Event {
        data class Started(val message: String) : Event

        data object Ended : Event
    }
}
