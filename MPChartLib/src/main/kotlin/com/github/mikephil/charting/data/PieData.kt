package com.github.mikephil.charting.data

import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet

/**
 * Data for a pie chart. Unlike the other data objects it holds exactly one set, whose entries are the slices;
 * the legend is built from the entry labels, not from the set label.
 */
public open class PieData : ChartData<IPieDataSet<*>> {

    /** Creates a data object without a set; assign [dataSet] before use. */
    public constructor() : super()

    /** Creates a data object holding [dataSet]. */
    public constructor(dataSet: IPieDataSet<*>) : super(dataSet)

    /**
     * The single set of this data, or null while no set has been assigned. Assigning replaces any existing set,
     * null removes it, and the cached ranges are recomputed.
     */
    public var dataSet: IPieDataSet<*>?
        get() = dataSets.getOrNull(0)
        set(value) {
            dataSets.clear()
            if (value != null) dataSets.add(value)
            notifyDataChanged()
        }

    /**
     * Returns [dataSet] for index 0 and null for every other index or while no set has been assigned.
     */
    override fun getDataSetByIndex(index: Int): IPieDataSet<*>? {
        return if (index == 0) dataSets.getOrNull(0) else null
    }

    /**
     * Returns [dataSet] if its label equals [label], otherwise null, also while no set has been assigned.
     *
     * @param ignoreCase true to compare labels case insensitively.
     */
    override fun getDataSetByLabel(label: String, ignoreCase: Boolean): IPieDataSet<*>? {
        val set = dataSets.getOrNull(0) ?: return null
        return if (label.equals(set.label, ignoreCase)) set else null
    }

    /**
     * Returns the slice a highlight refers to. For pie charts [Highlight.x] holds the entry index, not an x value.
     *
     * @return the slice, or null when that index is out of range or no set has been assigned.
     */
    override fun getEntryForHighlight(highlight: Highlight): Entry<*>? {
        val set = dataSets.getOrNull(0) ?: return null
        val index = highlight.x.toInt()
        return if (index in 0 until set.entryCount) set.getEntryForIndex(index) else null
    }

    /**
     * Sum of all slice values, the whole pie. 0 while no set has been assigned.
     */
    public val yValueSum: Float
        get() {
            val set = dataSets.getOrNull(0) ?: return 0f
            var sum = 0f
            for (i in 0 until set.entryCount) {
                sum += set.getEntryForIndex(i).y
            }
            return sum
        }
}
