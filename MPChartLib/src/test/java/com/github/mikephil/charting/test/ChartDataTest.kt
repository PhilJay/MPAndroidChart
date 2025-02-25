package com.github.mikephil.charting.test

import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.ScatterData
import com.github.mikephil.charting.data.ScatterDataSet
import org.junit.Assert
import org.junit.Test

class ChartDataTest {
    @Test
    fun testDynamicChartData() {
        val entries1: MutableList<Entry> = ArrayList()
        entries1.add(Entry(10f, 10f))
        entries1.add(Entry(15f, -2f))
        entries1.add(Entry(21f, 50f))

        val set1 = ScatterDataSet(entries1, "")

        val entries2: MutableList<Entry> = ArrayList()
        entries2.add(Entry(-1f, 10f))
        entries2.add(Entry(10f, 2f))
        entries2.add(Entry(20f, 5f))

        val set2 = ScatterDataSet(entries2, "")

        val data = ScatterData(set1, set2)

        Assert.assertEquals(-2f, data.getYMin(YAxis.AxisDependency.LEFT), 0.01f)
        Assert.assertEquals(50f, data.getYMax(YAxis.AxisDependency.LEFT), 0.01f)

        Assert.assertEquals(6, data.entryCount)

        Assert.assertEquals(-1f, data.xMin, 0.01f)
        Assert.assertEquals(21f, data.xMax, 0.01f)

        Assert.assertEquals(-2f, data.yMin, 0.01f)
        Assert.assertEquals(50f, data.yMax, 0.01f)

        Assert.assertEquals(3, data.maxEntryCountSet.entryCount)

        // now add and remove values
        data.addEntry(Entry(-10f, -10f), 0)

        Assert.assertEquals(set1, data.maxEntryCountSet)
        Assert.assertEquals(4, data.maxEntryCountSet.entryCount)

        Assert.assertEquals(-10f, data.getYMin(YAxis.AxisDependency.LEFT), 0.01f)
        Assert.assertEquals(50f, data.getYMax(YAxis.AxisDependency.LEFT), 0.01f)

        Assert.assertEquals(-10f, data.xMin, 0.01f)
        Assert.assertEquals(21f, data.xMax, 0.01f)

        Assert.assertEquals(-10f, data.yMin, 0.01f)
        Assert.assertEquals(50f, data.yMax, 0.01f)

        data.addEntry(Entry(-100f, 100f), 0)
        data.addEntry(Entry(0f, -100f), 0)

        Assert.assertEquals(-100f, data.getYMin(YAxis.AxisDependency.LEFT), 0.01f)
        Assert.assertEquals(100f, data.getYMax(YAxis.AxisDependency.LEFT), 0.01f)

        // right axis will adapt left axis values if no right axis values are present
        Assert.assertEquals(-100f, data.getYMin(YAxis.AxisDependency.RIGHT), 0.01f)
        Assert.assertEquals(100f, data.getYMax(YAxis.AxisDependency.RIGHT), 0.01f)

        val entries3: MutableList<Entry> = ArrayList()
        entries3.add(Entry(0f, 200f))
        entries3.add(Entry(0f, -50f))

        val set3 = ScatterDataSet(entries3, "")
        set3.axisDependency = YAxis.AxisDependency.RIGHT

        data.addDataSet(set3)

        Assert.assertEquals(3, data.dataSetCount)

        Assert.assertEquals(-100f, data.getYMin(YAxis.AxisDependency.LEFT), 0.01f)
        Assert.assertEquals(100f, data.getYMax(YAxis.AxisDependency.LEFT), 0.01f)

        Assert.assertEquals(-50f, data.getYMin(YAxis.AxisDependency.RIGHT), 0.01f)
        Assert.assertEquals(200f, data.getYMax(YAxis.AxisDependency.RIGHT), 0.01f)

        val lineData = LineData()

        Assert.assertEquals(Float.MAX_VALUE, lineData.yMin, 0.01f)
        Assert.assertEquals(-Float.MAX_VALUE, lineData.yMax, 0.01f)

        Assert.assertEquals(Float.MAX_VALUE, lineData.getYMin(YAxis.AxisDependency.LEFT), 0.01f)
        Assert.assertEquals(-Float.MAX_VALUE, lineData.getYMax(YAxis.AxisDependency.LEFT), 0.01f)

        Assert.assertEquals(Float.MAX_VALUE, lineData.getYMin(YAxis.AxisDependency.RIGHT), 0.01f)
        Assert.assertEquals(-Float.MAX_VALUE, lineData.getYMax(YAxis.AxisDependency.RIGHT), 0.01f)

        Assert.assertEquals(0, lineData.dataSetCount)

        val lineEntries1: MutableList<Entry> = ArrayList()
        lineEntries1.add(Entry(10f, 90f))
        lineEntries1.add(Entry(1000f, 1000f))

        val lineSet1 = LineDataSet(lineEntries1, "")

        lineData.addDataSet(lineSet1)

        Assert.assertEquals(1, lineData.dataSetCount)
        Assert.assertEquals(2, lineSet1.entryCount)
        Assert.assertEquals(2, lineData.entryCount)

        Assert.assertEquals(10f, lineData.xMin, 0.01f)
        Assert.assertEquals(1000f, lineData.xMax, 0.01f)

        Assert.assertEquals(90f, lineData.yMin, 0.01f)
        Assert.assertEquals(1000f, lineData.yMax, 0.01f)

        Assert.assertEquals(90f, lineData.getYMin(YAxis.AxisDependency.LEFT), 0.01f)
        Assert.assertEquals(1000f, lineData.getYMax(YAxis.AxisDependency.LEFT), 0.01f)

        Assert.assertEquals(90f, lineData.getYMin(YAxis.AxisDependency.RIGHT), 0.01f)
        Assert.assertEquals(1000f, lineData.getYMax(YAxis.AxisDependency.RIGHT), 0.01f)

        val lineEntries2: MutableList<Entry> = ArrayList()
        lineEntries2.add(Entry(-1000f, 2000f))
        lineEntries2.add(Entry(2000f, -3000f))

        val e = Entry(-1000f, 2500f)
        lineEntries2.add(e)

        val lineSet2 = LineDataSet(lineEntries2, "")
        lineSet2.axisDependency = YAxis.AxisDependency.RIGHT

        lineData.addDataSet(lineSet2)

        Assert.assertEquals(2, lineData.dataSetCount)
        Assert.assertEquals(3, lineSet2.entryCount)
        Assert.assertEquals(5, lineData.entryCount)

        Assert.assertEquals(-1000f, lineData.xMin, 0.01f)
        Assert.assertEquals(2000f, lineData.xMax, 0.01f)

        Assert.assertEquals(-3000f, lineData.yMin, 0.01f)
        Assert.assertEquals(2500f, lineData.yMax, 0.01f)

        Assert.assertEquals(90f, lineData.getYMin(YAxis.AxisDependency.LEFT), 0.01f)
        Assert.assertEquals(1000f, lineData.getYMax(YAxis.AxisDependency.LEFT), 0.01f)

        Assert.assertEquals(-3000f, lineData.getYMin(YAxis.AxisDependency.RIGHT), 0.01f)
        Assert.assertEquals(2500f, lineData.getYMax(YAxis.AxisDependency.RIGHT), 0.01f)

        Assert.assertTrue(lineData.removeEntry(e, 1))

        Assert.assertEquals(-1000f, lineData.xMin, 0.01f)
        Assert.assertEquals(2000f, lineData.xMax, 0.01f)

        Assert.assertEquals(-3000f, lineData.yMin, 0.01f)
        Assert.assertEquals(2000f, lineData.yMax, 0.01f)

        Assert.assertEquals(90f, lineData.getYMin(YAxis.AxisDependency.LEFT), 0.01f)
        Assert.assertEquals(1000f, lineData.getYMax(YAxis.AxisDependency.LEFT), 0.01f)

        Assert.assertEquals(-3000f, lineData.getYMin(YAxis.AxisDependency.RIGHT), 0.01f)
        Assert.assertEquals(2000f, lineData.getYMax(YAxis.AxisDependency.RIGHT), 0.01f)

        Assert.assertEquals(2, lineData.dataSetCount)
        Assert.assertTrue(lineData.removeDataSet(lineSet2))
        Assert.assertEquals(1, lineData.dataSetCount)

        Assert.assertEquals(10f, lineData.xMin, 0.01f)
        Assert.assertEquals(1000f, lineData.xMax, 0.01f)

        Assert.assertEquals(90f, lineData.yMin, 0.01f)
        Assert.assertEquals(1000f, lineData.yMax, 0.01f)

        Assert.assertEquals(90f, lineData.getYMin(YAxis.AxisDependency.LEFT), 0.01f)
        Assert.assertEquals(1000f, lineData.getYMax(YAxis.AxisDependency.LEFT), 0.01f)

        Assert.assertEquals(90f, lineData.getYMin(YAxis.AxisDependency.RIGHT), 0.01f)
        Assert.assertEquals(1000f, lineData.getYMax(YAxis.AxisDependency.RIGHT), 0.01f)

        Assert.assertTrue(lineData.removeDataSet(lineSet1))
        Assert.assertEquals(0, lineData.dataSetCount)

        Assert.assertEquals(Float.MAX_VALUE, lineData.xMin, 0.01f)
        Assert.assertEquals(-Float.MAX_VALUE, lineData.xMax, 0.01f)

        Assert.assertEquals(Float.MAX_VALUE, lineData.yMin, 0.01f)
        Assert.assertEquals(-Float.MAX_VALUE, lineData.yMax, 0.01f)

        Assert.assertEquals(Float.MAX_VALUE, lineData.getYMin(YAxis.AxisDependency.LEFT), 0.01f)
        Assert.assertEquals(-Float.MAX_VALUE, lineData.getYMax(YAxis.AxisDependency.LEFT), 0.01f)

        Assert.assertEquals(Float.MAX_VALUE, lineData.getYMin(YAxis.AxisDependency.RIGHT), 0.01f)
        Assert.assertEquals(-Float.MAX_VALUE, lineData.getYMax(YAxis.AxisDependency.RIGHT), 0.01f)

        Assert.assertFalse(lineData.removeDataSet(lineSet1))
        Assert.assertFalse(lineData.removeDataSet(lineSet2))
    }
}
