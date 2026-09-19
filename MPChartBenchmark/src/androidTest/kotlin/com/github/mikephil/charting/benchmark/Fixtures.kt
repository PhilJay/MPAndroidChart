package com.github.mikephil.charting.benchmark

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.view.View
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.BaseDataSet
import com.github.mikephil.charting.data.BubbleDataSet
import com.github.mikephil.charting.data.BubbleEntry
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.ScatterDataSet
import kotlin.random.Random

/** Chart sizing, deterministic data generators and the offscreen canvas the benchmarks draw into. */
object Fixtures {

    const val WIDTH = 1080
    const val HEIGHT = 1200

    /** How many points stay visible after [zoomToVisiblePoints], roughly what a user looks at while panning. */
    const val VISIBLE_POINTS = 300

    private const val SEED = 42

    /** Builds and lays out a chart on the main thread; a chart sets up a [android.view.GestureDetector] and so needs a Looper. */
    fun <C : View> lay(create: () -> C): C {
        var built: C? = null
        androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().runOnMainSync {
            val chart = create()
            chart.measure(
                View.MeasureSpec.makeMeasureSpec(WIDTH, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(HEIGHT, View.MeasureSpec.EXACTLY),
            )
            chart.layout(0, 0, WIDTH, HEIGHT)
            built = chart
        }
        return built!!
    }

    fun canvas(): Canvas = Canvas(Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888))

    fun lineEntries(n: Int): ArrayList<Entry<Nothing>> {
        val random = Random(SEED)
        val out = ArrayList<Entry<Nothing>>(n)
        var y = 100f
        for (i in 0 until n) {
            y += random.nextFloat() * 10f - 5f
            out.add(Entry(i.toFloat(), y))
        }
        return out
    }

    fun barEntries(n: Int): ArrayList<BarEntry<Nothing>> {
        val random = Random(SEED)
        val out = ArrayList<BarEntry<Nothing>>(n)
        for (i in 0 until n) out.add(BarEntry(i.toFloat(), random.nextFloat() * 100f))
        return out
    }

    fun stackedBarEntries(n: Int): ArrayList<BarEntry<Nothing>> {
        val random = Random(SEED)
        val out = ArrayList<BarEntry<Nothing>>(n)
        for (i in 0 until n) {
            val stack = listOf(random.nextFloat() * 30f, random.nextFloat() * 30f, random.nextFloat() * 30f)
            out.add(BarEntry(i.toFloat(), stack))
        }
        return out
    }

    fun candleEntries(n: Int): ArrayList<CandleEntry<Nothing>> {
        val random = Random(SEED)
        val out = ArrayList<CandleEntry<Nothing>>(n)
        var mid = 100f
        for (i in 0 until n) {
            mid += random.nextFloat() * 6f - 3f
            val spread = random.nextFloat() * 4f + 1f
            out.add(CandleEntry(i.toFloat(), mid + spread, mid - spread, mid - spread / 2f, mid + spread / 2f))
        }
        return out
    }

    fun bubbleEntries(n: Int): ArrayList<BubbleEntry<Nothing>> {
        val random = Random(SEED)
        val out = ArrayList<BubbleEntry<Nothing>>(n)
        for (i in 0 until n) out.add(BubbleEntry(i.toFloat(), random.nextFloat() * 100f, random.nextFloat() * 10f))
        return out
    }

    fun lineSet(entries: List<Entry<Nothing>>, circles: Boolean = false, mode: LineDataSet.Mode = LineDataSet.Mode.LINEAR, colors: Int = 1) =
        LineDataSet(entries, "line").apply {
            this.mode = mode
            isDrawCirclesEnabled = circles
            quiet(this)
            if (colors > 1) this.colors = List(colors) { Color.rgb(it * 20 % 255, 120, 200) }
        }

    fun barSet(entries: List<BarEntry<Nothing>>, stacked: Boolean = false) =
        BarDataSet(entries, "bar").apply {
            quiet(this)
            if (stacked) colors = listOf(Color.RED, Color.GREEN, Color.BLUE)
        }

    fun scatterSet(entries: List<Entry<Nothing>>) =
        ScatterDataSet(entries, "scatter").apply {
            setScatterShape(ScatterChart.ScatterShape.CIRCLE)
            scatterShapeSize = 6f
            quiet(this)
        }

    fun candleSet(entries: List<CandleEntry<Nothing>>) = CandleDataSet(entries, "candle").apply { quiet(this) }

    fun bubbleSet(entries: List<BubbleEntry<Nothing>>) = BubbleDataSet(entries, "bubble").apply { quiet(this) }

    /** Value labels and icons are off in every fixture; nobody renders 100k of either, and they would swamp every other cost. */
    private fun quiet(set: BaseDataSet<*>) {
        set.isDrawValuesEnabled = false
        set.isDrawIconsEnabled = false
    }

    /** Scales x so that about [VISIBLE_POINTS] of [total] points fill the viewport. */
    fun zoomToVisiblePoints(chart: BarLineChartBase<*>, total: Int, visible: Int = VISIBLE_POINTS) {
        val scale = (total.toFloat() / visible).coerceAtLeast(1f)
        val matrix = Matrix(chart.viewPortHandler.matrixTouch)
        matrix.postScale(scale, 1f, 0f, 0f)
        chart.viewPortHandler.refresh(matrix, chart, false)
    }

    /** Moves the viewport sideways the way a drag does, without going through the touch listener. */
    fun panBy(chart: BarLineChartBase<*>, dx: Float) {
        val matrix = Matrix(chart.viewPortHandler.matrixTouch)
        matrix.postTranslate(dx, 0f)
        chart.viewPortHandler.refresh(matrix, chart, false)
    }

    fun resetViewport(chart: BarLineChartBase<*>) {
        chart.viewPortHandler.refresh(Matrix(), chart, false)
    }

    fun context(): Context = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().targetContext
}
