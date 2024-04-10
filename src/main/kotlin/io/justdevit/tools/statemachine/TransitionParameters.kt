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
}
