package com.github.mikephil.charting.devicetest

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.SystemClock
import android.view.Choreographer
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.test.core.app.ActivityScenario
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
import com.github.mikephil.charting.charts.PieRadarChartBase
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.YAxis.AxisDependency
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.BaseDataSet
import com.github.mikephil.charting.data.BubbleData
import com.github.mikephil.charting.data.BubbleDataSet
import com.github.mikephil.charting.data.BubbleEntry
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.ChartData
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.DataSet
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
import com.github.mikephil.charting.interfaces.datasets.IDataSet
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/** Feeds every chart type hostile data and hostile calls, drawing each through the window and into a bitmap. */
@RunWith(AndroidJUnit4::class)
class HostileInputTest {

    enum class Kind { LINE, BAR, HORIZONTAL_BAR, COMBINED, PIE, RADAR, SCATTER, BUBBLE, CANDLE }

    private class Case(
        val name: String,
        val kinds: Set<Kind> = Kind.entries.toSet(),
        val setup: (Chart<*>) -> Unit = {},
        val build: (Kind) -> ChartData<*>?,
    )

    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private lateinit var scenario: ActivityScenario<HostActivity>
    private lateinit var frame: ChartFrame
    private val failures = mutableListOf<String>()

    @Before
    fun open() {
        scenario = ActivityScenario.launch(HostActivity::class.java)
        scenario.onActivity {
            frame = ChartFrame(it)
            it.setContentView(frame)
        }
    }

    @After
    fun close() = scenario.close()

    @Test
    fun hostileDataDrawsAndTakesTouches() {
        for (kind in Kind.entries) for (case in cases) {
            if (kind !in case.kinds) continue
            val label = "$kind / ${case.name}"
            val started = SystemClock.uptimeMillis()
            val chart = attach(kind)
            guard("$label set data") {
                case.setup(chart)
                chart.put(case.build(kind))
            }
            drawBoth(chart, label)
            touch(chart, label)
            guard("$label highlight") { chart.highlightValue(firstX(chart), 0) }
            drawBoth(chart, "$label after touch")
            val took = SystemClock.uptimeMillis() - started
            if (took > 30_000) fail("$label slow: $took ms")
        }
        report()
    }

    @Test
    fun hostileCallsOnLiveCharts() {
        for (kind in Kind.entries) {
            animateWhileReplacingData(kind)
            highlightThenShrink(kind)
            highlightOutOfRange(kind)
            extremeViewport(kind)
            detachWhileAnimating(kind)
        }
        report()
    }

    @Test
    fun zeroSizedChart() {
        for (kind in Kind.entries) {
            for (data in listOf(null, normal(kind))) {
                val label = "$kind 0x0 ${if (data == null) "no data" else "data"}"
                val chart = attach(kind, 0, 0)
                guard("$label set data") { chart.put(data) }
                drawBoth(chart, label)
                guard("$label toBitmap") { chart.toBitmap() }
                touch(chart, label)
                guard("$label highlight") { chart.highlightValue(0f, 0) }
                guard("$label animate") { chart.animateXY(200, 200) }
                if (chart is BarLineChartBase<*>) guard("$label zoom") {
                    chart.zoom(5f, 5f, 0f, 0f)
                    chart.moveViewTo(3f, 3f, AxisDependency.LEFT)
                    chart.fitScreen()
                }
                awaitFrames(20)
                drawBoth(chart, label)
            }
        }
        report()
    }

    private fun animateWhileReplacingData(kind: Kind) {
        val label = "$kind animate + setData"
        val chart = attach(kind)
        guard(label) {
            chart.put(normal(kind))
            chart.animateXY(400, 400)
        }
        awaitFrames(5)
        guard(label) { chart.put(points(kind, List(3) { it.toFloat() to it * 2f })) }
        awaitFrames(5)
        guard(label) { chart.put(null) }
        awaitFrames(3)
        guard(label) {
            chart.put(empty(kind))
            chart.animateXY(300, 300)
        }
        awaitFrames(5)
        guard(label) { chart.put(normal(kind)) }
        awaitFrames(30)
        drawBoth(chart, label)
    }

