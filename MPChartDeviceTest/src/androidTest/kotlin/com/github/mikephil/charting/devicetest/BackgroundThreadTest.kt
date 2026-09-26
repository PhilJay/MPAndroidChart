package com.github.mikephil.charting.devicetest

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.BubbleChart
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.charts.ScatterChart
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BackgroundThreadTest {

    @Test
    fun everyChartCanBeBuiltOnAThreadWithoutALooper() {
        val context = Fixtures.context()
        var failure: Throwable? = null
        val thread = Thread {
            try {
                LineChart(context)
                BarChart(context)
                HorizontalBarChart(context)
                ScatterChart(context)
                BubbleChart(context)
                CandleStickChart(context)
                CombinedChart(context)
                PieChart(context)
                RadarChart(context)
            } catch (t: Throwable) {
                failure = t
            }
        }
        thread.start()
        thread.join()
        assertNull(failure)
    }
}
