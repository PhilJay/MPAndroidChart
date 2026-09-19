package com.github.mikephil.charting.data

import kotlin.math.abs

/**
 * A group of entries that belong together, for example one line in a line chart or one group of bars.
 * Holds the entries and caches their x and y ranges. Entries must be sorted by x ascending for the x value
 * lookups ([getEntryForXValue], [getEntryIndex], [getEntriesForXValue]) to work. Looking an entry up by the
 * entry itself works either way; sorted entries only make it faster.
 *
 * @param T the entry type this set holds.
 * @param entries the initial entries; an `ArrayList` is kept by reference, any other list is copied.
 * @param label name of the set, shown in the legend.
 */
abstract class DataSet<T : Entry<*>>(entries: List<T>, label: String) : BaseDataSet<T>(label) {

    /**
     * The entries of this set. An assigned `ArrayList` is kept by reference so the caller can keep changing it,
     * any other list is copied. Assigning recomputes the cached ranges; after changing the list in place call
     * [notifyDataSetChanged] yourself.
     */
    var entries: MutableList<T> = entries as? ArrayList<T> ?: entries.toMutableList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    /** Largest y over the entries, or `-Float.MAX_VALUE` when empty. Refreshed by [calcMinMax] and [calcMinMaxY]. */
    override var yMax = -Float.MAX_VALUE
        protected set

    /** Smallest y over the entries, or `Float.MAX_VALUE` when empty. Refreshed by [calcMinMax] and [calcMinMaxY]. */
    override var yMin = Float.MAX_VALUE
        protected set

    /** Largest x over the entries, or `-Float.MAX_VALUE` when empty. Refreshed by [calcMinMax]. */
    override var xMax = -Float.MAX_VALUE
        protected set

    /** Smallest x over the entries, or `Float.MAX_VALUE` when empty. Refreshed by [calcMinMax]. */
    override var xMin = Float.MAX_VALUE
        protected set

    init {
        calcMinMax()
    }

    /** Recomputes [xMin], [xMax], [yMin] and [yMax] from all entries. */
    override fun calcMinMax() {
        yMax = -Float.MAX_VALUE
        yMin = Float.MAX_VALUE
        xMax = -Float.MAX_VALUE
        xMin = Float.MAX_VALUE

        if (entries.isEmpty()) return

        for (e in entries) {
            calcMinMax(e)
        }
    }

    /**
     * Recomputes only [yMin] and [yMax] from the entries between [fromX] and [toX], used by charts that
     * auto scale the y axis to the visible x range. The x range is left untouched. With no entries in the
     * range the y range is reset to its empty values.
     *
     * @param fromX first x value to include; rounds down to the nearest entry.
     * @param toX last x value to include; rounds up to the nearest entry.
     */
    override fun calcMinMaxY(fromX: Float, toX: Float) {
        yMax = -Float.MAX_VALUE
        yMin = Float.MAX_VALUE

        if (entries.isEmpty()) return

        val indexFrom = getEntryIndex(fromX, Float.NaN, Rounding.DOWN)
        val indexTo = getEntryIndex(toX, Float.NaN, Rounding.UP)

        if (indexTo < indexFrom) return

        for (i in indexFrom..indexTo) {
            calcMinMaxY(entries[i])
        }
    }

    /** Widens the cached x and y ranges to include [e]. */
    protected open fun calcMinMax(e: T) {
        calcMinMaxX(e)
        calcMinMaxY(e)
    }

    /** Widens [xMin] and [xMax] to include the x of [e]. */
    protected fun calcMinMaxX(e: T) {
        if (e.x < xMin) xMin = e.x
        if (e.x > xMax) xMax = e.x
    }

    /** Widens [yMin] and [yMax] to include the y of [e]. */
    protected open fun calcMinMaxY(e: T) {
        if (e.y < yMin) yMin = e.y
        if (e.y > yMax) yMax = e.y
    }

    override val entryCount: Int
        get() = entries.size

    /** Returns a deep copy: the entries are copied with [Entry.copy], the styling as in [BaseDataSet.copy]. */
    abstract fun copy(): DataSet<T>

    /** Copies the styling from this set into [dataSet]; entries are not copied here. */
    protected open fun copy(dataSet: DataSet<*>) {
        super.copy(dataSet)
    }

    override fun toString(): String {
        val buffer = StringBuilder()
        buffer.append(toSimpleString())
        for (entry in entries) {
            buffer.append(entry.toString()).append(" ")
        }
        return buffer.toString()
    }

    /** Returns the label and the entry count in one line, without listing the entries. */
    fun toSimpleString(): String = "DataSet, label: $label, entries: ${entries.size}\n"

    /**
     * Adds [e] so that the entries stay sorted by x: it is appended when its x is not smaller than the last
     * entry's x, otherwise inserted before the first entry with a larger x. The cached ranges are widened to
     * include [e] without a full recalculation.
     */
    override fun addEntryOrdered(e: T) {
        calcMinMax(e)

        if (entries.size > 0 && entries[entries.size - 1].x > e.x) {
            val closestIndex = getEntryIndex(e.x, e.y, Rounding.UP)
            entries.add(closestIndex, e)
        } else {
            entries.add(e)
        }
    }

    /** Removes all entries and resets the cached ranges. */
    override fun clear() {
        entries.clear()
        notifyDataSetChanged()
    }

    /**
     * Appends [e] to the end without checking the x order; use [addEntryOrdered] to keep the entries sorted.
     * The cached ranges are widened to include [e].
     *
     * @return always true.
     */
    override fun addEntry(e: T): Boolean {
        calcMinMax(e)
        return entries.add(e)
    }

