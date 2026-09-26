package com.github.mikephil.charting.devicetest

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BoundsLookupTest {

    /** Counts the entry-to-index lookups a draw performs; that form scans the whole list. */
    private class CountingLineSet(private val delegate: ILineDataSet<Nothing>) : ILineDataSet<Nothing> by delegate {
        var indexLookups = 0
            private set

        override fun getEntryIndex(e: Entry<*>): Int = delegate.getEntryIndex(e).also { indexLookups++ }
    }

    @Test
    fun drawingNeverLooksAnEntryUpByScanningTheList() {
        val total = 100_000
        val set = CountingLineSet(Fixtures.lineSet(Fixtures.lineEntries(total)))
        val chart = Fixtures.lay { LineChart(Fixtures.context()) }
        chart.data = LineData(set)

        Fixtures.resetViewport(chart)
        Fixtures.zoomToVisiblePoints(chart, total, 500)
        val canvas = Fixtures.canvas()
        chart.draw(canvas)

        val before = set.indexLookups
        chart.draw(canvas)

        assertEquals("entry-to-index scans during one frame", 0, set.indexLookups - before)
    }
}
