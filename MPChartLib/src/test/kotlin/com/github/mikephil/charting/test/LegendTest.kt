package com.github.mikephil.charting.test

import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LegendTest {

    @Test
    fun assigningEntriesMakesTheLegendCustomUntilReset() {
        val legend = Legend()
        assertFalse(legend.isLegendCustom)

        legend.entries = listOf(LegendEntry("Custom"))
        assertTrue(legend.isLegendCustom)
        assertEquals("Custom", legend.entries[0].label)

        legend.resetCustom()
        assertFalse(legend.isLegendCustom)

        legend.setExtra(listOf(0xFF0000, 0), listOf("red", "hidden"))
        assertEquals(2, legend.extraEntries.size)
        assertEquals(Legend.LegendForm.NONE, legend.extraEntries[1].form)
    }
}
