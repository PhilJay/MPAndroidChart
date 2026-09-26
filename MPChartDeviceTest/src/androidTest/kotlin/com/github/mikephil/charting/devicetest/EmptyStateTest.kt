package com.github.mikephil.charting.devicetest

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.os.SystemClock
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.BubbleChart
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.atomic.AtomicInteger

@RunWith(AndroidJUnit4::class)
class EmptyStateTest {

    @Test
    fun emptyStateShowsForNoDataAndEmptyDataAndSwitchesToLoading() {
        val noData = Fixtures.lay { LineChart(Fixtures.context()) }
        assertEquals(listOf("No data yet"), texts(noData))

        val emptyData = Fixtures.lay { LineChart(Fixtures.context()).apply { data = LineData(LineDataSet(emptyList<Entry<Nothing>>(), "empty")) } }
        assertEquals(listOf("No data yet"), texts(emptyData))

        noData.isLoading = true
        assertEquals(listOf("Loading…"), texts(noData))

        noData.isLoading = false
        noData.noDataText = ""
        noData.isNoDataIconEnabled = false
        assertEquals(emptyList<String>(), texts(noData))
    }

    @Test
    fun everyChartTypeDrawsItsOwnOutline() {
        val context = Fixtures.context()
        val outlines = mapOf(
            "line" to { LineChart(context) }, "bar" to { BarChart(context) }, "horizontal bar" to { HorizontalBarChart(context) },
            "pie" to { PieChart(context) }, "radar" to { RadarChart(context) }, "scatter" to { ScatterChart(context) },
            "bubble" to { BubbleChart(context) }, "candle" to { CandleStickChart(context) }, "combined" to { CombinedChart(context) },
        ).mapValues { (_, make) ->
            val chart = Fixtures.lay { make().apply { noDataText = "" } }
            pixels(chart)
        }

        for ((name, pixels) in outlines) assertTrue("$name draws no outline", pixels.any { Color.alpha(it) != 0 })
        val distinct = outlines.filterKeys { it != "candle" && it != "combined" }.values.map { it.contentHashCode() }.toSet()
        assertEquals("the seven outline shapes should differ", 7, distinct.size)
        assertTrue("candle and combined use the bar outline", outlines.getValue("candle").contentEquals(outlines.getValue("bar")))
    }

    @Test
    fun alignmentPutsTheTextAtTheEdges() {
        val chart = Fixtures.lay { LineChart(Fixtures.context()) }
        chart.noDataTextAlignment = Paint.Align.LEFT
        assertEquals(0f, textX(chart), 0f)
        chart.noDataTextAlignment = Paint.Align.RIGHT
        assertEquals(Fixtures.WIDTH.toFloat(), textX(chart), 0f)
    }

    @Test
    fun aCustomIconIsDrawnInsteadOfTheOutline() {
        val icon = CountingDrawable()
        val chart = Fixtures.lay { PieChart(Fixtures.context()).apply { noDataIcon = icon } }
        texts(chart)
        assertEquals(1, icon.draws)
        assertTrue("icon bounds ${icon.bounds}", icon.bounds.width() > 0 && icon.bounds.height() > 0)
    }

    @Test
    fun loadingRedrawsUntilItEnds() {
        val frames = AtomicInteger()
        ActivityScenario.launch(HostActivity::class.java).use { scenario ->
            lateinit var chart: CountingChart
            scenario.onActivity { activity ->
                chart = CountingChart(activity, frames)
                activity.root.addView(chart)
                chart.isLoading = true
            }
            SystemClock.sleep(600)
            assertTrue("only ${frames.get()} frames while loading", frames.get() > 10)

            scenario.onActivity { chart.isLoading = false }
            InstrumentationRegistry.getInstrumentation().waitForIdleSync()
            SystemClock.sleep(100)
            val afterLoading = frames.get()
            SystemClock.sleep(400)
            assertTrue("kept redrawing after loading ended", frames.get() - afterLoading <= 1)
        }
    }

    private class CountingChart(context: Context, val frames: AtomicInteger) : LineChart(context) {
        override fun drawEmptyState(canvas: Canvas) {
            frames.incrementAndGet()
            super.drawEmptyState(canvas)
        }
    }

    private fun texts(chart: Chart<*>): List<String> = record(chart).texts.map { it.text }

    private fun textX(chart: Chart<*>): Float = record(chart).texts.single().x

    private fun record(chart: Chart<*>): RecordingCanvas {
        val canvas = RecordingCanvas(Bitmap.createBitmap(Fixtures.WIDTH, Fixtures.HEIGHT, Bitmap.Config.ARGB_8888))
        InstrumentationRegistry.getInstrumentation().runOnMainSync { chart.draw(canvas) }
        return canvas
    }

    private fun pixels(chart: Chart<*>): IntArray {
        val bitmap = Bitmap.createBitmap(Fixtures.WIDTH, Fixtures.HEIGHT, Bitmap.Config.ARGB_8888)
        InstrumentationRegistry.getInstrumentation().runOnMainSync { chart.draw(Canvas(bitmap)) }
        return IntArray(bitmap.width * bitmap.height).also { bitmap.getPixels(it, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height) }
    }

    private class CountingDrawable : Drawable() {
        var draws = 0
        override fun draw(canvas: Canvas) {
            draws++
        }
        override fun setAlpha(alpha: Int) {}
        override fun setColorFilter(colorFilter: ColorFilter?) {}
        @Deprecated("Deprecated in Java")
        override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
    }
}
