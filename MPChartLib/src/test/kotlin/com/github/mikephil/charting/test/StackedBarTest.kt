package com.github.mikephil.charting.test

import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StackedBarTest {

    @Test
    fun stackedEntriesSplitIntoPositiveAndNegativeParts() {
        val stacked = BarEntry(0f, listOf(2f, -3f, 5f))
        assertTrue(stacked.isStacked)
        assertEquals(4f, stacked.y, 0.001f)
        assertEquals(7f, stacked.positiveSum, 0.001f)
        assertEquals(3f, stacked.negativeSum, 0.001f)
        assertEquals(2f, stacked.getSumBelow(0), 0.001f)
        assertEquals(0f, stacked.getSumBelow(2), 0.001f)
        val ranges = checkNotNull(stacked.ranges)
        assertEquals(3, ranges.size)
        assertEquals(0f, ranges[0].from, 0.001f)
        assertEquals(2f, ranges[0].to, 0.001f)
        assertEquals(-3f, ranges[1].from, 0.001f)
        assertEquals(0f, ranges[1].to, 0.001f)
        assertEquals(2f, ranges[2].from, 0.001f)
        assertEquals(7f, ranges[2].to, 0.001f)

        val set = BarDataSet(listOf(stacked, BarEntry(1f, listOf(1f, 1f, 1f))), "stacks")
        assertTrue(set.isStacked)
        assertEquals(3, set.stackSize)
        assertEquals(-3f, set.yMin, 0.001f)
        assertEquals(7f, set.yMax, 0.001f)

        val flat = BarDataSet(listOf(BarEntry(0f, 4f)), "flat")
        assertFalse(flat.isStacked)
        assertEquals(1, flat.stackSize)
    }
}
