package com.github.mikephil.charting.data

import android.graphics.drawable.Drawable
import com.github.mikephil.charting.highlight.Range
import kotlin.math.abs

/**
 * Entry for a bar chart. A bar is either a single value in [y] or a stack of values in [stackValues].
 * Use the [BarEntry] factory functions when there is no payload.
 *
 * @param D the type of the payload stored in [data].
 */
open class BarEntry<out D> : Entry<D> {

    /**
     * Values of a stacked bar, drawn bottom to top in list order, or null for a single bar.
     * Assigning them sets [y] to their sum and recomputes [positiveSum], [negativeSum] and [ranges].
     */
    var stackValues: List<Float>? = null
        set(value) {
            field = value
            y = calcSum(value)
            calcPosNegSum()
            calcRanges()
        }

    /**
     * Start and end on the y axis of each stack value, in the order of [stackValues]. Positive values stack
     * upwards from zero, negative values downwards. Null until stack values have been set.
     */
    var ranges: List<Range>? = null
        private set

    /** Sum of the absolute values of all negative stack values, so a positive number. 0 for a single bar. */
    var negativeSum = 0f
        private set

    /** Sum of all positive stack values. 0 for a single bar. */
    var positiveSum = 0f
        private set

    /**
     * Creates a single, unstacked bar.
     *
     * @param x position on the x axis in value space.
     * @param y height of the bar.
     * @param icon drawable drawn at the entry when icons are enabled, or null.
     * @param data payload carried with the entry.
     */
    constructor(x: Float, y: Float, icon: Drawable? = null, data: D?) : super(x, y, icon, data)

    /**
     * Creates a stacked bar whose [y] is the sum of [stackValues].
     *
     * @param x position on the x axis in value space.
     * @param stackValues the stack values, bottom to top.
     * @param icon drawable drawn at the entry when icons are enabled, or null.
     * @param data one payload for the whole stack.
     */
    constructor(x: Float, stackValues: List<Float>, icon: Drawable? = null, data: D?) : super(x, calcSum(stackValues), icon, data) {
        this.stackValues = stackValues
    }

    /** Returns a new entry with the same values. The stack list, icon and payload are shared, not copied. */
    override fun copy(): BarEntry<D> {
        val copied = BarEntry(x, y, icon, data)
        copied.stackValues = stackValues
        return copied
    }

    /** True when this entry holds stack values, even if the stack has only one value. */
    val isStacked: Boolean
        get() = stackValues != null

    /**
     * Sums the stack values that come after [stackIndex] in [stackValues].
     *
     * @return the sum, or 0 for an unstacked entry.
     */
    fun getSumBelow(stackIndex: Int): Float {
        val vals = stackValues ?: return 0f
        var remainder = 0f
        var index = vals.size - 1
        while (index > stackIndex && index >= 0) {
            remainder += vals[index]
            index--
        }
        return remainder
    }

    private fun calcPosNegSum() {
        val vals = stackValues
        if (vals == null) {
            negativeSum = 0f
            positiveSum = 0f
            return
        }
        var sumNeg = 0f
        var sumPos = 0f
        for (f in vals) {
            if (f <= 0f) sumNeg += abs(f) else sumPos += f
        }
        negativeSum = sumNeg
        positiveSum = sumPos
    }

    private fun calcRanges() {
        val values = stackValues
        if (values == null || values.isEmpty()) {
            ranges = null
            return
        }
        var negRemain = -negativeSum
        var posRemain = 0f
        ranges = values.map { value ->
            if (value < 0) {
                val range = Range(negRemain, negRemain - value)
                negRemain -= value
                range
            } else {
                val range = Range(posRemain, posRemain + value)
                posRemain += value
                range
            }
        }
    }

    companion object {
        private fun calcSum(vals: List<Float>?): Float {
            if (vals == null) return 0f
            var sum = 0f
            for (f in vals) sum += f
            return sum
        }
    }
}

/**
 * Creates a single, unstacked bar without a payload.
 *
 * @param x position on the x axis in value space.
 * @param y height of the bar.
 * @param icon drawable drawn at the entry when icons are enabled, or null.
 */
fun BarEntry(x: Float, y: Float, icon: Drawable? = null): BarEntry<Nothing> = BarEntry(x, y, icon, null)

/**
 * Creates a stacked bar without a payload.
 *
 * @param x position on the x axis in value space.
 * @param stackValues the stack values, bottom to top.
 * @param icon drawable drawn at the entry when icons are enabled, or null.
 */
fun BarEntry(x: Float, stackValues: List<Float>, icon: Drawable? = null): BarEntry<Nothing> = BarEntry(x, stackValues, icon, null)
