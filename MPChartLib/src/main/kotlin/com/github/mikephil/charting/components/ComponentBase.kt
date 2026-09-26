package com.github.mikephil.charting.components

import android.graphics.Color
import android.graphics.Typeface

/**
 * Settings shared by every chart component that draws text: the axes, the legend, limit lines and the description.
 */
public abstract class ComponentBase {

    /** Whether the component is drawn at all. Default true. */
    public var isEnabled: Boolean = true

    /** Horizontal space in dp kept between the component's labels and whatever they sit next to. Default 5. */
    public var xOffset: Float = 5f

    /** Vertical space in dp kept between the component's labels and whatever they sit next to. Default 5. */
    public var yOffset: Float = 5f

    /** Typeface for the labels, or null for the default typeface. */
    public var typeface: Typeface? = null

    /** Label text size in dp, clamped to 6..24. Default 10. */
    public var textSize: Float = 10f
        set(value) {
            field = value.coerceIn(6f, 24f)
        }

    /** Label text color. Default black. */
    public var textColor: Int = Color.BLACK
}
