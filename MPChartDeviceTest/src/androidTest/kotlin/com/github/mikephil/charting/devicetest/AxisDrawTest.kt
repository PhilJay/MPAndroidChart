package com.github.mikephil.charting.devicetest

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Paint
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.utils.Utils
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

    @Test
    fun limitLineLabelsAtTheContentEdgeStayInsideTheContent() {
        val chart = lineChart(List(11) { Entry(it.toFloat(), it * 10f) })
        chart.axisLeft.axisMinimum = 0f
        chart.axisLeft.axisMaximum = 100f
        val yLimit = LimitLine(100f, "top label").apply { labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP }
        val xLimit = LimitLine(10f, "right label").apply { labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP }
        chart.axisLeft.addLimitLine(yLimit)
        chart.xAxis.addLimitLine(xLimit)
        chart.notifyDataSetChanged()

        val canvas = RecordingCanvas(Bitmap.createBitmap(Fixtures.WIDTH, Fixtures.HEIGHT, Bitmap.Config.ARGB_8888))
        chart.draw(canvas)

        val paint = Paint().apply { textSize = Utils.convertDpToPixel(yLimit.textSize) }
        val content = chart.viewPortHandler.contentRect
        val top = canvas.texts.single { it.text == "top label" }
        val right = canvas.texts.single { it.text == "right label" }
        val topHeight = Utils.calcTextHeight(paint, "top label")
        val rightWidth = Utils.calcTextWidth(paint, "right label")

        assertTrue("top label baseline ${top.y}, content top ${content.top}", top.y - topHeight >= content.top - 0.5f)
        assertTrue("right label x ${right.x}, content right ${content.right}", right.x + rightWidth <= content.right + 0.5f)
    }
}
