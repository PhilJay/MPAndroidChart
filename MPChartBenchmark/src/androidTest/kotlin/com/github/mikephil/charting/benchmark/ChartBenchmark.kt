package com.github.mikephil.charting.benchmark

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.BubbleChart
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.BubbleData
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.ScatterData
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.random.Random

@RunWith(AndroidJUnit4::class)
class ChartBenchmark {

    private val context get() = Fixtures.context()

    @Test
    fun lineSizeSweep() = SIZES.forEach { runScenarios(lineBed("line-linear", it)) }

    @Test
    fun barSizeSweep() = SIZES.forEach { runScenarios(barBed("bar", it)) }

    @Test
    fun lineVariants() {
        runScenarios(lineBed("line-circles", MID, circles = true))
        runScenarios(lineBed("line-cubic", MID, mode = LineDataSet.Mode.CUBIC_BEZIER))
        runScenarios(lineBed("line-stepped", MID, mode = LineDataSet.Mode.STEPPED))
        runScenarios(lineBed("line-multicolor", MID, colors = 8))
    }

    @Test
    fun otherChartTypes() {
        runScenarios(barBed("bar-stacked", MID, stacked = true))
        runScenarios(horizontalBarBed(MID))
        runScenarios(scatterBed(MID))
        runScenarios(candleBed(MID))
        runScenarios(bubbleBed(MID))
        runScenarios(combinedBed(MID))
    }

    private fun lineBed(
        variant: String,
        n: Int,
        circles: Boolean = false,
        mode: LineDataSet.Mode = LineDataSet.Mode.LINEAR,
        colors: Int = 1,
    ): Bed {
        val chart = Fixtures.lay { LineChart(context) }
        var data: LineData? = null
        var nextX = n.toFloat()
        val random = Random(7)
        return Bed(
            variant = variant,
            points = n,
            chart = chart,
            assign = {
                data = LineData(Fixtures.lineSet(Fixtures.lineEntries(n), circles, mode, colors))
                nextX = n.toFloat()
                chart.data = data
            },
            append = {
                data!!.addEntry(Entry(nextX++, random.nextFloat() * 100f), 0)
                chart.notifyDataSetChanged()
            },
        )
    }

    private fun barBed(variant: String, n: Int, stacked: Boolean = false): Bed {
        val chart = Fixtures.lay { BarChart(context) }
        var data: BarData? = null
        var nextX = n.toFloat()
        val random = Random(7)
        return Bed(
            variant = variant,
            points = n,
            chart = chart,
            assign = {
                val entries = if (stacked) Fixtures.stackedBarEntries(n) else Fixtures.barEntries(n)
                data = BarData(Fixtures.barSet(entries, stacked))
                nextX = n.toFloat()
                chart.data = data
            },
            append = {
                data!!.addEntry(BarEntry(nextX++, random.nextFloat() * 100f), 0)
                chart.notifyDataSetChanged()
            },
        )
    }

    private fun horizontalBarBed(n: Int): Bed {
        val chart = Fixtures.lay { HorizontalBarChart(context) }
        return Bed("hbar", n, chart, assign = {
            chart.data = BarData(Fixtures.barSet(Fixtures.barEntries(n)))
        })
    }

    private fun scatterBed(n: Int): Bed {
        val chart = Fixtures.lay { ScatterChart(context) }
        return Bed("scatter", n, chart, assign = {
            chart.data = ScatterData(Fixtures.scatterSet(Fixtures.lineEntries(n)))
        })
    }

    private fun candleBed(n: Int): Bed {
        val chart = Fixtures.lay { CandleStickChart(context) }
        return Bed("candle", n, chart, assign = {
            chart.data = CandleData(Fixtures.candleSet(Fixtures.candleEntries(n)))
        })
    }

    private fun bubbleBed(n: Int): Bed {
        val chart = Fixtures.lay { BubbleChart(context) }
        return Bed("bubble", n, chart, assign = {
            chart.data = BubbleData(Fixtures.bubbleSet(Fixtures.bubbleEntries(n)))
        })
    }

    private fun combinedBed(n: Int): Bed {
        val chart = Fixtures.lay { CombinedChart(context) }
        return Bed("combined-line-bar", n, chart, assign = {
            chart.data = CombinedData().apply {
                lineData = LineData(Fixtures.lineSet(Fixtures.lineEntries(n)))
                barData = BarData(Fixtures.barSet(Fixtures.barEntries(n)))
            }
        })
    }

    private companion object {
        val SIZES = intArrayOf(10_000, 50_000, 100_000).toList()
        const val MID = 50_000
    }
}