    private fun highlightThenShrink(kind: Kind) {
        val label = "$kind highlight then shrink"
        val chart = attach(kind)
        guard(label) {
            chart.put(normal(kind))
            chart.highlightValue(19f, 0, stackIndex = 1)
        }
        drawBoth(chart, label)
        guard("$label remove tail") { shrink(chart, 5) }
        drawBoth(chart, label)
        tap(chart, label)
        guard("$label remove all") { shrink(chart, 0) }
        drawBoth(chart, label)
        touch(chart, label)
        guard("$label remove set") {
            chart.put(normal(kind))
            chart.highlightValue(3f, 0)
            chart.data!!.removeDataSet(0)
            chart.notifyDataSetChanged()
        }
        drawBoth(chart, label)
        tap(chart, label)
    }

    private fun highlightOutOfRange(kind: Kind) {
        val label = "$kind highlight out of range"
        val chart = attach(kind)
        guard(label) { chart.put(normal(kind)) }
        val calls = listOf<Pair<String, Chart<*>.() -> Unit>>(
            "x 1e9" to { highlightValue(1e9f, 0) },
            "x NaN" to { highlightValue(Float.NaN, 0) },
            "x -Inf" to { highlightValue(Float.NEGATIVE_INFINITY, 0) },
            "set 99" to { highlightValue(3f, 99) },
            "set -1" to { highlightValue(3f, -1) },
            "dataIndex 9" to { highlightValue(3f, 0, dataIndex = 9) },
            "stack 99" to { highlightValue(3f, 0, stackIndex = 99) },
            "stack -7" to { highlightValue(3f, 0, stackIndex = -7) },
            "raw set 99" to { highlightValues(listOf(Highlight(3f, 99, 0))) },
            "raw dataIndex 9" to { highlightValues(listOf(Highlight(3f, 1f, 0, 9))) },
            "raw stack 99" to { highlightValues(listOf(Highlight(3f, 0, 99))) },
            "raw x 1e9" to { highlightValues(listOf(Highlight(1e9f, 0, -1))) },
            "raw NaN" to { highlightValues(listOf(Highlight(Float.NaN, Float.NaN, 0))) },
        )
        for ((name, call) in calls) {
            guard("$label $name") { chart.call() }
            drawBoth(chart, "$label $name")
        }
    }

    private fun extremeViewport(kind: Kind) {
        val label = "$kind extreme viewport"
        val chart = attach(kind)
        guard(label) { chart.put(normal(kind)) }
        val calls: List<Pair<String, () -> Unit>> = when (chart) {
            is BarLineChartBase<*> -> listOf(
                "zoom in 1e6" to { chart.zoom(1e6f, 1e6f, 10f, 10f) },
                "zoom out 1e-6" to { chart.zoom(1e-6f, 1e-6f, 10f, 10f) },
                "zoom 0" to { chart.zoom(0f, 0f, 10f, 10f) },
                "zoom NaN" to { chart.zoom(Float.NaN, Float.NaN, 10f, 10f) },
                "zoom value job" to { chart.zoom(1e7f, 1e7f, 1e9f, -1e9f, AxisDependency.LEFT) },
                "move far" to { chart.moveViewTo(1e9f, 1e9f, AxisDependency.LEFT) },
                "move NaN" to { chart.moveViewToX(Float.NaN) },
                "center far" to { chart.centerViewTo(-1e9f, -1e9f, AxisDependency.RIGHT) },
                "animated move" to { chart.moveViewToAnimated(1e9f, 0f, AxisDependency.LEFT, 200) },
                "visible range 0" to { chart.setVisibleXRange(0f, 0f) },
                "visible range negative" to { chart.setVisibleXRangeMaximum(-5f) },
                "fit" to { chart.fitScreen() },
            )
            is PieRadarChartBase<*> -> listOf(
                "rotate huge" to { chart.rotationAngle = 1e30f },
                "rotate NaN" to { chart.rotationAngle = Float.NaN },
                "spin" to { chart.spin(200, 0f, 1e9f) },
            )
            else -> emptyList()
        }
        for ((name, call) in calls) {
            guard("$label $name", call)
            awaitFrames(3)
            drawBoth(chart, "$label $name")
            touch(chart, "$label $name")
        }
        awaitFrames(20)
        drawBoth(chart, label)
    }

