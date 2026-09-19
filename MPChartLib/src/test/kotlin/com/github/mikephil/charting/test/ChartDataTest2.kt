package com.github.mikephil.charting.test

import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class ChartDataListsTest {

    @Test
    fun exposesLabelsColorsAndLookupsAsLists() {
        val a = LineDataSet(listOf(Entry(0f, 1f)), "A").apply { colors = listOf(1, 2) }
        val b = LineDataSet(listOf(Entry(0f, 2f)), "B").apply { color = 3 }
        val data = LineData(a, b)

        assertEquals(listOf("A", "B"), data.dataSetLabels)
        assertEquals(listOf(1, 2, 3), data.colors)
        assertSame(b, data.getDataSetByLabel("b", ignoreCase = true))
        assertEquals(1, data.getIndexOfDataSet(b))
        assertEquals(2, data.entryCount)
    }
}
