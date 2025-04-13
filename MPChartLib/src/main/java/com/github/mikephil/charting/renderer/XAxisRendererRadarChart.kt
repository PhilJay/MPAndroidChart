package com.github.mikephil.charting.renderer

import android.graphics.Canvas
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler

class XAxisRendererRadarChart(viewPortHandler: ViewPortHandler, xAxis: XAxis, private val chart: RadarChart) : XAxisRenderer(viewPortHandler, xAxis, null) {
    override fun renderAxisLabels(c: Canvas) {
        if (!xAxis.isEnabled || !xAxis.isDrawLabelsEnabled)
            return

        val labelRotationAngleDegrees = xAxis.labelRotationAngle
        val drawLabelAnchor = MPPointF.getInstance(0.5f, 0.25f)

        paintAxisLabels!!.setTypeface(xAxis.typeface)
        paintAxisLabels!!.textSize = xAxis.textSize
        paintAxisLabels!!.color = xAxis.textColor

        val sliceAngle = chart.sliceAngle

        // calculate the factor that is needed for transforming the value to
        // pixels
        val factor = chart.factor

        val center = chart.centerOffsets
        val pOut = MPPointF.getInstance(0f, 0f)
        for (i in 0..<chart.data!!.maxEntryCountSet.entryCount) {
            val label = xAxis.valueFormatter.getFormattedValue(i.toFloat(), xAxis)

            val angle = (sliceAngle * i + chart.rotationAngle) % 360f

            Utils.getPosition(
                center, chart.yRange * factor + xAxis.mLabelWidth / 2f, angle, pOut
            )

            drawLabel(
                c, label, pOut.x, pOut.y - xAxis.mLabelHeight / 2f,
                drawLabelAnchor, labelRotationAngleDegrees
            )
        }

        MPPointF.recycleInstance(center)
        MPPointF.recycleInstance(pOut)
        MPPointF.recycleInstance(drawLabelAnchor)
    }

    /**
     * XAxis LimitLines on RadarChart not yet supported.
     */
    override fun renderLimitLines(c: Canvas) = Unit
}
