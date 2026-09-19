package com.github.mikephil.charting.benchmark

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BarBoundsTest {

    /** Counts calls to [getEntryForIndex], the fetch the renderer makes once per bar it builds. */
    private class CountingBarSet(private val delegate: IBarDataSet<Nothing>) : IBarDataSet<Nothing> by delegate {
        var fetches = 0
            private set

        override fun getEntryForIndex(index: Int) = delegate.getEntryForIndex(index).also { fetches++ }
    }

    @Test
    fun onlyFetchesTheBarsOnScreen() {
        val total = 100_000
        val set = CountingBarSet(Fixtures.barSet(Fixtures.barEntries(total)))
        val chart = Fixtures.lay { BarChart(Fixtures.context()) }
        chart.isDrawBarShadowEnabled = true
        chart.data = BarData(set)
        val canvas = Fixtures.canvas()

        Fixtures.resetViewport(chart)
        Fixtures.zoomToVisiblePoints(chart, total, 500)
        Fixtures.panBy(chart, -(total / 2).toFloat())

        chart.draw(canvas)
        val before = set.fetches
        chart.draw(canvas)

        assertTrue("fetched ${set.fetches - before} of $total entries", set.fetches - before < total / 10)
    }
}
