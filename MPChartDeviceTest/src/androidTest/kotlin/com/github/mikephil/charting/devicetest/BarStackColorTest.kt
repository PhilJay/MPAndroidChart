package com.github.mikephil.charting.devicetest

import android.graphics.Bitmap
import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.DataSet
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BarStackColorTest {

    private val total = 60

    private val stackSize = 3

    /** Four colors for a stack of three, so that a color counted from the wrong place does not land right anyway. */
    private val palette = listOf(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW)

    /** Entries carrying three, two and one value, so the stack size of the set is larger than most entries need. */
    private fun raggedStacks() = List(total) {
        when (it % stackSize) {
            0 -> BarEntry(it.toFloat(), listOf(4f, 6f, 3f))
            1 -> BarEntry(it.toFloat(), listOf(5f, 7f))
            else -> BarEntry(it.toFloat(), 9f)
        }
    }

    /**
     * The colors each bar must paint, bottom section first, for the first sixteen entries. Colors cycle over the
     * sections of the whole set, so the section at index i of entry e takes the color at `e * 3 + i`, counted
     * over the set and not over whatever the viewport starts at; an entry carrying fewer than three values still
     * takes three places. Written out rather than computed, so the test does not restate the rule it checks.
     */
    private val expected = listOf(
        listOf(Color.RED, Color.GREEN, Color.BLUE),
        listOf(Color.YELLOW, Color.RED),
        listOf(Color.BLUE),
        listOf(Color.GREEN, Color.BLUE, Color.YELLOW),
        listOf(Color.RED, Color.GREEN),
        listOf(Color.YELLOW),
        listOf(Color.BLUE, Color.YELLOW, Color.RED),
        listOf(Color.GREEN, Color.BLUE),
        listOf(Color.RED),
        listOf(Color.YELLOW, Color.RED, Color.GREEN),
        listOf(Color.BLUE, Color.YELLOW),
        listOf(Color.GREEN),
        listOf(Color.RED, Color.GREEN, Color.BLUE),
        listOf(Color.YELLOW, Color.RED),
        listOf(Color.BLUE),
        listOf(Color.GREEN, Color.BLUE, Color.YELLOW),
    )

    /** The colors each bar was painted with, keyed by the x value of the bar, after scrolling by [pan] pixels. */
    private fun colorsByBar(chart: BarChart, pan: Float): Map<Int, List<Int>> {
        Fixtures.resetViewport(chart)
        Fixtures.zoomToVisiblePoints(chart, total, 15)
        Fixtures.panBy(chart, pan)

        // Only the four-coordinate drawRect goes through RecordingCanvas, so bar shadows and grid lines, which
        // take a RectF, never reach these lists.
        val canvas = RecordingCanvas(Bitmap.createBitmap(Fixtures.WIDTH, Fixtures.HEIGHT, Bitmap.Config.ARGB_8888))
        chart.draw(canvas)

        return canvas.barColorsByX(chart)
    }

    /**
     * The entry the renderer starts filling its buffer from, which is where a color counted over the viewport
     * instead of over the set would start counting. Worked out the way the renderer works out its visible range.
     */
    private fun firstFedEntry(chart: BarChart, set: BarDataSet<Nothing>): Int =
        set.getEntryIndex(chart.lowestVisibleX, Float.NaN, DataSet.Rounding.DOWN).coerceAtLeast(0)

    private fun assertPaintsExpectedColors(where: String, bars: Map<Int, List<Int>>): Int {
        var checked = 0
        for ((x, colors) in bars) {
            if (x !in expected.indices) continue
            assertEquals("colors of the bar at x=$x $where", expected[x], colors)
            checked++
        }
        return checked
    }

    @Test
    fun aBarKeepsItsColorsWhereverTheChartIsScrolled() {
        val set = BarDataSet(raggedStacks(), "bars").apply {
            colors = palette
            isDrawValuesEnabled = false
            isDrawIconsEnabled = false
        }
        val chart = Fixtures.lay { BarChart(Fixtures.context()) }
        // The legend draws a colored square of its own, which would land on a different bar in each viewport.
        chart.legend.isEnabled = false
        chart.data = BarData(set)

        val start = colorsByBar(chart, 0f)
        val scrolled = colorsByBar(chart, -650f)

        // Counting colors over the viewport instead of over the set shifts them by the first fed entry times the
        // stack size, which is invisible when that lands on a whole number of palettes. The scrolled viewport has
        // to avoid it or the rest of this test proves nothing; move the pan if this ever fails.
        val firstFed = firstFedEntry(chart, set)
        assertTrue(
            "scrolled viewport is fed from entry $firstFed, where a color counted over the viewport comes out the same",
            firstFed * stackSize % palette.size != 0
        )

        assertPaintsExpectedColors("at the start of the set", start)
        val checked = assertPaintsExpectedColors("after scrolling", scrolled)
        assertTrue("bars of the scrolled viewport with an expectation of their own: $checked", checked >= 3)

        val shared = start.keys.intersect(scrolled.keys)
        assertTrue("bars visible from both scroll positions: $shared", shared.size >= 5)

        for (x in shared) {
            assertEquals("colors of the bar at x=$x after scrolling", start[x], scrolled[x])
        }
    }
}
