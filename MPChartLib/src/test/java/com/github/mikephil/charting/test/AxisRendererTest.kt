package com.github.mikephil.charting.test

import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.renderer.AxisRenderer
import com.github.mikephil.charting.renderer.YAxisRenderer
import org.junit.Assert
import org.junit.Test

class AxisRendererTest {
    @Test
    fun testComputeAxisValues() {
        var yAxis = YAxis()
        yAxis.labelCount = 6
        var renderer: AxisRenderer = YAxisRenderer(null, yAxis, null)

        renderer.computeAxis(0f, 100f, false)
        var entries = yAxis.mEntries

        Assert.assertEquals(6, entries.size)
        Assert.assertEquals(20.0, (entries[1] - entries[0]).toDouble(), 0.01) // interval 20
        Assert.assertEquals(0.0, entries[0].toDouble(), 0.01)
        Assert.assertEquals(100.0, entries[entries.size - 1].toDouble(), 0.01)

        yAxis = YAxis()
        yAxis.labelCount = 6
        yAxis.granularity = 50f
        renderer = YAxisRenderer(null, yAxis, null)

        renderer.computeAxis(0f, 100f, false)
        entries = yAxis.mEntries

        Assert.assertEquals(3, entries.size)
        Assert.assertEquals(50.0, (entries[1] - entries[0]).toDouble(), 0.01) // interval 50
        Assert.assertEquals(0.0, entries[0].toDouble(), 0.01)
        Assert.assertEquals(100.0, entries[entries.size - 1].toDouble(), 0.01)

        yAxis = YAxis()
        yAxis.setLabelCount(5, true)
        renderer = YAxisRenderer(null, yAxis, null)

        renderer.computeAxis(0f, 100f, false)
        entries = yAxis.mEntries

        Assert.assertEquals(5, entries.size)
        Assert.assertEquals(25.0, (entries[1] - entries[0]).toDouble(), 0.01) // interval 25
        Assert.assertEquals(0.0, entries[0].toDouble(), 0.01)
        Assert.assertEquals(100.0, entries[entries.size - 1].toDouble(), 0.01)

        yAxis = YAxis()
        yAxis.setLabelCount(5, true)
        renderer = YAxisRenderer(null, yAxis, null)

        renderer.computeAxis(0f, 0.01f, false)
        entries = yAxis.mEntries

        Assert.assertEquals(5, entries.size)
        Assert.assertEquals(0.0025, (entries[1] - entries[0]).toDouble(), 0.0001)
        Assert.assertEquals(0.0, entries[0].toDouble(), 0.0001)
        Assert.assertEquals(0.01, entries[entries.size - 1].toDouble(), 0.0001)

        yAxis = YAxis()
        yAxis.setLabelCount(5, false)
        renderer = YAxisRenderer(null, yAxis, null)

        renderer.computeAxis(0f, 0.01f, false)
        entries = yAxis.mEntries

        Assert.assertEquals(5, entries.size)
        Assert.assertEquals(0.0020, (entries[1] - entries[0]).toDouble(), 0.0001)
        Assert.assertEquals(0.0, entries[0].toDouble(), 0.0001)
        Assert.assertEquals(0.0080, entries[entries.size - 1].toDouble(), 0.0001)

        yAxis = YAxis()
        yAxis.labelCount = 6
        renderer = YAxisRenderer(null, yAxis, null)

        renderer.computeAxis(-50f, 50f, false)
        entries = yAxis.mEntries

        Assert.assertEquals(5, entries.size)
        Assert.assertEquals(-40.0, entries[0].toDouble(), 0.0001)
        Assert.assertEquals(0.0, entries[2].toDouble(), 0.0001)
        Assert.assertEquals(40.0, entries[entries.size - 1].toDouble(), 0.0001)

        yAxis = YAxis()
        yAxis.labelCount = 6
        renderer = YAxisRenderer(null, yAxis, null)

        renderer.computeAxis(-50f, 100f, false)
        entries = yAxis.mEntries

        Assert.assertEquals(5, entries.size)
        Assert.assertEquals(-30.0, entries[0].toDouble(), 0.0001)
        Assert.assertEquals(30.0, entries[2].toDouble(), 0.0001)
        Assert.assertEquals(90.0, entries[entries.size - 1].toDouble(), 0.0001)
    }
}
