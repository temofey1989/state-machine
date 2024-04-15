package io.justdevit.tools.statemachine

import io.justdevit.tools.statemachine.StateMachineTest.OrderEvent.CUSTOMER_CANCELLATION
import io.justdevit.tools.statemachine.StateMachineTest.OrderEvent.DELIVERED
import io.justdevit.tools.statemachine.StateMachineTest.OrderEvent.PAYMENT_ARRIVED
import io.justdevit.tools.statemachine.StateMachineTest.OrderState.CANCELLED
import io.justdevit.tools.statemachine.StateMachineTest.OrderState.DONE
import io.justdevit.tools.statemachine.StateMachineTest.OrderState.IN_PROGRESS
import io.justdevit.tools.statemachine.StateMachineTest.OrderState.NEW
import io.justdevit.tools.statemachine.action.TransitionAction
import io.justdevit.tools.statemachine.dsl.stateMachine
import io.justdevit.tools.statemachine.guard.TransitionGuard
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.util.UUID

internal class StateMachineTest :
    FreeSpec(
        {

            "Should be able to move to done" {
                val order =
                    Order(
                        id = UUID.randomUUID(),
                        state = NEW,
                        price = BigDecimal.valueOf(100),
                    )
                val stateMachine =
                    stateMachine(state = order.state) {
                        initialState = NEW
                        finalStates = setOf(DONE, CANCELLED)

                        globalActions {
                            +LogAction()
                        }
                        globalGuards {
                        }

                        from(NEW) {
                            to(IN_PROGRESS).with(PAYMENT_ARRIVED) {
                                +RejectWrongPaymentAmount()

                                +PersistPayment()
                                +NotifyWarehouse()
                                +UpdateMonitoringDashboard()
                            }
                            to(CANCELLED).with(CUSTOMER_CANCELLATION) {
                                +SendCancellationEmail()
                            }
                        }

                        from(IN_PROGRESS) {
                            to(DONE).with(DELIVERED) {
                                +SendThankYouEmail()
                            }
                            to(CANCELLED).with(CUSTOMER_CANCELLATION) {
                                +SendCancellationEmail()
                            }
                        }
                    }

                stateMachine.sendEvent(PAYMENT_ARRIVED) {
                    "order" to order
                    "payment" to Payment(100.toBigDecimal())
                }
                stateMachine.actualState shouldBe IN_PROGRESS

                stateMachine.sendEvent(DELIVERED)
                stateMachine.actualState shouldBe DONE
            }

            "Should be able to move to done (with key resolvers)" {
                val order =
                    Order(
                        id = UUID.randomUUID(),
                        state = NEW,
                        price = BigDecimal.valueOf(100),
                    )
                val stateMachine =
                    stateMachine<OrderState, OrderEvent>(state = order.state) {
                        initialState = NEW
                        finalStates = setOf(DONE, CANCELLED)

                        eventKeyResolver = { it.name.lowercase() }
                        stateKeyResolver = { it.name.lowercase() }

                        globalActions {
                            +LogAction()
                        }
                        globalGuards {
                        }

                        from(NEW) {
                            to(IN_PROGRESS).with(PAYMENT_ARRIVED) {
                                +RejectWrongPaymentAmount()

                                +PersistPayment()
                                +NotifyWarehouse()
                                +UpdateMonitoringDashboard()
                            }
                            to(CANCELLED).with(CUSTOMER_CANCELLATION) {
                                +SendCancellationEmail()
                            }
                        }

                        from(IN_PROGRESS) {
                            to(DONE).with(DELIVERED) {
                                +SendThankYouEmail()
                            }
                            to(CANCELLED).with(CUSTOMER_CANCELLATION) {
                                +SendCancellationEmail()
                            }
                        }
                    }

                stateMachine.sendEvent(PAYMENT_ARRIVED) {
                    "order" to order
                    "payment" to Payment(100.toBigDecimal())
                }
                stateMachine.actualState shouldBe IN_PROGRESS

                stateMachine.sendEvent(DELIVERED)
                stateMachine.actualState shouldBe DONE
            }
        },
    ) {

    enum class OrderState {
        NEW,
        IN_PROGRESS,
        DONE,
        CANCELLED,
    }

    enum class OrderEvent {
        PAYMENT_ARRIVED,
        DELIVERED,
        CUSTOMER_CANCELLATION,
    }

    data class Order(
        val id: UUID,
        var state: OrderState,
        val price: BigDecimal,
    )

    data class Payment(val amount: BigDecimal)

    class LogAction : TransitionAction<OrderState, OrderEvent> {
        override suspend fun beforeExit(context: TransitionContext<OrderState, OrderEvent>) {
            println("Trying to move from state ${context.sourceState} to ${context.targetState} with ${context.event}.")
        }

        override suspend fun afterEntry(context: TransitionContext<OrderState, OrderEvent>) {
            println("Moved to state ${context.targetState} from ${context.sourceState} with ${context.event}.")
        }
    }

    class PersistPayment : TransitionAction<OrderState, OrderEvent> {
        override suspend fun afterEntry(context: TransitionContext<OrderState, OrderEvent>) {
            val payment = context.parameters["payment"] as Payment
            println("Persisting payment: ${payment.amount}")
        }
    }

    class NotifyWarehouse : TransitionAction<OrderState, OrderEvent> {
        override suspend fun afterEntry(context: TransitionContext<OrderState, OrderEvent>) {
            println("Notification to the warehouse has been sent.")
        }
    }

    class UpdateMonitoringDashboard : TransitionAction<OrderState, OrderEvent> {
        override suspend fun afterEntry(context: TransitionContext<OrderState, OrderEvent>) {
            println("Monitoring dashboard updated.")
        }
    }

    class SendCancellationEmail : TransitionAction<OrderState, OrderEvent> {
        override suspend fun afterEntry(context: TransitionContext<OrderState, OrderEvent>) {
            println("Cancellation email has been sent.")
        }
    }

    class SendThankYouEmail : TransitionAction<OrderState, OrderEvent> {
        override suspend fun afterEntry(context: TransitionContext<OrderState, OrderEvent>) {
            println("Thank You email has been sent.")
        }
    }

    class RejectWrongPaymentAmount : TransitionGuard<OrderState, OrderEvent> {
        override suspend fun onEntry(context: TransitionContext<OrderState, OrderEvent>): Boolean {
            val order = context.parameters["order"] as Order
            val payment = context.parameters["payment"] as Payment
            return order.price == payment.amount
        }
    }
}
