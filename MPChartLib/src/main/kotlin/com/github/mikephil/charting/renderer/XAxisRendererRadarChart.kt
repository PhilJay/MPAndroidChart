package com.github.mikephil.charting.renderer

import android.graphics.Canvas
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler

/**
 * Draws the x axis labels of a [RadarChart]: one label per entry index at the outer end of its spoke. Grid lines
 * are part of the web drawn by the chart's data renderer, and limit lines are not supported.
 */
public open class XAxisRendererRadarChart(viewPortHandler: ViewPortHandler, xAxis: XAxis, private val chart: RadarChart) :
    XAxisRenderer(viewPortHandler, xAxis, null) {

    /**
     * Draws one label per entry index, formatted from the index, just outside the web at the angle
     * `sliceAngle * index + rotationAngle` degrees clockwise from 3 o'clock.
     */
    override fun renderAxisLabels(c: Canvas) {
        if (!xAxis.isEnabled || !xAxis.isDrawLabelsEnabled) return

        val labelRotationAngleDegrees = xAxis.labelRotationAngle
        val drawLabelAnchor = MPPointF.getInstance(0.5f, 0.25f)

        paintAxisLabels.typeface = xAxis.typeface
        paintAxisLabels.textSize = Utils.convertDpToPixel(xAxis.textSize)
        paintAxisLabels.color = xAxis.textColor

        val sliceangle = chart.sliceAngle
        val factor = chart.factor

        val center = chart.centerOffsets
        val pOut = MPPointF.getInstance(0f, 0f)
        val entryCount = chart.data?.maxEntryCountSet?.entryCount ?: 0
        for (i in 0 until entryCount) {
            val label = xAxis.valueFormatter.getFormattedValue(i.toFloat(), xAxis)

            val angle = (sliceangle * i + chart.rotationAngle) % 360f

            Utils.getPosition(center, chart.yRange * factor + xAxis.labelRotatedWidth / 2f, angle, pOut)

            drawLabel(c, label, pOut.x, pOut.y - xAxis.labelRotatedHeight / 2f, drawLabelAnchor, labelRotationAngleDegrees)
        }

        MPPointF.recycleInstance(center)
        MPPointF.recycleInstance(pOut)
        MPPointF.recycleInstance(drawLabelAnchor)
    }

    /**
     * Does nothing; the x axis of a radar chart has no limit lines.
     */
    override fun renderLimitLines(c: Canvas) {
    }
}
