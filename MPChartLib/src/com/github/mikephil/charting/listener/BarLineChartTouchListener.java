
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
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.data.BarLineScatterCandleData;
import com.github.mikephil.charting.data.BarLineScatterCandleDataSet;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * TouchListener for Bar-, Line-, Scatter- and CandleStickChart with handles all
 * touch interaction. Longpress == Zoom out. Double-Tap == Zoom in.
 * 
 * @author Philipp Jahoda
 */
public class BarLineChartTouchListener<T extends BarLineChartBase<? extends BarLineScatterCandleData<? extends BarLineScatterCandleDataSet<? extends Entry>>>>
        extends SimpleOnGestureListener implements OnTouchListener {

    // private static final long REFRESH_MILLIS = 20;

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

    private DataSet<?> mClosestDataSetToTouch;

    /** the chart the listener represents */
    private T mChart;

    /** the gesturedetector used for detecting taps and longpresses, ... */
    private GestureDetector mGestureDetector;

    public BarLineChartTouchListener(T chart, Matrix touchMatrix) {
        this.mChart = chart;
        this.mMatrix = touchMatrix;

        mGestureDetector = new GestureDetector(chart.getContext(), this);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (mTouchMode == NONE) {
            mGestureDetector.onTouchEvent(event);
        }

        if (!mChart.isDragEnabled() && (!mChart.isScaleXEnabled() && !mChart.isScaleYEnabled()))
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

                    if (mChart.isDragEnabled())
                        performDrag(event);

                } else if (mTouchMode == X_ZOOM || mTouchMode == Y_ZOOM || mTouchMode == PINCH_ZOOM) {

                    mChart.disableScroll();

                    if (mChart.isScaleXEnabled() || mChart.isScaleYEnabled())
                        performZoom(event);

                } else if (mTouchMode == NONE
                        && Math.abs(distance(event.getX(), mTouchStartPoint.x, event.getY(),
                                mTouchStartPoint.y)) > 5f) {

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
        // if (needsRefresh())
        mMatrix = mChart.getViewPortHandler().refresh(mMatrix, mChart, true);

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

        mClosestDataSetToTouch = mChart.getDataSetByTouchPoint(event.getX(), event.getY());
    }

    /**
     * Performs all necessary operations needed for dragging.
     * 
     * @param event
     */
    private void performDrag(MotionEvent event) {

        mMatrix.set(mSavedMatrix);

		OnChartGestureListener l = mChart.getOnChartGestureListener();

		float dX, dY;

		// check if axis is inverted
        if (mChart.isAnyAxisInverted() && mClosestDataSetToTouch != null
                && mChart.getAxis(mClosestDataSetToTouch.getAxisDependency()).isInverted()) {

            // if there is an inverted horizontalbarchart
            if (mChart instanceof HorizontalBarChart) {
				dX = -(event.getX() - mTouchStartPoint.x);
				dY = event.getY() - mTouchStartPoint.y;
            } else {
				dX = event.getX() - mTouchStartPoint.x;
				dY = -(event.getY() - mTouchStartPoint.y);
            }
        }
        else {
            dX = event.getX() - mTouchStartPoint.x;
			dY = event.getY() - mTouchStartPoint.y;
        }

		mMatrix.postTranslate(dX, dY);

		if (l != null)
			l.onChartTranslate(event, dX, dY);
    }

    /**
     * Performs the all operations necessary for pinch and axis zoom.
     * 
     * @param event
     */
    private void performZoom(MotionEvent event) {

        if (event.getPointerCount() >= 2) {
            
            OnChartGestureListener l = mChart.getOnChartGestureListener();

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
                    
                    float scaleX = (mChart.isScaleXEnabled()) ? scale : 1f;
                    float scaleY = (mChart.isScaleYEnabled()) ? scale : 1f;

                    mMatrix.set(mSavedMatrix);
                    mMatrix.postScale(scaleX, scaleY, t.x, t.y);                   

                    if (l != null)
                        l.onChartScale(event, scaleX, scaleY);

                } else if (mTouchMode == X_ZOOM && mChart.isScaleXEnabled()) {

                    float xDist = getXDist(event);
                    float scaleX = xDist / mSavedXDist; // x-axis
                                                        // scale

                    mMatrix.set(mSavedMatrix);
                    mMatrix.postScale(scaleX, 1f, t.x, t.y);
                    
                    if (l != null)
                        l.onChartScale(event, scaleX, 1f);

                } else if (mTouchMode == Y_ZOOM && mChart.isScaleYEnabled()) {

                    float yDist = getYDist(event);
                    float scaleY = yDist / mSavedYDist; // y-axis
                                                        // scale

                    mMatrix.set(mSavedMatrix);

                    // y-axis comes from top to bottom, revert y
                    mMatrix.postScale(1f, scaleY, t.x, t.y);
                    
                    if (l != null)
                        l.onChartScale(event, 1f, scaleY);
                }
            }
        }
    }

    /**
     * Perform a highlight operation.
     * 
     * @param e
     */
    private void performHighlight(MotionEvent e) {

        Highlight h = mChart.getHighlightByTouchPoint(e.getX(), e.getY());

        if (h == null || h.equalTo(mLastHighlighted)) {
            mChart.highlightTouch(null);
            mLastHighlighted = null;
        } else {
            mLastHighlighted = h;
            mChart.highlightTouch(h);
        }
    }

    /**
     * Highlights upon dragging.
     * 
     * @param e
     */
    private void performHighlightDrag(MotionEvent e) {

        Highlight h = mChart.getHighlightByTouchPoint(e.getX(), e.getY());

        if (h != null && !h.equalTo(mLastHighlighted)) {
            mLastHighlighted = h;
            mChart.highlightTouch(h);
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

        ViewPortHandler vph = mChart.getViewPortHandler();

        float xTrans = x - vph.offsetLeft();
        float yTrans = 0f;

        // check if axis is inverted
        if (mChart.isAnyAxisInverted() && mClosestDataSetToTouch != null
                && mChart.isInverted(mClosestDataSetToTouch.getAxisDependency())) {
            yTrans = -(y - vph.offsetTop());
        } else {
            yTrans = -(mChart.getMeasuredHeight() - y - vph.offsetBottom());
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

            mChart.zoom(1.4f, 1.4f, trans.x, trans.y);

            if (mChart.isLogEnabled())
                Log.i("BarlineChartTouch", "Double-Tap, Zooming In, x: " + trans.x + ", y: "
                        + trans.y);
        }

        return super.onDoubleTap(e);
    }

    @Override
    public void onLongPress(MotionEvent e) {

        OnChartGestureListener l = mChart.getOnChartGestureListener();

        if (l != null) {

            l.onChartLongPressed(e);
        }
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {

        performHighlight(e);

        return super.onSingleTapUp(e);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {

        OnChartGestureListener l = mChart.getOnChartGestureListener();

        if (l != null) {
            l.onChartSingleTapped(e);
        }

        return super.onSingleTapConfirmed(e);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        OnChartGestureListener l = mChart.getOnChartGestureListener();

        if (l != null)
            l.onChartFling(e1, e2, velocityX, velocityY);

        return super.onFling(e1, e2, velocityX, velocityY);
    }
}
