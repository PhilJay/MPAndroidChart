package com.github.mikephil.charting.benchmark

import android.os.Debug
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.random.Random

/** Appends one entry and tells the chart, the way a live feed does, and reports what one tick costs. */
@RunWith(AndroidJUnit4::class)
class StreamingCostTest {

    @Test fun line10k() = line(10_000)

    @Test fun line100k() = line(100_000)

    @Test fun bar10k() = bar(10_000)

    @Test fun bar100k() = bar(100_000)

    private fun line(total: Int) {
        val chart = Fixtures.lay { LineChart(Fixtures.context()) }
        val data = LineData(Fixtures.lineSet(Fixtures.lineEntries(total)))
        chart.data = data
        var nextX = total.toFloat()
        val random = Random(7)
        report("line", total) {
            data.addEntry(Entry(nextX++, random.nextFloat() * 100f), 0)
            chart.notifyDataSetChanged()
        }
    }

    private fun bar(total: Int) {
        val chart = Fixtures.lay { BarChart(Fixtures.context()) }
        val data = BarData(Fixtures.barSet(Fixtures.barEntries(total)))
        chart.data = data
        var nextX = total.toFloat()
        val random = Random(7)
        report("bar", total) {
            data.addEntry(BarEntry(nextX++, random.nextFloat() * 100f), 0)
            chart.notifyDataSetChanged()
        }
    }

    private fun gcCount(): Long = Debug.getRuntimeStat("art.gc.gc-count")?.toLongOrNull() ?: -1L

    private fun report(kind: String, total: Int, tick: () -> Unit) {
        repeat(200) { tick() }
        val gcBefore = gcCount()
        val start = System.nanoTime()
        repeat(500) { tick() }
        val perTick = (System.nanoTime() - start) / 1e6 / 500
        val gc = gcCount() - gcBefore
        Log.i(Bench.TAG, "STREAM chart=$kind total=$total perTick=${"%.4f".format(perTick)}ms gcPer500=$gc")
    }
}
