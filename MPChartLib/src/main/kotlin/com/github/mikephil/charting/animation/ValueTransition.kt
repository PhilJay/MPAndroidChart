package com.github.mikephil.charting.animation

import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.BubbleEntry
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.ChartData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.interfaces.datasets.IDataSet

/**
 * Moves the values of entries from start values to target values. The values of an entry are its y, its size for a
 * bubble, and y, high, low, open and close for a candle; a stacked bar has its stack values instead.
 *
 * @property dataSets the data sets holding the moved entries, whose ranges must be recomputed after each step.
 */
internal class ValueTransition private constructor(
    private val items: List<Item>,
    val dataSets: List<IDataSet<*>>
) {

    private class Item(val entry: Entry<*>, val from: FloatArray, val to: FloatArray, val targetStack: List<Float>?) {
        var isReleased = false
    }

    /** Writes the values [fraction] of the way from start to target into the entries; 0 is the start, 1 the target. */
    fun apply(fraction: Float) {
        for (item in items) {
            if (item.isReleased) continue
            val values = FloatArray(item.to.size) { item.from[it] + (item.to[it] - item.from[it]) * fraction }
            write(item.entry, values)
        }
    }

    /** Writes the exact target values, and for stacked bars the original stack list, into the entries. */
    fun finish() {
        for (item in items) {
            if (item.isReleased) continue
            val targetStack = item.targetStack
            if (targetStack != null) (item.entry as BarEntry<*>).stackValues = targetStack else write(item.entry, item.to)
        }
    }

    /** Stops moving [entry], so that another animation can take it over. */
    fun release(entry: Entry<*>) {
        for (item in items) {
            if (item.entry === entry) item.isReleased = true
        }
    }

    companion object {

        /**
         * Pairs the entries of [newData] with those of [oldData] by data set index and entry index. Entries past the
         * end of their old data set start at 0. Reads every value before anything is written, so the two data objects
         * may share entries.
         *
         * @return the transition, or null when the shapes differ: another data class, another number of data sets or
         * another data set class at any index.
         */
        fun between(oldData: ChartData<*>, newData: ChartData<*>): ValueTransition? {
            if (oldData::class != newData::class || oldData.dataSetCount != newData.dataSetCount) return null
            for (i in 0 until newData.dataSetCount) {
                if (oldData.dataSets[i]::class != newData.dataSets[i]::class) return null
            }

            val items = ArrayList<Item>(newData.entryCount)
            for (i in 0 until newData.dataSetCount) {
                val oldSet = oldData.dataSets[i]
                val newSet = newData.dataSets[i]
                for (j in 0 until newSet.entryCount) {
                    val target = newSet.getEntryForIndex(j)
                    val old = if (j < oldSet.entryCount) oldSet.getEntryForIndex(j) else null
                    val to = valuesOf(target)
                    val targetStack = (target as? BarEntry<*>)?.stackValues
                    items.add(Item(target, startValues(old, target, to.size), to, targetStack))
                }
            }
            return ValueTransition(items, newData.dataSets.toList())
        }

        /** Moves the y value of [entry] from its current value to [toY]. */
        fun ofY(entry: Entry<*>, toY: Float, dataSet: IDataSet<*>): ValueTransition =
            ValueTransition(listOf(Item(entry, floatArrayOf(entry.y), floatArrayOf(toY), null)), listOf(dataSet))

        private fun valuesOf(entry: Entry<*>): FloatArray = when {
            entry is BarEntry<*> && entry.isStacked -> entry.stackValues.orEmpty().toFloatArray()
            entry is CandleEntry<*> -> floatArrayOf(entry.y, entry.high, entry.low, entry.open, entry.close)
            entry is BubbleEntry<*> -> floatArrayOf(entry.y, entry.size)
            else -> floatArrayOf(entry.y)
        }

        private fun startValues(old: Entry<*>?, target: Entry<*>, size: Int): FloatArray {
            if (old == null) return FloatArray(size)
            if (target is BarEntry<*> && target.isStacked) {
                val values = valuesOf(old)
                return FloatArray(size) { values.getOrElse(it) { 0f } }
            }
            val values = if (old is BarEntry<*> && old.isStacked) floatArrayOf(old.y) else valuesOf(old)
            return if (values.size == size) values else FloatArray(size) { old.y }
        }

        /** Writes [values] as read by [valuesOf]; an array holding only y leaves the other values alone. */
        private fun write(entry: Entry<*>, values: FloatArray) {
            if (entry is BarEntry<*> && entry.isStacked) {
                entry.stackValues = values.asList()
                return
            }
            entry.y = values[0]
            if (values.size == 1) return
            when (entry) {
                is CandleEntry<*> -> {
                    entry.high = values[1]
                    entry.low = values[2]
                    entry.open = values[3]
                    entry.close = values[4]
                }
                is BubbleEntry<*> -> entry.size = values[1]
                else -> {}
            }
        }
    }
}
