package io.justdevit.tools.statemachine

/**
 * Assigns a key-value pair to the given mutable map.
 *
 * @param value The value to be assigned.
 */
context(MutableMap<String, Any>)
infix fun String.to(value: Any) {
    this@MutableMap[this] = value
}
