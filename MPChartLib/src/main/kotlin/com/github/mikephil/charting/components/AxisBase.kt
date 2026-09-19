package com.github.mikephil.charting.components

import android.graphics.Color
import android.graphics.DashPathEffect
import android.util.Log
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import kotlin.math.abs
import kotlin.math.max

/**
 * Base of [XAxis] and [YAxis]: the value range, the label entries, grid and axis lines, limit lines and label
 * formatting. Sizes are in dp and converted to px at draw time. The label entries are computed by the chart's axis
 * renderer from the visible range, so they are empty until the chart has data and was laid out.
 */
abstract class AxisBase : ComponentBase() {

    private var axisValueFormatter: IAxisValueFormatter? = null

    /** Color of the grid lines drawn from each label across the content area. Default gray. */
    var gridColor = Color.GRAY

    /** Width of the grid lines in dp. Default 0.5. */
    var gridLineWidth = 0.5f

    /** Color of the line drawn along the axis. Default gray. */
    var axisLineColor = Color.GRAY

    /** Width of the line drawn along the axis in dp. Default 0.5. */
    var axisLineWidth = 0.5f

    /**
     * Values in value space at which labels and grid lines are drawn. Computed by the axis renderer, which
     * only grows the array, so read the first [entryCount] of them.
     */
    var entries = FloatArray(0)
        internal set

    /** Values halfway between neighbouring [entries], used when [isCenterAxisLabelsEnabled] is true. */
    var centeredEntries = FloatArray(0)
        internal set

    /** Number of computed [entries]. */
    var entryCount = 0
        internal set

    /** Decimal digits the default label formatter uses, derived from the label interval by the axis renderer. */
    var decimals = 0
        internal set

    /**
     * Number of labels the axis should have, clamped to [axisMinLabels]..[axisMaxLabels]. Default 6.
     * The label interval is rounded to a nice value, so the real count only approximates this unless
     * [isForceLabelsEnabled] is true.
     */
    var labelCount = 6
        set(value) {
            field = value.coerceIn(axisMinLabels, axisMaxLabels)
        }

    /** Draws exactly [labelCount] labels, evenly spread, instead of rounding to nice values. Default false. */
    var isForceLabelsEnabled = false

    /**
     * Smallest allowed interval between two labels in value space, which stops labels from repeating when zooming in.
     * Assigning a value also sets [isGranularityEnabled] to true. Default 1.
     */
    var granularity = 1.0f
        set(value) {
            field = value
            isGranularityEnabled = true
        }

    /** Whether the label interval is kept at or above [granularity]. Default false; assigning [granularity] enables it. */
    var isGranularityEnabled = false

    /** Whether grid lines are drawn from each label across the content area. Default true. */
    var isDrawGridLinesEnabled = true

    /** Whether the line along the axis is drawn. Default true. */
    var isDrawAxisLineEnabled = true

    /** Whether the labels are drawn. Grid and axis lines are not affected. Default true. */
    var isDrawLabelsEnabled = true

    /**
     * Draws each label halfway between two grid lines instead of at its value, useful for grouped bar charts.
     * Reads as false while the axis has no [entries]. Default false.
     */
    var isCenterAxisLabelsEnabled = false
        get() = field && entryCount > 0

    /** Dash pattern of the axis line, or null for a solid line. See [enableAxisLineDashedLine]. */
    var axisLineDashPathEffect: DashPathEffect? = null

    /** Dash pattern of the grid lines, or null for solid lines. See [enableGridDashedLine]. */
    var gridDashPathEffect: DashPathEffect? = null

    /** Limit lines drawn on this axis, in the order they were added. */
    val limitLines: MutableList<LimitLine> = mutableListOf()

    /** Whether the limit lines are drawn behind the data instead of on top of it. Default false. */
    var isDrawLimitLinesBehindDataEnabled = false

    /** Whether the grid lines are drawn behind the data instead of on top of it. Default true. */
    var isDrawGridLinesBehindDataEnabled = true

    /** Value space padding subtracted from the data minimum when the axis minimum is computed. Default 0. */
    var spaceMin = 0f

    /** Value space padding added to the data maximum when the axis maximum is computed. Default 0. */
    var spaceMax = 0f

    private var axisMinimumValue = 0f

    private var axisMaximumValue = 0f

    /**
     * Smallest value on the axis, in value space. Assigning it fixes the minimum, so it is no longer computed from the
     * data until [resetAxisMinimum] is called, and updates [axisRange].
     */
    var axisMinimum: Float
        get() = axisMinimumValue
        set(value) {
            isAxisMinCustom = true
            axisMinimumValue = value
            axisRange = abs(axisMaximumValue - value)
        }

    /**
     * Largest value on the axis, in value space. Assigning it fixes the maximum, so it is no longer computed from the
     * data until [resetAxisMaximum] is called, and updates [axisRange].
     */
    var axisMaximum: Float
        get() = axisMaximumValue
        set(value) {
            isAxisMaxCustom = true
            axisMaximumValue = value
            axisRange = abs(value - axisMinimumValue)
        }

    /** Distance between [axisMinimum] and [axisMaximum] in value space. */
    var axisRange = 0f
        protected set

    /** Whether [axisMinimum] was assigned by the user rather than computed from the data. */
    var isAxisMinCustom = false
        private set

    /** Whether [axisMaximum] was assigned by the user rather than computed from the data. */
    var isAxisMaxCustom = false
        private set

