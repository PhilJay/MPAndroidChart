package com.github.mikephil.charting.listener;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.github.mikephil.charting.utils.Highlight;

/**
 * Created by philipp on 12/06/15.
 */
public abstract class ChartTouchListener extends GestureDetector.SimpleOnGestureListener implements View.OnTouchListener {

    /** the last highlighted object (via touch) */
    protected Highlight mLastHighlighted;

    /**
     * Sets the last value that was highlighted via touch.
     * @param high
     */
    public void setLastHighlighted(Highlight high) {
        mLastHighlighted = high;
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
