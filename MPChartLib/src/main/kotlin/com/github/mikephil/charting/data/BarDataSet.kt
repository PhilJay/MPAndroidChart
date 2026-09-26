package com.github.mikephil.charting.data

import android.graphics.Color
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.utils.Fill

/**
 * One group of bars in a bar chart. Bars may be stacked when their entries carry [BarEntry.stackValues].
 *
 * @param D the payload type of the entries.
 * @param entries the bars, sorted by x; an `ArrayList` is kept by reference, other lists are copied.
 * @param label name of the set, shown in the legend.
 */
public open class BarDataSet<D>(entries: List<BarEntry<D>>, label: String) : BarLineScatterCandleBubbleDataSet<BarEntry<D>>(entries, label), IBarDataSet<D> {

    /**
     * Largest number of stack values of any entry, 1 for unstacked bars. Recomputed whenever the ranges are,
     * so after [notifyDataSetChanged] or after assigning [entries], and widened by [addEntry].
     */
    override var stackSize: Int = 1
        protected set

    override var barShadowColor: Int = Color.rgb(215, 215, 215)

    override var barBorderWidth: Float = 0f

    override var barBorderColor: Int = Color.BLACK

    override var highlightAlpha: Int = 120

    /**
     * Number of entries where each stack value counts separately. Recomputed whenever the full ranges are,
     * so an entry added through [addEntry] leaves it stale until [notifyDataSetChanged].
     */
    public var entryCountStacks: Int = 0
        private set

    override var stackLabels: List<String> = emptyList()

    override var barCornerRadius: Float = 0f

    override var isStackSectionsRounded: Boolean = false

    /** Gradient fills used instead of [colors] when set, reused from the start when there are more bars than fills. */
    override var fills: List<Fill>? = null

    init {
        highlightColor = Color.rgb(0, 0, 0)
        calcStackSize(this.entries)
        calcEntryCountIncludingStacks(this.entries)
    }

    override fun copy(): DataSet<BarEntry<D>> {
        val copiedEntries = entries.mapTo(mutableListOf()) { it.copy() }
        val copied = BarDataSet(copiedEntries, label)
        copy(copied)
        return copied
    }

    /** Copies the styling from this set into [barDataSet]; the entries are not copied. */
    protected fun copy(barDataSet: BarDataSet<D>) {
        super.copy(barDataSet)
        barDataSet.barBorderColor = barBorderColor
        barDataSet.fills = fills?.toList()
        barDataSet.stackSize = stackSize
        barDataSet.barCornerRadius = barCornerRadius
        barDataSet.isStackSectionsRounded = isStackSectionsRounded
        barDataSet.barShadowColor = barShadowColor
        barDataSet.barBorderWidth = barBorderWidth
        barDataSet.stackLabels = stackLabels
        barDataSet.highlightAlpha = highlightAlpha
    }

    /** Returns the fill for the bar at [index], wrapping around [fills], or an empty fill that draws nothing when none are set. */
    override fun getFill(index: Int): Fill {
        val fills = fills?.takeIf { it.isNotEmpty() } ?: return Fill()
        return fills[Math.floorMod(index, fills.size)]
    }

    /** Replaces [fills] with a single gradient from [startColor] to [endColor] used for every bar. */
    public fun setGradientColor(startColor: Int, endColor: Int) {
        fills = listOf(Fill(startColor, endColor))
    }

    private fun calcEntryCountIncludingStacks(entries: List<BarEntry<D>>) {
        entryCountStacks = 0
        for (entry in entries) {
            val vals = entry.stackValues
            entryCountStacks += vals?.size ?: 1
        }
    }

    private fun calcStackSize(entries: List<BarEntry<D>>) {
        stackSize = 1
        for (entry in entries) {
            val vals = entry.stackValues
            if (vals != null && vals.size > stackSize) stackSize = vals.size
        }
    }

    /**
     * Widens the ranges with the full extent of a bar: for stacked entries from `-negativeSum` to
     * `positiveSum` instead of y. Entries whose y is NaN or infinite are skipped.
     */
    override fun calcMinMax() {
        calcStackSize(entries)
        calcEntryCountIncludingStacks(entries)
        super.calcMinMax()
    }

    override fun calcMinMax(e: BarEntry<D>) {
        if (!e.y.isFinite()) return

        val vals = e.stackValues
        if (vals == null) {
            if (e.y < yMin) yMin = e.y
            if (e.y > yMax) yMax = e.y
        } else {
            if (vals.size > stackSize) stackSize = vals.size
            if (-e.negativeSum < yMin) yMin = -e.negativeSum
            if (e.positiveSum > yMax) yMax = e.positiveSum
        }

        calcMinMaxX(e)
    }

    /** True when any entry has more than one stack value, that is when [stackSize] is above 1. */
    override val isStacked: Boolean
        get() = stackSize > 1
}
