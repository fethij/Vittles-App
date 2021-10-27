package com.example.vittles.enums

import com.example.vittles.R

/**
 * Enumerator for indication colors on the main products activity.
 *
 *
 * @property value Value of the color which is a color from the resources.
 */
enum class IndicationColor(val value: Int) {
    /** Indicates the color red. */
    RED(R.color.red),
    /** Indicates the color yellow. */
    YELLOW(R.color.yellow),
    /** Indicates the color green. */
    GREEN(R.color.green)
}