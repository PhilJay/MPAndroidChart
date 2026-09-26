package com.github.mikephil.charting.compose

import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.animation.Easing.EasingFunction
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.charts.PieRadarChartBase
import com.github.mikephil.charting.components.YAxis.AxisDependency
import com.github.mikephil.charting.data.ChartData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.interfaces.datasets.IDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.jobs.MoveViewJob
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.MPPointD
import java.lang.ref.WeakReference

/**
 * Exposes what the user selects and where the viewport is as Compose state, and lets
 * composables drive the chart. Create it with [rememberChartState] and pass it to one chart
 * composable; one state belongs to one chart at a time, and passing it to a second chart moves it
 * there. Listeners set on the chart itself keep working, the state observes alongside them. All
 * members are main-thread only.
 *
 * The functions do nothing while [isAttached] is false, that is before the chart composable has
 * created its view and after it left the composition. The viewport functions [fitScreen], [zoomIn],
 * [zoomOut], [resetZoom] and [moveViewToX] only affect line, bar, scatter, candle, bubble and combined charts;
 * the viewport properties reflect them after the next layout pass.
 */
@Stable
public class ChartState {

    /** Entry the user tapped or [highlight] selected, null while nothing is highlighted. Every chart type updates it. */
    public var selectedEntry: Entry<*>? by mutableStateOf(null)
        private set

    /** Highlight of [selectedEntry] with its data set, data and stack index, null while nothing is highlighted. */
    public var selectedHighlight: Highlight? by mutableStateOf(null)
        private set

    /**
     * Lowest x value visible after dragging or zooming, in value space. Only
     * line, bar, scatter, candle, bubble and combined charts update it.
     */
    public var lowestVisibleX: Float by mutableFloatStateOf(0f)
        private set

    /**
     * Highest x value visible after dragging or zooming, in value space. Only
     * line, bar, scatter, candle, bubble and combined charts update it.
     */
    public var highestVisibleX: Float by mutableFloatStateOf(0f)
        private set

    /**
     * Horizontal zoom factor of the viewport, 1 when not zoomed. Only line, bar, scatter, candle, bubble
     * and combined charts update it.
     */
    public var zoomX: Float by mutableFloatStateOf(1f)
        private set

    /**
     * Vertical zoom factor of the viewport, 1 when not zoomed. Only line, bar, scatter, candle, bubble
     * and combined charts update it.
     */
    public var zoomY: Float by mutableFloatStateOf(1f)
        private set

    /**
     * Current rotation of the chart in degrees clockwise from 3 o'clock, 270 (12 o'clock) until the user
     * rotates. Only pie and radar charts update it.
     */
    public var rotationAngle: Float by mutableFloatStateOf(270f)
        private set

    private var chartRef: WeakReference<Chart<*>>? = null

    private var pendingRestore: List<Float>? = null

    private val topLeft = MPPointD(0.0, 0.0)
    private var topLeftX = Float.NaN
    private var topLeftY = Float.NaN

    private val chart: Chart<*>?
        get() = chartRef?.get()

    /** False until the chart composable has created its view, and again after it left the composition. */
    public val isAttached: Boolean
        get() = chart != null

    /**
     * Highlights the entry closest to x value [x] in the data set at [dataSetIndex], as if the user had
     * tapped it, and updates [selectedEntry] and [selectedHighlight]. Clears the selection instead when
     * the index is out of range or the data set has no entry near [x]. Does nothing before the chart is
     * attached.
     *
     * @param dataIndex on a combined chart, the position of the data in `CombinedData.allData` that holds the
     *   data set, as `CombinedData.getDataIndex` returns it. Ignored by the other charts.
     */
    public fun highlight(x: Float, dataSetIndex: Int = 0, dataIndex: Int = -1) {
        chart?.highlightValue(x, dataSetIndex, dataIndex, callListener = true)
    }

    /**
     * Removes the highlight and sets [selectedEntry] and [selectedHighlight] to null. Does nothing before
     * the chart is attached.
     */
    public fun clearHighlight() {
        chart?.highlightValue(null, callListener = true)
    }

    /**
     * Recomputes axes, legend and offsets and redraws after the data was changed in place, then refreshes
     * the viewport properties. Does nothing before the chart is attached.
     */
    public fun notifyDataChanged() {
        val chart = chart ?: return
        chart.notifyDataSetChanged()
        syncSelection()
        refresh()
    }

