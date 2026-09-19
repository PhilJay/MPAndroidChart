package com.github.mikephil.charting.data

import android.graphics.Typeface
import android.util.Log
import com.github.mikephil.charting.components.YAxis.AxisDependency
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IDataSet

/**
 * Everything a chart draws: a list of data sets plus the cached x and y ranges over all of them, split by
 * y axis. The ranges are computed once at construction and again by [notifyDataChanged]; adding sets or
 * entries through this class widens them on the fly, changing a set in place does not.
 *
 * @param T the data set type this object holds.
 * @param dataSets the initial sets; an `ArrayList` is kept by reference, any other list is copied.
 */
abstract class ChartData<T : IDataSet<out Entry<*>>>(dataSets: List<T>) {

    /** Creates an empty data object; add sets with [addDataSet]. */
    constructor() : this(mutableListOf())

    /** Creates a data object holding the given sets. */
    constructor(vararg dataSets: T) : this(dataSets.toMutableList())

    /**
     * The data sets in drawing order. Changing this list in place does not update the cached ranges; call
     * [notifyDataChanged] afterwards or use [addDataSet] and [removeDataSet].
     */
    val dataSets: MutableList<T> = dataSets as? ArrayList<T> ?: dataSets.toMutableList()

    /** Largest y over all sets on both axes, or `-Float.MAX_VALUE` when there is no data. */
    var yMax = -Float.MAX_VALUE
        protected set

    /** Smallest y over all sets on both axes, or `Float.MAX_VALUE` when there is no data. */
    var yMin = Float.MAX_VALUE
        protected set

    /** Largest x over all sets, or `-Float.MAX_VALUE` when there is no data. */
    var xMax = -Float.MAX_VALUE
        protected set

    /** Smallest x over all sets, or `Float.MAX_VALUE` when there is no data. */
    var xMin = Float.MAX_VALUE
        protected set

    /** Largest y over the sets on the left axis, or `-Float.MAX_VALUE` when there are none. */
    protected var leftAxisMax = -Float.MAX_VALUE

    /** Smallest y over the sets on the left axis, or `Float.MAX_VALUE` when there are none. */
    protected var leftAxisMin = Float.MAX_VALUE

    /** Largest y over the sets on the right axis, or `-Float.MAX_VALUE` when there are none. */
    protected var rightAxisMax = -Float.MAX_VALUE

    /** Smallest y over the sets on the right axis, or `Float.MAX_VALUE` when there are none. */
    protected var rightAxisMin = Float.MAX_VALUE

    init {
        notifyDataChanged()
    }

    /**
     * Recomputes the cached ranges from the sets' own ranges. It does not ask the sets to recompute theirs,
     * so after changing a set's entries in place call [DataSet.notifyDataSetChanged] on that set first.
     * The chart still needs its own notify call to redraw.
     */
    open fun notifyDataChanged() {
        calcMinMax()
    }

    /**
     * Tells every set to recompute its y range from the entries between [fromX] and [toX] only, then
     * recomputes the cached ranges. Used by charts that auto scale the y axis to the visible x range.
     *
     * @param fromX first x value to include.
     * @param toX last x value to include.
     */
    fun calcMinMaxY(fromX: Float, toX: Float) {
        for (set in dataSets) {
            set.calcMinMaxY(fromX, toX)
        }
        calcMinMax()
    }

    /** Recomputes the overall and per axis x and y ranges from the sets' cached ranges. */
    protected open fun calcMinMax() {
        yMax = -Float.MAX_VALUE
        yMin = Float.MAX_VALUE
        xMax = -Float.MAX_VALUE
        xMin = Float.MAX_VALUE

        for (set in dataSets) {
            calcMinMax(set)
        }

        leftAxisMax = -Float.MAX_VALUE
        leftAxisMin = Float.MAX_VALUE
        rightAxisMax = -Float.MAX_VALUE
        rightAxisMin = Float.MAX_VALUE

        val firstLeft = getFirstLeft(dataSets)
        if (firstLeft != null) {
            leftAxisMax = firstLeft.yMax
            leftAxisMin = firstLeft.yMin
            for (dataSet in dataSets) {
                if (dataSet.axisDependency == AxisDependency.LEFT) {
                    if (dataSet.yMin < leftAxisMin) leftAxisMin = dataSet.yMin
                    if (dataSet.yMax > leftAxisMax) leftAxisMax = dataSet.yMax
                }
            }
        }

        val firstRight = getFirstRight(dataSets)
        if (firstRight != null) {
            rightAxisMax = firstRight.yMax
            rightAxisMin = firstRight.yMin
            for (dataSet in dataSets) {
                if (dataSet.axisDependency == AxisDependency.RIGHT) {
                    if (dataSet.yMin < rightAxisMin) rightAxisMin = dataSet.yMin
                    if (dataSet.yMax > rightAxisMax) rightAxisMax = dataSet.yMax
                }
            }
        }
    }

    /** Number of data sets. */
    val dataSetCount: Int
        get() = dataSets.size

