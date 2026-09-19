package com.github.mikephil.charting.benchmark

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineData
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Pans a chart that always holds the same data while varying how many points are on screen, to find how many
 * a frame can afford. Each count is its own test so it can run in a fresh process.
 */
@RunWith(AndroidJUnit4::class)
class VisiblePointsTest {

    @Test fun visible500() = measure(500)

    @Test fun visible1k() = measure(1_000)

    @Test fun visible2k() = measure(2_000)

    @Test fun visible5k() = measure(5_000)

    @Test fun visible10k() = measure(10_000)

    @Test fun visible20k() = measure(20_000)

    private fun measure(visible: Int) {
        val total = 200_000
        val chart = Fixtures.lay { LineChart(Fixtures.context()) }
        chart.data = LineData(Fixtures.lineSet(Fixtures.lineEntries(total)))
        val canvas = Fixtures.canvas()

        fun panPass(frames: Int) {
            Fixtures.resetViewport(chart)
            Fixtures.zoomToVisiblePoints(chart, total, visible)
            repeat(frames) {
                Fixtures.panBy(chart, -PAN_PIXELS)
                chart.draw(canvas)
            }
        }

        panPass(20)

        val samples = DoubleArray(5)
        for (i in samples.indices) {
            Fixtures.resetViewport(chart)
            Fixtures.zoomToVisiblePoints(chart, total, visible)
            val start = System.nanoTime()
            repeat(30) {
                Fixtures.panBy(chart, -PAN_PIXELS)
                chart.draw(canvas)
            }
            samples[i] = (System.nanoTime() - start) / 1e6 / 30
        }
        samples.sort()
        Log.i(Bench.TAG, "VISIBLE points=$visible perFrame=${"%.2f".format(samples[2])}ms best=${"%.2f".format(samples[0])}ms")
    }
}