    /**
     * Animates the drawing along the x axis over [durationMillis] milliseconds. Does nothing before the
     * chart is attached.
     *
     * @param easing curve of the animation, linear by default.
     */
    public fun animateX(durationMillis: Int, easing: EasingFunction = Easing.Linear) {
        chart?.animateX(durationMillis, easing)
    }

    /**
     * Animates the drawing along the y axis over [durationMillis] milliseconds. Does nothing before the
     * chart is attached.
     *
     * @param easing curve of the animation, linear by default.
     */
    public fun animateY(durationMillis: Int, easing: EasingFunction = Easing.Linear) {
        chart?.animateY(durationMillis, easing)
    }

    /**
     * Animates the drawing along both axes at once, over [durationMillisX] and [durationMillisY]
     * milliseconds. Does nothing before the chart is attached.
     *
     * @param easingX curve of the x animation, linear by default.
     * @param easingY curve of the y animation, same as [easingX] by default.
     */
    public fun animateXY(durationMillisX: Int, durationMillisY: Int, easingX: EasingFunction = Easing.Linear, easingY: EasingFunction = easingX) {
        chart?.animateXY(durationMillisX, durationMillisY, easingX, easingY)
    }

    /**
     * Sets [newData] and animates every entry from the value it replaces to its own value over [durationMillis]
     * milliseconds, see [Chart.animateDataChange]. [selectedEntry] and [selectedHighlight] are refreshed when the
     * animation ends. Does nothing before the chart is attached; [onEnd] is not called then.
     *
     * @param newData data of the type the chart shows, for example `LineData` for a line chart.
     * @param easing curve of the animation, linear by default.
     * @param onEnd called once when the animation ends.
     */
    public fun animateDataChange(
        newData: ChartData<out IDataSet<out Entry<*>>>,
        durationMillis: Int,
        easing: EasingFunction = Easing.Linear,
        onEnd: () -> Unit = {},
    ) {
        @Suppress("UNCHECKED_CAST")
        val chart = chart as? Chart<ChartData<out IDataSet<out Entry<*>>>> ?: return
        chart.animateDataChange(newData, durationMillis, easing) {
            syncSelection()
            refresh()
            onEnd()
        }
    }

    /** Resets zoom and scroll position so that all data is visible. */
    public fun fitScreen(): Unit = withViewport { fitScreen() }

    /** Zooms in by a factor of 1.4 around the center of the content area. */
    public fun zoomIn(): Unit = withViewport { zoomIn() }

    /** Zooms out by a factor of 0.7 around the center of the content area. */
    public fun zoomOut(): Unit = withViewport { zoomOut() }

    /** Resets the zoom level to 1 without moving the scroll position; use [fitScreen] to reset both. */
    public fun resetZoom(): Unit = withViewport { resetZoom() }

    /** Scrolls so that x value [xValue] sits at the left edge of the content area, keeping the zoom. */
    public fun moveViewToX(xValue: Float): Unit = withViewport { moveViewToX(xValue) }

    private fun withViewport(action: BarLineChartBase<*>.() -> Unit) {
        val chart = chart as? BarLineChartBase<*> ?: return
        chart.action()
        chart.invalidate()
        chart.post { refresh() }
    }

    private val selectionListener = object : OnChartValueSelectedListener {
        override fun onValueSelected(e: Entry<*>, h: Highlight) {
            selectedEntry = e
            selectedHighlight = h
        }

        override fun onNothingSelected() {
            selectedEntry = null
            selectedHighlight = null
        }
    }

    private val gestureListener = object : OnChartGestureListener {
        override fun onChartGestureEnd(me: MotionEvent, lastPerformedGesture: ChartTouchListener.ChartGesture) = refresh()
        override fun onChartDoubleTapped(me: MotionEvent) = refresh()
        override fun onChartScale(me: MotionEvent, scaleX: Float, scaleY: Float) = refresh()
        override fun onChartTranslate(me: MotionEvent, dX: Float, dY: Float) = refresh()
    }

    private val drawListener = ViewTreeObserver.OnDrawListener { refresh() }