    private fun detachWhileAnimating(kind: Kind) {
        val label = "$kind detach while animating"
        val chart = attach(kind)
        guard(label) {
            chart.put(normal(kind))
            chart.animateXY(300, 300)
            if (chart is PieRadarChartBase<*>) chart.spin(300, 0f, 360f)
        }
        awaitFrames(3)
        guard(label) {
            fling(chart)
            frame.removeView(chart)
        }
        awaitFrames(30)
        guard(label) { frame.addView(chart, FrameLayout.LayoutParams(WIDTH, HEIGHT)) }
        awaitFrames(5)
        drawBoth(chart, label)
    }

    // Data

    private val cases = listOf(
        Case("null data") { null },
        Case("empty ChartData") { empty(it) },
        Case("set without entries") { points(it, emptyList()) },
        Case("one entry") { points(it, listOf(0f to 5f)) },
        Case("two identical entries") { points(it, listOf(1f to 5f, 1f to 5f)) },
        Case("all zero") { points(it, List(5) { i -> i.toFloat() to 0f }) },
        Case("negative") { points(it, listOf(0f to -1f, 1f to -5f, 2f to -3f)) },
        Case("mixed sign") { points(it, listOf(0f to -5f, 1f to 5f, 2f to -2f, 3f to 7f)) },
        Case("NaN y") { points(it, listOf(0f to 1f, 1f to Float.NaN, 2f to 3f)) },
        Case("all NaN y") { points(it, List(3) { i -> i.toFloat() to Float.NaN }) },
        Case("NaN x") { points(it, listOf(Float.NaN to 1f, 1f to 2f, 2f to 3f)) },
        Case("infinite y") { points(it, listOf(0f to Float.POSITIVE_INFINITY, 1f to Float.NEGATIVE_INFINITY, 2f to 3f)) },
        Case("infinite x") { points(it, listOf(Float.NEGATIVE_INFINITY to 1f, 1f to 2f, Float.POSITIVE_INFINITY to 3f)) },
        Case("float extremes") { points(it, listOf(0f to Float.MAX_VALUE, 1f to -Float.MAX_VALUE, 2f to Float.MIN_VALUE)) },
        Case("huge x") { points(it, listOf(-Float.MAX_VALUE to 1f, 0f to 2f, Float.MAX_VALUE to 3f)) },
        Case("tiny range") { points(it, listOf(0f to Float.MIN_VALUE, Float.MIN_VALUE to 2 * Float.MIN_VALUE)) },
        // Scatter and bubble draw every point exactly, so 500k of them in one combined chart need decimation.
        Case("100k entries", Kind.entries.toSet() - Kind.PIE - Kind.RADAR, { if (it is CombinedChart) it.isDecimationEnabled = true }) { points(it, List(100_000) { i -> i.toFloat() to (i % 97).toFloat() }) },
        // A pie or radar draws every slice and label each frame, so 100k of them take seconds per frame.
        Case("2k slices or corners", setOf(Kind.PIE, Kind.RADAR)) { points(it, List(2_000) { i -> i.toFloat() to (i % 97 + 1).toFloat() }) },
        Case("duplicate x") { points(it, listOf(1f to 1f, 1f to 5f, 1f to 3f, 2f to 2f)) },
        Case("unsorted x") { points(it, listOf(5f to 1f, 1f to 2f, 3f to 3f, -2f to 4f)) },
        Case("sets of different length", Kind.entries.toSet() - Kind.PIE) {
            val a = points(it, List(7) { i -> i.toFloat() to i.toFloat() })!!
            val b = points(it, listOf(0f to 2f))!!
            merge(a, b)
        },
        Case("stacks empty, single and hostile", setOf(Kind.BAR, Kind.HORIZONTAL_BAR, Kind.COMBINED)) {
            val set = BarDataSet(
                listOf(
                    BarEntry(0f, emptyList()),
                    BarEntry(1f, listOf(5f)),
                    BarEntry(2f, listOf(-3f, 4f, Float.NaN)),
                    BarEntry(3f, listOf(-1f, -2f)),
                    BarEntry(4f, listOf(Float.POSITIVE_INFINITY, 1f)),
                ),
                "stacks",
            ).apply { colors = listOf(0xffff0000.toInt(), 0xff00ff00.toInt(), 0xff0000ff.toInt()) }
            if (it == Kind.COMBINED) CombinedData().apply { barData = BarData(set) } else BarData(set)
        },
        Case("radar 1, 2 and 7 entries", setOf(Kind.RADAR)) {
            RadarData(
                RadarDataSet(listOf(RadarEntry(3f)), "one"),
                RadarDataSet(listOf(RadarEntry(1f), RadarEntry(2f)), "two"),
                RadarDataSet(List(7) { i -> RadarEntry(i.toFloat()) }, "seven"),
            )
        },
        Case("candle high below low", setOf(Kind.CANDLE, Kind.COMBINED)) {
            val set = CandleDataSet(
                listOf(
                    CandleEntry(0f, 1f, 10f, 5f, 3f),
                    CandleEntry(1f, 2f, 2f, 9f, -9f),
                    CandleEntry(2f, Float.NaN, 1f, 1f, Float.NaN),
                ),
                "candle",
            )
            if (it == Kind.COMBINED) CombinedData().apply { candleData = CandleData(set) } else CandleData(set)
        },
        Case("bubble zero, negative and NaN size", setOf(Kind.BUBBLE, Kind.COMBINED)) {
            val set = BubbleDataSet(
                listOf(BubbleEntry(0f, 1f, 0f), BubbleEntry(1f, 2f, -5f), BubbleEntry(2f, 3f, Float.NaN), BubbleEntry(3f, 1f, Float.POSITIVE_INFINITY)),
                "bubble",
            )
            if (it == Kind.COMBINED) CombinedData().apply { bubbleData = BubbleData(set) } else BubbleData(set)
        },
        Case("all bubbles size 0", setOf(Kind.BUBBLE)) { BubbleData(BubbleDataSet(listOf(BubbleEntry(0f, 1f, 0f), BubbleEntry(1f, 1f, 0f)), "b")) },
        Case("combined with only bars", setOf(Kind.COMBINED)) { CombinedData().apply { barData = BarData(BarDataSet(listOf(BarEntry(0f, 3f)), "b")) } },
        Case("combined with empty parts", setOf(Kind.COMBINED)) {
            CombinedData().apply {
                lineData = LineData()
                barData = BarData(BarDataSet(emptyList<BarEntry<Nothing>>(), "b"))
                candleData = CandleData()
                scatterData = ScatterData(ScatterDataSet(listOf(Entry(1f, 1f)), "s"))
            }
        },
        Case("pie all negative with labels", setOf(Kind.PIE)) {
            PieData(PieDataSet(listOf(PieEntry(-3f, "a"), PieEntry(-1f, "b")), "p"))
        },
        Case("pie one huge one tiny", setOf(Kind.PIE)) {
            PieData(PieDataSet(listOf(PieEntry(Float.MAX_VALUE, "a"), PieEntry(Float.MAX_VALUE, "b"), PieEntry(1e-30f, "c")), "p"))
        },
    )

