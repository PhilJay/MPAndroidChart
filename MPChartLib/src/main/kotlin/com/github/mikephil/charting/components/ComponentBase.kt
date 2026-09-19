package com.github.mikephil.charting.components

import android.graphics.Color
import android.graphics.Typeface

/**
 * Settings shared by every chart component that draws text: the axes, the legend, limit lines and the description.
 */
abstract class ComponentBase {

    /** Whether the component is drawn at all. Default true. */
    var isEnabled = true

    /** Horizontal space in dp kept between the component's labels and whatever they sit next to. Default 5. */
    var xOffset = 5f

    /** Vertical space in dp kept between the component's labels and whatever they sit next to. Default 5. */
    var yOffset = 5f

    /** Typeface for the labels, or null for the default typeface. */
    var typeface: Typeface? = null

    /** Label text size in dp, clamped to 6..24. Default 10. */
    var textSize = 10f
        set(value) {
            field = value.coerceIn(6f, 24f)
        }

    /** Label text color. Default black. */
    var textColor = Color.BLACK
}
