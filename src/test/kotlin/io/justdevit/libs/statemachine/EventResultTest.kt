package io.justdevit.libs.statemachine

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class EventResultTest {

    @Nested
    inner class SuccessResultTests {

        private val result: EventResult = SuccessResult

        @Test
        fun `Should have correct extension attribute`() {
            assertThat(result.successful).isTrue
        }

        @Test
        fun `Should be able to execute action on success`() {
            var variable = false

            result.ifSuccess {
                variable = true
            }

            assertThat(variable).isTrue
        }

        @Test
        fun `Should be able to ignore execute action on rejection`() {
            var variable = false

            result.ifRejected {
                variable = true
            }

            assertThat(variable).isFalse
        }

        @Test
        fun `Should be able to ignore execute action on failure`() {
            var variable = false

            result.ifFailed {
                variable = true
            }

            assertThat(variable).isFalse
        }
    }

    @Nested
    inner class RejectedResultTests {

        private val result: EventResult = RejectedResult("TEST")

        @Test
        fun `Should have correct extension attribute`() {
            assertThat(result.successful).isFalse
        }

        @Test
        fun `Should be able to execute ignore action on success`() {
            var variable = false

            result.ifSuccess {
                variable = true
            }

            assertThat(variable).isFalse
        }

        @Test
        fun `Should be able to execute action on rejection`() {
            var variable = false

            result.ifRejected {
                variable = true
            }

            assertThat(variable).isTrue
        }

        @Test
        fun `Should be able to execute ignore action on failure`() {
            var variable = false

            result.ifFailed {
                variable = true
            }

            assertThat(variable).isFalse
        }
    }

    @Nested
    inner class FailedResultTests {

        private val result: EventResult = FailedResult(IllegalArgumentException("TEST"))

        @Test
        fun `Should have correct extension attribute`() {
            assertThat(result.successful).isFalse
        }

        @Test
        fun `Should be able to execute ignore action on success`() {
            var variable = false

            result.ifSuccess {
                variable = true
            }

            assertThat(variable).isFalse
        }

        @Test
        fun `Should be able to execute ignore action on rejection`() {
            var variable = false

            result.ifRejected {
                variable = true
            }

            assertThat(variable).isFalse
        }

        @Test
        fun `Should be able to execute action on failure`() {
            var variable = false

            result.ifFailed {
                variable = true
            }

            assertThat(variable).isTrue
        }
    }
}
