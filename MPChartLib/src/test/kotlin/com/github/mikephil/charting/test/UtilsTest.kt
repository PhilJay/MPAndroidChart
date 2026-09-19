package com.github.mikephil.charting.test

import com.github.mikephil.charting.utils.Utils
import org.junit.Assert.assertEquals
import org.junit.Test

class UtilsTest {

    @Test
    fun formatNumber() {
        assertEquals("0", Utils.formatNumber(0f, 2, true))
        assertEquals("1234,5", Utils.formatNumber(1234.5f, 1, false))
        assertEquals("12.345,50", Utils.formatNumber(12345.5f, 2, true))
        assertEquals("1 234 567", Utils.formatNumber(1234567f, 0, true, ' '))
        assertEquals("-0,75", Utils.formatNumber(-0.75f, 2, false))
        assertEquals("1,500000000", Utils.formatNumber(1.5f, 10, false))
    }

    @Test
    fun numericHelpers() {
        assertEquals(1000f, Utils.roundToNextSignificant(1234.0), 0.001f)
        assertEquals(0.05f, Utils.roundToNextSignificant(0.0468), 0.0001f)
        assertEquals(0f, Utils.roundToNextSignificant(0.0), 0f)
        assertEquals(1, Utils.getDecimals(12.3f))
        assertEquals(4, Utils.getDecimals(0.0468f))
        assertEquals(30f, Utils.getNormalizedAngle(390f), 0.001f)
        assertEquals(350f, Utils.getNormalizedAngle(-10f), 0.001f)
    }
}
