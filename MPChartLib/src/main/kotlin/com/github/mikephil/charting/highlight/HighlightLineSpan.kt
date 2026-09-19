package com.github.mikephil.charting.highlight

/**
 * How far a highlight line reaches across the chart. The near edge is the bottom of the content for the vertical
 * line and its left for the horizontal one; the far edge is the top and the right.
 */
enum class HighlightLineSpan {

    /** From one edge of the content to the other, through the entry. The default. */
    FULL,

    /** From the near edge up to the entry, so the line stops where the value is. */
    TO_ENTRY,

    /** From the entry to the far edge, so the line starts where the value is. */
    FROM_ENTRY
}
