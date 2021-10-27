package com.example.vittles.enums

import com.example.domain.barcode.Invokable

/**
 * Enumerator for navigating to deeper fragments that have an overridden onBackPressed.
 *
 * @property index Index value of the previous fragment.
 */
enum class PreviousFragmentIndex(protected val index: Int) : Invokable {
    /** The product list fragment. */
    PRODUCT_LIST(0) {
        /** {@inheritDoc} */
        override operator fun invoke(): Int = index
    },
    /** The waste report fragment */
    WASTE_REPORT(1) {
        /** {@inheritDoc} */
        override operator fun invoke(): Int = index
    },
    /** The settings fragment */
    SETTINGS(2) {
        /** {@inheritDoc} */
        override operator fun invoke(): Int = index
    }
}