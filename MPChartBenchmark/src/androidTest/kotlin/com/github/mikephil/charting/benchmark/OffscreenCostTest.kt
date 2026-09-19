package com.github.mikephil.charting.benchmark

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineData
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Pans with the same number of points on screen while the data set behind them grows. Frame cost should not
 * move: anything that does is work being done for entries nobody can see. Each size runs in its own process.
 */
@RunWith(AndroidJUnit4::class)
class OffscreenCostTest {

    @Test fun total10k() = measure(10_000)

    @Test fun total100k() = measure(100_000)

    @Test fun total500k() = measure(500_000)

    @Test fun highlightedTotal10k() = measureHighlighted(10_000)

    @Test fun highlightedTotal100k() = measureHighlighted(100_000)

    @Test fun highlightedTotal500k() = measureHighlighted(500_000)

    @Test fun barTotal10k() = measureBar(10_000)

    @Test fun barTotal100k() = measureBar(100_000)

    @Test fun barTotal300k() = measureBar(300_000)

    /** Pans with one entry highlighted, which the renderer has to find in the set again on every frame. */
    private fun measureHighlighted(total: Int) {
        val visible = 500
        val chart = Fixtures.lay { LineChart(Fixtures.context()) }
        chart.data = LineData(Fixtures.lineSet(Fixtures.lineEntries(total)))
        val canvas = Fixtures.canvas()

        fun seek() {
            Fixtures.resetViewport(chart)
            Fixtures.zoomToVisiblePoints(chart, total, visible)
            Fixtures.panBy(chart, -(total / 2).toFloat())
            chart.highlightValue((chart.lowestVisibleX + chart.highestVisibleX) / 2f, 0, callListener = false)
        }

        seek()
        repeat(20) {
            Fixtures.panBy(chart, -PAN_PIXELS)
            chart.draw(canvas)
        }

        val samples = DoubleArray(5)
        for (i in samples.indices) {
            seek()
            val start = System.nanoTime()
            repeat(30) {
                Fixtures.panBy(chart, -PAN_PIXELS)
                chart.draw(canvas)
            }
            samples[i] = (System.nanoTime() - start) / 1e6 / 30
        }
        samples.sort()
        Log.i(Bench.TAG, "OFFSCREEN chart=line highlight=1 total=$total visible=$visible perFrame=${"%.3f".format(samples[2])}ms best=${"%.3f".format(samples[0])}ms")
    }

    private fun measureBar(total: Int) {
        val visible = 500
        val chart = Fixtures.lay { com.github.mikephil.charting.charts.BarChart(Fixtures.context()) }
        chart.data = com.github.mikephil.charting.data.BarData(Fixtures.barSet(Fixtures.barEntries(total)))
        val canvas = Fixtures.canvas()

        fun seek() {
            Fixtures.resetViewport(chart)
            Fixtures.zoomToVisiblePoints(chart, total, visible)
            Fixtures.panBy(chart, -(total / 2).toFloat())
        }

        seek()
        repeat(10) { chart.draw(canvas) }

        val samples = DoubleArray(5)
        for (i in samples.indices) {
            seek()
            val start = System.nanoTime()
            repeat(10) {
                Fixtures.panBy(chart, -PAN_PIXELS)
                chart.draw(canvas)
            }
            samples[i] = (System.nanoTime() - start) / 1e6 / 10
        }
        samples.sort()
        Log.i(Bench.TAG, "OFFSCREEN chart=bar total=$total visible=$visible perFrame=${"%.3f".format(samples[2])}ms best=${"%.3f".format(samples[0])}ms")
    }

    private fun measure(total: Int) {
        val visible = 500
        val chart = Fixtures.lay { LineChart(Fixtures.context()) }
        chart.data = LineData(Fixtures.lineSet(Fixtures.lineEntries(total)))
        val canvas = Fixtures.canvas()

        fun pass(frames: Int) {
            Fixtures.resetViewport(chart)
            Fixtures.zoomToVisiblePoints(chart, total, visible)
            Fixtures.panBy(chart, -(total / 2).toFloat())
            repeat(frames) {
                Fixtures.panBy(chart, -PAN_PIXELS)
                chart.draw(canvas)
            }
        }

        pass(20)

        val samples = DoubleArray(5)
        for (i in samples.indices) {
            Fixtures.resetViewport(chart)
            Fixtures.zoomToVisiblePoints(chart, total, visible)
            Fixtures.panBy(chart, -(total / 2).toFloat())
            val start = System.nanoTime()
            repeat(30) {
                Fixtures.panBy(chart, -PAN_PIXELS)
                chart.draw(canvas)
            }
            samples[i] = (System.nanoTime() - start) / 1e6 / 30
        }
        samples.sort()
        Log.i(Bench.TAG, "OFFSCREEN total=$total visible=$visible perFrame=${"%.3f".format(samples[2])}ms best=${"%.3f".format(samples[0])}ms")
    }
}
