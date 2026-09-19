package com.github.mikephil.charting.compose

import android.view.MotionEvent
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
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import java.lang.ref.WeakReference

/**
 * Exposes what the user selects and where the viewport is as Compose state, and lets
 * composables drive the chart. Create it with [rememberChartState] and pass it to one chart
 * composable; one state belongs to one chart at a time. Listeners set on the chart itself keep
 * working, the state observes alongside them. All members are main-thread only.
 *
 * The functions do nothing while [isAttached] is false, that is before the chart composable has
 * created its view and after it left the composition. The viewport functions [fitScreen], [zoomIn],
 * [zoomOut], [resetZoom] and [moveViewToX] only affect line, bar, scatter, candle, bubble and combined charts;
 * the viewport properties reflect them after the next layout pass.
 */
@Stable
class ChartState {

    /** Entry the user tapped or [highlight] selected, null while nothing is highlighted. Every chart type updates it. */
    var selectedEntry: Entry<*>? by mutableStateOf(null)
        private set

    /** Highlight of [selectedEntry] with its data set, data and stack index, null while nothing is highlighted. */
    var selectedHighlight: Highlight? by mutableStateOf(null)
        private set

    /**
     * Lowest x value visible after dragging or zooming, in value space. Only
     * line, bar, scatter, candle, bubble and combined charts update it.
     */
    var lowestVisibleX by mutableFloatStateOf(0f)
        private set

    /**
     * Highest x value visible after dragging or zooming, in value space. Only
     * line, bar, scatter, candle, bubble and combined charts update it.
     */
    var highestVisibleX by mutableFloatStateOf(0f)
        private set

    /**
     * Horizontal zoom factor of the viewport, 1 when not zoomed. Only line, bar, scatter, candle, bubble
     * and combined charts update it.
     */
    var zoomX by mutableFloatStateOf(1f)
        private set

    /**
     * Vertical zoom factor of the viewport, 1 when not zoomed. Only line, bar, scatter, candle, bubble
     * and combined charts update it.
     */
    var zoomY by mutableFloatStateOf(1f)
        private set

    /**
     * Current rotation of the chart in degrees clockwise from 3 o'clock, 270 (12 o'clock) until the user
     * rotates. Only pie and radar charts update it.
     */
    var rotationAngle by mutableFloatStateOf(270f)
        private set

    private var chartRef: WeakReference<Chart<*>>? = null

    private var restored: List<Float>? = null

    private val chart: Chart<*>?
        get() = chartRef?.get()

    /** False until the chart composable has created its view, and again after it left the composition. */
    val isAttached: Boolean
        get() = chart != null

    /**
     * Highlights the entry closest to x value [x] in the data set at [dataSetIndex], as if the user had
     * tapped it, and updates [selectedEntry] and [selectedHighlight]. Clears the selection instead when
     * the index is out of range or the data set has no entry near [x]. Does nothing before the chart is attached.
     */
    fun highlight(x: Float, dataSetIndex: Int = 0) {
        chart?.highlightValue(x, dataSetIndex, callListener = true)
    }

    /**
     * Removes the highlight and sets [selectedEntry] and [selectedHighlight] to null. Does nothing before
     * the chart is attached.
     */
    fun clearHighlight() {
        chart?.highlightValue(null, callListener = true)
    }

    /**
     * Recomputes axes, legend and offsets and redraws after the data was changed in place, then refreshes
     * the viewport properties. Does nothing before the chart is attached.
     */
    fun notifyDataChanged() {
        chart?.notifyDataSetChanged()
        refresh()
    }

    /**
     * Animates the drawing along the x axis over [durationMillis] milliseconds. Does nothing before the
     * chart is attached.
     *
     * @param easing curve of the animation, linear by default.
     */
    fun animateX(durationMillis: Int, easing: EasingFunction = Easing.Linear) {
        chart?.animateX(durationMillis, easing)
    }

    /**
     * Animates the drawing along the y axis over [durationMillis] milliseconds. Does nothing before the
     * chart is attached.
     *
     * @param easing curve of the animation, linear by default.
     */
    fun animateY(durationMillis: Int, easing: EasingFunction = Easing.Linear) {
        chart?.animateY(durationMillis, easing)
    }

