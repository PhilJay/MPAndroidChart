package com.github.mikephil.charting.devicetest

import android.graphics.Matrix
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AxisDrawTest {

    private fun lineChart(entries: List<Entry<Nothing>>): LineChart {
        val chart = Fixtures.lay { LineChart(Fixtures.context()) }
        chart.legend.isEnabled = false
        chart.description.isEnabled = false
        chart.data = LineData(Fixtures.lineSet(entries))
        Fixtures.resetViewport(chart)
        return chart
    }

    @Test
    fun autoScaleRescalesAnAxisThatIsNotDrawn() {
        val chart = lineChart(List(100) { Entry(it.toFloat(), it.toFloat()) })
        chart.axisLeft.isEnabled = false
        chart.axisRight.isEnabled = false
        chart.isAutoScaleMinMaxEnabled = true

        val matrix = Matrix(chart.viewPortHandler.matrixTouch)
        matrix.postScale(10f, 1f, 0f, 0f)
        chart.viewPortHandler.refresh(matrix, chart, false)
        chart.draw(Fixtures.canvas())

        assertTrue("left axis max ${chart.axisLeft.axisMaximum}", chart.axisLeft.axisMaximum < 20f)
    }
}
