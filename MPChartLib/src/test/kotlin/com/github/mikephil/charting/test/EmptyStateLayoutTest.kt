package com.github.mikephil.charting.test

import android.graphics.Paint
import com.github.mikephil.charting.charts.EmptyStateLayout
import com.github.mikephil.charting.charts.LOADING_PULSE_MILLIS
import com.github.mikephil.charting.charts.loadingPulse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EmptyStateLayoutTest {

    private fun layout(align: Paint.Align, iconHeight: Float = 24f, gap: Float = 10f) =
        EmptyStateLayout(400f, 300f, iconWidth = 30f, iconHeight = iconHeight, gap = gap, textWidth = 90f, textHeight = 16f, align = align)

    @Test
    fun theBlockIsCenteredVerticallyAndPlacedByTheAlignment() {
        val center = layout(Paint.Align.CENTER)
        assertEquals((300f - 24f - 10f - 16f) / 2f, center.top, 0f)
        assertEquals(center.top + 24f + 10f, center.textTop, 0f)
        assertEquals(200f, center.centerX, 0f)
        assertEquals(200f, center.textX, 0f)

        val left = layout(Paint.Align.LEFT)
        assertEquals(45f, left.centerX, 0f)
        assertEquals(0f, left.textX, 0f)

        val right = layout(Paint.Align.RIGHT)
        assertEquals(355f, right.centerX, 0f)
        assertEquals(400f, right.textX, 0f)

        val textOnly = layout(Paint.Align.CENTER, iconHeight = 0f, gap = 0f)
        assertEquals((300f - 16f) / 2f, textOnly.textTop, 0f)
    }

    @Test
    fun theLoadingPulseBreathesBetween30And100Percent() {
        val samples = (0 until LOADING_PULSE_MILLIS.toInt() step 10).map { loadingPulse(it.toLong()) }
        assertEquals(0.3f, samples.min(), 0.01f)
        assertEquals(1f, samples.max(), 0.01f)
        assertEquals(loadingPulse(123), loadingPulse(123 + LOADING_PULSE_MILLIS), 0.0001f)
        assertTrue(samples.all { it in 0.3f..1f })
    }
}
