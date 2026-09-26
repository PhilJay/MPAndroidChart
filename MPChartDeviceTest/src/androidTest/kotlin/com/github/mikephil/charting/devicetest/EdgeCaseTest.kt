package com.github.mikephil.charting.devicetest

import android.os.SystemClock
import android.view.MotionEvent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.charts.BubbleChart
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.components.YAxis.AxisDependency
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.BubbleData
import com.github.mikephil.charting.data.BubbleDataSet
import com.github.mikephil.charting.data.BubbleEntry
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.ChartData
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.data.ScatterData
import com.github.mikephil.charting.data.ScatterDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.components.MarkerImage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EdgeCaseTest {

    private val context get() = Fixtures.context()

    private fun onMain(block: () -> Unit) {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(block)
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
    }

    private fun <C : Chart<*>> drawAll(name: String, create: () -> C, data: List<Pair<String, ChartData<*>?>>) {
        for ((shape, value) in data) {
            val laidOut = Fixtures.lay(create)
            var notLaidOut: C? = null
            onMain { notLaidOut = create() }
            for (chart in listOf(laidOut, notLaidOut!!)) {
                try {
                    @Suppress("UNCHECKED_CAST")
                    (chart as Chart<ChartData<*>>).data = value
                    chart.marker = MarkerImage(context, android.R.drawable.star_on)
                    chart.highlightValue(0f, 0)
                    chart.draw(Fixtures.canvas())
                } catch (e: Exception) {
                    throw AssertionError("$name with $shape", e)
                }
            }
        }
    }

    private val shapesOfValues = listOf(
        "no values" to emptyList(),
        "one value" to listOf(3f),
        "equal values" to listOf(2f, 2f, 2f),
        "zeros" to listOf(0f, 0f, 0f),
        "negative values" to listOf(-5f, 3f, -1f),
        "a NaN" to listOf(1f, Float.NaN, 2f),
        "an infinity" to listOf(1f, Float.POSITIVE_INFINITY, 2f),
    )

    @Test
    fun everyChartDrawsEveryShapeOfData() {
        fun <D : ChartData<*>> cases(empty: D, make: (List<Float>) -> D) =
            listOf<Pair<String, ChartData<*>?>>("null data" to null, "no data sets" to empty) +
                shapesOfValues.map { (name, values) -> name to make(values) }

        drawAll("LineChart", { LineChart(context) }, cases(LineData()) { v ->
            LineData(LineDataSet(v.mapIndexed { i, y -> Entry(i.toFloat(), y) }, "").apply { isDrawCirclesEnabled = true; isDrawFilledEnabled = true })
        })
        drawAll("BarChart", { BarChart(context) }, cases(BarData()) { v -> BarData(BarDataSet(v.mapIndexed { i, y -> BarEntry(i.toFloat(), y) }, "")) })
        drawAll("StackedBarChart", { BarChart(context) }, cases(BarData()) { v ->
            BarData(BarDataSet(v.mapIndexed { i, y -> BarEntry(i.toFloat(), if (i == 0) emptyList() else listOf(y, y)) }, ""))
        })
        drawAll("HorizontalBarChart", { HorizontalBarChart(context) }, cases(BarData()) { v -> BarData(BarDataSet(v.mapIndexed { i, y -> BarEntry(i.toFloat(), y) }, "")) })
        drawAll("ScatterChart", { ScatterChart(context) }, cases(ScatterData()) { v -> ScatterData(ScatterDataSet(v.mapIndexed { i, y -> Entry(i.toFloat(), y) }, "")) })
        drawAll("BubbleChart", { BubbleChart(context) }, cases(BubbleData()) { v -> BubbleData(BubbleDataSet(v.mapIndexed { i, y -> BubbleEntry(i.toFloat(), y, y) }, "")) })
        drawAll("CandleStickChart", { CandleStickChart(context) }, cases(CandleData()) { v ->
            // high below low on purpose
            CandleData(CandleDataSet(v.mapIndexed { i, y -> CandleEntry(i.toFloat(), y - 1f, y + 1f, y, y) }, ""))
        })
        drawAll("PieChart", { PieChart(context) }, cases(PieData()) { v -> PieData(PieDataSet(v.map { PieEntry(it) }, "")) })
        drawAll("RadarChart", { RadarChart(context) }, cases(RadarData()) { v ->
            RadarData(listOf(RadarDataSet(v.map { RadarEntry(it) }, "a"), RadarDataSet(v.take(1).map { RadarEntry(it) }, "b")))
        })
        drawAll("CombinedChart", { CombinedChart(context) }, cases(CombinedData()) { v ->
            CombinedData().apply { lineData = LineData(LineDataSet(v.mapIndexed { i, y -> Entry(i.toFloat(), y) }, "")) }
        })
    }

    @Test
    fun pieAnglesStayFinite() {
        val chart = Fixtures.lay { PieChart(context) }

        chart.data = PieData(PieDataSet(listOf(PieEntry(0f), PieEntry(0f)), ""))
        assertTrue(chart.drawAngles.all { it == 0f })

        chart.data = PieData(PieDataSet(listOf(PieEntry(-1f), PieEntry(3f)), ""))
        assertEquals(360f, chart.absoluteAngles.last(), 0.01f)

        chart.minAngleForSlices = 90f
        chart.data = PieData(PieDataSet(List(4) { PieEntry(1f) }, ""))
        assertTrue(chart.drawAngles.all { it == 90f })

        assertTrue(chart.getAngleForPoint(chart.centerOffsets.x, chart.centerOffsets.y).isFinite())
    }

    @Test
    fun highlightsPastTheDataClearTheSelection() {
        val pie = Fixtures.lay { PieChart(context) }
        pie.marker = MarkerImage(context, android.R.drawable.star_on)
        pie.data = PieData(PieDataSet(List(5) { PieEntry(1f) }, ""))
        pie.highlightValue(4f, 0)
        pie.data = PieData(PieDataSet(List(2) { PieEntry(1f) }, ""))
        pie.draw(Fixtures.canvas())
        pie.highlightValue(9f, 0)
        assertFalse(pie.valuesToHighlight())

        val radar = Fixtures.lay { RadarChart(context) }
        radar.data = RadarData(listOf(RadarDataSet(List(6) { RadarEntry(1f) }, "long"), RadarDataSet(List(2) { RadarEntry(2f) }, "short")))
        radar.highlightValues(listOf(Highlight(5f, 1, -1)))
        radar.draw(Fixtures.canvas())
        val c = radar.centerOffsets
        for (angle in 0 until 360 step 15) {
            val rad = Math.toRadians(angle.toDouble())
            radar.getHighlightByTouchPoint(c.x + 100f * Math.cos(rad).toFloat(), c.y + 100f * Math.sin(rad).toFloat())
        }
    }

    @Test
    fun combinedChartTapHighlightsTheBar() {
        val chart = Fixtures.lay { CombinedChart(context) }
        chart.data = CombinedData().apply {
            barData = BarData(BarDataSet(List(5) { BarEntry(it.toFloat(), 10f) }, "bars"))
            lineData = LineData(LineDataSet(List(5) { Entry(it.toFloat(), 5f) }, "line"))
        }
        chart.draw(Fixtures.canvas())

        val pixel = chart.getPixelForValues(2f, 9f, AxisDependency.LEFT)
        val h = chart.getHighlightByTouchPoint(pixel.x.toFloat(), pixel.y.toFloat())!!
        assertNotEquals(-1, h.dataIndex)
        chart.highlightValue(h)
        assertTrue(chart.valuesToHighlight())
    }

    @Test
    fun unusableViewportValuesLeaveTheMatrixFinite() {
        val chart = Fixtures.lay { LineChart(context) }
        chart.data = LineData(LineDataSet(List(11) { Entry(it.toFloat(), it.toFloat()) }, ""))

        onMain {
            chart.moveViewTo(Float.NaN, 1f, AxisDependency.LEFT)
            chart.centerViewTo(Float.POSITIVE_INFINITY, 1f, AxisDependency.LEFT)
            chart.zoom(Float.NaN, 1f, 0f, 0f)
            chart.setVisibleXRangeMaximum(0f)
            chart.setVisibleXRangeMinimum(-1f)
            chart.setVisibleYRange(Float.NaN, 5f, AxisDependency.LEFT)
        }
        assertFinite(chart)

        chart.setVisibleXRange(2f, 5f)
        assertEquals(2f, chart.viewPortHandler.minScaleX, 0.01f)
        assertEquals(5f, chart.viewPortHandler.maxScaleX, 0.01f)

        var unsized: BarChart? = null
        onMain { unsized = BarChart(context) }
        assertFalse(unsized!!.saveToGallery("never"))
    }

    @Test
    fun gesturesKeepTheChartFinite() {
        val line = Fixtures.lay { LineChart(context) }
        line.data = LineData(LineDataSet(List(11) { Entry(it.toFloat(), it.toFloat()) }, ""))
        line.isScaleYEnabled = false
        onMain {
            // Two fingers above each other while only x can scale: the x distance is 0 before and after.
            val points = listOf(500f to 300f, 500f to 700f)
            line.onTouchEvent(touch(MotionEvent.ACTION_DOWN, points.take(1)))
            line.onTouchEvent(touch(MotionEvent.ACTION_POINTER_DOWN or (1 shl MotionEvent.ACTION_POINTER_INDEX_SHIFT), points))
            line.onTouchEvent(touch(MotionEvent.ACTION_MOVE, listOf(500f to 250f, 500f to 750f)))
            line.onTouchEvent(touch(MotionEvent.ACTION_POINTER_UP or (1 shl MotionEvent.ACTION_POINTER_INDEX_SHIFT), points))
            line.onTouchEvent(touch(MotionEvent.ACTION_UP, points.take(1)))
        }
        assertFinite(line)

        val pie = Fixtures.lay { PieChart(context) }
        pie.isDrawHoleEnabled = false
        pie.data = PieData(PieDataSet(List(3) { PieEntry(1f) }, ""))
        val c = pie.centerOffsets
        onMain {
            pie.onTouchEvent(touch(MotionEvent.ACTION_DOWN, listOf(c.x to c.y)))
            pie.onTouchEvent(touch(MotionEvent.ACTION_MOVE, listOf(c.x + 100f to c.y + 100f)))
            pie.onTouchEvent(touch(MotionEvent.ACTION_MOVE, listOf(c.x + 150f to c.y)))
            pie.onTouchEvent(touch(MotionEvent.ACTION_UP, listOf(c.x + 150f to c.y)))
        }
        assertTrue(pie.rotationAngle.isFinite())
    }

    @Test
    fun negativeAnimationDurationsAreAccepted() {
        val pie = Fixtures.lay { PieChart(context) }
        onMain {
            pie.animateXY(-1, -1)
            pie.spin(-1, 0f, 90f)
        }
    }

    private fun assertFinite(chart: BarLineChartBase<*>) {
        val values = FloatArray(9)
        chart.viewPortHandler.matrixTouch.getValues(values)
        assertTrue(values.joinToString(), values.all { it.isFinite() })
    }

    private fun touch(action: Int, points: List<Pair<Float, Float>>): MotionEvent {
        val now = SystemClock.uptimeMillis()
        val properties = Array(points.size) { MotionEvent.PointerProperties().apply { id = it; toolType = MotionEvent.TOOL_TYPE_FINGER } }
        val coords = Array(points.size) { MotionEvent.PointerCoords().apply { x = points[it].first; y = points[it].second; pressure = 1f; size = 1f } }
        return MotionEvent.obtain(now, now, action, points.size, properties, coords, 0, 0, 1f, 1f, 0, 0, 0, 0)
    }
}
