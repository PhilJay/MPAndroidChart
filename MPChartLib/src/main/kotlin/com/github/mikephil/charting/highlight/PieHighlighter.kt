package com.github.mikephil.charting.highlight

import com.github.mikephil.charting.charts.PieChart

/**
 * Highlighter for the pie chart. A pie chart has one data set, so the slice index found by
 * [PieRadarHighlighter] is the whole answer; the highlight carries the slice index as x, the slice value as y
 * and the touch position as pixel position.
 */
public class PieHighlighter(chart: PieChart) : PieRadarHighlighter<PieChart>(chart) {

    override fun getClosestHighlight(index: Int, x: Float, y: Float): Highlight? {
        val set = chart.data?.dataSet ?: return null
        if (index !in 0 until set.entryCount) return null
        val entry = set.getEntryForIndex(index)
        return Highlight(index.toFloat(), entry.y, x, y, 0, set.axisDependency)
    }
}
