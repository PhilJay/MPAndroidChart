package com.github.mikephil.charting.components

/**
 * The horizontal axis of bar, line, scatter, candle and bubble charts, and the outer ring of a radar chart.
 * Its values are the entries' x values, not their indices. Not every setting applies to the radar chart.
 */
open class XAxis : AxisBase() {

    /** Width of the widest label in px, measured by the axis renderer. */
    var labelWidth = 1
        internal set

    /** Height of the labels in px, measured by the axis renderer. */
    var labelHeight = 1
        internal set

    /** Width in px of the label bounds after rotating by [labelRotationAngle], measured by the axis renderer. */
    var labelRotatedWidth = 1
        internal set

    /** Height in px of the label bounds after rotating by [labelRotationAngle], measured by the axis renderer. */
    var labelRotatedHeight = 1
        internal set

    /** Rotation of the labels in degrees, clockwise for positive values. Default 0. */
    var labelRotationAngle = 0f

    /** Whether the first and last label are moved inwards so they are not clipped at the content edge. Default false. */
    var isAvoidFirstLastClippingEnabled = false

    /** Where the labels are drawn relative to the content area. Default [XAxisPosition.TOP]. */
    var position = XAxisPosition.TOP

    /**
     * Placement of the x-axis labels: TOP above the content area, BOTTOM below it, BOTH_SIDED above and below,
     * TOP_INSIDE and BOTTOM_INSIDE inside the content area along its top or bottom edge.
     */
    enum class XAxisPosition {
        TOP, BOTTOM, BOTH_SIDED, TOP_INSIDE, BOTTOM_INSIDE
    }

    init {
        yOffset = 4f
    }
}
