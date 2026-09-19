package com.github.mikephil.charting.benchmark

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineData
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Draws the whole data range with every point on screen. Each size is its own test so it can be run in a
 * fresh process; sweeping sizes in one process lets the warming JIT and growing heap hide the real trend.
 */
@RunWith(AndroidJUnit4::class)
class FullDrawTest {

    @Test fun draw10k() = measure(10_000)

    @Test fun draw50k() = measure(50_000)

    @Test fun draw100k() = measure(100_000)

    @Test fun draw50kCircles() = measure(50_000, circles = true)

    @Test fun draw50kMultiColor() = measure(50_000, colors = 8)

    @Test fun draw10kNoDecimation() = measure(10_000, decimation = false)

    @Test fun draw50kNoDecimation() = measure(50_000, decimation = false)

    @Test fun draw100kNoDecimation() = measure(100_000, decimation = false)

    @Test fun draw50kCirclesNoDecimation() = measure(50_000, circles = true, decimation = false)

    @Test fun draw50kMultiColorNoDecimation() = measure(50_000, colors = 8, decimation = false)

    @Test fun drawBars50k() = measureBars(50_000)

    @Test fun drawBars100k() = measureBars(100_000)

    private fun measureBars(n: Int) {
        val chart = Fixtures.lay { com.github.mikephil.charting.charts.BarChart(Fixtures.context()) }
        chart.data = com.github.mikephil.charting.data.BarData(Fixtures.barSet(Fixtures.barEntries(n)))
        Fixtures.resetViewport(chart)
        val canvas = Fixtures.canvas()

        repeat(3) { chart.draw(canvas) }

        val samples = DoubleArray(9)
        for (i in samples.indices) {
            val start = System.nanoTime()
            chart.draw(canvas)
            samples[i] = (System.nanoTime() - start) / 1e6
        }
        samples.sort()
        Log.i(Bench.TAG, "FULLDRAW chart=bar n=$n median=${"%.2f".format(samples[4])}ms best=${"%.2f".format(samples[0])}ms worst=${"%.2f".format(samples[8])}ms")
    }

    private fun measure(n: Int, circles: Boolean = false, colors: Int = 1, decimation: Boolean = true) {
        val chart = Fixtures.lay { LineChart(Fixtures.context()) }
        chart.isDecimationEnabled = decimation
        chart.data = LineData(Fixtures.lineSet(Fixtures.lineEntries(n), circles = circles, colors = colors))
        Fixtures.resetViewport(chart)
        val canvas = Fixtures.canvas()

        repeat(3) { chart.draw(canvas) }

        val samples = DoubleArray(9)
        for (i in samples.indices) {
            val start = System.nanoTime()
            chart.draw(canvas)
            samples[i] = (System.nanoTime() - start) / 1e6
        }
        samples.sort()
        Log.i(Bench.TAG, "FULLDRAW n=$n circles=$circles colors=$colors decimation=$decimation median=${"%.2f".format(samples[4])}ms best=${"%.2f".format(samples[0])}ms worst=${"%.2f".format(samples[8])}ms")
    }
}
