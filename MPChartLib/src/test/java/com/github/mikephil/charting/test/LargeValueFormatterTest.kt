package com.github.mikephil.charting.test

import com.github.mikephil.charting.formatter.LargeValueFormatter
import org.junit.Assert
import org.junit.Test

class LargeValueFormatterTest {
    @Test
    fun test() {
        val formatter = LargeValueFormatter()

        val resultDouble = formatter.getFormattedValue(5.0.toFloat(), null)
        Assert.assertEquals("5", resultDouble)

        var result = formatter.getFormattedValue(5f, null)
        Assert.assertEquals("5", result)

        result = formatter.getFormattedValue(5.5f, null)
        Assert.assertEquals("5.5", result)

        result = formatter.getFormattedValue(50f, null)
        Assert.assertEquals("50", result)

        result = formatter.getFormattedValue(50.5f, null)
        Assert.assertEquals("50.5", result)

        result = formatter.getFormattedValue(500f, null)
        Assert.assertEquals("500", result)

        result = formatter.getFormattedValue(1100f, null)
        Assert.assertEquals("1.1k", result)

        result = formatter.getFormattedValue(10000f, null)
        Assert.assertEquals("10k", result)

        result = formatter.getFormattedValue(10500f, null)
        Assert.assertEquals("10.5k", result)

        result = formatter.getFormattedValue(100000f, null)
        Assert.assertEquals("100k", result)

        result = formatter.getFormattedValue(1000000f, null)
        Assert.assertEquals("1m", result)

        result = formatter.getFormattedValue(1500000f, null)
        Assert.assertEquals("1.5m", result)

        result = formatter.getFormattedValue(9500000f, null)
        Assert.assertEquals("9.5m", result)

        result = formatter.getFormattedValue(22200000f, null)
        Assert.assertEquals("22.2m", result)

        result = formatter.getFormattedValue(222000000f, null)
        Assert.assertEquals("222m", result)

        result = formatter.getFormattedValue(1000000000f, null)
        Assert.assertEquals("1b", result)

        result = formatter.getFormattedValue(9900000000f, null)
        Assert.assertEquals("9.9b", result)

        result = formatter.getFormattedValue(99000000000f, null)
        Assert.assertEquals("99b", result)

        result = formatter.getFormattedValue(99500000000f, null)
        Assert.assertEquals("99.5b", result)

        result = formatter.getFormattedValue(999000000000f, null)
        Assert.assertEquals("999b", result)

        result = formatter.getFormattedValue(1000000000000f, null)
        Assert.assertEquals("1t", result)

        formatter.setSuffix(arrayOf("", "k", "m", "b", "t", "q")) // quadrillion support
        result = formatter.getFormattedValue(1000000000000000f, null)
        Assert.assertEquals("1q", result)

        result = formatter.getFormattedValue(1100000000000000f, null)
        Assert.assertEquals("1.1q", result)

        result = formatter.getFormattedValue(10000000000000000f, null)
        Assert.assertEquals("10q", result)

        result = formatter.getFormattedValue(13300000000000000f, null)
        Assert.assertEquals("13.3q", result)

        result = formatter.getFormattedValue(100000000000000000f, null)
        Assert.assertEquals("100q", result)
    }
}
