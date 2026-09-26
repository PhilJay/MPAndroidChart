package com.github.mikephil.charting.highlight

import com.github.mikephil.charting.data.BarLineScatterCandleBubbleData
import com.github.mikephil.charting.data.DataSet
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.utils.MPPointD
import kotlin.math.abs
import kotlin.math.max

/**
 * Highlighter for the bar chart.
 *
 * Strategy: like [ChartHighlighter], but only the horizontal pixel distance counts, so a touch anywhere
 * above or below a bar selects it. For stacked bars the touched y value decides which stack value is
 * selected and is stored in [Highlight.stackIndex].
 */
public open class BarHighlighter(chart: BarDataProvider) : ChartHighlighter<BarDataProvider>(chart) {

    override fun getHighlight(x: Float, y: Float): Highlight? {
        val high = super.getHighlight(x, y) ?: return null

        val pos = getValsForTouch(x, y)

        val barData = chart.barData
        val set = barData?.getDataSetByIndex(high.dataSetIndex)
        if (set != null && set.isStacked) {
            val stacked = getStackedHighlight(high, set, pos.x.toFloat(), pos.y.toFloat())
            MPPointD.recycleInstance(pos)
            return stacked
        }

        MPPointD.recycleInstance(pos)

        return high
    }

    /**
     * Turns [high] into a highlight of the stack value that contains [yVal].
     *
     * @param set the stacked data set [high] belongs to.
     * @param xVal the touch x value.
     * @param yVal the touch y value.
     * @return [high] unchanged if the entry has no stack values, a new highlight with the stack index and the
     *   pixel position of the top of that stack value, or null if no entry or ranges are found.
     */
    public fun getStackedHighlight(high: Highlight, set: IBarDataSet<*>, xVal: Float, yVal: Float): Highlight? {
        val entry = set.getEntryForXValue(xVal, yVal, DataSet.Rounding.CLOSEST) ?: return null

        if (entry.stackValues == null) return high

        val ranges = entry.ranges ?: return null
        if (ranges.isEmpty()) return null

        val stackIndex = getClosestStackIndex(ranges, yVal)

        val pixels = chart.getTransformer(set.axisDependency).getPixelForValues(high.x, ranges[stackIndex].to)

        val stackedHigh = Highlight(
            entry.x,
            entry.y,
            pixels.x.toFloat(),
            pixels.y.toFloat(),
            high.dataSetIndex,
            stackIndex,
            high.axis
        )

        MPPointD.recycleInstance(pixels)

        return stackedHigh
    }

    /**
     * Returns the index of the range in [ranges] that contains [value]. Values beyond the last range give
     * the last index, values below the first range and empty or null lists give 0.
     */
    protected fun getClosestStackIndex(ranges: List<Range>?, value: Float): Int {
        if (ranges == null || ranges.isEmpty()) return 0

        var stackIndex = 0
        for (range in ranges) {
            if (range.contains(value)) return stackIndex
            stackIndex++
        }

        val length = max(ranges.size - 1, 0)
        return if (value > ranges[length].to) length else 0
    }

    /** Only the horizontal distance counts for bars. */
    override fun getDistance(x1: Float, y1: Float, x2: Float, y2: Float): Float = abs(x1 - x2)

    override val data: BarLineScatterCandleBubbleData<*>?
        get() = chart.barData
}
