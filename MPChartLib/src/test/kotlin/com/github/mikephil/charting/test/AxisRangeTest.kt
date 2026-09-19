package com.github.mikephil.charting.test

import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AxisRangeTest {

    @Test
    fun xAxisRangeAddsSpaceUnlessCustomLimitsAreSet() {
        val axis = XAxis()
        axis.spaceMin = 1f
        axis.spaceMax = 2f
        axis.calculate(10f, 20f)
        assertEquals(9f, axis.axisMinimum, 0.001f)
        assertEquals(22f, axis.axisMaximum, 0.001f)
        assertEquals(13f, axis.axisRange, 0.001f)

        axis.axisMinimum = 0f
        assertTrue(axis.isAxisMinCustom)
        axis.calculate(10f, 20f)
        assertEquals(0f, axis.axisMinimum, 0.001f)
        assertEquals(22f, axis.axisMaximum, 0.001f)

        axis.resetAxisMinimum()
        assertFalse(axis.isAxisMinCustom)
        axis.spaceMin = 0f
        axis.spaceMax = 0f
        axis.calculate(5f, 5f)
        assertEquals(4f, axis.axisMinimum, 0.001f)
        assertEquals(6f, axis.axisMaximum, 0.001f)

        // A value this large does not change when 1 is added to it, so the padding has to scale with it.
        axis.calculate(7244926976f, 7244926976f)
        assertTrue(axis.axisRange > 0f)
    }

    @Test
    fun yAxisRangeAddsPercentageSpace() {
        val axis = YAxis()
        axis.calculate(0f, 100f)
        assertEquals(-10f, axis.axisMinimum, 0.001f)
        assertEquals(110f, axis.axisMaximum, 0.001f)

        axis.spaceTop = 0f
        axis.spaceBottom = 50f
        axis.calculate(0f, 100f)
        assertEquals(-50f, axis.axisMinimum, 0.001f)
        assertEquals(100f, axis.axisMaximum, 0.001f)

        axis.axisMaximum = 40f
        axis.calculate(0f, 100f)
        assertEquals(40f, axis.axisMaximum, 0.001f)
        assertEquals(-50f, axis.axisMinimum, 0.001f)
    }
}
