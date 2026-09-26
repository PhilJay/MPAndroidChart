package com.github.mikephil.charting.test

import android.graphics.Paint
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.renderer.YAxisRenderer
import com.github.mikephil.charting.utils.ViewPortHandler
import org.junit.Assert.assertEquals
import org.junit.Test

class AxisRendererTest {

    private fun compute(axis: YAxis, min: Float, max: Float): FloatArray {
        YAxisRenderer(ViewPortHandler(), axis, null).computeAxis(min, max, false)
        return axis.entries
    }

    @Test
    fun forcedLabelsStayOnTheGranularity() {
        val yAxis = YAxis()
        yAxis.labelCount = 6
        yAxis.isForceLabelsEnabled = true
        yAxis.granularity = 1f
        val entries = compute(yAxis, 0f, 4f)
        assertEquals(5, yAxis.entryCount)
        assertEquals(1f, entries[1] - entries[0], 0.01f)
        assertEquals(0f, entries[0], 0.01f)
        assertEquals(4f, entries[4], 0.01f)
    }

    @Test
    fun computeAxisValues() {
        var yAxis = YAxis()
        yAxis.labelCount = 6
        var entries = compute(yAxis, 0f, 100f)
        assertEquals(6, entries.size)
        assertEquals(20f, entries[1] - entries[0], 0.01f)
        assertEquals(0f, entries[0], 0.01f)
        assertEquals(100f, entries[entries.size - 1], 0.01f)

        yAxis = YAxis()
        yAxis.labelCount = 6
        yAxis.granularity = 50f
        entries = compute(yAxis, 0f, 100f)
        assertEquals(3, entries.size)
        assertEquals(50f, entries[1] - entries[0], 0.01f)
        assertEquals(0f, entries[0], 0.01f)
        assertEquals(100f, entries[entries.size - 1], 0.01f)

        yAxis = YAxis()
        yAxis.labelCount = 5
        yAxis.isForceLabelsEnabled = true
        entries = compute(yAxis, 0f, 100f)
        assertEquals(5, entries.size)
        assertEquals(25f, entries[1] - entries[0], 0.01f)
        assertEquals(0f, entries[0], 0.01f)
        assertEquals(100f, entries[entries.size - 1], 0.01f)

        yAxis = YAxis()
        yAxis.labelCount = 5
        yAxis.isForceLabelsEnabled = true
        entries = compute(yAxis, 0f, 0.01f)
        assertEquals(5, entries.size)
        assertEquals(0.0025f, entries[1] - entries[0], 0.0001f)
        assertEquals(0f, entries[0], 0.0001f)
        assertEquals(0.01f, entries[entries.size - 1], 0.0001f)

        yAxis = YAxis()
        yAxis.labelCount = 5
        entries = compute(yAxis, 0f, 0.01f)
        assertEquals(5, entries.size)
        assertEquals(0.0020f, entries[1] - entries[0], 0.0001f)
        assertEquals(0f, entries[0], 0.0001f)
        assertEquals(0.0080f, entries[entries.size - 1], 0.0001f)

        yAxis = YAxis()
        yAxis.labelCount = 6
        entries = compute(yAxis, -50f, 50f)
        assertEquals(5, entries.size)
        assertEquals(-40f, entries[0], 0.0001f)
        assertEquals(0f, entries[2], 0.0001f)
        assertEquals(40f, entries[entries.size - 1], 0.0001f)

        yAxis = YAxis()
        yAxis.labelCount = 6
        entries = compute(yAxis, -50f, 100f)
        assertEquals(5, entries.size)
        assertEquals(-30f, entries[0], 0.0001f)
        assertEquals(30f, entries[2], 0.0001f)
        assertEquals(90f, entries[entries.size - 1], 0.0001f)
    }

    @Test
    fun rotatedLabelsNeedTheWidthOfTheirRotatedBounds() {
        val paint = object : Paint() {
            override fun measureText(text: String): Float = 100f
        }
        val yAxis = YAxis()
        yAxis.xOffset = 0f

        assertEquals(100f, yAxis.getRequiredWidthSpace(paint), 0.01f)
        yAxis.labelRotationAngle = 90f
        assertEquals(0f, yAxis.getRequiredWidthSpace(paint), 0.01f)
    }
}