    /**
     * Returns the smallest y of the sets on [axis]. When no set uses that axis the other axis' minimum is
     * returned, so both axes show the same range.
     */
    fun getYMin(axis: AxisDependency): Float {
        return if (axis == AxisDependency.LEFT) {
            if (leftAxisMin == Float.MAX_VALUE) rightAxisMin else leftAxisMin
        } else {
            if (rightAxisMin == Float.MAX_VALUE) leftAxisMin else rightAxisMin
        }
    }

    /**
     * Returns the largest y of the sets on [axis]. When no set uses that axis the other axis' maximum is
     * returned, so both axes show the same range.
     */
    fun getYMax(axis: AxisDependency): Float {
        return if (axis == AxisDependency.LEFT) {
            if (leftAxisMax == -Float.MAX_VALUE) rightAxisMax else leftAxisMax
        } else {
            if (rightAxisMax == -Float.MAX_VALUE) leftAxisMax else rightAxisMax
        }
    }

    /**
     * Finds the first set in [dataSets] whose label equals [label] by scanning the list.
     *
     * @param ignoreCase true to compare labels case insensitively.
     * @return the index in [dataSets], or -1 if no label matches.
     */
    protected fun getDataSetIndexByLabel(dataSets: List<T>, label: String, ignoreCase: Boolean): Int {
        for (i in dataSets.indices) {
            if (label.equals(dataSets[i].label, ignoreCase)) return i
        }
        return -1
    }

    /** The labels of all sets in set order. */
    val dataSetLabels: List<String>
        get() = dataSets.map { it.label }

    /**
     * Returns the entry a highlight refers to: the entry in the set at [Highlight.dataSetIndex] whose x is
     * closest to [Highlight.x] and, among equal x, whose y is closest to [Highlight.y].
     *
     * @return the entry, or null when the set index is too high or the set is empty.
     */
    open fun getEntryForHighlight(highlight: Highlight): Entry<*>? {
        if (highlight.dataSetIndex !in dataSets.indices) return null
        return dataSets[highlight.dataSetIndex].getEntryForXValue(highlight.x, highlight.y)
    }

    /**
     * Returns the first set whose label equals [label] by scanning all sets.
     *
     * @param ignoreCase true to compare labels case insensitively.
     * @return the set, or null if no label matches.
     */
    open fun getDataSetByLabel(label: String, ignoreCase: Boolean): T? {
        val index = getDataSetIndexByLabel(dataSets, label, ignoreCase)
        return if (index < 0 || index >= dataSets.size) null else dataSets[index]
    }

    /** Returns the set at [index], or null when the index is out of range. */
    open fun getDataSetByIndex(index: Int): T? {
        if (index < 0 || index >= dataSets.size) return null
        return dataSets[index]
    }

    /** Appends [d] and widens the cached ranges to include it. Does nothing for null. */
    fun addDataSet(d: T?) {
        if (d == null) return
        calcMinMax(d)
        dataSets.add(d)
    }

    /**
     * Removes [d] and recomputes the cached ranges if it was found.
     *
     * @return true if the set was removed, false for null or a set that is not part of this data.
     */
    open fun removeDataSet(d: T?): Boolean {
        if (d == null) return false
        val removed = dataSets.remove(d)
        if (removed) notifyDataChanged()
        return removed
    }

    /**
     * Removes the set at [index] and recomputes the cached ranges.
     *
     * @return true if a set was removed, false when the index is out of range.
     */
    open fun removeDataSet(index: Int): Boolean {
        if (index >= dataSets.size || index < 0) return false
        return removeDataSet(dataSets[index])
    }

    /**
     * Appends [e] to the set at [dataSetIndex] with [IDataSet.addEntry] and widens the cached ranges to
     * include it. An index out of range only logs an error.
     */
    fun addEntry(e: Entry<*>, dataSetIndex: Int) {
        if (dataSets.size > dataSetIndex && dataSetIndex >= 0) {
            @Suppress("UNCHECKED_CAST")
            val set = dataSets[dataSetIndex] as IDataSet<Entry<*>>
            if (!set.addEntry(e)) return
            calcMinMax(e, set.axisDependency)
        } else {
            Log.e("addEntry", "Cannot add Entry<*> because dataSetIndex too high or too low.")
        }
    }

    /**
     * Widens the overall ranges and the ranges of [axis] to include [e].
     *
     * @param axis the y axis the entry's set belongs to.
     */
    protected fun calcMinMax(e: Entry<*>, axis: AxisDependency) {
        if (yMax < e.y) yMax = e.y
        if (yMin > e.y) yMin = e.y
        if (xMax < e.x) xMax = e.x
        if (xMin > e.x) xMin = e.x

        if (axis == AxisDependency.LEFT) {
            if (leftAxisMax < e.y) leftAxisMax = e.y
            if (leftAxisMin > e.y) leftAxisMin = e.y
        } else {
            if (rightAxisMax < e.y) rightAxisMax = e.y
            if (rightAxisMin > e.y) rightAxisMin = e.y
        }
    }