    /**
     * Removes [e], matched with `equals` (reference for [Entry]), and recomputes the ranges if it was found.
     *
     * @return true if the entry was removed.
     */
    override fun removeEntry(e: T): Boolean {
        val removed = entries.remove(e)
        if (removed) calcMinMax()
        return removed
    }

    /**
     * Returns the index of [e] in the entries, or -1 when the set does not hold it. Matching is by identity, so an
     * entry with the same x and y as [e] but built separately is not a match.
     *
     * While the entries are sorted by x ascending, a binary search finds [e] by its x and the entries sharing that
     * x are then searched for [e] itself. A set sorted differently falls back to walking all entries, which is
     * slower but still returns the right index.
     */
    override fun getEntryIndex(e: Entry<*>): Int {
        val sorted = indexOfEntrySortedByX(e)
        if (sorted >= 0) return sorted

        for (i in entries.indices) {
            if (entries[i] === e) return i
        }
        return -1
    }

    /** Returns the index of [e] found through the x order of the entries, or -1 when that order does not lead to it. */
    private fun indexOfEntrySortedByX(e: Entry<*>): Int {
        val x = e.x
        var low = 0
        var high = entries.size - 1

        while (low <= high) {
            val middle = (low + high) ushr 1
            val middleX = entries[middle].x

            if (middleX < x) {
                low = middle + 1
            } else if (middleX > x) {
                high = middle - 1
            } else {
                if (entries[middle] === e) return middle

                var i = middle - 1
                while (i >= 0 && entries[i].x == x) {
                    if (entries[i] === e) return i
                    i--
                }

                i = middle + 1
                while (i < entries.size && entries[i].x == x) {
                    if (entries[i] === e) return i
                    i++
                }
                return -1
            }
        }

        return -1
    }

    /**
     * Returns the entry at the index found by [getEntryIndex] with the same arguments, or null when the set
     * is empty. The x need not match exactly; see [rounding].
     */
    override fun getEntryForXValue(xValue: Float, closestToY: Float, rounding: Rounding): T? {
        val index = getEntryIndex(xValue, closestToY, rounding)
        return if (index > -1) entries[index] else null
    }

    /**
     * Returns the entry at [index].
     *
     * @throws IndexOutOfBoundsException when [index] is not a valid entry index.
     */
    override fun getEntryForIndex(index: Int): T = entries[index]

    /**
     * Finds the index of the entry nearest to [xValue] by binary search; the entries must be sorted by x.
     * When two entries are equally close, the one with the larger x is taken.
     *
     * @param xValue the x value to look for, in value space.
     * @param closestToY when not NaN and several entries share the found x, the one whose y is closest to
     * this value is returned. NaN takes the first of them.
     * @param rounding [Rounding.CLOSEST] takes the nearest entry. [Rounding.UP] moves one entry up when the
     * nearest x is below [xValue] (unless it is the last entry), [Rounding.DOWN] moves one entry down when the
     * nearest x is above [xValue] (unless it is the first entry).
     * @return the entry index, or -1 when the set is empty.
     */
    override fun getEntryIndex(xValue: Float, closestToY: Float, rounding: Rounding): Int {
        if (entries.isEmpty()) return -1

        var low = 0
        var high = entries.size - 1
        var closest = high

        while (low < high) {
            val m = (low + high) / 2

            val d1 = entries[m].x - xValue
            val d2 = entries[m + 1].x - xValue
            val ad1 = abs(d1)
            val ad2 = abs(d2)

            if (ad2 < ad1) {
                low = m + 1
            } else if (ad1 < ad2) {
                high = m
            } else {
                if (d1 >= 0.0) {
                    high = m
                } else if (d1 < 0.0) {
                    low = m + 1
                }
            }
            closest = high
        }

        val closestXValue = entries[closest].x
        if (rounding == Rounding.UP) {
            if (closestXValue < xValue && closest < entries.size - 1) {
                ++closest
            }
        } else if (rounding == Rounding.DOWN) {
            if (closestXValue > xValue && closest > 0) {
                --closest
            }
        }

        if (!closestToY.isNaN()) {
            while (closest > 0 && entries[closest - 1].x == closestXValue) closest -= 1

            var closestYValue = entries[closest].y
            var closestYIndex = closest

            while (true) {
                closest += 1
                if (closest >= entries.size) break

                val value = entries[closest]
                if (value.x != closestXValue) break

                if (abs(value.y - closestToY) <= abs(closestYValue - closestToY)) {
                    closestYValue = value.y
                    closestYIndex = closest
                }
            }
            closest = closestYIndex
        }

        return closest
    }

    /**
     * Returns all entries whose x equals [xValue] exactly, in list order, found by binary search over the
     * x sorted entries. Empty when there is no exact match.
     */
    override fun getEntriesForXValue(xValue: Float): List<T> {
        val result = mutableListOf<T>()

        var low = 0
        var high = entries.size - 1

        while (low <= high) {
            var m = (high + low) / 2
            var entry = entries[m]

            if (xValue == entry.x) {
                while (m > 0 && entries[m - 1].x == xValue) m--

                high = entries.size
                while (m < high) {
                    entry = entries[m]
                    if (entry.x == xValue) {
                        result.add(entry)
                    } else {
                        break
                    }
                    m++
                }
                break
            } else {
                if (xValue > entry.x) low = m + 1 else high = m - 1
            }
        }

        return result
    }

    /** How [getEntryIndex] picks an entry when no entry has exactly the requested x. */
    enum class Rounding {
        /** Prefer the entry with the next larger x. */
        UP,
        /** Prefer the entry with the next smaller x. */
        DOWN,
        /** Take the entry whose x is nearest. */
        CLOSEST
    }
}
