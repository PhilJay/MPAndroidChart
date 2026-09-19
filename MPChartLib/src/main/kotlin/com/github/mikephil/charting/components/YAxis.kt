package com.github.mikephil.charting.components

import android.graphics.Color
import android.graphics.Paint
import com.github.mikephil.charting.utils.Utils
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * A vertical axis of bar, line, scatter, candle and bubble charts, and the value axis of a radar chart. Charts have a
 * left and a right one; each data set picks its side with [AxisDependency]. Settings that change the value range must
 * be applied before data is set.
 * @property axisDependency the side of the chart this axis belongs to.
 */
open class YAxis(val axisDependency: AxisDependency = AxisDependency.LEFT) : AxisBase() {

    /** Whether the label at the bottom end of the axis is drawn. Default true. */
    var isDrawBottomYLabelEntryEnabled = true

    /** Whether the label at the top end of the axis is drawn. Disable it when it collides with an x-axis label. Default true. */
    var isDrawTopYLabelEntryEnabled = true

    /** Whether small values are at the top and large values at the bottom. Default false. */
    var isInverted = false

    /** Whether a line is drawn at value 0 even when grid lines are disabled. Default false. */
    var isDrawZeroLineEnabled = false

    /** Color of the zero line. Default gray. */
    var zeroLineColor = Color.GRAY

    /** Width of the zero line in dp. Default 0.5. */
    var zeroLineWidth = 0.5f

    /** Space above the largest value as a percentage of the data range. Ignored when [axisMaximum] is fixed. Default 10. */
    var spaceTop = 10f

    /** Space below the smallest value as a percentage of the data range. Ignored when [axisMinimum] is fixed. Default 10. */
    var spaceBottom = 10f

    /** Whether the labels are drawn next to or inside the content area. Default [YAxisLabelPosition.OUTSIDE_CHART]. */
    var labelPosition = YAxisLabelPosition.OUTSIDE_CHART

    /** Horizontal shift in dp added to the label positions. Default 0. */
    var labelXOffset = 0f

    /** Smallest width in dp the chart reserves for this axis. Default 0. */
    var minWidth = 0f

    /** Largest width in dp the chart reserves for this axis. Default [Float.POSITIVE_INFINITY], no limit. */
    var maxWidth = Float.POSITIVE_INFINITY

    /** Placement of the y-axis labels: OUTSIDE_CHART next to the content area, INSIDE_CHART over it. */
    enum class YAxisLabelPosition {
        OUTSIDE_CHART, INSIDE_CHART
    }

    /** The y-axis a data set is plotted against: the LEFT or the RIGHT one. */
    enum class AxisDependency {
        LEFT, RIGHT
    }

    init {
        yOffset = 0f
    }

    /**
     * Width in px this axis needs in a vertical chart: the widest label plus [xOffset] on both sides, clamped to
     * [minWidth]..[maxWidth].
     * @param p paint used to measure the labels; its text size is set to [textSize]
     */
    fun getRequiredWidthSpace(p: Paint): Float {
        p.textSize = Utils.convertDpToPixel(textSize)

        val label = longestLabel
        var width = Utils.calcTextWidth(p, label).toFloat() + Utils.convertDpToPixel(xOffset) * 2f

        var minWidth = minWidth
        var maxWidth = maxWidth

        if (minWidth > 0f) minWidth = Utils.convertDpToPixel(minWidth)
        if (maxWidth > 0f && maxWidth != Float.POSITIVE_INFINITY) maxWidth = Utils.convertDpToPixel(maxWidth)

        width = max(minWidth, min(width, if (maxWidth > 0.0) maxWidth else width))
        return width
    }

    /**
     * Height in px this axis needs in a horizontal bar chart: the label height plus [yOffset] on both sides.
     * @param p paint used to measure the labels; its text size is set to [textSize]
     */
    fun getRequiredHeightSpace(p: Paint): Float {
        p.textSize = Utils.convertDpToPixel(textSize)
        val label = longestLabel
        return Utils.calcTextHeight(p, label).toFloat() + Utils.convertDpToPixel(yOffset) * 2f
    }

    /** Whether the chart must reserve space next to the content area: enabled, drawing labels, and labels outside. */
    val needsOffset: Boolean
        get() = isEnabled && isDrawLabelsEnabled && labelPosition == YAxisLabelPosition.OUTSIDE_CHART

    /**
     * Computes the axis range from the data range. Fixed limits are kept, computed ones get [spaceBottom] and
     * [spaceTop] percent of the range added, an empty range is widened around the value, and when the data lies
     * entirely beyond one fixed limit the other end is derived from that limit.
     */
    override fun calculate(dataMin: Float, dataMax: Float) {
        var min = dataMin
        var max = dataMax

        if (min > max) {
            if (isAxisMaxCustom && isAxisMinCustom) {
                val t = min
                min = max
                max = t
            } else if (isAxisMaxCustom) {
                min = if (max < 0f) max * 1.5f else max * 0.5f
            } else if (isAxisMinCustom) {
                max = if (min < 0f) min * 0.5f else min * 1.5f
            }
        }

        var range = abs(max - min)

        if (range == 0f) {
            val padding = emptyRangePadding(max)
            max += padding
            min -= padding
        }

        range = abs(max - min)

        val newMin = if (isAxisMinCustom) axisMinimum else min - (range / 100f) * spaceBottom
        val newMax = if (isAxisMaxCustom) axisMaximum else max + (range / 100f) * spaceTop

        applyAxisRange(newMin, newMax)
    }
}
