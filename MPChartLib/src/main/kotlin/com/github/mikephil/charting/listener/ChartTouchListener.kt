package com.github.mikephil.charting.listener

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.highlight.Highlight
import kotlin.math.sqrt

/**
 * Base class for the touch handling of a chart. Every chart creates one touch listener for itself:
 * [BarLineChartTouchListener] for bar, line, scatter, candle and bubble charts and
 * [PieRadarChartTouchListener] for pie and radar charts.
 *
 * It detects taps, long presses and flings with a [GestureDetector], keeps the current touch mode and
 * forwards gesture events to the chart's [OnChartGestureListener]s.
 */
abstract class ChartTouchListener<T : Chart<*>>(protected val chart: T) : GestureDetector.SimpleOnGestureListener(), View.OnTouchListener {

    /**
     * The kind of gesture the user performed on the chart, reported to [OnChartGestureListener].
     *
     * [NONE] no gesture yet; [DRAG] single finger drag that pans the chart or moves the highlight;
     * [X_ZOOM] and [Y_ZOOM] two finger zoom along one axis; [PINCH_ZOOM] two finger zoom along both axes;
     * [ROTATE] single finger rotation of a pie or radar chart; [SINGLE_TAP], [DOUBLE_TAP], [LONG_PRESS] and
     * [FLING] the corresponding tap, press and swipe gestures.
     */
    enum class ChartGesture {
        NONE, DRAG, X_ZOOM, Y_ZOOM, PINCH_ZOOM, ROTATE, SINGLE_TAP, DOUBLE_TAP, LONG_PRESS, FLING
    }

    /** The last gesture that was recognised, [ChartGesture.NONE] before any touch. */
    var lastGesture = ChartGesture.NONE
        protected set

    /** The touch state the listener is currently in, one of the constants such as [NONE], [DRAG] or [PINCH_ZOOM]. */
    var touchMode = NONE
        protected set

    /** The highlight selected by the last touch, null when nothing is selected by touch. */
    var lastHighlighted: Highlight? = null

    /** Detector that turns raw touch events into taps, long presses and flings. */
    protected val gestureDetector = GestureDetector(chart.context, this)

    /** Reports the start of a touch gesture to the chart's gesture listeners. */
    fun startAction(me: MotionEvent) {
        chart.notifyGesture { it.onChartGestureStart(me, lastGesture) }
    }

    /** Reports the end of a touch gesture to the chart's gesture listeners. */
    fun endAction(me: MotionEvent) {
        chart.notifyGesture { it.onChartGestureEnd(me, lastGesture) }
    }

    /**
     * Applies [h] as the chart's highlight and remembers it in [lastHighlighted]. Tapping the already
     * highlighted value, or passing null, clears the highlight instead. The chart's selection listeners are called.
     */
    protected fun performHighlight(h: Highlight?, e: MotionEvent) {
        if (h == null || h.equalTo(lastHighlighted)) {
            chart.highlightValue(null, true)
            lastHighlighted = null
        } else {
            chart.highlightValue(h, true)
            lastHighlighted = h
        }
    }

    companion object {

        /** Touch mode: no gesture in progress. */
        const val NONE = 0
        /** Touch mode: one finger is dragging. */
        const val DRAG = 1
        /** Touch mode: two fingers are zooming the x axis. */
        const val X_ZOOM = 2
        /** Touch mode: two fingers are zooming the y axis. */
        const val Y_ZOOM = 3
        /** Touch mode: two fingers are zooming both axes. */
        const val PINCH_ZOOM = 4
        /** Touch mode: a zoom just ended and one finger is still down; no drag is started from it. */
        const val POST_ZOOM = 5
        /** Touch mode: one finger is rotating a pie or radar chart. */
        const val ROTATE = 6

        /** Straight line distance in pixels between the points ([eventX], [eventY]) and ([startX], [startY]). */
        fun distance(eventX: Float, startX: Float, eventY: Float, startY: Float): Float {
            val dx = eventX - startX
            val dy = eventY - startY
            return sqrt(dx * dx + dy * dy)
        }
    }
}
