package com.github.mikephil.charting.devicetest

import android.graphics.Bitmap
import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BarBufferReuseTest {

    private fun set(entries: List<BarEntry<Nothing>>) = BarDataSet(entries, "bars").apply {
        colors = listOf(Color.RED, Color.GREEN, Color.BLUE)
        isDrawValuesEnabled = false
        isDrawIconsEnabled = false
    }

    private fun draw(chart: BarChart): Map<Int, List<Int>> {
        val canvas = RecordingCanvas(Bitmap.createBitmap(Fixtures.WIDTH, Fixtures.HEIGHT, Bitmap.Config.ARGB_8888))
        chart.draw(canvas)
        return canvas.barColorsByX(chart)
    }

    @Test
    fun stacksReplacingPlainBarsOfTheSameBufferSizeKeepTheirSections() {
        val chart = Fixtures.lay { BarChart(Fixtures.context()) }
        chart.legend.isEnabled = false

        // Twelve plain bars need 12 * 4 * 1 floats; four bars of three sections need 4 * 4 * 3, which is the same.
        chart.data = BarData(set(List(12) { BarEntry(it.toFloat(), 9f) }))
        draw(chart)

        chart.data = BarData(set(List(4) { BarEntry(it.toFloat(), listOf(3f, 4f, 5f)) }))

        assertEquals(
            "sections of the bar at x=0",
            listOf(Color.RED, Color.GREEN, Color.BLUE),
            draw(chart)[0]
        )
    }
}
