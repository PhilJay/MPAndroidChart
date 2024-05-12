package com.github.mikephil.charting.test

import com.github.mikephil.charting.data.DataSet
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.ScatterDataSet
import org.junit.Assert
import org.junit.Test

class DataSetTest {
    @Test
    fun testCalcMinMax() {
        val entries: MutableList<Entry> = ArrayList()
        entries.add(Entry(10f, 10f))
        entries.add(Entry(15f, 2f))
        entries.add(Entry(21f, 5f))

        val set = ScatterDataSet(entries, "")

        Assert.assertEquals(10f, set.xMin, 0.01f)
        Assert.assertEquals(21f, set.xMax, 0.01f)

        Assert.assertEquals(2f, set.yMin, 0.01f)
        Assert.assertEquals(10f, set.yMax, 0.01f)

        Assert.assertEquals(3, set.entryCount)

        set.addEntry(Entry(25f, 1f))

        Assert.assertEquals(10f, set.xMin, 0.01f)
        Assert.assertEquals(25f, set.xMax, 0.01f)

        Assert.assertEquals(1f, set.yMin, 0.01f)
        Assert.assertEquals(10f, set.yMax, 0.01f)

        Assert.assertEquals(4, set.entryCount)

        set.removeEntry(3)

        Assert.assertEquals(10f, set.xMin, 0.01f)
        Assert.assertEquals(21f, set.xMax, 0.01f)

        Assert.assertEquals(2f, set.yMin, 0.01f)
        Assert.assertEquals(10f, set.yMax, 0.01f)
    }

    @Test
    fun testAddRemoveEntry() {
        val entries: MutableList<Entry> = ArrayList()
        entries.add(Entry(10f, 10f))
        entries.add(Entry(15f, 2f))
        entries.add(Entry(21f, 5f))

        val set = ScatterDataSet(entries, "")

        Assert.assertEquals(3, set.entryCount)

        set.addEntryOrdered(Entry(5f, 1f))

        Assert.assertEquals(4, set.entryCount)

        Assert.assertEquals(5f, set.xMin, 0.01f)
        Assert.assertEquals(21f, set.xMax, 0.01f)

        Assert.assertEquals(1f, set.yMin, 0.01f)
        Assert.assertEquals(10f, set.yMax, 0.01f)

        Assert.assertEquals(5f, set.getEntryForIndex(0).x, 0.01f)
        Assert.assertEquals(1f, set.getEntryForIndex(0).y, 0.01f)

        set.addEntryOrdered(Entry(20f, 50f))

        Assert.assertEquals(5, set.entryCount)

        Assert.assertEquals(20f, set.getEntryForIndex(3).x, 0.01f)
        Assert.assertEquals(50f, set.getEntryForIndex(3).y, 0.01f)

        Assert.assertTrue(set.removeEntry(3))

        Assert.assertEquals(4, set.entryCount)

        Assert.assertEquals(21f, set.getEntryForIndex(3).x, 0.01f)
        Assert.assertEquals(5f, set.getEntryForIndex(3).y, 0.01f)

        Assert.assertEquals(5f, set.getEntryForIndex(0).x, 0.01f)
        Assert.assertEquals(1f, set.getEntryForIndex(0).y, 0.01f)

        Assert.assertTrue(set.removeFirst())

        Assert.assertEquals(3, set.entryCount)

        Assert.assertEquals(10f, set.getEntryForIndex(0).x, 0.01f)
        Assert.assertEquals(10f, set.getEntryForIndex(0).y, 0.01f)

        set.addEntryOrdered(Entry(15f, 3f))

        Assert.assertEquals(4, set.entryCount)

        Assert.assertEquals(15f, set.getEntryForIndex(1).x, 0.01f)
        Assert.assertEquals(3f, set.getEntryForIndex(1).y, 0.01f)

        Assert.assertEquals(21f, set.getEntryForIndex(3).x, 0.01f)
        Assert.assertEquals(5f, set.getEntryForIndex(3).y, 0.01f)

        Assert.assertTrue(set.removeLast())

        Assert.assertEquals(3, set.entryCount)

        Assert.assertEquals(15f, set.getEntryForIndex(2).x, 0.01f)
        Assert.assertEquals(2f, set.getEntryForIndex(2).y, 0.01f)

        Assert.assertTrue(set.removeLast())

        Assert.assertEquals(2, set.entryCount)

        Assert.assertTrue(set.removeLast())

        Assert.assertEquals(1, set.entryCount)

        Assert.assertEquals(10f, set.getEntryForIndex(0).x, 0.01f)
        Assert.assertEquals(10f, set.getEntryForIndex(0).y, 0.01f)

        Assert.assertTrue(set.removeLast())

        Assert.assertEquals(0, set.entryCount)

        Assert.assertFalse(set.removeLast())
        Assert.assertFalse(set.removeFirst())
    }

