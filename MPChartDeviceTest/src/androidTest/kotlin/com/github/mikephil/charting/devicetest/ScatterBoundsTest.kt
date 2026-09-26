package com.github.mikephil.charting.devicetest

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.data.ScatterData
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScatterBoundsTest {

    /** Counts calls to [getEntryForIndex], the fetch the renderer makes once per entry it draws. */
    private class CountingScatterSet(private val delegate: IScatterDataSet<Nothing>) : IScatterDataSet<Nothing> by delegate {
        var fetches = 0
            private set

        override fun getEntryForIndex(index: Int) = delegate.getEntryForIndex(index).also { fetches++ }
    }

    @Test
    fun onlyFetchesTheEntriesOnScreen() {
        val total = 100_000
        val countingSet = CountingScatterSet(Fixtures.scatterSet(Fixtures.lineEntries(total)))
        val chart = Fixtures.lay { ScatterChart(Fixtures.context()) }
        chart.data = ScatterData(countingSet)
        val canvas = Fixtures.canvas()

        Fixtures.resetViewport(chart)
        Fixtures.zoomToVisiblePoints(chart, total, 300)
        Fixtures.panBy(chart, -300_000f)

        chart.draw(canvas)

        assertTrue("fetched ${countingSet.fetches} of $total entries", countingSet.fetches < total / 10)
    }
}