    /**
     * Animates the drawing along both axes at once, over [durationMillisX] and [durationMillisY]
     * milliseconds. Does nothing before the chart is attached.
     *
     * @param easingX curve of the x animation, linear by default.
     * @param easingY curve of the y animation, same as [easingX] by default.
     */
    fun animateXY(durationMillisX: Int, durationMillisY: Int, easingX: EasingFunction = Easing.Linear, easingY: EasingFunction = easingX) {
        chart?.animateXY(durationMillisX, durationMillisY, easingX, easingY)
    }

    /** Resets zoom and scroll position so that all data is visible. */
    fun fitScreen() = withViewport { fitScreen() }

    /** Zooms in by a factor of 1.4 around the center of the content area. */
    fun zoomIn() = withViewport { zoomIn() }

    /** Zooms out by a factor of 0.7 around the center of the content area. */
    fun zoomOut() = withViewport { zoomOut() }

    /** Resets the zoom level to 1 without moving the scroll position; use [fitScreen] to reset both. */
    fun resetZoom() = withViewport { resetZoom() }

    /** Scrolls so that x value [xValue] sits at the left edge of the content area, keeping the zoom. */
    fun moveViewToX(xValue: Float) = withViewport { moveViewToX(xValue) }

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

    internal fun attach(chart: Chart<*>) {
        val current = this.chart
        if (current === chart) return
        check(current == null) { "This ChartState is already attached to another chart. Use one state per chart." }
        chartRef = WeakReference(chart)
        chart.valueSelectedListeners += selectionListener
        chart.gestureListeners += gestureListener
        chart.viewTreeObserver.addOnDrawListener(drawListener)
        restored?.let { saved ->
            restored = null
            chart.addViewportJob { restore(chart, saved) }
        }
        refresh()
    }

    private fun restore(chart: Chart<*>, saved: List<Float>) {
        val (zoomX, zoomY, lowestX, rotation, hasHighlight) = saved
        if (chart is BarLineChartBase<*>) {
            chart.zoom(zoomX, zoomY, 0f, 0f)
            chart.moveViewToX(lowestX)
        }
        if (chart is PieRadarChartBase<*>) chart.rotationAngle = rotation
        if (hasHighlight == 1f) {
            chart.highlightValue(saved[5], saved[6], saved[7].toInt(), saved[8].toInt(), saved[9].toInt(), callListener = true)
        }
        chart.invalidate()
        chart.post { refresh() }
    }

    internal fun detach() {
        val chart = chart ?: return
        chart.valueSelectedListeners -= selectionListener
        chart.gestureListeners -= gestureListener
        chart.viewTreeObserver.takeIf { it.isAlive }?.removeOnDrawListener(drawListener)
        chartRef = null
    }

    internal fun refresh() {
        when (val chart = chart) {
            is BarLineChartBase<*> -> {
                lowestVisibleX = chart.lowestVisibleX
                highestVisibleX = chart.highestVisibleX
                zoomX = chart.zoomX
                zoomY = chart.zoomY
            }
            is PieRadarChartBase<*> -> rotationAngle = chart.rotationAngle
            else -> {}
        }
    }

    companion object {
        /**
         * Saver for `rememberSaveable` that keeps zoom, scroll position, rotation and the highlighted entry.
         * A restored state applies them once it is attached to a chart that has its data and size.
         */
        val Saver: Saver<ChartState, Any> = listSaver(
            save = { state ->
                val highlight = state.selectedHighlight
                listOf(
                    state.zoomX, state.zoomY, state.lowestVisibleX, state.rotationAngle,
                    if (highlight == null) 0f else 1f,
                    highlight?.x ?: 0f, highlight?.y ?: Float.NaN,
                    (highlight?.dataSetIndex ?: 0).toFloat(), (highlight?.dataIndex ?: -1).toFloat(), (highlight?.stackIndex ?: -1).toFloat(),
                )
            },
            restore = { saved -> ChartState().apply { restored = saved } },
        )
    }
}

/**
 * Creates a [ChartState] that survives configuration changes and process death. Zoom, scroll position,
 * rotation and the highlighted entry are saved and applied again once the chart has its data and size.
 * Pass the result to exactly one chart composable.
 */
@Composable
fun rememberChartState(): ChartState = rememberSaveable(saver = ChartState.Saver) { ChartState() }
