package com.github.mikephil.charting.test

import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EntryIndexLookupTest {

    /** Counts every entry a lookup touched, including the ones a list-wide scan would walk past. */
    private class CountingList<T>(private val backing: MutableList<T>) : MutableList<T> by backing {

        var reads = 0
            private set

        override fun get(index: Int): T {
            reads++
            return backing[index]
        }

        override fun indexOf(element: T): Int {
            for (i in backing.indices) if (get(i) == element) return i
            return -1
        }
    }

    private fun sortedEntries(n: Int) = ArrayList<Entry<Nothing>>(n).apply {
        for (i in 0 until n) add(Entry(i.toFloat(), i * 2f))
    }

    private fun setOf(entries: ArrayList<Entry<Nothing>>) = LineDataSet(entries, "line")

    @Test
    fun findsTheIndexOfAnEntryOfASortedSet() {
        val entries = sortedEntries(50)
        val set = setOf(entries)

        assertEquals(0, set.getEntryIndex(entries[0]))
        assertEquals(23, set.getEntryIndex(entries[23]))
        assertEquals(49, set.getEntryIndex(entries[49]))
    }

    @Test
    fun picksTheInstanceItWasGivenWhenSeveralEntriesShareAnX() {
        val entries = arrayListOf(
            Entry(0f, 1f),
            Entry(1f, 10f),
            Entry(1f, 20f),
            Entry(1f, 30f),
            Entry(2f, 4f),
        )
        val set = setOf(entries)

        assertEquals(1, set.getEntryIndex(entries[1]))
        assertEquals(2, set.getEntryIndex(entries[2]))
        assertEquals(3, set.getEntryIndex(entries[3]))
        assertEquals(-1, set.getEntryIndex(Entry(1f, 20f)))
    }

    @Test
    fun returnsMinusOneForAnEntryThatIsNotInTheSet() {
        val set = setOf(sortedEntries(20))

        assertEquals(-1, set.getEntryIndex(Entry(7f, 14f)))
        assertEquals(-1, set.getEntryIndex(Entry(100f, 3f)))
        assertEquals(-1, set.getEntryIndex(Entry(-5f, 3f)))
    }

    @Test
    fun tellsTwoEntriesWithTheSameXAndYApart() {
        val first = Entry(5f, 9f)
        val second = Entry(5f, 9f)
        val set = setOf(arrayListOf(Entry(4f, 1f), first, second, Entry(6f, 1f)))

        assertEquals(1, set.getEntryIndex(first))
        assertEquals(2, set.getEntryIndex(second))
    }

    @Test
    fun findsTheIndexInASetThatIsNotSortedByX() {
        val entries = arrayListOf(Entry(9f, 1f), Entry(2f, 2f), Entry(7f, 3f), Entry(0f, 4f), Entry(4f, 5f))
        val set = setOf(entries)

        for (i in entries.indices) assertEquals(i, set.getEntryIndex(entries[i]))
        assertEquals(-1, set.getEntryIndex(Entry(7f, 3f)))
    }

    @Test
    fun findsTheIndexWithoutReadingEveryEntry() {
        val entries = sortedEntries(100_000)
        val set = setOf(entries)
        val counting = CountingList(entries)
        set.entries = counting

        val wanted = entries[90_000]
        val before = counting.reads
        assertEquals(90_000, set.getEntryIndex(wanted))

        val read = counting.reads - before
        assertTrue("read $read of ${entries.size} entries to find one of them", read <= 100)
    }
}
