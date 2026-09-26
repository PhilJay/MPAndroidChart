package com.github.mikephil.charting.devicetest

import android.graphics.Bitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.ScatterData
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.utils.MPPointD
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.abs
import kotlin.math.roundToInt

@RunWith(AndroidJUnit4::class)
class ValueLabelDecimationTest {

    private val total = 50_000

    private val visible = 20_000

    @Test
    fun aValueLabelIsDrawnOnlyWhereAPointWasDrawn() {
        val set = Fixtures.scatterSet(Fixtures.lineEntries(total)).apply {
            isDrawValuesEnabled = true
            // The entries carry their index as their x, so a label says which entry it was built from.
            valueFormatter = IValueFormatter { _, entry, _, _ -> entry.x.roundToInt().toString() }
        }
        val chart = Fixtures.lay { ScatterChart(Fixtures.context()) }
        // Everything else that draws text or circles is off, so the canvas records only points and their labels.
        chart.legend.isEnabled = false
        chart.description.isEnabled = false
        chart.xAxis.isEnabled = false
        chart.axisLeft.isEnabled = false
        chart.axisRight.isEnabled = false
        chart.maxVisibleCount = total
        chart.isDecimationEnabled = true
        chart.data = ScatterData(set)

        Fixtures.resetViewport(chart)
        Fixtures.zoomToVisiblePoints(chart, total, visible)

        val canvas = RecordingCanvas(Bitmap.createBitmap(Fixtures.WIDTH, Fixtures.HEIGHT, Bitmap.Config.ARGB_8888))
        chart.draw(canvas)

        val points = canvas.circleCenters
        val labels = canvas.texts

        assertTrue("drew ${points.size} of $visible visible points, so the reduction did nothing", points.size < visible / 4)
        assertEquals("labels drawn for ${points.size} drawn points", points.size, labels.size)

        val transformer = chart.getTransformer(YAxis.AxisDependency.LEFT)
        val value = MPPointD.getInstance(0.0, 0.0)

        for (i in points.indices) {
            assertTrue(
                "label $i sits at ${labels[i].x} while its point is at ${points[i].first}",
                abs(labels[i].x - points[i].first) < 0.01f
            )

            transformer.getValuesByTouchPoint(labels[i].x, labels[i].y, value)
            assertEquals("entry behind label $i", value.x.roundToInt().toString(), labels[i].text)
        }

        MPPointD.recycleInstance(value)
    }
}
