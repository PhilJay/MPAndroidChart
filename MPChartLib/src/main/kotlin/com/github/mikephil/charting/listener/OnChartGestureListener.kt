package com.github.mikephil.charting.listener

import android.view.MotionEvent

/**
 * Receives the touch gestures performed on a chart. Register it as `chart.onChartGestureListener`
 * or add it to `chart.gestureListeners`.
 *
 * Every method has an empty default body, so implement only the callbacks you need.
 */
interface OnChartGestureListener {

    /**
     * Called when a finger touches the chart.
     *
     * @param lastPerformedGesture the gesture recognised before this touch began.
     */
    fun onChartGestureStart(me: MotionEvent, lastPerformedGesture: ChartTouchListener.ChartGesture) {}

    /**
     * Called when the touch ends or is cancelled.
     *
     * @param lastPerformedGesture the gesture that was recognised during this touch.
     */
    fun onChartGestureEnd(me: MotionEvent, lastPerformedGesture: ChartTouchListener.ChartGesture) {}

    /** Called when the chart is long pressed. */
    fun onChartLongPressed(me: MotionEvent) {}

    /** Called when the chart is double tapped, before the double tap zoom is applied. */
    fun onChartDoubleTapped(me: MotionEvent) {}

    /** Called when the chart is single tapped, before the tap highlight is applied. */
    fun onChartSingleTapped(me: MotionEvent) {}

    /**
     * Called when a fast swipe is made on the chart.
     *
     * @param me1 the down event that started the fling, may be null.
     * @param me2 the move event that triggered the fling.
     * @param velocityX horizontal velocity in pixels per second.
     * @param velocityY vertical velocity in pixels per second.
     */
    fun onChartFling(me1: MotionEvent?, me2: MotionEvent, velocityX: Float, velocityY: Float) {}

    /**
     * Called when the chart is zoomed by pinch or double tap.
     *
     * @param scaleX scale factor applied on the x axis, 1 when the axis was not zoomed.
     * @param scaleY scale factor applied on the y axis, 1 when the axis was not zoomed.
     */
    fun onChartScale(me: MotionEvent, scaleX: Float, scaleY: Float) {}

    /**
     * Called while the chart is dragged, including deceleration after a fling.
     *
     * @param dX horizontal drag distance in pixels since the drag started.
     * @param dY vertical drag distance in pixels since the drag started.
     */
    fun onChartTranslate(me: MotionEvent, dX: Float, dY: Float) {}
}
