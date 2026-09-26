package com.github.mikephil.charting.utils

import com.github.mikephil.charting.data.Entry

/**
 * Orders entries by their x value, ascending. Use it to sort entries before creating a data set,
 * which expects them in x order.
 */
public class EntryXComparator : Comparator<Entry<*>> {

    override fun compare(entry1: Entry<*>, entry2: Entry<*>): Int {
        if (entry1.x == entry2.x) return 0
        return entry1.x.compareTo(entry2.x)
    }
}