    private val windowListener = object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(view: View) = view.viewTreeObserver.addOnDrawListener(drawListener)
        override fun onViewDetachedFromWindow(view: View) = view.viewTreeObserver.removeOnDrawListener(drawListener)
    }

    internal fun attach(chart: Chart<*>) {
        val current = this.chart
        if (current === chart) return
        if (current != null) detach(current)
        chartRef = WeakReference(chart)
        chart.valueSelectedListeners += selectionListener
        chart.gestureListeners += gestureListener
        chart.addOnAttachStateChangeListener(windowListener)
        if (chart.isAttachedToWindow) chart.viewTreeObserver.addOnDrawListener(drawListener)
        pendingRestore?.let { saved ->
            chart.addViewportJob {
                if (this.chart === chart && pendingRestore === saved) {
                    pendingRestore = null
                    restore(chart, saved)
                }
            }
        }
        syncSelection()
        refresh()
    }

    private fun restore(chart: Chart<*>, saved: List<Float>) {
        val (zoomX, zoomY, lowestX, rotation, hasHighlight) = saved
        if (chart is BarLineChartBase<*>) {
            chart.zoom(zoomX, zoomY, 0f, 0f)
            val topLeftX = saved.getOrElse(10) { Float.NaN }
            val topLeftY = saved.getOrElse(11) { Float.NaN }
            if (topLeftX.isFinite() && topLeftY.isFinite()) {
                chart.addViewportJob(MoveViewJob.getInstance(chart.viewPortHandler, topLeftX, topLeftY, chart.getTransformer(AxisDependency.LEFT), chart))
            } else {
                chart.moveViewToX(lowestX)
            }
        }
        if (chart is PieRadarChartBase<*>) chart.rotationAngle = rotation
        if (hasHighlight == 1f) {
            chart.highlightValue(saved[5], saved[6], saved[7].toInt(), saved[8].toInt(), saved[9].toInt(), callListener = true)
        }
        chart.invalidate()
        chart.post { refresh() }
    }

    internal fun detach(chart: Chart<*>) {
        if (this.chart !== chart) return
        chart.valueSelectedListeners -= selectionListener
        chart.gestureListeners -= gestureListener
        chart.removeOnAttachStateChangeListener(windowListener)
        if (chart.isAttachedToWindow) chart.viewTreeObserver.removeOnDrawListener(drawListener)
        chartRef = null
    }

    internal fun syncSelection() {
        val chart = chart ?: return
        val highlight = chart.highlighted.firstOrNull()
        val entry = highlight?.let { chart.data?.getEntryForHighlight(it) }
        selectedEntry = entry
        selectedHighlight = if (entry == null) null else highlight
    }

    internal fun refresh() {
        when (val chart = chart) {
            is BarLineChartBase<*> -> {
                lowestVisibleX = chart.lowestVisibleX
                highestVisibleX = chart.highestVisibleX
                zoomX = chart.zoomX
                zoomY = chart.zoomY
                val handler = chart.viewPortHandler
                chart.getValuesByTouchPoint(handler.contentLeft, handler.contentTop, AxisDependency.LEFT, topLeft)
                topLeftX = topLeft.x.toFloat()
                topLeftY = topLeft.y.toFloat()
            }
            is PieRadarChartBase<*> -> rotationAngle = chart.rotationAngle
            else -> {}
        }
    }

    public companion object {
        private const val SAVED_SIZE = 12
        private const val SAVED_SIZE_WITHOUT_Y_SCROLL = 10

        /**
         * Saver for `rememberSaveable` that keeps zoom, the x and y scroll position, rotation and the highlighted entry.
         * A restored state applies them once it is attached to a chart that has its size, so the data should
         * be set by then; a highlight that no longer matches the data is dropped.
         */
        public val Saver: Saver<ChartState, Any> = listSaver(
            save = { state ->
                state.pendingRestore?.let { return@listSaver it }
                val highlight = state.selectedHighlight
                listOf(
                    state.zoomX, state.zoomY, state.lowestVisibleX, state.rotationAngle,
                    if (highlight == null) 0f else 1f,
                    highlight?.x ?: 0f, highlight?.y ?: Float.NaN,
                    (highlight?.dataSetIndex ?: 0).toFloat(), (highlight?.dataIndex ?: -1).toFloat(), (highlight?.stackIndex ?: -1).toFloat(),
                    state.topLeftX, state.topLeftY,
                )
            },
            restore = { saved -> ChartState().apply { pendingRestore = saved.takeIf { it.size == SAVED_SIZE || it.size == SAVED_SIZE_WITHOUT_Y_SCROLL } } },
        )
    }
}

/**
 * Creates a [ChartState] that survives configuration changes and process death. Zoom, x and y scroll position,
 * rotation and the highlighted entry are saved and applied again once the chart has its size.
 * Pass the result to exactly one chart composable.
 */
@Composable
public fun rememberChartState(): ChartState = rememberSaveable(saver = ChartState.Saver) { ChartState() }
