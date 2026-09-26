package com.github.mikephil.charting.highlight

/**
 * Finds the value to highlight for a touch position. Every chart owns one highlighter and asks it
 * on tap and, if enabled, while dragging.
 *
 * Can be written as a lambda: `IHighlighter { x, y -> ... }`.
 */
public fun interface IHighlighter {

    /**
     * Returns the highlight for the pixel position ([x], [y]), or null if nothing is close enough
     * or the chart has no data.
     */
    public fun getHighlight(x: Float, y: Float): Highlight?
}
