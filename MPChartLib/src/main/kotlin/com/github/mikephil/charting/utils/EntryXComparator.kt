package com.github.mikephil.charting.utils

import com.github.mikephil.charting.data.Entry

/**
 * Orders entries by their x value, ascending. Use it to sort entries before creating a data set,
 * which expects them in x order.
 */
class EntryXComparator : Comparator<Entry<*>> {

    override fun compare(entry1: Entry<*>, entry2: Entry<*>): Int {
        val diff = entry1.x - entry2.x
        return when {
            diff == 0f -> 0
            diff > 0f -> 1
            else -> -1
        }
    }
}