    private fun normal(kind: Kind) = points(kind, List(20) { it.toFloat() to (it % 7 + 1).toFloat() })!!.let {
        if (kind == Kind.BAR || kind == Kind.HORIZONTAL_BAR) {
            BarData(BarDataSet(List(20) { i -> BarEntry(i.toFloat(), listOf(1f, 2f, (i % 3).toFloat())) }, "stacked"))
        } else it
    }

    private fun empty(kind: Kind): ChartData<*> = when (kind) {
        Kind.LINE -> LineData()
        Kind.BAR, Kind.HORIZONTAL_BAR -> BarData()
        Kind.COMBINED -> CombinedData()
        Kind.PIE -> PieData()
        Kind.RADAR -> RadarData()
        Kind.SCATTER -> ScatterData()
        Kind.BUBBLE -> BubbleData()
        Kind.CANDLE -> CandleData()
    }

    private fun points(kind: Kind, xy: List<Pair<Float, Float>>): ChartData<*>? {
        val line = { LineDataSet(xy.map { Entry(it.first, it.second) }, "line") }
        val bar = { BarDataSet(xy.map { BarEntry(it.first, it.second) }, "bar") }
        val scatter = { ScatterDataSet(xy.map { Entry(it.first, it.second) }, "scatter") }
        val bubble = { BubbleDataSet(xy.map { BubbleEntry(it.first, it.second, it.second) }, "bubble") }
        val candle = { CandleDataSet(xy.map { (x, y) -> CandleEntry(x, y + 1f, y - 1f, y - 0.5f, y + 0.5f) }, "candle") }
        return when (kind) {
            Kind.LINE -> LineData(line())
            Kind.BAR, Kind.HORIZONTAL_BAR -> BarData(bar())
            Kind.SCATTER -> ScatterData(scatter())
            Kind.BUBBLE -> BubbleData(bubble())
            Kind.CANDLE -> CandleData(candle())
            Kind.PIE -> PieData(PieDataSet(xy.map { PieEntry(it.second, "s") }, "pie"))
            Kind.RADAR -> RadarData(RadarDataSet(xy.map { RadarEntry(it.second) }, "radar"))
            Kind.COMBINED -> CombinedData().apply {
                lineData = LineData(line())
                barData = BarData(bar())
                scatterData = ScatterData(scatter())
                bubbleData = BubbleData(bubble())
                candleData = CandleData(candle())
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun merge(a: ChartData<*>, b: ChartData<*>): ChartData<*> {
        if (a is CombinedData && b is CombinedData) {
            a.lineData!!.addDataSet(b.lineData!!.dataSets[0])
            a.barData!!.addDataSet(b.barData!!.dataSets[0])
            a.notifyDataChanged()
            return a
        }
        (a as ChartData<IDataSet<out Entry<*>>>).addDataSet(b.dataSets[0] as IDataSet<out Entry<*>>)
        return a
    }

    /** Keeps the first [keep] entries of every data set and tells the chart. */
    private fun shrink(chart: Chart<*>, keep: Int) {
        val data = chart.data ?: return
        val sets = if (data is CombinedData) data.allData.flatMap { it.dataSets } else data.dataSets
        for (set in sets) {
            val entries = (set as DataSet<*>).entries
            if (entries.size > keep) entries.subList(keep, entries.size).clear()
            set.notifyDataSetChanged()
        }
        data.notifyDataChanged()
        chart.notifyDataSetChanged()
    }

    private fun firstX(chart: Chart<*>): Float = chart.data?.dataSets?.firstOrNull()?.let { if (it.entryCount > 0) it.getEntryForIndex(0).x else 0f } ?: 0f

    @Suppress("UNCHECKED_CAST")
    private fun Chart<*>.put(data: ChartData<*>?) {
        (this as Chart<ChartData<*>>).data = data
        (data?.dataSets?.firstOrNull() as? BaseDataSet<*>)?.isDrawValuesEnabled = true
        invalidate()
    }

    // Chart, drawing and touch

    /** Each chart catches what its own draw throws, since the window redraws a chart without going through its parent. */
    private fun newChart(kind: Kind): Chart<*> {
        val context = frame.context
        val errors = frame.errors
        return when (kind) {
            Kind.LINE -> object : LineChart(context) { override fun draw(canvas: Canvas) = catching(errors) { super.draw(canvas) } }
            Kind.BAR -> object : BarChart(context) { override fun draw(canvas: Canvas) = catching(errors) { super.draw(canvas) } }
            Kind.HORIZONTAL_BAR -> object : HorizontalBarChart(context) { override fun draw(canvas: Canvas) = catching(errors) { super.draw(canvas) } }
            Kind.COMBINED -> object : CombinedChart(context) { override fun draw(canvas: Canvas) = catching(errors) { super.draw(canvas) } }
            Kind.PIE -> object : PieChart(context) { override fun draw(canvas: Canvas) = catching(errors) { super.draw(canvas) } }
            Kind.RADAR -> object : RadarChart(context) { override fun draw(canvas: Canvas) = catching(errors) { super.draw(canvas) } }
            Kind.SCATTER -> object : ScatterChart(context) { override fun draw(canvas: Canvas) = catching(errors) { super.draw(canvas) } }
            Kind.BUBBLE -> object : BubbleChart(context) { override fun draw(canvas: Canvas) = catching(errors) { super.draw(canvas) } }
            Kind.CANDLE -> object : CandleStickChart(context) { override fun draw(canvas: Canvas) = catching(errors) { super.draw(canvas) } }
        }
    }

    private fun attach(kind: Kind, width: Int = WIDTH, height: Int = HEIGHT): Chart<*> {
        var chart: Chart<*>? = null
        instrumentation.runOnMainSync {
            frame.removeAllViews()
            chart = newChart(kind).also {
                it.marker = MarkerView(it.context, android.R.layout.simple_list_item_1)
                frame.addView(it, FrameLayout.LayoutParams(width, height))
            }
        }
        awaitFrames(2)
        return chart!!
    }

    private fun drawBoth(chart: Chart<*>, label: String) {
        guard("$label bitmap draw") {
            val bitmap = Bitmap.createBitmap(chart.width.coerceAtLeast(1), chart.height.coerceAtLeast(1), Bitmap.Config.ARGB_8888)
            chart.draw(Canvas(bitmap))
            dump(bitmap, label)
        }
        instrumentation.runOnMainSync { chart.invalidate() }
        awaitFrames(2)
        instrumentation.runOnMainSync {
            frame.errors.forEach { fail("$label draw: ${it.short()}") }
            frame.errors.clear()
        }
    }

    /** Saves what was drawn when the run is started with `-e dump true`, to look at the pictures by eye. */
    private fun dump(bitmap: Bitmap, label: String) {
        if (InstrumentationRegistry.getArguments().getString("dump") != "true") return
        val dir = java.io.File(frame.context.getExternalFilesDir(null), "hostile").apply { mkdirs() }
        java.io.File(dir, label.replace(Regex("[^A-Za-z0-9]+"), "_") + ".png").outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
    }

    private fun touch(chart: View, label: String) {
        tap(chart, label)
        val w = chart.width.coerceAtLeast(1).toFloat()
        val h = chart.height.coerceAtLeast(1).toFloat()
        guard("$label double tap") {
            val t = SystemClock.uptimeMillis()
            send(chart, t, MotionEvent.ACTION_DOWN, w / 2 to h / 2)
            send(chart, t, MotionEvent.ACTION_UP, w / 2 to h / 2)
            send(chart, t + 80, MotionEvent.ACTION_DOWN, w / 2 to h / 2)
            send(chart, t + 80, MotionEvent.ACTION_UP, w / 2 to h / 2)
        }
        guard("$label drag") {
            val t = SystemClock.uptimeMillis()
            send(chart, t, MotionEvent.ACTION_DOWN, w / 2 to h / 2)
            for (i in 1..10) send(chart, t + i * 30, MotionEvent.ACTION_MOVE, w / 2 - i * 40f to h / 2 + i * 10f)
            send(chart, t + 330, MotionEvent.ACTION_UP, w / 2 - 400f to h / 2 + 100f)
        }
        guard("$label pinch") {
            val t = SystemClock.uptimeMillis()
            send(chart, t, MotionEvent.ACTION_DOWN, w / 2 to h / 2)
            send(chart, t + 10, MotionEvent.ACTION_POINTER_DOWN or (1 shl MotionEvent.ACTION_POINTER_INDEX_SHIFT), w / 2 to h / 2, w / 2 + 20f to h / 2 + 20f)
            for (i in 1..10) send(chart, t + 10 + i * 20, MotionEvent.ACTION_MOVE, w / 2 - i * 30f to h / 2 - i * 30f, w / 2 + i * 30f to h / 2 + i * 30f)
            send(chart, t + 250, MotionEvent.ACTION_POINTER_UP or (1 shl MotionEvent.ACTION_POINTER_INDEX_SHIFT), w / 2 - 300f to h / 2 - 300f, w / 2 + 300f to h / 2 + 300f)
            send(chart, t + 260, MotionEvent.ACTION_UP, w / 2 - 300f to h / 2 - 300f)
        }
        guard("$label fling") { fling(chart) }
        awaitFrames(10)
    }

    private fun tap(chart: View, label: String) = guard("$label tap") {
        val t = SystemClock.uptimeMillis()
        val p = chart.width / 2f to chart.height / 2f
        send(chart, t, MotionEvent.ACTION_DOWN, p)
        send(chart, t + 50, MotionEvent.ACTION_UP, p)
    }

    private fun fling(chart: View) {
        val t = SystemClock.uptimeMillis()
        val w = chart.width.coerceAtLeast(1).toFloat()
        send(chart, t, MotionEvent.ACTION_DOWN, w / 2 to 50f)
        for (i in 1..4) send(chart, t + i * 5, MotionEvent.ACTION_MOVE, w / 2 + i * 150f to 50f)
        send(chart, t + 25, MotionEvent.ACTION_UP, w / 2 + 600f to 50f)
    }

    private fun send(view: View, time: Long, action: Int, vararg pointers: Pair<Float, Float>) {
        val props = Array(pointers.size) { MotionEvent.PointerProperties().apply { id = it; toolType = MotionEvent.TOOL_TYPE_FINGER } }
        val coords = Array(pointers.size) { MotionEvent.PointerCoords().apply { x = pointers[it].first; y = pointers[it].second; pressure = 1f; size = 1f } }
        val event = MotionEvent.obtain(time, time, action, pointers.size, props, coords, 0, 0, 1f, 1f, 0, 0, 0, 0)
        try {
            view.dispatchTouchEvent(event)
        } finally {
            event.recycle()
        }
    }

    private fun guard(label: String, block: () -> Unit) {
        android.util.Log.d("HostileInputTest", "run $label")
        instrumentation.runOnMainSync {
            try {
                block()
            } catch (t: Throwable) {
                fail("$label: ${t.short()}")
            }
        }
    }

    /** Waits for [count] frames, so posted jobs, animations and window draws have run. */
    private fun awaitFrames(count: Int) {
        repeat(count) {
            val latch = CountDownLatch(1)
            instrumentation.runOnMainSync { Choreographer.getInstance().postFrameCallback { latch.countDown() } }
            latch.await(5, TimeUnit.SECONDS)
        }
        instrumentation.waitForIdleSync()
    }

    private fun fail(message: String) {
        android.util.Log.e("HostileInputTest", message)
        failures += message
    }

    private fun report() {
        instrumentation.runOnMainSync {
            frame.errors.forEach { fail("late window draw: ${it.short()}") }
        }
        val distinct = failures.distinct()
        assertTrue("${distinct.size} failures:\n" + distinct.joinToString("\n\n"), distinct.isEmpty())
    }

    private fun catching(errors: MutableList<Throwable>, block: () -> Unit) {
        try {
            block()
        } catch (t: Throwable) {
            errors += t
        }
    }

    private fun Throwable.short() = toString() + "\n" + stackTrace.filterNot { it.className.startsWith("jdk.") || it.className.startsWith("java.util.") }.take(8).joinToString("\n") { "    at $it" }

    private companion object {
        const val WIDTH = 1080
        const val HEIGHT = 1200
    }
}
