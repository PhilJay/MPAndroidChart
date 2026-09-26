package com.github.mikephil.charting.test

import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.LargeValueFormatter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LargeValueFormatterTest {

    @Test
    fun formatsSmallAndVeryLargeNumbers() {
        val formatter = LargeValueFormatter()
        val axis = XAxis()
        assertEquals("856", formatter.getFormattedValue(856f, axis))
        assertEquals("5.8k", formatter.getFormattedValue(5821f, axis))
        assertEquals("102k", formatter.getFormattedValue(101800f, axis))
        assertEquals("0.5", formatter.getFormattedValue(0.5f, axis))
        assertEquals("-5.8k", formatter.getFormattedValue(-5821f, axis))
        assertTrue(formatter.getFormattedValue(1e18f, axis).endsWith("t"))
    }

    @Test
    fun formatsWithSuffixes() {
        val formatter = LargeValueFormatter()
        val axis = XAxis()

        assertEquals("5", formatter.getFormattedValue(5f, axis))
        assertEquals("5.5", formatter.getFormattedValue(5.5f, axis))
        assertEquals("50", formatter.getFormattedValue(50f, axis))
        assertEquals("50.5", formatter.getFormattedValue(50.5f, axis))
        assertEquals("500", formatter.getFormattedValue(500f, axis))
        assertEquals("1.1k", formatter.getFormattedValue(1100f, axis))
        assertEquals("10k", formatter.getFormattedValue(10000f, axis))
        assertEquals("10.5k", formatter.getFormattedValue(10500f, axis))
        assertEquals("100k", formatter.getFormattedValue(100000f, axis))
        assertEquals("1m", formatter.getFormattedValue(1000000f, axis))
        assertEquals("1.5m", formatter.getFormattedValue(1500000f, axis))
        assertEquals("9.5m", formatter.getFormattedValue(9500000f, axis))
        assertEquals("22.2m", formatter.getFormattedValue(22200000f, axis))
        assertEquals("222m", formatter.getFormattedValue(222000000f, axis))
        assertEquals("1b", formatter.getFormattedValue(1000000000f, axis))
        assertEquals("9.9b", formatter.getFormattedValue(9900000000f, axis))
        assertEquals("99b", formatter.getFormattedValue(99000000000f, axis))
        assertEquals("99.5b", formatter.getFormattedValue(99500000000f, axis))
        assertEquals("999b", formatter.getFormattedValue(999000000000f, axis))
        assertEquals("1t", formatter.getFormattedValue(1000000000000f, axis))

        formatter.suffix = listOf("", "k", "m", "b", "t", "q")
        assertEquals("1q", formatter.getFormattedValue(1000000000000000f, axis))
        assertEquals("1.1q", formatter.getFormattedValue(1100000000000000f, axis))
        assertEquals("10q", formatter.getFormattedValue(10000000000000000f, axis))
        assertEquals("13.3q", formatter.getFormattedValue(13300000000000000f, axis))
        assertEquals("100q", formatter.getFormattedValue(100000000000000000f, axis))
    }
}
