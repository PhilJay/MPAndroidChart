package com.github.mikephil.charting.listener;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.highlight.Highlight;

/**
 * Created by philipp on 12/06/15.
 */
public abstract class ChartTouchListener<T extends Chart<?>> extends GestureDetector.SimpleOnGestureListener implements View.OnTouchListener {

    // states
    protected static final int NONE = 0;
    protected static final int DRAG = 1;
    protected static final int X_ZOOM = 2;
    protected static final int Y_ZOOM = 3;
    protected static final int PINCH_ZOOM = 4;
    protected static final int POST_ZOOM = 5;
    protected static final int ROTATE = 6;

    /**
     * integer field that holds the current touch-state
     */
    protected int mTouchMode = NONE;

    /**
     * the last highlighted object (via touch)
     */
    protected Highlight mLastHighlighted;

    /**
     * the gesturedetector used for detecting taps and longpresses, ...
     */
    protected GestureDetector mGestureDetector;

    /**
     * the chart the listener represents
     */
    protected T mChart;

    public ChartTouchListener(T chart) {
        this.mChart = chart;

        mGestureDetector = new GestureDetector(chart.getContext(), this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        OnChartGestureListener l = mChart.getOnChartGestureListener();

        if (l != null) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    l.onChartGestureStart(event);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    l.onChartGestureEnd(event);
                    break;
                case MotionEvent.ACTION_UP:
                    l.onChartGestureEnd(event);
                    break;
            }
        }

        return false;
    }

    /**
     * Sets the last value that was highlighted via touch.
     *
     * @param high
     */
    public void setLastHighlighted(Highlight high) {
        mLastHighlighted = high;
    }

    /**
     * returns the touch mode the listener is currently in
     *
     * @return
     */
    public int getTouchMode() {
        return mTouchMode;
    }

    /**
     * returns the distance between two points
     *
     * @param eventX
     * @param startX
     * @param eventY
     * @param startY
     * @return
     */
    protected static float distance(float eventX, float startX, float eventY, float startY) {
        float dx = eventX - startX;
        float dy = eventY - startY;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
}
