package com.github.mikephil.charting.highlight

import com.github.mikephil.charting.data.DataSet
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.interfaces.datasets.IDataSet
import com.github.mikephil.charting.utils.MPPointD
import kotlin.math.abs

/**
 * Highlighter for the horizontal bar chart, where x values run along the vertical screen axis.
 *
 * Strategy: like [BarHighlighter] with x and y swapped, so only the vertical pixel distance counts and the
 * touched x pixel decides the stack value.
 */
class HorizontalBarHighlighter(chart: BarDataProvider) : BarHighlighter(chart) {

    override fun getHighlight(x: Float, y: Float): Highlight? {
        val barData = chart.barData

        val pos = getValsForTouch(y, x)

        val high = getHighlightForX(pos.y.toFloat(), y, x) ?: return null

        val set = barData?.getDataSetByIndex(high.dataSetIndex)
        if (set != null && set.isStacked) {
            val stacked = getStackedHighlight(high, set, pos.y.toFloat(), pos.x.toFloat())
            MPPointD.recycleInstance(pos)
            return stacked
        }

        MPPointD.recycleInstance(pos)

        return high
    }

    override fun buildHighlights(set: IDataSet<out Entry<*>>, dataSetIndex: Int, xVal: Float, rounding: DataSet.Rounding): List<Highlight> {
        val highlights = mutableListOf<Highlight>()

        var entries: List<Entry<*>> = set.getEntriesForXValue(xVal)
        if (entries.isEmpty()) {
            val closest = set.getEntryForXValue(xVal, Float.NaN, rounding)
            if (closest != null) entries = set.getEntriesForXValue(closest.x)
        }

        if (entries.isEmpty()) return highlights

        for (e in entries) {
            val pixels = chart.getTransformer(set.axisDependency).getPixelForValues(e.y, e.x)
            highlights.add(Highlight(e.x, e.y, pixels.x.toFloat(), pixels.y.toFloat(), dataSetIndex, set.axisDependency))
        }

        return highlights
    }

    /** Only the vertical distance counts for horizontal bars. */
    override fun getDistance(x1: Float, y1: Float, x2: Float, y2: Float): Float = abs(y1 - y2)
}
