package com.github.mikephil.charting.interfaces.datasets

import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.Fill

/**
 * What the bar chart renderer reads from a data set: fills, borders, shadows and stack information.
 * Implemented by [com.github.mikephil.charting.data.BarDataSet].
 *
 * @param D the type of the payload attached to each entry.
 */
public interface IBarDataSet<D> : IBarLineScatterCandleBubbleDataSet<BarEntry<D>> {

    /** Fills used instead of plain colors, cycled per bar; null or empty to draw with [colors]. */
    public val fills: List<Fill>?

    /** Returns the fill for bar [index], wrapping around [fills], or an empty fill that draws nothing when there are none. */
    public fun getFill(index: Int): Fill

    /** True if the entries hold stacked values, that is [stackSize] is greater than 1. */
    public val isStacked: Boolean

    /** Largest number of stacked values in one entry; 1 for plain bars. */
    public val stackSize: Int

    /** Color of the shadow bar drawn behind each bar when the chart enables bar shadows. */
    public val barShadowColor: Int

    /** Width of the border around each bar in dp. 0 draws no border. */
    public val barBorderWidth: Float

    /** Color of the border around each bar. */
    public val barBorderColor: Int

    /** Opacity of the highlight drawn over a selected bar, from 0 to 255. Default 120. */
    public val highlightAlpha: Int

    /** Legend labels for the stack values, one per stack position. Only used for stacked bars. */
    public val stackLabels: List<String>

    /** Corner radius of the bars in dp. 0 draws sharp corners. Stacked bars round only the segment farthest from zero. */
    public val barCornerRadius: Float

    /** True to give every section of a stacked bar its own rounded corners instead of rounding the bar as a whole. */
    public val isStackSectionsRounded: Boolean
}
