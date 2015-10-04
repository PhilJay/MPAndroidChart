package com.github.mikephil.charting.listener;

import android.view.MotionEvent;

/**
 * Listener for callbacks when doing gestures on the chart.
 *
 * @author Philipp Jahoda
 */
public interface OnChartGestureListener {

    /**
     * Callbacks when a touch-gesture has started on the chart (ACTION_DOWN)
     *
     * @param me
     */
    void onChartGestureStart(MotionEvent me);

    /**
     * Callbacks when a touch-gesture has ended on the chart (ACTION_UP, ACTION_CANCEL)
     *
     * @param me
     */
    void onChartGestureEnd(MotionEvent me);

    /**
     * Callbacks when the chart is longpressed.
     *
     * @param me
     */
    void onChartLongPressed(MotionEvent me);

    /**
     * Callbacks when the chart is double-tapped.
     *
     * @param me
     */
    void onChartDoubleTapped(MotionEvent me);

    /**
     * Callbacks when the chart is single-tapped.
     *
     * @param me
     */
    void onChartSingleTapped(MotionEvent me);

    /**
     * Callbacks then a fling gesture is made on the chart.
     *
     * @param me1
     * @param me2
     * @param velocityX
     * @param velocityY
     */
    void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY);

    /**
     * Callbacks when the chart is scaled / zoomed via pinch zoom gesture.
     *
     * @param me
     * @param scaleX scalefactor on the x-axis
     * @param scaleY scalefactor on the y-axis
     */
    void onChartScale(MotionEvent me, float scaleX, float scaleY);

    /**
     * Callbacks when the chart is moved / translated via drag gesture.
     *
     * @param me
     * @param dX translation distance on the x-axis
     * @param dY translation distance on the y-axis
     */
    void onChartTranslate(MotionEvent me, float dX, float dY);
}
