package io.justdevit.tools.statemachine

import io.justdevit.tools.statemachine.TransitionByEventKeyTest.Event.Started
import io.justdevit.tools.statemachine.TransitionByEventKeyTest.State.FINISHED
import io.justdevit.tools.statemachine.TransitionByEventKeyTest.State.NEW
import io.justdevit.tools.statemachine.TransitionByEventKeyTest.State.STARTED
import io.justdevit.tools.statemachine.dsl.stateMachine
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class TransitionByEventKeyTest :
    FreeSpec(
        {

            "Should be able to provide transition by event key" {
                val stateMachine = stateMachine<State, Event> {
                    initialState = NEW
                    finalStates = setOf(FINISHED)
                    eventKeyResolver = { it.key }

                    from(NEW) {
                        to(STARTED).withKey(Started.key)
                    }
                }

                stateMachine.sendEvent(Started)

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
        val key: String

        data object Started : Event {
            override val key: String
                get() = "started"
        }

        data object Ended : Event {
            override val key: String
                get() = "ended"
        }
    }
}
