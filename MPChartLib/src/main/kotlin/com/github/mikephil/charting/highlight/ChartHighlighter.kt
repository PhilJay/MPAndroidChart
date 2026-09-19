package com.github.mikephil.charting.highlight

import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarLineScatterCandleBubbleData
import com.github.mikephil.charting.data.DataSet
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.interfaces.dataprovider.BarLineScatterCandleBubbleDataProvider
import com.github.mikephil.charting.interfaces.datasets.IDataSet
import com.github.mikephil.charting.utils.MPPointD
import com.github.mikephil.charting.utils.Utils
import kotlin.math.abs
import kotlin.math.hypot

/**
 * Default highlighter for line, scatter, candle and bubble charts.
 *
 * Strategy: convert the touch to an x value, collect the entry closest to that x value in every
 * data set with highlighting enabled, prefer the y axis whose entries are nearer to the touch, then
 * pick the entry with the smallest pixel distance to the touch. Nothing is selected when that
 * distance exceeds [com.github.mikephil.charting.interfaces.dataprovider.ChartInterface.maxHighlightDistance].
 */
open class ChartHighlighter<T : BarLineScatterCandleBubbleDataProvider>(protected val chart: T) : IHighlighter {

    /** Reused list that collects the candidate highlights of one lookup. */
    protected val highlightBuffer: MutableList<Highlight> = mutableListOf()

    override fun getHighlight(x: Float, y: Float): Highlight? {
        val pos = getValsForTouch(x, y)
        val xVal = pos.x.toFloat()
        MPPointD.recycleInstance(pos)
        return getHighlightForX(xVal, x, y)
    }

    /**
     * Converts the pixel position ([x], [y]) to values using the left axis transformer.
     *
     * @return a point that may be given to [MPPointD.recycleInstance] when done.
     */
    protected fun getValsForTouch(x: Float, y: Float): MPPointD {
        return chart.getTransformer(YAxis.AxisDependency.LEFT).getValuesByTouchPoint(x, y)
    }

    /**
     * Finds the highlight for the x value [xVal] closest to the pixel position ([x], [y]).
     *
     * Picks the y axis whose candidates are nearer to the touch, then the nearest candidate on that axis
     * within the maximum highlight distance.
     */
    protected open fun getHighlightForX(xVal: Float, x: Float, y: Float): Highlight? {
        val closestValues = getHighlightsAtXValue(xVal, x, y)
        if (closestValues.isEmpty()) return null

        val leftAxisMinDist = getMinimumDistance(closestValues, y, YAxis.AxisDependency.LEFT)
        val rightAxisMinDist = getMinimumDistance(closestValues, y, YAxis.AxisDependency.RIGHT)

        val axis = if (leftAxisMinDist < rightAxisMinDist) YAxis.AxisDependency.LEFT else YAxis.AxisDependency.RIGHT

        return getClosestHighlightByPixel(closestValues, x, y, axis, Utils.convertDpToPixel(chart.maxHighlightDistance))
    }

    /**
     * Returns the smallest pixel distance between [pos] and the [getHighlightPos] of the candidates on [axis],
     * or [Float.MAX_VALUE] if none belongs to that axis.
     */
    protected fun getMinimumDistance(closestValues: List<Highlight>, pos: Float, axis: YAxis.AxisDependency): Float {
        var distance = Float.MAX_VALUE
        for (high in closestValues) {
            if (high.axis == axis) {
                val tempDistance = abs(getHighlightPos(high) - pos)
                if (tempDistance < distance) distance = tempDistance
            }
        }
        return distance
    }

    /** The pixel coordinate of [h] used to compare against the touch when choosing the axis; the y pixel here. */
    protected open fun getHighlightPos(h: Highlight): Float = h.yPx

    /**
     * Collects one candidate highlight per entry at [xVal] from every data set with highlighting enabled.
     *
     * @param xVal the touch position converted to an x value.
     * @param x the touch x position in pixels.
     * @param y the touch y position in pixels.
     * @return the shared [highlightBuffer]; empty when the chart has no data.
     */
    protected open fun getHighlightsAtXValue(xVal: Float, x: Float, y: Float): List<Highlight> {
        highlightBuffer.clear()

        val data = data ?: return highlightBuffer

        for (i in 0 until data.dataSetCount) {
            val dataSet = data.getDataSetByIndex(i) ?: continue
            if (!dataSet.isHighlightEnabled) continue
            highlightBuffer.addAll(buildHighlights(dataSet, i, xVal, DataSet.Rounding.CLOSEST))
        }

        return highlightBuffer
    }

    /**
     * Builds a highlight for every entry of [set] at [xVal], or at the closest x value according to [rounding]
     * if there is none exactly at [xVal].
     *
     * @param dataSetIndex index of [set] in the chart data, stored in the highlights.
     * @return an empty list if the set has no entries near [xVal].
     */
    protected open fun buildHighlights(set: IDataSet<out Entry<*>>, dataSetIndex: Int, xVal: Float, rounding: DataSet.Rounding): List<Highlight> {
        val highlights = mutableListOf<Highlight>()

        var entries: List<Entry<*>> = set.getEntriesForXValue(xVal)
        if (entries.isEmpty()) {
            val closest = set.getEntryForXValue(xVal, Float.NaN, rounding)
            if (closest != null) entries = set.getEntriesForXValue(closest.x)
        }

        if (entries.isEmpty()) return highlights

        for (e in entries) {
            val pixels = chart.getTransformer(set.axisDependency).getPixelForValues(e.x, e.y)
            highlights.add(Highlight(e.x, e.y, pixels.x.toFloat(), pixels.y.toFloat(), dataSetIndex, set.axisDependency))
        }

        return highlights
    }

    /**
     * Picks the candidate with the smallest [getDistance] to the pixel position ([x], [y]).
     *
     * @param axis only candidates on this axis are considered; null considers all.
     * @param minSelectionDistance candidates at this pixel distance or further are ignored.
     * @return the closest candidate, or null if none is within [minSelectionDistance].
     */
    fun getClosestHighlightByPixel(closestValues: List<Highlight>, x: Float, y: Float, axis: YAxis.AxisDependency?, minSelectionDistance: Float): Highlight? {
        var closest: Highlight? = null
        var distance = minSelectionDistance

        for (high in closestValues) {
            if (axis == null || high.axis == axis) {
                val cDistance = getDistance(x, y, high.xPx, high.yPx)
                if (cDistance < distance) {
                    closest = high
                    distance = cDistance
                }
            }
        }

        return closest
    }

    /** Distance in pixels between two points; the straight line distance here. */
    protected open fun getDistance(x1: Float, y1: Float, x2: Float, y2: Float): Float = hypot(x1 - x2, y1 - y2)

    /** The chart data the highlights are searched in. */
    protected open val data: BarLineScatterCandleBubbleData<*>?
        get() = chart.data
}
