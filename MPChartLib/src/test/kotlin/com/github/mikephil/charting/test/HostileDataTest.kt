package com.github.mikephil.charting.test

import com.github.mikephil.charting.buffer.BarBuffer
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.DataSet
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.EntryXComparator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HostileDataTest {

    @Test(timeout = 5000)
    fun lookupsFinishWhenAnXIsNotANumber() {
        val set = LineDataSet(listOf(Entry(0f, 1f), Entry(1f, 2f), Entry(2f, 3f)), "")
        assertEquals(-1, set.getEntryIndex(Float.NaN, Float.NaN, DataSet.Rounding.CLOSEST))
        set.calcMinMaxY(Float.NaN, Float.NaN)

        val brokenX = LineDataSet(listOf(Entry(0f, 1f), Entry(Float.NaN, 2f), Entry(2f, 3f)), "")
        assertTrue(brokenX.getEntryIndex(1.5f, Float.NaN, DataSet.Rounding.CLOSEST) in 0..2)

        val comparator = EntryXComparator()
        assertEquals(-comparator.compare(Entry(1f, 0f), Entry(Float.NaN, 0f)), comparator.compare(Entry(Float.NaN, 0f), Entry(1f, 0f)))
    }

    @Test
    fun infiniteValuesStayOutOfTheRanges() {
        val line = LineDataSet(listOf(Entry(0f, 1f), Entry(1f, Float.POSITIVE_INFINITY), Entry(2f, -3f)), "")
        assertEquals(1f, line.yMax, 0f)
        assertEquals(-3f, line.yMin, 0f)
        assertTrue(line.hasNonFiniteY)

        val bars = BarDataSet(listOf(BarEntry(0f, 2f), BarEntry(1f, Float.NEGATIVE_INFINITY)), "")
        assertEquals(2f, bars.yMax, 0f)
        assertEquals(2f, bars.yMin, 0f)

        val candles = CandleDataSet(listOf(CandleEntry(0f, 4f, 1f, 2f, 3f), CandleEntry(1f, Float.POSITIVE_INFINITY, 0f, 1f, 1f)), "")
        assertEquals(4f, candles.yMax, 0f)
        assertEquals(1f, candles.yMin, 0f)

        val data = LineData(LineDataSet(mutableListOf(Entry(0f, 1f)), ""))
        data.addEntry(Entry(1f, Float.POSITIVE_INFINITY), 0)
        assertEquals(1f, data.yMax, 0f)
        assertEquals(1f, data.xMax, 0f)
    }

    @Test
    fun barBufferStaysInBoundsWhenAStackGrows() {
        val set = BarDataSet(mutableListOf(BarEntry(0f, listOf(1f, 2f))), "")
        set.addEntry(BarEntry(1f, listOf(1f, 2f, 3f)))
        assertEquals(3, set.stackSize)

        val entry = BarEntry(0f, listOf(1f, 2f))
        val grown = BarDataSet(listOf(entry), "")
        entry.stackValues = listOf(1f, 2f, 3f, 4f)
        val buffer = BarBuffer(grown.entryCount * 4 * grown.stackSize, 1, true)
        buffer.feed(grown)
        assertEquals(8, buffer.filledSize)
    }

    @Test
    fun highlightsForMissingEntriesFindNothing() {
        val pie = PieData(PieDataSet(listOf(PieEntry(1f), PieEntry(2f)), "pie"))
        assertNull(pie.getEntryForHighlight(Highlight(5f, 0f, 0)))
        assertNull(pie.getEntryForHighlight(Highlight(-1f, 0f, 0)))

        val noSet = PieData()
        assertNull(noSet.getEntryForHighlight(Highlight(0f, 0f, 0)))
        assertNull(noSet.dataSet)
        assertNull(noSet.getDataSetByIndex(0))
        assertNull(noSet.getDataSetByLabel("pie", true))
        assertEquals(0f, noSet.yValueSum, 0f)

        val radar = RadarData(RadarDataSet(listOf(RadarEntry(1f)), "radar"))
        assertNull(radar.getEntryForHighlight(Highlight(3f, 0f, 0)))
    }

    @Test
    fun emptyColorListsFallBackInsteadOfThrowing() {
        val line = LineDataSet(listOf(Entry(0f, 1f)), "").apply {
            colors = emptyList()
            valueTextColors = emptyList()
            resetCircleColors()
        }
        assertEquals(android.graphics.Color.BLACK, line.getColor(3))
        assertEquals(android.graphics.Color.BLACK, line.color)
        assertEquals(android.graphics.Color.BLACK, line.getValueTextColor(3))
        assertEquals(android.graphics.Color.BLACK, line.getCircleColor(3))

        val bars = BarDataSet(listOf(BarEntry(0f, 1f)), "").apply { fills = emptyList() }
        assertEquals(com.github.mikephil.charting.utils.Fill.Type.EMPTY, bars.getFill(0).type)
    }
}
