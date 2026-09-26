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
public abstract class ChartTouchListener<T : Chart<*>>(protected val chart: T) : GestureDetector.SimpleOnGestureListener(), View.OnTouchListener {

    /**
     * The kind of gesture the user performed on the chart, reported to [OnChartGestureListener].
     *
     * [NONE] no gesture yet; [DRAG] single finger drag that pans the chart or moves the highlight;
     * [X_ZOOM] and [Y_ZOOM] two finger zoom along one axis; [PINCH_ZOOM] two finger zoom along both axes;
     * [ROTATE] single finger rotation of a pie or radar chart; [SINGLE_TAP], [DOUBLE_TAP], [LONG_PRESS] and
     * [FLING] the corresponding tap, press and swipe gestures.
     */
    public enum class ChartGesture {
        NONE, DRAG, X_ZOOM, Y_ZOOM, PINCH_ZOOM, ROTATE, SINGLE_TAP, DOUBLE_TAP, LONG_PRESS, FLING
    }

    /** The last gesture that was recognised, [ChartGesture.NONE] before any touch. */
    public var lastGesture: ChartGesture = ChartGesture.NONE
        protected set

    /** The touch state the listener is currently in, one of the constants such as [NONE], [DRAG] or [PINCH_ZOOM]. */
    public var touchMode: Int = NONE
        protected set

    /** The highlight selected by the last touch, null when nothing is selected by touch. */
    public var lastHighlighted: Highlight? = null

    /**
     * Detector that turns raw touch events into taps, long presses and flings. It is created on first use, on the
     * thread that handles touches, so a chart can be built on a thread without a Looper.
     */
    protected val gestureDetector: GestureDetector by lazy(LazyThreadSafetyMode.NONE) { GestureDetector(chart.context, this) }

    /** Reports the start of a touch gesture to the chart's gesture listeners. */
    public fun startAction(me: MotionEvent) {
        chart.notifyGesture { it.onChartGestureStart(me, lastGesture) }
    }

    /** Reports the end of a touch gesture to the chart's gesture listeners. */
    public fun endAction(me: MotionEvent) {
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

    public companion object {

        /** Touch mode: no gesture in progress. */
        public const val NONE: Int = 0
        /** Touch mode: one finger is dragging. */
        public const val DRAG: Int = 1
        /** Touch mode: two fingers are zooming the x axis. */
        public const val X_ZOOM: Int = 2
        /** Touch mode: two fingers are zooming the y axis. */
        public const val Y_ZOOM: Int = 3
        /** Touch mode: two fingers are zooming both axes. */
        public const val PINCH_ZOOM: Int = 4
        /** Touch mode: a zoom just ended and one finger is still down; no drag is started from it. */
        public const val POST_ZOOM: Int = 5
        /** Touch mode: one finger is rotating a pie or radar chart. */
        public const val ROTATE: Int = 6

        /** Straight line distance in pixels between the points ([eventX], [eventY]) and ([startX], [startY]). */
        public fun distance(eventX: Float, startX: Float, eventY: Float, startY: Float): Float {
            val dx = eventX - startX
            val dy = eventY - startY
            return sqrt(dx * dx + dy * dy)
        }
    }
}
