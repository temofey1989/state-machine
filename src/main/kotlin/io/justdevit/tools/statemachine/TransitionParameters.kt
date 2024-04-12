package io.justdevit.tools.statemachine

/**
 * Represents a map of transition parameters.
 *
 * @param parameters Initial map of parameters.
 */
class TransitionParameters(parameters: Map<String, Any> = emptyMap()) : Map<String, Any> by parameters {

    /**
     * Returns the value associated with the specified key in this map.
     * If the key is not found, throws [NoSuchElementException].
     *
     * @param key The key whose associated value is to be returned.
     * @return The value associated with the specified key.
     * @throws NoSuchElementException If the key is not found in the map.
     * @throws ClassCastException If the value is not of type [T].
     */
    inline fun <reified T : Any> value(key: String): T = getOrElse(key) { throw NoSuchElementException("No element for key: $key") } as T

    /**
     * Builder class for creating [TransitionParameters] instances.
     */
    class Builder {
        private val parameters = mutableMapOf<String, Any>()

        /**
         * Adds a parameter to the [TransitionParameters] map.
         *
         * @param key The key for the parameter.
         * @param value The value for the parameter.
         */
        infix fun String.to(value: Any) {
            parameters[this] = value
        }

        /**
         * Creates a new [TransitionParameters] instance based on the current state of the builder.
         *
         * @return A new [TransitionParameters] instance.
         */
        fun build() = TransitionParameters(parameters.toMap())
    }
}
