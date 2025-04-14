package com.github.mikephil.charting.highlight

import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.Entry

class PieHighlighter(chart: PieChart) : PieRadarHighlighter<PieChart>(chart) {
    override fun getClosestHighlight(index: Int, x: Float, y: Float): Highlight {
        val set = mChart!!.data!!.dataSet

        val entry: Entry = set.getEntryForIndex(index)

        return Highlight(index.toFloat(), entry.y, x, y, 0, set.axisDependency)
    }
}
