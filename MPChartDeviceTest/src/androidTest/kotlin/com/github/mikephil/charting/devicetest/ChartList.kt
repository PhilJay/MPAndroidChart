package com.github.mikephil.charting.devicetest

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.platform.app.InstrumentationRegistry
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.ChartData
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import java.lang.ref.WeakReference
import kotlin.random.Random

/** Activity whose content view comes from [content], so each test decides what it hosts. Without it, [root] is an empty frame. */
class HostActivity : Activity() {

    lateinit var root: FrameLayout
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(content?.invoke(this) ?: FrameLayout(this).also { root = it })
    }

    companion object {
        var content: ((Activity) -> View)? = null
    }
}

class ChartHolder(val chart: Chart<*>) : RecyclerView.ViewHolder(chart)

/**
 * Mixed line, bar, pie and combined charts. Every bind builds new data labelled with the position. Positions
 * divisible by 5 highlight their last entry, positions divisible by 7 (and not 5) leave whatever highlight the
 * recycled chart still has, all others clear it. Positions divisible by 3 run animateY on bind.
 */
class ChartAdapter(
    private val created: MutableList<WeakReference<Chart<*>>>,
    private val count: Int = 200,
    private val animate: Boolean = true,
) : RecyclerView.Adapter<ChartHolder>() {

    val bound = arrayOfNulls<ChartData<*>>(count)
    var binds = 0
        private set

    override fun getItemCount() = count

    override fun getItemViewType(position: Int) = position % 4

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChartHolder {
        val context = parent.context
        val chart: Chart<*> = when (viewType) {
            LINE -> LineChart(context)
            BAR -> BarChart(context)
            PIE -> PieChart(context)
            else -> CombinedChart(context)
        }
        chart.layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, parent.height / 3)
        if (chart is BarLineChartBase<*>) chart.isDragEnabled = true
        created += WeakReference(chart)
        return ChartHolder(chart)
    }

    override fun onBindViewHolder(holder: ChartHolder, position: Int) {
        binds++
        val chart = holder.chart
        val random = Random(position * 7919 + binds)
        val size = 5 + random.nextInt(40)
        val label = label(position)
        when (chart) {
            is CombinedChart -> chart.data = CombinedData().apply {
                lineData = LineData(LineDataSet(entries(size, random), label))
                barData = BarData(BarDataSet(entries(size, random).map { BarEntry(it.x, it.y) }, "$label bars"))
            }
            is LineChart -> chart.data = LineData(LineDataSet(entries(size, random), label))
            is BarChart -> chart.data = BarData(BarDataSet(entries(size, random).map { BarEntry(it.x, it.y) }, label))
            is PieChart -> chart.data = PieData(PieDataSet(List(3 + size % 6) { PieEntry(1f + random.nextFloat() * 10f) }, label))
        }
        bound[position] = chart.data

        when (expectation(position)) {
            Expect.Highlighted -> chart.highlightValue(lastX(chart), 0, dataIndex = if (chart is CombinedChart) 0 else -1)
            Expect.Untouched -> {}
            Expect.Cleared -> chart.highlightValue(null, callListener = false)
        }
        if (animate && position % 3 == 0) chart.animateY(800)
    }

    enum class Expect { Highlighted, Untouched, Cleared }

    companion object {
        const val LINE = 0
        const val BAR = 1
        const val PIE = 2

        fun label(position: Int) = "item $position"

        fun expectation(position: Int) = when {
            position % 5 == 0 -> Expect.Highlighted
            position % 7 == 0 -> Expect.Untouched
            else -> Expect.Cleared
        }

        /** The x of the last entry, which a smaller data set bound later does not have. */
        fun lastX(chart: Chart<*>): Float = when (val data = chart.data) {
            is CombinedData -> data.lineData!!.getDataSetByIndex(0)!!.xMax
            is PieData -> (data.dataSet!!.entryCount - 1).toFloat()
            else -> data!!.xMax
        }

        private fun entries(size: Int, random: Random): List<Entry<Nothing>> =
            List(size) { Entry(it.toFloat(), random.nextFloat() * 100f) }
    }
}

/** Builds a vertical list of charts that never takes focus, so nothing outside the list keeps it alive. */
fun chartList(activity: Activity, adapter: ChartAdapter): RecyclerView =
    RecyclerView(activity).apply {
        isFocusable = false
        descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
        layoutManager = LinearLayoutManager(activity)
        this.adapter = adapter
    }

/** The list [chartList] put into [activity]'s content. */
fun listIn(activity: Activity): RecyclerView =
    (activity.findViewById<ViewGroup>(android.R.id.content).getChildAt(0)) as RecyclerView

object Ui {

    private val instrumentation get() = InstrumentationRegistry.getInstrumentation()

    fun onMain(block: () -> Unit) = instrumentation.runOnMainSync(block)

    fun idle() = instrumentation.waitForIdleSync()

    /** Waits until the list stops scrolling, at most [timeoutMs]. */
    fun awaitScrollIdle(list: RecyclerView, timeoutMs: Long = 20_000) {
        val deadline = SystemClock.uptimeMillis() + timeoutMs
        while (SystemClock.uptimeMillis() < deadline) {
            idle()
            var state = RecyclerView.SCROLL_STATE_SETTLING
            onMain { state = list.scrollState }
            if (state == RecyclerView.SCROLL_STATE_IDLE) return
            Thread.sleep(50)
        }
        throw AssertionError("list still scrolling after $timeoutMs ms")
    }

    /** Draws [chart] into a software canvas of its own size, which runs every renderer including highlights and markers. */
    fun drawOffscreen(chart: View) {
        if (chart.width <= 0 || chart.height <= 0) return
        chart.draw(Canvas(Bitmap.createBitmap(chart.width, chart.height, Bitmap.Config.ARGB_8888)))
    }

    /** Starts a touch at [x], [y] in screen pixels and returns its down time for [move] and [up]. */
    fun down(x: Float, y: Float): Long {
        val downTime = SystemClock.uptimeMillis()
        send(downTime, downTime, MotionEvent.ACTION_DOWN, x, y)
        return downTime
    }

    /** Moves the pointer from [fromX], [fromY] by [dx], [dy] in steps of about 10 px, one step per frame. */
    fun move(downTime: Long, fromX: Float, fromY: Float, dx: Float, dy: Float) {
        val steps = (maxOf(kotlin.math.abs(dx), kotlin.math.abs(dy)) / 10f).toInt().coerceAtLeast(1)
        for (i in 1..steps) {
            send(downTime, SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, fromX + dx * i / steps, fromY + dy * i / steps)
            Thread.sleep(16)
        }
    }

    fun up(downTime: Long, x: Float, y: Float) = send(downTime, SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, x, y)

    private fun send(downTime: Long, eventTime: Long, action: Int, x: Float, y: Float) {
        val event = MotionEvent.obtain(downTime, eventTime, action, x, y, 0)
        instrumentation.sendPointerSync(event)
        event.recycle()
    }

    /**
     * Runs the garbage collector until every reference in [refs] is cleared or [timeoutMs] passes, and returns
     * how many are still reachable.
     */
    @Suppress("DEPRECATION")
    fun awaitCollected(refs: List<WeakReference<*>>, timeoutMs: Long = 10_000): Int {
        val deadline = SystemClock.uptimeMillis() + timeoutMs
        while (true) {
            idle()
            Runtime.getRuntime().gc()
            System.runFinalization()
            Runtime.getRuntime().gc()
            val alive = refs.count { it.get() != null }
            if (alive == 0 || SystemClock.uptimeMillis() > deadline) return alive
            Thread.sleep(100)
        }
    }
}
