package com.example.domain.barcode

/**
 * Interface for invokable enumerators.
 *
 */
interface Invokable {
    /**
     * Returns the assigned status value.
     *
     * @return The status value of the enumerator.
     */
    operator fun invoke(): Any
}
