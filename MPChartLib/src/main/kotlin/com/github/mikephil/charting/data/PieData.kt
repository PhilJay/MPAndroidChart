package com.github.mikephil.charting.data

import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet

/**
 * Data for a pie chart. Unlike the other data objects it holds exactly one set, whose entries are the slices;
 * the legend is built from the entry labels, not from the set label.
 */
open class PieData : ChartData<IPieDataSet<*>> {

    /** Creates a data object without a set; assign [dataSet] before use. */
    constructor() : super()

    /** Creates a data object holding [dataSet]. */
    constructor(dataSet: IPieDataSet<*>) : super(dataSet)

    /**
     * The single set of this data. Assigning replaces any existing set and recomputes the cached ranges.
     *
     * @throws IndexOutOfBoundsException when read while no set has been assigned.
     */
    var dataSet: IPieDataSet<*>
        get() = dataSets[0]
        set(value) {
            dataSets.clear()
            dataSets.add(value)
            notifyDataChanged()
        }

    /**
     * Returns [dataSet] for index 0 and null for every other index.
     *
     * @throws IndexOutOfBoundsException for index 0 while no set has been assigned.
     */
    override fun getDataSetByIndex(index: Int): IPieDataSet<*>? {
        return if (index == 0) dataSet else null
    }

    /**
     * Returns [dataSet] if its label equals [label], otherwise null.
     *
     * @param ignoreCase true to compare labels case insensitively.
     * @throws IndexOutOfBoundsException while no set has been assigned.
     */
    override fun getDataSetByLabel(label: String, ignoreCase: Boolean): IPieDataSet<*>? {
        val set = dataSets[0]
        return if (label.equals(set.label, ignoreCase)) set else null
    }

    /**
     * Returns the slice a highlight refers to. For pie charts [Highlight.x] holds the entry index, not an x value.
     *
     * @throws IndexOutOfBoundsException when that index is out of range or no set has been assigned.
     */
    override fun getEntryForHighlight(highlight: Highlight): Entry<*>? {
        return dataSet.getEntryForIndex(highlight.x.toInt())
    }

    /**
     * Sum of all slice values, the whole pie.
     *
     * @throws IndexOutOfBoundsException while no set has been assigned.
     */
    val yValueSum: Float
        get() {
            var sum = 0f
            for (i in 0 until dataSet.entryCount) {
                sum += dataSet.getEntryForIndex(i).y
            }
            return sum
        }
}
