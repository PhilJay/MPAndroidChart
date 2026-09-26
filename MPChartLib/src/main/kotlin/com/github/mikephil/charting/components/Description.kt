package com.github.mikephil.charting.components

import android.graphics.Paint
import com.github.mikephil.charting.utils.MPPointF

/**
 * A short text drawn on the chart, by default at the bottom right corner of the content area.
 */
public open class Description : ComponentBase() {

    /** The text to draw. Default "Description Label". */
    public var text: String = "Description Label"

    /** Fixed position of the text in px on the chart view, or null to place it at the bottom right using [xOffset] and [yOffset]. */
    public var position: MPPointF? = null

    /** Alignment of the text relative to its position. Default [Paint.Align.RIGHT]. */
    public var textAlign: Paint.Align = Paint.Align.RIGHT

    init {
        textSize = 8f
    }

    /**
     * Places the text at a fixed position on the chart view.
     * @param x x coordinate in px
     * @param y y coordinate of the text baseline in px
     */
    public fun setPosition(x: Float, y: Float) {
        val current = position
        if (current == null) {
            position = MPPointF.getInstance(x, y)
        } else {
            current.x = x
            current.y = y
        }
    }
}
