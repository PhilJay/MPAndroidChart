package com.github.mikephil.charting.highlight

import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Utils
import kotlin.math.abs

/**
 * Highlighter for the radar chart.
 *
 * Strategy: the touch angle picks the entry index on the web, then among all data sets the entry whose
 * distance from the center is closest to the touch distance wins. The highlight carries the entry index
 * as x and the entry value as y.
 */
class RadarHighlighter(chart: RadarChart) : PieRadarHighlighter<RadarChart>(chart) {

    override fun getClosestHighlight(index: Int, x: Float, y: Float): Highlight? {
        val highlights = getHighlightsAtIndex(index)

        val distanceToCenter = chart.distanceToCenter(x, y) / chart.factor

        var closest: Highlight? = null
        var distance = Float.MAX_VALUE

        for (high in highlights) {
            val cdistance = abs(high.y - distanceToCenter)
            if (cdistance < distance) {
                closest = high
                distance = cdistance
            }
        }

        return closest
    }

    /**
     * Builds one highlight per data set for the entry at [index], with the pixel position of that entry on
     * the web including the current animation phase.
     *
     * @return the shared [highlightBuffer]; empty when the chart has no data.
     */
    protected fun getHighlightsAtIndex(index: Int): List<Highlight> {
        highlightBuffer.clear()

        val data = chart.data ?: return highlightBuffer

        val phaseX = chart.animator.phaseX
        val phaseY = chart.animator.phaseY
        val sliceangle = chart.sliceAngle
        val factor = chart.factor

        val pOut = MPPointF.getInstance(0f, 0f)
        for (i in 0 until data.dataSetCount) {
            val dataSet = data.getDataSetByIndex(i) ?: continue
            val entry = dataSet.getEntryForIndex(index)

            val y = entry.y - chart.yChartMin

            Utils.getPosition(chart.centerOffsets, y * factor * phaseY, sliceangle * index * phaseX + chart.rotationAngle, pOut)

            highlightBuffer.add(Highlight(index.toFloat(), entry.y, pOut.x, pOut.y, i, dataSet.axisDependency))
        }

        return highlightBuffer
    }
}
