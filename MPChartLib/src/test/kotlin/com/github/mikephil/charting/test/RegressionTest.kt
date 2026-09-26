package com.github.mikephil.charting.test

import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.BubbleDataSet
import com.github.mikephil.charting.data.BubbleEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.ScatterDataSet
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.utils.Utils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RegressionTest {

    @Test
    fun easeOutExpoProgressesThroughTheAnimation() {
        assertEquals(0.97f, Easing.EaseOutExpo.getInterpolation(0.5f), 0.01f)
        assertTrue(Easing.EaseOutExpo.getInterpolation(0.1f) > 0.4f)
    }

    @Test
    fun closestYPicksTheNearestEntryAmongEqualX() {
        val set = ScatterDataSet(listOf(Entry(1f, 1f), Entry(1f, 5f), Entry(1f, 3f)), "")
        assertEquals(5f, set.getEntryForXValue(1f, 4.4f)!!.y, 0.001f)
        assertEquals(3f, set.getEntryForXValue(1f, 2.6f)!!.y, 0.001f)
    }

    @Test
    fun derivedSizesFollowLaterChanges() {
        val bars = BarDataSet(listOf(BarEntry(0f, 1f)), "bars")
        assertEquals(1, bars.stackSize)
        bars.addEntry(BarEntry(1f, listOf(1f, 2f, 3f)))
        bars.notifyDataSetChanged()
        assertEquals(3, bars.stackSize)
        assertTrue(bars.isStacked)

        val bubbles = BubbleDataSet(listOf(BubbleEntry(0f, 0f, 9f), BubbleEntry(1f, 1f, 2f)), "bubbles")
        assertEquals(9f, bubbles.maxSize, 0.001f)
        bubbles.entries = mutableListOf(BubbleEntry(0f, 0f, 4f))
        assertEquals(4f, bubbles.maxSize, 0.001f)

        val stacked = BarEntry(0f, listOf(1f, 2f))
        stacked.stackValues = null
        assertNull(stacked.ranges)
    }

    @Test
    fun formattersAndHelpersHandleEdgeValues() {
        assertEquals(0, Utils.getDecimals(0f))
        assertTrue(LargeValueFormatter().getFormattedValue(1e16f, XAxis()).endsWith("t"))
        assertEquals(1, PercentFormatter().decimalDigits)
        val line = LineDataSet(listOf(Entry(0f, 0f)), "").apply { circleColors = listOf(1, 2) }
        assertEquals(1, line.getCircleColor(2))
    }
}
