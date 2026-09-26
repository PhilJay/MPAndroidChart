package com.github.mikephil.charting.test

import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.ScatterData
import com.github.mikephil.charting.data.ScatterDataSet
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ChartDataTest {

    @Test
    fun dynamicChartData() {
        val set1 = ScatterDataSet(listOf(Entry(10f, 10f), Entry(15f, -2f), Entry(21f, 50f)), "")
        val set2 = ScatterDataSet(listOf(Entry(-1f, 10f), Entry(10f, 2f), Entry(20f, 5f)), "")

        val data = ScatterData(set1, set2)

        assertEquals(-2f, data.getYMin(YAxis.AxisDependency.LEFT), 0.01f)
        assertEquals(50f, data.getYMax(YAxis.AxisDependency.LEFT), 0.01f)
        assertEquals(6, data.entryCount)
        assertEquals(-1f, data.xMin, 0.01f)
        assertEquals(21f, data.xMax, 0.01f)
        assertEquals(-2f, data.yMin, 0.01f)
        assertEquals(50f, data.yMax, 0.01f)
        assertEquals(3, data.maxEntryCountSet!!.entryCount)

        data.addEntry(Entry(-10f, -10f), 0)

        assertEquals(set1, data.maxEntryCountSet)
        assertEquals(4, data.maxEntryCountSet!!.entryCount)
        assertEquals(-10f, data.getYMin(YAxis.AxisDependency.LEFT), 0.01f)
        assertEquals(50f, data.getYMax(YAxis.AxisDependency.LEFT), 0.01f)
        assertEquals(-10f, data.xMin, 0.01f)
        assertEquals(21f, data.xMax, 0.01f)
        assertEquals(-10f, data.yMin, 0.01f)
        assertEquals(50f, data.yMax, 0.01f)

        data.addEntry(Entry(-100f, 100f), 0)
        data.addEntry(Entry(0f, -100f), 0)

        assertEquals(-100f, data.getYMin(YAxis.AxisDependency.LEFT), 0.01f)
        assertEquals(100f, data.getYMax(YAxis.AxisDependency.LEFT), 0.01f)
        assertEquals(-100f, data.getYMin(YAxis.AxisDependency.RIGHT), 0.01f)
        assertEquals(100f, data.getYMax(YAxis.AxisDependency.RIGHT), 0.01f)

        val set3 = ScatterDataSet(listOf(Entry(0f, 200f), Entry(0f, -50f)), "")
        set3.axisDependency = YAxis.AxisDependency.RIGHT
        data.addDataSet(set3)

        assertEquals(3, data.dataSetCount)
        assertEquals(-100f, data.getYMin(YAxis.AxisDependency.LEFT), 0.01f)
        assertEquals(100f, data.getYMax(YAxis.AxisDependency.LEFT), 0.01f)
        assertEquals(-50f, data.getYMin(YAxis.AxisDependency.RIGHT), 0.01f)
        assertEquals(200f, data.getYMax(YAxis.AxisDependency.RIGHT), 0.01f)

        val lineData = LineData()

        assertEquals(Float.MAX_VALUE, lineData.yMin, 0.01f)
        assertEquals(-Float.MAX_VALUE, lineData.yMax, 0.01f)
        assertEquals(Float.MAX_VALUE, lineData.getYMin(YAxis.AxisDependency.LEFT), 0.01f)
        assertEquals(-Float.MAX_VALUE, lineData.getYMax(YAxis.AxisDependency.LEFT), 0.01f)
        assertEquals(Float.MAX_VALUE, lineData.getYMin(YAxis.AxisDependency.RIGHT), 0.01f)
        assertEquals(-Float.MAX_VALUE, lineData.getYMax(YAxis.AxisDependency.RIGHT), 0.01f)
        assertEquals(0, lineData.dataSetCount)

        val lineSet1 = LineDataSet(listOf(Entry(10f, 90f), Entry(1000f, 1000f)), "")
        lineData.addDataSet(lineSet1)

        assertEquals(1, lineData.dataSetCount)
        assertEquals(2, lineSet1.entryCount)
        assertEquals(2, lineData.entryCount)
        assertEquals(10f, lineData.xMin, 0.01f)
        assertEquals(1000f, lineData.xMax, 0.01f)
        assertEquals(90f, lineData.yMin, 0.01f)
        assertEquals(1000f, lineData.yMax, 0.01f)
        assertEquals(90f, lineData.getYMin(YAxis.AxisDependency.LEFT), 0.01f)
        assertEquals(1000f, lineData.getYMax(YAxis.AxisDependency.LEFT), 0.01f)
        assertEquals(90f, lineData.getYMin(YAxis.AxisDependency.RIGHT), 0.01f)
        assertEquals(1000f, lineData.getYMax(YAxis.AxisDependency.RIGHT), 0.01f)

        val e = Entry(-1000f, 2500f)
        val lineSet2 = LineDataSet(listOf(Entry(-1000f, 2000f), Entry(2000f, -3000f), e), "")
        lineSet2.axisDependency = YAxis.AxisDependency.RIGHT
        lineData.addDataSet(lineSet2)

        assertEquals(2, lineData.dataSetCount)
        assertEquals(3, lineSet2.entryCount)
        assertEquals(5, lineData.entryCount)
        assertEquals(-1000f, lineData.xMin, 0.01f)
        assertEquals(2000f, lineData.xMax, 0.01f)
        assertEquals(-3000f, lineData.yMin, 0.01f)
        assertEquals(2500f, lineData.yMax, 0.01f)
        assertEquals(90f, lineData.getYMin(YAxis.AxisDependency.LEFT), 0.01f)
        assertEquals(1000f, lineData.getYMax(YAxis.AxisDependency.LEFT), 0.01f)
        assertEquals(-3000f, lineData.getYMin(YAxis.AxisDependency.RIGHT), 0.01f)
        assertEquals(2500f, lineData.getYMax(YAxis.AxisDependency.RIGHT), 0.01f)

        assertTrue(lineData.removeEntry(e, 1))

        assertEquals(-1000f, lineData.xMin, 0.01f)
        assertEquals(2000f, lineData.xMax, 0.01f)
        assertEquals(-3000f, lineData.yMin, 0.01f)
        assertEquals(2000f, lineData.yMax, 0.01f)
        assertEquals(90f, lineData.getYMin(YAxis.AxisDependency.LEFT), 0.01f)
        assertEquals(1000f, lineData.getYMax(YAxis.AxisDependency.LEFT), 0.01f)
        assertEquals(-3000f, lineData.getYMin(YAxis.AxisDependency.RIGHT), 0.01f)
        assertEquals(2000f, lineData.getYMax(YAxis.AxisDependency.RIGHT), 0.01f)
        assertEquals(2, lineData.dataSetCount)

        assertTrue(lineData.removeDataSet(lineSet2))

        assertEquals(1, lineData.dataSetCount)
        assertEquals(10f, lineData.xMin, 0.01f)
        assertEquals(1000f, lineData.xMax, 0.01f)
        assertEquals(90f, lineData.yMin, 0.01f)
        assertEquals(1000f, lineData.yMax, 0.01f)
        assertEquals(90f, lineData.getYMin(YAxis.AxisDependency.LEFT), 0.01f)
        assertEquals(1000f, lineData.getYMax(YAxis.AxisDependency.LEFT), 0.01f)
        assertEquals(90f, lineData.getYMin(YAxis.AxisDependency.RIGHT), 0.01f)
        assertEquals(1000f, lineData.getYMax(YAxis.AxisDependency.RIGHT), 0.01f)

        assertTrue(lineData.removeDataSet(lineSet1))

        assertEquals(0, lineData.dataSetCount)
        assertEquals(Float.MAX_VALUE, lineData.xMin, 0.01f)
        assertEquals(-Float.MAX_VALUE, lineData.xMax, 0.01f)
        assertEquals(Float.MAX_VALUE, lineData.yMin, 0.01f)
        assertEquals(-Float.MAX_VALUE, lineData.yMax, 0.01f)
        assertEquals(Float.MAX_VALUE, lineData.getYMin(YAxis.AxisDependency.LEFT), 0.01f)
        assertEquals(-Float.MAX_VALUE, lineData.getYMax(YAxis.AxisDependency.LEFT), 0.01f)
        assertEquals(Float.MAX_VALUE, lineData.getYMin(YAxis.AxisDependency.RIGHT), 0.01f)
        assertEquals(-Float.MAX_VALUE, lineData.getYMax(YAxis.AxisDependency.RIGHT), 0.01f)

        assertFalse(lineData.removeDataSet(lineSet1))
        assertFalse(lineData.removeDataSet(lineSet2))
    }
}