    @Test
    fun testGetEntryForXValue() {
        val entries: MutableList<Entry> = ArrayList()
        entries.add(Entry(10f, 10f))
        entries.add(Entry(15f, 5f))
        entries.add(Entry(21f, 5f))

        val set = ScatterDataSet(entries, "")

        var closest = set.getEntryForXValue(17f, Float.NaN, DataSet.Rounding.CLOSEST)
        Assert.assertEquals(15f, closest.x, 0.01f)
        Assert.assertEquals(5f, closest.y, 0.01f)

        closest = set.getEntryForXValue(17f, Float.NaN, DataSet.Rounding.DOWN)
        Assert.assertEquals(15f, closest.x, 0.01f)
        Assert.assertEquals(5f, closest.y, 0.01f)

        closest = set.getEntryForXValue(15f, Float.NaN, DataSet.Rounding.DOWN)
        Assert.assertEquals(15f, closest.x, 0.01f)
        Assert.assertEquals(5f, closest.y, 0.01f)

        closest = set.getEntryForXValue(14f, Float.NaN, DataSet.Rounding.DOWN)
        Assert.assertEquals(10f, closest.x, 0.01f)
        Assert.assertEquals(10f, closest.y, 0.01f)

        closest = set.getEntryForXValue(17f, Float.NaN, DataSet.Rounding.UP)
        Assert.assertEquals(21f, closest.x, 0.01f)
        Assert.assertEquals(5f, closest.y, 0.01f)

        closest = set.getEntryForXValue(21f, Float.NaN, DataSet.Rounding.UP)
        Assert.assertEquals(21f, closest.x, 0.01f)
        Assert.assertEquals(5f, closest.y, 0.01f)

        closest = set.getEntryForXValue(21f, Float.NaN, DataSet.Rounding.CLOSEST)
        Assert.assertEquals(21f, closest.x, 0.01f)
        Assert.assertEquals(5f, closest.y, 0.01f)
    }

    @Test
    fun testGetEntryForXValueWithDuplicates() {
        // sorted list of values (by x position)

        val values: MutableList<Entry> = ArrayList()
        values.add(Entry(0f, 10f))
        values.add(Entry(1f, 20f))
        values.add(Entry(2f, 30f))
        values.add(Entry(3f, 40f))
        values.add(Entry(3f, 50f)) // duplicate
        values.add(Entry(4f, 60f))
        values.add(Entry(4f, 70f)) // duplicate
        values.add(Entry(5f, 80f))
        values.add(Entry(6f, 90f))
        values.add(Entry(7f, 100f))
        values.add(Entry(8f, 110f))
        values.add(Entry(8f, 120f)) // duplicate

        val set = ScatterDataSet(values, "")

        var closest = set.getEntryForXValue(0f, Float.NaN, DataSet.Rounding.CLOSEST)
        Assert.assertEquals(0f, closest.x, 0.01f)
        Assert.assertEquals(10f, closest.y, 0.01f)

        closest = set.getEntryForXValue(5f, Float.NaN, DataSet.Rounding.CLOSEST)
        Assert.assertEquals(5f, closest.x, 0.01f)
        Assert.assertEquals(80f, closest.y, 0.01f)

        closest = set.getEntryForXValue(5.4f, Float.NaN, DataSet.Rounding.CLOSEST)
        Assert.assertEquals(5f, closest.x, 0.01f)
        Assert.assertEquals(80f, closest.y, 0.01f)

        closest = set.getEntryForXValue(4.6f, Float.NaN, DataSet.Rounding.CLOSEST)
        Assert.assertEquals(5f, closest.x, 0.01f)
        Assert.assertEquals(80f, closest.y, 0.01f)

        closest = set.getEntryForXValue(7f, Float.NaN, DataSet.Rounding.CLOSEST)
        Assert.assertEquals(7f, closest.x, 0.01f)
        Assert.assertEquals(100f, closest.y, 0.01f)

        closest = set.getEntryForXValue(4f, Float.NaN, DataSet.Rounding.CLOSEST)
        Assert.assertEquals(4f, closest.x, 0.01f)
        Assert.assertEquals(60f, closest.y, 0.01f)

        var entries = set.getEntriesForXValue(4f)
        Assert.assertEquals(2, entries.size)
        Assert.assertEquals(60f, entries[0].y, 0.01f)
        Assert.assertEquals(70f, entries[1].y, 0.01f)

        entries = set.getEntriesForXValue(3.5f)
        Assert.assertEquals(0, entries.size)

        entries = set.getEntriesForXValue(2f)
        Assert.assertEquals(1, entries.size)
        Assert.assertEquals(30f, entries[0].y, 0.01f)
    }
}
