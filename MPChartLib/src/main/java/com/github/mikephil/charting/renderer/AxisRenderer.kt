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
 * Baseclass of all axis renderers.
 */
abstract class AxisRenderer(
    viewPortHandler: ViewPortHandler,
    /** transformer to transform values to screen pixels and return  */
    var transformer: Transformer?,
    /** base axis this axis renderer works with  */
    @JvmField
    protected var axis: AxisBase
) : Renderer(viewPortHandler) {

    /**
     * paint object for the grid lines
     */
    var paintGrid = Paint().apply {
        color = Color.GRAY
        strokeWidth = 1f
        style = Paint.Style.STROKE
        alpha = 90
    }
        protected set

    /**
     * Returns the Paint object used for drawing the axis (labels).
     *
     * @return
     */
    /**
     * paint for the x-label values
     */
    var paintAxisLabels = Paint(Paint.ANTI_ALIAS_FLAG)
        protected set

    /**
     * paint for the line surrounding the chart
     */
    var paintAxisLine = Paint().apply {
        color = Color.BLACK
        strokeWidth = 1f
        style = Paint.Style.STROKE
    }
        protected set

    /**
     * paint used for the limit lines
     */
    @JvmField
    protected var limitLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }

    /**
     * paint used for the limit ranges
     */
    @JvmField
    protected var limitRangePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }

    /**
     * paint used for the limit range fill
     */
    @JvmField
    protected var limitRangePaintFill = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    /**
     * Computes the axis values.
     *
     * @param min - the minimum value in the data object for this axis
     * @param max - the maximum value in the data object for this axis
     */
    open fun computeAxis(min: Float, max: Float, inverted: Boolean) {
        // calculate the starting and entry point of the y-labels (depending on
        // zoom / content rect bounds)

        var minLocal = min
        var maxLocal = max

        if (viewPortHandler.contentWidth() > 10 && !viewPortHandler.isFullyZoomedOutY) {
            transformer?.let {
                val p1 = it.getValuesByTouchPoint(viewPortHandler.contentLeft(), viewPortHandler.contentTop())
                val p2 = it.getValuesByTouchPoint(viewPortHandler.contentLeft(), viewPortHandler.contentBottom())

                if (!inverted) {
                    minLocal = p2.y.toFloat()
                    maxLocal = p1.y.toFloat()
                } else {
                    minLocal = p1.y.toFloat()
                    maxLocal = p2.y.toFloat()
                }

                MPPointD.recycleInstance(p1)
                MPPointD.recycleInstance(p2)
            }
        }
        computeAxisValues(minLocal, maxLocal)
    }

    /**
     * Sets up the axis values. Computes the desired number of labels between the two given extremes.
     *
     * @return
     */
    protected open fun computeAxisValues(min: Float, max: Float) {
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

        var n = if (axis.isCenterAxisLabelsEnabled) 1 else 0

        // force label count
        if (axis.isForceLabelsEnabled) {
            interval = (range.toFloat() / (labelCount - 1).toFloat()).toDouble()
            // When force label is enabled
            // If granularity is enabled, then do not allow the interval to go below specified granularity.
            if (axis.isGranularityEnabled) interval = if (interval < axis.granularity) axis.granularity.toDouble() else interval

            axis.mEntryCount = labelCount

            // Ensure stops contains at least numStops elements.
            axis.mEntries = FloatArray(labelCount)

            var v = min

            for (i in 0..<labelCount) {
                axis.mEntries[i] = v
                v += interval.toFloat()
            }

            n = labelCount

            // no forced count
        } else {
            var first = if (interval == 0.0) 0.0 else ceil(min / interval) * interval
            if (axis.isCenterAxisLabelsEnabled) {
                first -= interval
            }

            val last = if (interval == 0.0) 0.0 else Utils.nextUp(floor(max / interval) * interval)

            var f: Double

            if (interval != 0.0 && last != first) {
                f = first
                while (f <= last) {
                    ++n
                    f += interval
                }
            } else if (last == first && n == 0) {
                n = 1
            }

            axis.mEntryCount = n

            axis.mEntries = FloatArray(n)

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

        if (axis.isCenterAxisLabelsEnabled) {
            if (axis.mCenteredEntries.size < n) {
                axis.mCenteredEntries = FloatArray(n)
            }

            val offset = interval.toFloat() / 2f

            for (i in 0..<n) {
                axis.mCenteredEntries[i] = axis.mEntries[i] + offset
            }
        }
    }

    /**
     * Draws the axis labels to the screen.
     *
     * @param c
     */
    abstract fun renderAxisLabels(c: Canvas)

    /**
     * Draws the grid lines belonging to the axis.
     *
     * @param c
     */
    abstract fun renderGridLines(c: Canvas)

    /**
     * Draws the line that goes alongside the axis.
     *
     * @param c
     */
    abstract fun renderAxisLine(c: Canvas)

    /**
     * Draws the LimitLines associated with this axis to the screen.
     *
     * @param c
     */
    abstract fun renderLimitLines(c: Canvas)

    /**
     * Sets the text color to use for the labels. Make sure to use
     * ContextCompat.getColor(context,...) when using a color from the resources.
     *
     * @param color
     */
    fun setTextColor(color: Int) {
        axis.textColor = color
    }
}
