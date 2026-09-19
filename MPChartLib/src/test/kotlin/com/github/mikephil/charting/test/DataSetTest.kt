package com.github.mikephil.charting.test

import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.DataSet
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.data.ScatterDataSet
import com.github.mikephil.charting.highlight.HighlightLineSpan
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DataSetTest {

    @Test
    fun newHighlightAndStackStylingIsCopied() {
        val line = LineDataSet(listOf(Entry(0f, 1f)), "line")
        line.verticalHighlightIndicatorSpan = HighlightLineSpan.TO_ENTRY
        line.horizontalHighlightIndicatorSpan = HighlightLineSpan.FROM_ENTRY
        val lineCopy = line.copy() as LineDataSet
        assertEquals(HighlightLineSpan.TO_ENTRY, lineCopy.verticalHighlightIndicatorSpan)
        assertEquals(HighlightLineSpan.FROM_ENTRY, lineCopy.horizontalHighlightIndicatorSpan)

        val bar = BarDataSet(listOf(BarEntry(0f, listOf(1f, 2f))), "bar")
        bar.isStackSectionsRounded = true
        assertTrue((bar.copy() as BarDataSet).isStackSectionsRounded)
    }

    @Test
    fun calcMinMax() {
        val set = ScatterDataSet(listOf(Entry(10f, 10f), Entry(15f, 2f), Entry(21f, 5f)), "")

        assertEquals(10f, set.xMin, 0.01f)
        assertEquals(21f, set.xMax, 0.01f)
        assertEquals(2f, set.yMin, 0.01f)
        assertEquals(10f, set.yMax, 0.01f)
        assertEquals(3, set.entryCount)

        set.addEntry(Entry(25f, 1f))

        assertEquals(10f, set.xMin, 0.01f)
        assertEquals(25f, set.xMax, 0.01f)
        assertEquals(1f, set.yMin, 0.01f)
        assertEquals(10f, set.yMax, 0.01f)
        assertEquals(4, set.entryCount)

        set.removeEntry(3)

        assertEquals(10f, set.xMin, 0.01f)
        assertEquals(21f, set.xMax, 0.01f)
        assertEquals(2f, set.yMin, 0.01f)
        assertEquals(10f, set.yMax, 0.01f)
    }

    @Test
    fun addRemoveEntry() {
        val set = ScatterDataSet(listOf(Entry(10f, 10f), Entry(15f, 2f), Entry(21f, 5f)), "")

        assertEquals(3, set.entryCount)

        set.addEntryOrdered(Entry(5f, 1f))

        assertEquals(4, set.entryCount)
        assertEquals(5f, set.xMin, 0.01f)
        assertEquals(21f, set.xMax, 0.01f)
        assertEquals(1f, set.yMin, 0.01f)
        assertEquals(10f, set.yMax, 0.01f)
        assertEquals(5f, set.getEntryForIndex(0).x, 0.01f)
        assertEquals(1f, set.getEntryForIndex(0).y, 0.01f)

        set.addEntryOrdered(Entry(20f, 50f))

        assertEquals(5, set.entryCount)
        assertEquals(20f, set.getEntryForIndex(3).x, 0.01f)
        assertEquals(50f, set.getEntryForIndex(3).y, 0.01f)

        assertTrue(set.removeEntry(3))

        assertEquals(4, set.entryCount)
        assertEquals(21f, set.getEntryForIndex(3).x, 0.01f)
        assertEquals(5f, set.getEntryForIndex(3).y, 0.01f)
        assertEquals(5f, set.getEntryForIndex(0).x, 0.01f)
        assertEquals(1f, set.getEntryForIndex(0).y, 0.01f)

        assertTrue(set.removeFirst())

        assertEquals(3, set.entryCount)
        assertEquals(10f, set.getEntryForIndex(0).x, 0.01f)
        assertEquals(10f, set.getEntryForIndex(0).y, 0.01f)

        set.addEntryOrdered(Entry(15f, 3f))

        assertEquals(4, set.entryCount)
        assertEquals(15f, set.getEntryForIndex(1).x, 0.01f)
        assertEquals(3f, set.getEntryForIndex(1).y, 0.01f)
        assertEquals(21f, set.getEntryForIndex(3).x, 0.01f)
        assertEquals(5f, set.getEntryForIndex(3).y, 0.01f)

        assertTrue(set.removeLast())

        assertEquals(3, set.entryCount)
        assertEquals(15f, set.getEntryForIndex(2).x, 0.01f)
        assertEquals(2f, set.getEntryForIndex(2).y, 0.01f)

        assertTrue(set.removeLast())
        assertEquals(2, set.entryCount)

        assertTrue(set.removeLast())
        assertEquals(1, set.entryCount)
        assertEquals(10f, set.getEntryForIndex(0).x, 0.01f)
        assertEquals(10f, set.getEntryForIndex(0).y, 0.01f)

        assertTrue(set.removeLast())
        assertEquals(0, set.entryCount)

        assertFalse(set.removeLast())
        assertFalse(set.removeFirst())
    }

    @Test
    fun getEntryForXValue() {
        val set = ScatterDataSet(listOf(Entry(10f, 10f), Entry(15f, 5f), Entry(21f, 5f)), "")

        var closest = set.getEntryForXValue(17f, Float.NaN, DataSet.Rounding.CLOSEST)!!
        assertEquals(15f, closest.x, 0.01f)
        assertEquals(5f, closest.y, 0.01f)

        closest = set.getEntryForXValue(17f, Float.NaN, DataSet.Rounding.DOWN)!!
        assertEquals(15f, closest.x, 0.01f)
        assertEquals(5f, closest.y, 0.01f)

        closest = set.getEntryForXValue(15f, Float.NaN, DataSet.Rounding.DOWN)!!
        assertEquals(15f, closest.x, 0.01f)
        assertEquals(5f, closest.y, 0.01f)

        closest = set.getEntryForXValue(14f, Float.NaN, DataSet.Rounding.DOWN)!!
        assertEquals(10f, closest.x, 0.01f)
        assertEquals(10f, closest.y, 0.01f)

        closest = set.getEntryForXValue(17f, Float.NaN, DataSet.Rounding.UP)!!
        assertEquals(21f, closest.x, 0.01f)
        assertEquals(5f, closest.y, 0.01f)

        closest = set.getEntryForXValue(21f, Float.NaN, DataSet.Rounding.UP)!!
        assertEquals(21f, closest.x, 0.01f)
        assertEquals(5f, closest.y, 0.01f)

        closest = set.getEntryForXValue(21f, Float.NaN, DataSet.Rounding.CLOSEST)!!
        assertEquals(21f, closest.x, 0.01f)
        assertEquals(5f, closest.y, 0.01f)
    }

    @Test
    fun getEntryForXValueWithDuplicates() {
        val values = listOf(
            Entry(0f, 10f), Entry(1f, 20f), Entry(2f, 30f), Entry(3f, 40f), Entry(3f, 50f),
            Entry(4f, 60f), Entry(4f, 70f), Entry(5f, 80f), Entry(6f, 90f), Entry(7f, 100f),
            Entry(8f, 110f), Entry(8f, 120f)
        )
        val set = ScatterDataSet(values, "")

        var closest = set.getEntryForXValue(0f, Float.NaN, DataSet.Rounding.CLOSEST)!!
        assertEquals(0f, closest.x, 0.01f)
        assertEquals(10f, closest.y, 0.01f)

        closest = set.getEntryForXValue(5f, Float.NaN, DataSet.Rounding.CLOSEST)!!
        assertEquals(5f, closest.x, 0.01f)
        assertEquals(80f, closest.y, 0.01f)

        closest = set.getEntryForXValue(5.4f, Float.NaN, DataSet.Rounding.CLOSEST)!!
        assertEquals(5f, closest.x, 0.01f)
        assertEquals(80f, closest.y, 0.01f)

        closest = set.getEntryForXValue(4.6f, Float.NaN, DataSet.Rounding.CLOSEST)!!
        assertEquals(5f, closest.x, 0.01f)
        assertEquals(80f, closest.y, 0.01f)

        closest = set.getEntryForXValue(7f, Float.NaN, DataSet.Rounding.CLOSEST)!!
        assertEquals(7f, closest.x, 0.01f)
        assertEquals(100f, closest.y, 0.01f)

        closest = set.getEntryForXValue(4f, Float.NaN, DataSet.Rounding.CLOSEST)!!
        assertEquals(4f, closest.x, 0.01f)
        assertEquals(60f, closest.y, 0.01f)

        var entries = set.getEntriesForXValue(4f)
        assertEquals(2, entries.size)
        assertEquals(60f, entries[0].y, 0.01f)
        assertEquals(70f, entries[1].y, 0.01f)

        entries = set.getEntriesForXValue(3.5f)
        assertEquals(0, entries.size)

        entries = set.getEntriesForXValue(2f)
        assertEquals(1, entries.size)
        assertEquals(30f, entries[0].y, 0.01f)
    }

    @Test
    fun radarCornerRadiusIsNeverNegativeAndIsCopied() {
        val set = RadarDataSet(listOf(RadarEntry(4f), RadarEntry(8f), RadarEntry(6f)), "skills")
        assertEquals(0f, set.cornerRadius, 0.01f)

        set.cornerRadius = 12f
        assertEquals(12f, set.cornerRadius, 0.01f)

        set.cornerRadius = -5f
        assertEquals(0f, set.cornerRadius, 0.01f)

        set.cornerRadius = 9f
        val copy = set.copy() as RadarDataSet<*>
        assertEquals(9f, copy.cornerRadius, 0.01f)
    }
}
