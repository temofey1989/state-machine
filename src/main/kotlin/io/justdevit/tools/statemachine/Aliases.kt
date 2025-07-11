package io.justdevit.tools.statemachine

/**
 * Represents a function that resolves a state key based on a given state.
 *
 * @param S The type of the state.
 * @return The resolved state key.
 */
typealias StateKeyResolver<S> = (S) -> Any

/**
 * Represents a function that resolves an event key based on a given event.
 *
 * @param E The type of the event.
 * @return The resolved event key.
 */
typealias EventKeyResolver<E> = (E) -> Any
