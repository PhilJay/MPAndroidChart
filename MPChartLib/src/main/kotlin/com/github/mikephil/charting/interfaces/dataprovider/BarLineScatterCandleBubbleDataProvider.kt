package com.github.mikephil.charting.interfaces.dataprovider

import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarLineScatterCandleBubbleData
import com.github.mikephil.charting.utils.Transformer

/**
 * What charts with an x axis and two y axes expose to their renderers and highlighters.
 * Implemented by [com.github.mikephil.charting.charts.BarLineChartBase].
 */
public interface BarLineScatterCandleBubbleDataProvider : ChartInterface {

    /** Returns the transformer that maps values of [axis] to pixels and back. */
    public fun getTransformer(axis: YAxis.AxisDependency): Transformer

    /** True if [axis] is drawn with larger values further down. */
    public fun isInverted(axis: YAxis.AxisDependency): Boolean

    /** Smallest x value currently visible in the content rectangle, at least the axis minimum. */
    public val lowestVisibleX: Float

    /** Largest x value currently visible in the content rectangle, at most the axis maximum. */
    public val highestVisibleX: Float

    /**
     * True while the renderers may drop entries that share a pixel column with another entry. The entries they
     * keep are the first, the lowest, the highest and the last of each column, so peaks survive. It also governs
     * the circles of a line chart, of which only those no other circle would cover are drawn.
     *
     * Off unless a chart turns it on, so nothing is dropped from a drawing by default.
     */
    public val isDecimationEnabled: Boolean get() = false

    override val data: BarLineScatterCandleBubbleData<*>?
}
