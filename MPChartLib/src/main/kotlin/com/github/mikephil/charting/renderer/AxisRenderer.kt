package com.github.mikephil.charting.renderer

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.utils.MPPointD
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

/**
 * Base class of the axis renderers. Computes the label values of an axis from its visible range and draws its
 * labels, grid lines, axis line and limit lines.
 *
 * On every draw pass the chart calls [computeAxis] first, then [renderAxisLine], [renderGridLines] and
 * [renderLimitLines] (each either before or after the data, depending on the axis settings), and finally
 * [renderAxisLabels]. Subclass a concrete renderer and assign it to the chart's axis renderer slot
 * (`rendererXAxis`, `rendererLeftYAxis`, `rendererRightYAxis`) to change how an axis is drawn.
 *
 * @property transformer Converts axis values to pixels and back; null for the radar chart axes, which are
 * placed by the chart instead.
 * @property axis The axis this renderer draws.
 */
public abstract class AxisRenderer(viewPortHandler: ViewPortHandler, public val transformer: Transformer?, protected val axis: AxisBase) : Renderer(viewPortHandler) {

    /**
     * Paint for the axis labels; typeface, text size (dp) and color are copied from the axis on every draw.
     */
    public val paintAxisLabels: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    /**
     * Paint for the grid lines; color, line width (dp) and dash effect are copied from the axis on every draw.
     */
    public val paintGrid: Paint = Paint().apply {
        color = Color.GRAY
        strokeWidth = 1f
        style = Paint.Style.STROKE
        alpha = 90
    }

    /**
     * Paint for the axis line; color and line width (dp) are copied from the axis on every draw.
     */
    public val paintAxisLine: Paint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 1f
        style = Paint.Style.STROKE
    }

    /**
     * Paint for the limit lines and their labels; restyled per [com.github.mikephil.charting.components.LimitLine]
     * while drawing.
     */
    protected val limitLinePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }

    /** Reused for the value at the top edge of the content rectangle, so a draw pass allocates nothing. */
    protected val computeAxisPoint1: MPPointD = MPPointD(0.0, 0.0)

    /** Reused for the value at the bottom edge of the content rectangle. */
    protected val computeAxisPoint2: MPPointD = MPPointD(0.0, 0.0)

    /**
     * Computes the label values of the axis for the value range [min] to [max]. When the chart is zoomed in, the
     * range is replaced by the values at the top and bottom edge of the content rectangle so that labels are only
     * computed for what is visible.
     *
     * @param inverted true when the axis runs in the opposite direction, which swaps the edge that holds the minimum
     */
    public open fun computeAxis(min: Float, max: Float, inverted: Boolean) {
        var newMin = min
        var newMax = max

        val transformer = transformer
        if (transformer != null && viewPortHandler.contentWidth > 10 && !viewPortHandler.isFullyZoomedOutY) {
            val p1 = computeAxisPoint1
            val p2 = computeAxisPoint2
            transformer.getValuesByTouchPoint(viewPortHandler.contentLeft, viewPortHandler.contentTop, p1)
            transformer.getValuesByTouchPoint(viewPortHandler.contentLeft, viewPortHandler.contentBottom, p2)

            if (!inverted) {
                newMin = p2.y.toFloat()
                newMax = p1.y.toFloat()
            } else {
                newMin = p1.y.toFloat()
                newMax = p2.y.toFloat()
            }
        }

        computeAxisValues(newMin, newMax)
    }

    /**
     * Fills the axis with evenly spaced label values between [min] and [max] (axis values) and stores them in its
     * `entries`, `entryCount`, `centeredEntries` and `decimals`. Honors the label count, granularity, forced label
     * count, centered labels and [minimumInterval]. Leaves the axis without entries when the range is zero or
     * infinite.
     */
    protected open fun computeAxisValues(min: Float, max: Float) {
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

        val minInterval = minimumInterval(range)
        if (interval < minInterval) {
            val magnitude = Utils.roundToNextSignificant(10.0.pow(log10(minInterval).toInt())).toDouble()
            val rounded = Utils.roundToNextSignificant(minInterval).toDouble()
            // Rounding to a significant digit must not fall back below the minimum, so round up in that case.
            interval = if (rounded < minInterval) ((minInterval / magnitude).toInt() + 1) * magnitude else rounded
        }

        val intervalMagnitude = Utils.roundToNextSignificant(10.0.pow(log10(interval).toInt())).toDouble()
        val intervalSigDigit = (interval / intervalMagnitude).toInt()
        if (intervalSigDigit > 5) {
            interval = if (floor(10.0 * intervalMagnitude) == 0.0) interval else floor(10.0 * intervalMagnitude)
        }

        var n = if (axis.isCenterAxisLabelsEnabled) 1 else 0

        if (axis.isForceLabelsEnabled) {
            interval = (range.toFloat() / (labelCount - 1).toFloat()).toDouble()

            var forcedCount = labelCount
            if (axis.isGranularityEnabled && interval < axis.granularity) {
                interval = axis.granularity.toDouble()
                forcedCount = (range / interval).toInt() + 1
            }

            axis.entryCount = forcedCount

            if (axis.entries.size < forcedCount) {
                axis.entries = FloatArray(forcedCount)
            }

            var v = min
            for (i in 0 until forcedCount) {
                axis.entries[i] = v
                v += interval.toFloat()
            }

            n = forcedCount
        } else {
            var first = if (interval == 0.0) 0.0 else ceil(yMin / interval) * interval
            if (axis.isCenterAxisLabelsEnabled) {
                first -= interval
            }

            val last = if (interval == 0.0) 0.0 else Utils.nextUp(floor(yMax / interval) * interval)

            if (interval != 0.0 && last != first) {
                var f = first
                while (f <= last) {
                    ++n
                    f += interval
                }
            } else if (last == first && n == 0) {
                n = 1
            }

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

        if (axis.isCenterAxisLabelsEnabled) {
            if (axis.centeredEntries.size < n) {
                axis.centeredEntries = FloatArray(n)
            }

            val offset = interval.toFloat() / 2f
            for (i in 0 until n) {
                axis.centeredEntries[i] = axis.entries[i] + offset
            }
        }
    }

    /**
     * Smallest interval in axis values the labels may use, so that neighbouring labels do not overlap.
     * 0, the default, places no limit.
     */
    protected open fun minimumInterval(range: Double): Double = 0.0

    /**
     * Draws the axis labels onto [c]. Does nothing when the axis or its labels are disabled.
     */
    public abstract fun renderAxisLabels(c: Canvas)

    /**
     * Draws the grid lines onto [c]. Does nothing when the axis or its grid lines are disabled.
     */
    public abstract fun renderGridLines(c: Canvas)

    /**
     * Draws the axis line onto [c]. Does nothing when the axis or its line is disabled.
     */
    public abstract fun renderAxisLine(c: Canvas)

    /**
     * Draws the enabled limit lines of the axis and their labels onto [c].
     */
    public abstract fun renderLimitLines(c: Canvas)
}
