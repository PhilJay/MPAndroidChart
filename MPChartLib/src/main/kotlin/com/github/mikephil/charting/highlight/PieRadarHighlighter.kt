package com.github.mikephil.charting.highlight

import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.charts.PieRadarChartBase

/**
 * Shared highlighter logic for pie and radar charts.
 *
 * Strategy: touches outside the chart radius select nothing. Otherwise the angle of the touch around the
 * center, measured clockwise from 3 o'clock and corrected for the running animation on pie charts, is turned
 * into an entry index and [getClosestHighlight] decides which data set to pick at that index.
 */
abstract class PieRadarHighlighter<T : PieRadarChartBase<*>>(protected val chart: T) : IHighlighter {

    /** Reused list that collects the candidate highlights of one lookup. */
    protected val highlightBuffer: MutableList<Highlight> = mutableListOf()

    override fun getHighlight(x: Float, y: Float): Highlight? {
        val touchDistanceToCenter = chart.distanceToCenter(x, y)

        if (touchDistanceToCenter > chart.radius) return null

        var angle = chart.getAngleForPoint(x, y)

        if (chart is PieChart) {
            angle /= chart.animator.phaseY
        }

        val index = chart.getIndexForAngle(angle)

        val entryCount = chart.data?.maxEntryCountSet?.entryCount ?: 0
        if (index < 0 || index >= entryCount) return null

        return getClosestHighlight(index, x, y)
    }

    /**
     * Returns the highlight for the entry [index] closest to the pixel position ([x], [y]), or null if
     * the chart has no data.
     */
    protected abstract fun getClosestHighlight(index: Int, x: Float, y: Float): Highlight?
}
