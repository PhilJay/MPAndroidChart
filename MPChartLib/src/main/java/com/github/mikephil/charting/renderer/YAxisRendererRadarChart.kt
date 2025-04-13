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

class YAxisRendererRadarChart(viewPortHandler: ViewPortHandler, yAxis: YAxis, private val chart: RadarChart) : YAxisRenderer(viewPortHandler, yAxis, null) {

    private val renderLimitLinesPathBuffer = Path()

    override fun computeAxisValues(min: Float, max: Float) {

        val labelCount = axis.labelCount
        val range = abs((max - min).toDouble())

        if (labelCount == 0 || range <= 0 || java.lang.Double.isInfinite(range)) {
            axis.mEntries = floatArrayOf()
            axis.mCenteredEntries = floatArrayOf()
            axis.mEntryCount = 0
            return
        }

        // Find out how much spacing (in y value space) between axis values
        val rawInterval = range / labelCount
        var interval = Utils.roundToNextSignificant(rawInterval).toDouble()

        // If granularity is enabled, then do not allow the interval to go below specified granularity.
        // This is used to avoid repeated values when rounding values for display.
        if (axis.isGranularityEnabled) interval = if (interval < axis.granularity) axis.granularity.toDouble() else interval

        // Normalize interval
        val intervalMagnitude = Utils.roundToNextSignificant(10.0.pow(log10(interval).toInt().toDouble())).toDouble()
        val intervalSigDigit = (interval / intervalMagnitude).toInt()
        if (intervalSigDigit > 5) {
            // Use one order of magnitude higher, to avoid intervals like 0.9 or 90
            // if it's 0.0 after floor(), we use the old value
            interval = if (floor(10.0 * intervalMagnitude) == 0.0)
                interval
            else floor(10.0 * intervalMagnitude)
        }

        val centeringEnabled = axis.isCenterAxisLabelsEnabled
        var n = if (centeringEnabled) 1 else 0

        // force label count
        if (axis.isForceLabelsEnabled) {
            val step = range.toFloat() / (labelCount - 1).toFloat()
            axis.mEntryCount = labelCount

            if (axis.mEntries.size < labelCount) {
                // Ensure stops contains at least numStops elements.
                axis.mEntries = FloatArray(labelCount)
            }

            var v = min

            for (i in 0..<labelCount) {
                axis.mEntries[i] = v
                v += step
            }

            n = labelCount

            // no forced count
        } else {
            var first = if (interval == 0.0) 0.0 else ceil(min / interval) * interval
            if (centeringEnabled) {
                first -= interval
            }

            val last = if (interval == 0.0) 0.0 else Utils.nextUp(floor(max / interval) * interval)

            var f: Double

            if (interval != 0.0) {
                f = first
                while (f <= last) {
                    ++n
                    f += interval
                }
            }

            n++

            axis.mEntryCount = n

            if (axis.mEntries.size < n) {
                // Ensure stops contains at least numStops elements.
                axis.mEntries = FloatArray(n)
            }

            f = first
            var i = 0
            while (i < n) {
                if (f == 0.0)  // Fix for negative zero case (Where value == -0.0, and 0.0 == -0.0)
                    f = 0.0

                axis.mEntries[i] = f.toFloat()
                f += interval
                ++i
            }
        }

        // set decimals
        if (interval < 1) {
            axis.mDecimals = ceil(-log10(interval)).toInt()
        } else {
            axis.mDecimals = 0
        }

        if (centeringEnabled) {
            if (axis.mCenteredEntries.size < n) {
                axis.mCenteredEntries = FloatArray(n)
            }

            val offset = (axis.mEntries[1] - axis.mEntries[0]) / 2f

            for (i in 0..<n) {
                axis.mCenteredEntries[i] = axis.mEntries[i] + offset
            }
        }

        axis.mAxisMinimum = axis.mEntries[0]
        axis.mAxisMaximum = axis.mEntries[n - 1]
        axis.mAxisRange = abs((axis.mAxisMaximum - axis.mAxisMinimum).toDouble()).toFloat()
    }

    override fun renderAxisLabels(c: Canvas) {
        if (!yAxis.isEnabled || !yAxis.isDrawLabelsEnabled) return

        paintAxisLabels!!.setTypeface(yAxis.typeface)
        paintAxisLabels!!.textSize = yAxis.textSize
        paintAxisLabels!!.color = yAxis.textColor

        val center = chart.centerOffsets
        val pOut = MPPointF.getInstance(0f, 0f)
        val factor = chart.factor

        val from = if (yAxis.isDrawBottomYLabelEntryEnabled) 0 else 1
        val to = if (yAxis.isDrawTopYLabelEntryEnabled)
            yAxis.mEntryCount
        else
            (yAxis.mEntryCount - 1)

        val xOffset = yAxis.labelXOffset

        for (j in from..<to) {
            val r = (yAxis.mEntries[j] - yAxis.mAxisMinimum) * factor

            Utils.getPosition(center, r, chart.rotationAngle, pOut)

            val label = yAxis.getFormattedLabel(j)

            c.drawText(label, pOut.x + xOffset, pOut.y, paintAxisLabels!!)
        }
        MPPointF.recycleInstance(center)
        MPPointF.recycleInstance(pOut)
    }

    override fun renderLimitLines(c: Canvas) {
        val limitLines = yAxis.limitLines ?: return

        val sliceAngle = chart.sliceAngle

        // calculate the factor that is needed for transforming the value to
        // pixels
        val factor = chart.factor

        val center = chart.centerOffsets
        val pOut = MPPointF.getInstance(0f, 0f)
        for (i in limitLines.indices) {
            val l = limitLines[i]

            if (!l.isEnabled) continue

            limitLinePaint!!.color = l.lineColor
            limitLinePaint!!.setPathEffect(l.dashPathEffect)
            limitLinePaint!!.strokeWidth = l.lineWidth

            val r = (l.limit - chart.yChartMin) * factor

            val limitPath = renderLimitLinesPathBuffer
            limitPath.reset()


            for (j in 0..<chart.data!!.maxEntryCountSet.entryCount) {
                Utils.getPosition(center, r, sliceAngle * j + chart.rotationAngle, pOut)

                if (j == 0) limitPath.moveTo(pOut.x, pOut.y)
                else limitPath.lineTo(pOut.x, pOut.y)
            }
            limitPath.close()

            c.drawPath(limitPath, limitLinePaint!!)
        }
        MPPointF.recycleInstance(center)
        MPPointF.recycleInstance(pOut)
    }
}
