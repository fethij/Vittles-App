package com.example.vittles.enums

/**
 * Enumerator that holds the way items can be deleted
 *
 */
enum class DeleteType {
    /** Indicates that a product has been eaten. */
    EATEN,
    /** Indicates that a product has been thrown away. */
    THROWN_AWAY,
    /** Indicates that a product has been removed. */
    REMOVED
}