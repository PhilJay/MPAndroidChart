
package com.github.mikephil.charting.listener;

import android.annotation.SuppressLint;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.data.BarLineScatterCandleData;
import com.github.mikephil.charting.data.BarLineScatterCandleRadarDataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.OnChartGestureListener;
import com.github.mikephil.charting.utils.Highlight;

/**
 * TouchListener for Bar-, Line-, Scatter- and CandleStickChart with handles all
 * touch interaction. Longpress == Zoom out. Double-Tap == Zoom in.
 * 
 * @author Philipp Jahoda
 */
public class BarLineChartTouchListener<T extends BarLineChartBase<? extends BarLineScatterCandleData<? extends BarLineScatterCandleRadarDataSet<? extends Entry>>>>
        extends SimpleOnGestureListener implements OnTouchListener {

    /** the original touch-matrix from the chart */
    private Matrix mMatrix = new Matrix();

    /** matrix for saving the original matrix state */
    private Matrix mSavedMatrix = new Matrix();

    /** point where the touch action started */
    private PointF mTouchStartPoint = new PointF();

    /** center between two pointers (fingers on the display) */
    private PointF mTouchPointCenter = new PointF();

    // states
    private static final int NONE = 0;
    private static final int DRAG = 1;

    private static final int X_ZOOM = 2;
    private static final int Y_ZOOM = 3;
    private static final int PINCH_ZOOM = 4;
    private static final int POST_ZOOM = 5;

    /** integer field that holds the current touch-state */
    private int mTouchMode = NONE;

    private float mSavedXDist = 1f;
    private float mSavedYDist = 1f;
    private float mSavedDist = 1f;

    /** the last highlighted object */
    private Highlight mLastHighlighted;

    /** the chart the listener represents */
    private T mChart;

    /** the gesturedetector used for detecting taps and longpresses, ... */
    private GestureDetector mGestureDetector;

    public BarLineChartTouchListener(T chart, Matrix start) {
        this.mChart = chart;
        this.mMatrix = start;

        mGestureDetector = new GestureDetector(chart.getContext(), this);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (mTouchMode == NONE) {
            mGestureDetector.onTouchEvent(event);
        }

        if (!mChart.isDragScaleEnabled())
            return true;

        // Handle touch events here...
        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:

                saveTouchStart(event);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:

                if (event.getPointerCount() >= 2) {

                    mChart.disableScroll();

                    saveTouchStart(event);

                    // get the distance between the pointers on the x-axis
                    mSavedXDist = getXDist(event);

                    // get the distance between the pointers on the y-axis
                    mSavedYDist = getYDist(event);

                    // get the total distance between the pointers
                    mSavedDist = spacing(event);

                    if (mSavedDist > 10f) {

                        if (mChart.isPinchZoomEnabled()) {
                            mTouchMode = PINCH_ZOOM;
                        } else {
                            if (mSavedXDist > mSavedYDist)
                                mTouchMode = X_ZOOM;
                            else
                                mTouchMode = Y_ZOOM;
                        }
                    }

                    // determine the touch-pointer center
                    midPoint(mTouchPointCenter, event); 
                }
                break;
            case MotionEvent.ACTION_MOVE:

                if (mTouchMode == DRAG) {

                    mChart.disableScroll();

                    performDrag(event);

                } else if (mTouchMode == X_ZOOM || mTouchMode == Y_ZOOM || mTouchMode == PINCH_ZOOM) {

                    mChart.disableScroll();

                    performZoom(event);

                } else if (mTouchMode == NONE
                        && Math.abs(distance(event.getX(), mTouchStartPoint.x, event.getY(),
                                mTouchStartPoint.y)) > 25f) {

                    if (mChart.hasNoDragOffset()) {

                        if (!mChart.isFullyZoomedOut())
                            mTouchMode = DRAG;

                    } else {
                        mTouchMode = DRAG;
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                mTouchMode = NONE;
                mChart.enableScroll();
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mTouchMode = POST_ZOOM;
                break;
        }

        // Perform the transformation, update the chart
        mMatrix = mChart.refreshTouch(mMatrix);

        return true; // indicate event was handled
    }

    /**
     * ################ ################ ################ ################
     */
    /** BELOW CODE PERFORMS THE ACTUAL TOUCH ACTIONS */

    /**
     * Saves the current Matrix state and the touch-start point.
     * 
     * @param event
     */
    private void saveTouchStart(MotionEvent event) {

        mSavedMatrix.set(mMatrix);
        mTouchStartPoint.set(event.getX(), event.getY());
    }

    /**
     * Performs all necessary operations needed for dragging.
     * 
     * @param event
     */
    private void performDrag(MotionEvent event) {

        mMatrix.set(mSavedMatrix);
        PointF dragPoint = new PointF(event.getX(), event.getY());

        // check if axis is inverted
        if (!mChart.isInvertYAxisEnabled()) {
            mMatrix.postTranslate(dragPoint.x - mTouchStartPoint.x, dragPoint.y
                    - mTouchStartPoint.y);
        } else {
            mMatrix.postTranslate(dragPoint.x - mTouchStartPoint.x, -(dragPoint.y
                    - mTouchStartPoint.y));
        }
    }

    /**
     * Performs the all operations necessary for pinch and axis zoom.
     * 
     * @param event
     */
    private void performZoom(MotionEvent event) {

        if (event.getPointerCount() >= 2) {

            // get the distance between the pointers of the touch
            // event
            float totalDist = spacing(event);

            if (totalDist > 10f) {

                // get the translation
                PointF t = getTrans(mTouchPointCenter.x, mTouchPointCenter.y);

                // take actions depending on the activated touch
                // mode
                if (mTouchMode == PINCH_ZOOM) {

                    float scale = totalDist / mSavedDist; // total
                                                          // scale

                    mMatrix.set(mSavedMatrix);
                    mMatrix.postScale(scale, scale, t.x, t.y);

                } else if (mTouchMode == X_ZOOM) {

                    float xDist = getXDist(event);
                    float scaleX = xDist / mSavedXDist; // x-axis
                                                        // scale

                    mMatrix.set(mSavedMatrix);
                    mMatrix.postScale(scaleX, 1f, t.x, t.y);

                } else if (mTouchMode == Y_ZOOM) {

                    float yDist = getYDist(event);
                    float scaleY = yDist / mSavedYDist; // y-axis
                                                        // scale

                    mMatrix.set(mSavedMatrix);

                    // y-axis comes from top to bottom, revert y
                    mMatrix.postScale(1f, scaleY, t.x, t.y);

                }
            }
        }
    }

    /**
     * ################ ################ ################ ################
     */
    /** DOING THE MATH BELOW ;-) */

    /**
     * returns the distance between two points
     * 
     * @param eventX
     * @param startX
     * @param eventY
     * @param startY
     * @return
     */
    private static float distance(float eventX, float startX, float eventY, float startY) {
        float dx = eventX - startX;
        float dy = eventY - startY;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Determines the center point between two pointer touch points.
     * 
     * @param point
     * @param event
     */
    private static void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2f, y / 2f);
    }

    /**
     * returns the distance between two pointer touch points
     * 
     * @param event
     * @return
     */
    private static float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * calculates the distance on the x-axis between two pointers (fingers on
     * the display)
     * 
     * @param e
     * @return
     */
    private static float getXDist(MotionEvent e) {
        float x = Math.abs(e.getX(0) - e.getX(1));
        return x;
    }

    /**
     * calculates the distance on the y-axis between two pointers (fingers on
     * the display)
     * 
     * @param e
     * @return
     */
    private static float getYDist(MotionEvent e) {
        float y = Math.abs(e.getY(0) - e.getY(1));
        return y;
    }

    /**
     * returns the correct translation depending on the provided x and y touch
     * points
     * 
     * @param e
     * @return
     */
    public PointF getTrans(float x, float y) {

        float xTrans = x - mChart.getOffsetLeft();
        float yTrans = 0f;

        // check if axis is inverted
        if (!mChart.isInvertYAxisEnabled()) {
            yTrans = -(mChart.getMeasuredHeight() - y - mChart.getOffsetBottom());
        } else {
            yTrans = -(y - mChart.getOffsetTop());
        }

        return new PointF(xTrans, yTrans);
    }

    /**
     * ################ ################ ################ ################
     */
    /** GETTERS AND GESTURE RECOGNITION BELOW */

    /**
     * returns the matrix object the listener holds
     * 
     * @return
     */
    public Matrix getMatrix() {
        return mMatrix;
    }

    /**
     * returns the touch mode the listener is currently in
     * 
     * @return
     */
    public int getTouchMode() {
        return mTouchMode;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {

        OnChartGestureListener l = mChart.getOnChartGestureListener();

        if (l != null) {
            l.onChartDoubleTapped(e);
            return super.onDoubleTap(e);
        }

        // check if double-tap zooming is enabled
        if (mChart.isDoubleTapToZoomEnabled()) {

            PointF trans = getTrans(e.getX(), e.getY());

            mChart.zoomIn(trans.x, trans.y);

            Log.i("BarlineChartTouch", "Double-Tap, Zooming In, x: " + trans.x + ", y: " + trans.y);
        }

        return super.onDoubleTap(e);
    }

    @Override
    public void onLongPress(MotionEvent e) {

        OnChartGestureListener l = mChart.getOnChartGestureListener();

        if (l != null) {

            l.onChartLongPressed(e);
        } else if(mTouchMode == NONE) {

            mChart.fitScreen();

            Log.i("BarlineChartTouch",
                    "Longpress, resetting zoom and drag, adjusting chart bounds to screen.");

            // PointF trans = getTrans(e.getX(), e.getY());
            //
            // mChart.zoomOut(trans.x, trans.y);
            //
            // Log.i("BarlineChartTouch", "Longpress, Zooming Out, x: " +
            // trans.x +
            // ", y: " + trans.y);
        }
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {

        OnChartGestureListener l = mChart.getOnChartGestureListener();

        if (l != null) {

            l.onChartSingleTapped(e);
        }

        Highlight h = mChart.getHighlightByTouchPoint(e.getX(), e.getY());

        if (h == null || h.equalTo(mLastHighlighted)) {
            mChart.highlightTouch(null);
            mLastHighlighted = null;
        } else {
            mLastHighlighted = h;
            mChart.highlightTouch(h);
        }

        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return super.onSingleTapConfirmed(e);
    }
}