    /** Widens the overall ranges and the ranges of the set's axis to include the cached ranges of [d]. */
    protected fun calcMinMax(d: T) {
        if (yMax < d.yMax) yMax = d.yMax
        if (yMin > d.yMin) yMin = d.yMin
        if (xMax < d.xMax) xMax = d.xMax
        if (xMin > d.xMin) xMin = d.xMin

        if (d.axisDependency == AxisDependency.LEFT) {
            if (leftAxisMax < d.yMax) leftAxisMax = d.yMax
            if (leftAxisMin > d.yMin) leftAxisMin = d.yMin
        } else {
            if (rightAxisMax < d.yMax) rightAxisMax = d.yMax
            if (rightAxisMin > d.yMin) rightAxisMin = d.yMin
        }
    }

    /**
     * Removes [e] from the set at [dataSetIndex] and recomputes the cached ranges if it was found.
     *
     * @return true if the entry was removed, false for null or an index outside the sets.
     */
    open fun removeEntry(e: Entry<*>?, dataSetIndex: Int): Boolean {
        if (e == null || dataSetIndex !in dataSets.indices) return false
        @Suppress("UNCHECKED_CAST")
        val set = dataSets[dataSetIndex] as IDataSet<Entry<*>>
        val removed = set.removeEntry(e)
        if (removed) notifyDataChanged()
        return removed
    }

    /**
     * Removes the entry whose x is closest to [xValue] (not necessarily an exact match) from the set at
     * [dataSetIndex] and recomputes the cached ranges.
     *
     * @return true if an entry was removed, false for an empty set or an index outside the sets.
     */
    open fun removeEntry(xValue: Float, dataSetIndex: Int): Boolean {
        if (dataSetIndex !in dataSets.indices) return false
        val e = dataSets[dataSetIndex].getEntryForXValue(xValue, Float.NaN) ?: return false
        return removeEntry(e, dataSetIndex)
    }

    /**
     * Finds the set that holds an entry matching [e] by x, y and payload (see [Entry.equalTo]).
     *
     * @return the set, or null for a null entry or when no set contains a match.
     */
    fun getDataSetForEntry(e: Entry<*>?): T? {
        if (e == null) return null
        return dataSets.firstOrNull { set -> e.equalTo(set.getEntryForXValue(e.x, e.y)) }
    }

    /** The colors of all sets, set by set, in one list. */
    val colors: List<Int>
        get() = dataSets.flatMap { it.colors }

    /** Returns the index of [dataSet] in [dataSets], or -1 if it is not part of this data. */
    fun getIndexOfDataSet(dataSet: T): Int = dataSets.indexOf(dataSet)

    /** Returns the first of [sets] that is plotted against the left y axis, or null if there is none. */
    protected fun getFirstLeft(sets: List<T>): T? {
        return sets.firstOrNull { it.axisDependency == AxisDependency.LEFT }
    }

    /** Returns the first of [sets] that is plotted against the right y axis, or null if there is none. */
    fun getFirstRight(sets: List<T>): T? {
        return sets.firstOrNull { it.axisDependency == AxisDependency.RIGHT }
    }

    /** Assigns [f] as the value label formatter of every set. */
    fun setValueFormatter(f: IValueFormatter) {
        for (set in dataSets) set.valueFormatter = f
    }

    /** Assigns [color] as the only value label color of every set. */
    fun setValueTextColor(color: Int) {
        for (set in dataSets) set.valueTextColor = color
    }

    /** Assigns the same list of value label colors to every set. */
    fun setValueTextColors(colors: List<Int>) {
        for (set in dataSets) set.valueTextColors = colors
    }

    /** Assigns [tf] as the value label typeface of every set; null uses the default typeface. */
    fun setValueTypeface(tf: Typeface?) {
        for (set in dataSets) set.valueTypeface = tf
    }

    /** Assigns [size] in dp as the value label text size of every set. */
    fun setValueTextSize(size: Float) {
        for (set in dataSets) set.valueTextSize = size
    }

    /** Turns value labels on or off for every set. */
    fun setDrawValues(enabled: Boolean) {
        for (set in dataSets) set.isDrawValuesEnabled = enabled
    }

    /**
     * True when every set allows highlighting by touch or code (also true with no sets).
     * Assigning applies the value to every set.
     */
    var isHighlightEnabled: Boolean
        get() = dataSets.all { it.isHighlightEnabled }
        set(value) {
            for (set in dataSets) set.isHighlightEnabled = value
        }

    /** Removes all sets and resets the cached ranges. */
    fun clearValues() {
        dataSets.clear()
        notifyDataChanged()
    }

    /** Returns true if [dataSet] is one of the sets, compared with `equals`. */
    fun contains(dataSet: T): Boolean = dataSets.any { it == dataSet }

    /** Total number of entries over all sets. */
    val entryCount: Int
        get() = dataSets.sumOf { it.entryCount }

    /** The set with the most entries, or null when there are no sets. */
    val maxEntryCountSet: T?
        get() = dataSets.maxByOrNull { it.entryCount }
}
