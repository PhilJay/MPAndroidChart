package com.github.mikephil.charting.test

import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import org.junit.Assert.assertEquals
import org.junit.Test

class PieDataTest {

    @Test
    fun sumsValuesAndFindsDataSetByLabel() {
        val set = PieDataSet(listOf(PieEntry(10f, "A"), PieEntry(30f, "B"), PieEntry(60f, "C")), "Parties")
        val data = PieData(set)
        assertEquals(100f, data.yValueSum, 0.001f)
        assertEquals(3, data.entryCount)
        assertEquals(60f, data.yMax, 0.001f)
        assertEquals(set, data.getDataSetByLabel("parties", true))
        assertEquals(null, data.getDataSetByLabel("other", true))
        assertEquals("B", set.getEntryForIndex(1).label)
    }
}
