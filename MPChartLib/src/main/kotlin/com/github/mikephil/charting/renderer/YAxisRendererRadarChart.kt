package com.github.mikephil.charting.renderer

import android.graphics.Canvas
import android.graphics.Path
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

/**
 * Draws the y axis of a [RadarChart]: the label values along the spoke at the chart's rotation angle, and limit
 * lines as closed polygons around the web. Grid lines and the axis line are part of the web drawn by the chart's
 * data renderer.
 */
open class YAxisRendererRadarChart(viewPortHandler: ViewPortHandler, yAxis: YAxis, private val chart: RadarChart) :
    YAxisRenderer(viewPortHandler, yAxis, null) {

    /**
     * Like [AxisRenderer.computeAxisValues], but always adds one more entry after the last interval and applies the
     * first and last entry as the axis range, so the web ends exactly on a label.
     */
    override fun computeAxisValues(min: Float, max: Float) {
        val yMin = min
        val yMax = max

        val labelCount = axis.labelCount
        val range = abs(yMax - yMin).toDouble()

        if (labelCount == 0 || range <= 0 || range.isInfinite()) {
            axis.entries = FloatArray(0)
            axis.centeredEntries = FloatArray(0)
            axis.entryCount = 0
            return
        }

        val rawInterval = range / labelCount
        var interval = Utils.roundToNextSignificant(rawInterval).toDouble()

        if (axis.isGranularityEnabled) {
            interval = if (interval < axis.granularity) axis.granularity.toDouble() else interval
        }

        val intervalMagnitude = Utils.roundToNextSignificant(10.0.pow(log10(interval).toInt())).toDouble()
        val intervalSigDigit = (interval / intervalMagnitude).toInt()
        if (intervalSigDigit > 5) {
            interval = if (floor(10.0 * intervalMagnitude) == 0.0) interval else floor(10.0 * intervalMagnitude)
        }

        val centeringEnabled = axis.isCenterAxisLabelsEnabled
        var n = if (centeringEnabled) 1 else 0

        if (axis.isForceLabelsEnabled) {
            val step = range.toFloat() / (labelCount - 1).toFloat()
            axis.entryCount = labelCount

            if (axis.entries.size < labelCount) {
                axis.entries = FloatArray(labelCount)
            }

            var v = min
            for (i in 0 until labelCount) {
                axis.entries[i] = v
                v += step
            }

            n = labelCount
        } else {
            var first = if (interval == 0.0) 0.0 else ceil(yMin / interval) * interval
            if (centeringEnabled) {
                first -= interval
            }

            val last = if (interval == 0.0) 0.0 else Utils.nextUp(floor(yMax / interval) * interval)

            if (interval != 0.0) {
                var f = first
                while (f <= last) {
                    ++n
                    f += interval
                }
            }

            n++

            axis.entryCount = n

            if (axis.entries.size < n) {
                axis.entries = FloatArray(n)
            }

            var f = first
            for (i in 0 until n) {
                if (f == 0.0) f = 0.0
                axis.entries[i] = f.toFloat()
                f += interval
            }
        }

        axis.decimals = if (interval < 1) ceil(-log10(interval)).toInt() else 0

        if (centeringEnabled) {
            if (axis.centeredEntries.size < n) {
                axis.centeredEntries = FloatArray(n)
            }

            val offset = (axis.entries[1] - axis.entries[0]) / 2f
            for (i in 0 until n) {
                axis.centeredEntries[i] = axis.entries[i] + offset
            }
        }

        axis.applyAxisRange(axis.entries[0], axis.entries[n - 1])
    }

    /**
     * Draws the labels along the spoke at the chart's rotation angle, each at the distance from the center that
     * matches its value.
     */
    override fun renderAxisLabels(c: Canvas) {
        if (!yAxis.isEnabled || !yAxis.isDrawLabelsEnabled) return

        paintAxisLabels.typeface = yAxis.typeface
        paintAxisLabels.textSize = Utils.convertDpToPixel(yAxis.textSize)
        paintAxisLabels.color = yAxis.textColor

        val center = chart.centerOffsets
        val pOut = MPPointF.getInstance(0f, 0f)
        val factor = chart.factor

        val from = if (yAxis.isDrawBottomYLabelEntryEnabled) 0 else 1
        val to = if (yAxis.isDrawTopYLabelEntryEnabled) yAxis.entryCount else yAxis.entryCount - 1

        val xOffset = Utils.convertDpToPixel(yAxis.labelXOffset)

        for (j in from until to) {
            val r = (yAxis.entries[j] - yAxis.axisMinimum) * factor

            Utils.getPosition(center, r, chart.rotationAngle, pOut)

            val label = yAxis.getFormattedLabel(j)

            c.drawText(label, pOut.x + xOffset, pOut.y, paintAxisLabels)
        }
        MPPointF.recycleInstance(center)
        MPPointF.recycleInstance(pOut)
    }

    private val renderLimitLinesPathBuffer = Path()

    /**
     * Draws each enabled limit line as a closed polygon through all spokes at the radius of its limit value. Limit
     * line labels are not drawn.
     */
    override fun renderLimitLines(c: Canvas) {
        val limitLines = yAxis.limitLines

        val sliceangle = chart.sliceAngle
        val factor = chart.factor

        val center = chart.centerOffsets
        val pOut = MPPointF.getInstance(0f, 0f)
        val entryCount = chart.data?.maxEntryCountSet?.entryCount ?: 0
        for (l in limitLines) {
            if (!l.isEnabled) continue

            limitLinePaint.color = l.lineColor
            limitLinePaint.pathEffect = l.dashPathEffect
            limitLinePaint.strokeWidth = Utils.convertDpToPixel(l.lineWidth)

            val r = (l.limit - chart.yChartMin) * factor

            val limitPath = renderLimitLinesPathBuffer
            limitPath.reset()

            for (j in 0 until entryCount) {
                Utils.getPosition(center, r, sliceangle * j + chart.rotationAngle, pOut)

                if (j == 0) limitPath.moveTo(pOut.x, pOut.y) else limitPath.lineTo(pOut.x, pOut.y)
            }
            limitPath.close()

            c.drawPath(limitPath, limitLinePaint)
        }
        MPPointF.recycleInstance(center)
        MPPointF.recycleInstance(pOut)
    }
}
