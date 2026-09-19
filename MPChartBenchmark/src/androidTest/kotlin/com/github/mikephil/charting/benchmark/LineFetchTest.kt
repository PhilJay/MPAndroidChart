package com.github.mikephil.charting.benchmark

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LineFetchTest {

    private class CountingLineSet(private val delegate: ILineDataSet<Nothing>) : ILineDataSet<Nothing> by delegate {
        var fetches = 0
            private set

        override fun getEntryForIndex(index: Int): Entry<Nothing> = delegate.getEntryForIndex(index).also { fetches++ }
    }

    @Test
    fun aStraightLineReadsEachVisibleEntryOnce() {
        val total = 5_000
        val set = CountingLineSet(Fixtures.lineSet(Fixtures.lineEntries(total)))
        val chart = Fixtures.lay { LineChart(Fixtures.context()) }
        chart.isDecimationEnabled = false
        chart.data = LineData(set)
        Fixtures.resetViewport(chart)

        val canvas = Fixtures.canvas()
        chart.draw(canvas)

        val before = set.fetches
        chart.draw(canvas)
        val perFrame = set.fetches - before

        assertTrue("read $perFrame entries to draw about $total", perFrame <= total + 2)
    }
}