    /** Lower bound applied to [labelCount]. Values of 0 or less are ignored. Default 2. */
    var axisMinLabels = 2
        set(value) {
            if (value > 0) field = value
        }

    /** Upper bound applied to [labelCount]. Values of 0 or less are ignored. Default 25. */
    var axisMaxLabels = 25
        set(value) {
            if (value > 0) field = value
        }

    /** Adds [l] to [limitLines]. Logs a warning once the axis has more than 6 limit lines. */
    fun addLimitLine(l: LimitLine) {
        limitLines.add(l)
        if (limitLines.size > 6) {
            Log.e("MPAndroidChart", "Warning! You have more than 6 LimitLines on your axis, do you really want that?")
        }
    }

    /** Removes [l] from [limitLines]. Does nothing if it was not added. */
    fun removeLimitLine(l: LimitLine) {
        limitLines.remove(l)
    }

    /** Removes every limit line from this axis. */
    fun removeAllLimitLines() {
        limitLines.clear()
    }

    /** The formatted label with the most characters, or an empty string while the axis has no [entries]. */
    val longestLabel: String
        get() {
            var longest = ""
            for (i in 0 until entryCount) {
                val text = getFormattedLabel(i)
                if (longest.length < text.length) longest = text
            }
            return longest
        }

    /**
     * Formats the entry at [index] with [valueFormatter].
     * @return the label text, or an empty string if [index] is not below [entryCount].
     */
    fun getFormattedLabel(index: Int): String {
        if (index < 0 || index >= entryCount) return ""
        return valueFormatter.getFormattedValue(entries[index], this)
    }

    /**
     * Formatter for the axis labels. While none is assigned, reading it returns a [DefaultAxisValueFormatter] with
     * the computed [decimals], replaced whenever [decimals] changes.
     */
    var valueFormatter: IAxisValueFormatter
        get() {
            val current = axisValueFormatter
            if (current == null || (current is DefaultAxisValueFormatter && current.decimalDigits != decimals)) {
                return DefaultAxisValueFormatter(decimals).also { axisValueFormatter = it }
            }
            return current
        }
        set(value) {
            axisValueFormatter = value
        }

    /**
     * Draws the grid lines dashed.
     * @param lineLength length of each dash in px
     * @param spaceLength length of the gap between two dashes in px
     * @param phase offset into the dash pattern in px, normally 0
     */
    fun enableGridDashedLine(lineLength: Float, spaceLength: Float, phase: Float) {
        gridDashPathEffect = DashPathEffect(floatArrayOf(lineLength, spaceLength), phase)
    }

    /** Draws the grid lines solid again. */
    fun disableGridDashedLine() {
        gridDashPathEffect = null
    }

    /** Whether the grid lines are drawn dashed. */
    val isGridDashedLineEnabled: Boolean
        get() = gridDashPathEffect != null

    /**
     * Draws the axis line dashed.
     * @param lineLength length of each dash in px
     * @param spaceLength length of the gap between two dashes in px
     * @param phase offset into the dash pattern in px, normally 0
     */
    fun enableAxisLineDashedLine(lineLength: Float, spaceLength: Float, phase: Float) {
        axisLineDashPathEffect = DashPathEffect(floatArrayOf(lineLength, spaceLength), phase)
    }

    /** Draws the axis line solid again. */
    fun disableAxisLineDashedLine() {
        axisLineDashPathEffect = null
    }

    /** Whether the axis line is drawn dashed. */
    val isAxisLineDashedLineEnabled: Boolean
        get() = axisLineDashPathEffect != null

    /** Computes the maximum from the data again after [axisMaximum] was assigned. Applied on the next [calculate]. */
    fun resetAxisMaximum() {
        isAxisMaxCustom = false
    }

    /** Computes the minimum from the data again after [axisMinimum] was assigned. Applied on the next [calculate]. */
    fun resetAxisMinimum() {
        isAxisMinCustom = false
    }

    /**
     * Sets [axisMinimum], [axisMaximum] and [axisRange] without fixing either limit. Used by [calculate]; assign
     * [axisMinimum] or [axisMaximum] to fix a limit instead.
     * @param min the new minimum in value space
     * @param max the new maximum in value space
     */
    fun applyAxisRange(min: Float, max: Float) {
        axisMinimumValue = min
        axisMaximumValue = max
        axisRange = abs(max - min)
    }

    /**
     * Half the span an empty range is widened to. It grows with the value, because adding 1 to a value above about
     * 33 million leaves it unchanged in float precision, which would leave the axis with a range of zero and
     * nothing drawn.
     */
    protected fun emptyRangePadding(value: Float): Float = max(1f, abs(value) / 100f)

    /**
     * Computes the axis range from the data range. Fixed limits are kept, computed ones get [spaceMin] and [spaceMax]
     * added, and an empty range is widened around the value. The chart calls this whenever its data changes.
     * @param dataMin smallest value in the chart data
     * @param dataMax largest value in the chart data
     */
    open fun calculate(dataMin: Float, dataMax: Float) {
        var min = if (isAxisMinCustom) axisMinimumValue else dataMin - spaceMin
        var max = if (isAxisMaxCustom) axisMaximumValue else dataMax + spaceMax

        if (abs(max - min) == 0f) {
            val padding = emptyRangePadding(max)
            max += padding
            min -= padding
        }

        applyAxisRange(min, max)
    }
}
