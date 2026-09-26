package com.github.mikephil.charting.test

import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CombinedDataTest {

    @Test
    fun rangeCoversAllContainedData() {
        val data = CombinedData()
        data.lineData = LineData(LineDataSet(listOf(Entry(0f, 5f), Entry(4f, 50f)), "line"))
        data.barData = BarData(BarDataSet(listOf(BarEntry(1f, -10f), BarEntry(6f, 20f)), "bars"))
        data.notifyDataChanged()

        assertEquals(2, data.allData.size)
        assertEquals(0f, data.xMin, 0.001f)
        assertEquals(6f, data.xMax, 0.001f)
        assertEquals(-10f, data.yMin, 0.001f)
        assertEquals(50f, data.yMax, 0.001f)
        assertEquals(1, data.getDataIndex(data.barData!!))
    }

    @Test
    fun highlightWithoutADataIndexFindsNothing() {
        val data = CombinedData()
        data.lineData = LineData(LineDataSet(listOf(Entry(0f, 5f)), "line"))
        data.notifyDataChanged()

        assertNull(data.getEntryForHighlight(Highlight(0f, 5f, 0)))
        assertNull(data.getDataSetByHighlight(Highlight(0f, 5f, 0)))
    }
}
