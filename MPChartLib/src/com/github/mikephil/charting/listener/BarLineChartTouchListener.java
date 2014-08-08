
package com.github.mikephil.charting.listener;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.data.DrawingContext;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.PointD;

public class BarLineChartTouchListener extends SimpleOnGestureListener implements OnTouchListener {

    private Matrix mMatrix = new Matrix();
    private Matrix mSavedMatrix = new Matrix();

    /** point where the touch action started */
    private PointF mTouchStartPoint = new PointF();

    /** center between two pointers (fingers on the display) */
    private PointF mTouchPointCenter = new PointF();

    // states
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int POSTZOOM = 3;
    private static final int LONGPRESS = 4;
    private static final int DRAWING = 5;
    private static final int MOVE_POINT = 6;

    private static final int X_ZOOM = 7;
    private static final int Y_ZOOM = 8;
    private static final int PINCH_ZOOM = 9;

    /** if true, user can draw on the chart */
    private boolean mDrawingEnabled = false;

    private int mTouchMode = NONE;

    private float mSavedXDist = 1f;
    private float mSavedYDist = 1f;
    private float mSavedDist = 1f;

    private long mStartTimestamp = 0;
    private Highlight mLastHighlighted;

    private BarLineChartBase mChart;

    private DrawingContext mDrawingContext;
    private GestureDetector mGestureDetector;

    public BarLineChartTouchListener(BarLineChartBase chart, Matrix start) {
        this.mChart = chart;
        this.mMatrix = start;

        mGestureDetector = new GestureDetector(chart.getContext(), this);
        mDrawingContext = new DrawingContext();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (mTouchMode == NONE) {
            mGestureDetector.onTouchEvent(event);
        }

        if (!mChart.isDragEnabled() && !mDrawingEnabled)
            return true;

        mDrawingContext.init(mChart.getDrawListener(), mChart.isAutoFinishEnabled());
        LineData data = null;
        
//        data = (LineData) mChart.getDataCurrent();

        // Handle touch events here...
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (event.getPointerCount() == 1 && mDrawingEnabled) {
                    if (mLastHighlighted != null) {
                        Entry highlightedEntry = mChart.getDataCurrent().getEntryForHighlight(
                                mLastHighlighted);
                        Entry currentHoveredEntry = mChart.getEntryByTouchPoint(event.getX(),
                                event.getY());
                        if (highlightedEntry != null && highlightedEntry == currentHoveredEntry) {
                            mTouchMode = MOVE_POINT;
                            mDrawingContext.setMovingEntry(currentHoveredEntry);
                            break;
                        }
                    }
                    mTouchMode = DRAWING;
                    // TODO not always create new drawing sets
                    mStartTimestamp = System.currentTimeMillis();
                    mDrawingContext.createNewDrawingDataSet(data);
                    Log.i("Drawing", "New drawing data set created");
                } else {
                    mSavedMatrix.set(mMatrix);
                }
                mTouchStartPoint.set(event.getX(), event.getY());

                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() == 2) {
                    long deltaT = System.currentTimeMillis() - mStartTimestamp;
                    if ((mTouchMode == DRAWING && deltaT < 1000) || !mDrawingEnabled) {
                        mDrawingContext.deleteLastDrawingEntry(data);

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

                            mSavedMatrix.set(mMatrix);
                            midPoint(mTouchPointCenter, event);
                            mChart.disableScroll();
                        }
                    }
                }
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mTouchMode == DRAWING) {
                    long deltaT = System.currentTimeMillis() - mStartTimestamp;
                    if (deltaT < 1000 && Math.abs(event.getX() - mTouchStartPoint.x) < 25f) {
                        mDrawingContext.deleteLastDrawingEntry(data);
                        onSingleTapConfirmed(event);
                        Log.i("Drawing", "Drawing aborted");
                    } else {
                        mDrawingContext.finishNewDrawingEntry(data);
                        mChart.notifyDataSetChanged();
                        Log.i("Drawing", "Drawing finished");
                    }
                } else {
                    mChart.enableScroll();
                }
                mDrawingContext.setMovingEntry(null);
                mTouchMode = NONE;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mTouchMode = POSTZOOM;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mTouchMode == DRAWING || mTouchMode == MOVE_POINT) {
                    PointD p = mChart.getValuesByTouchPoint(event.getX(), event.getY());

                    int xIndex = (int) p.x;
                    float yVal = (float) p.y;

                    if (xIndex < 0)
                        xIndex = 0;
                    if (xIndex >= data.getXValCount()) {
                        xIndex = data.getXValCount() - 1;
                    }

                    boolean added = false;
                    if (mTouchMode == MOVE_POINT) {
                        mDrawingContext.getMovingEntry().setVal(yVal);
                        // do not allow x index to change for the moment
                        // mDrawingContext.getMovingEntry().setXIndex(xIndex);
                        mDrawingContext.notifyEntryMoved(data);
                        added = true;
                    } else {
                        Entry entry = new Entry((float) yVal, xIndex);
                        added = mDrawingContext.addNewDrawingEntry(entry, data);
                    }
                    if (added) {
                        mChart.notifyDataSetChanged();
                    }
                } else if (((mTouchMode == NONE && !mDrawingEnabled) || (mTouchMode != DRAG && event
                        .getPointerCount() == 3))
                        && Math.abs(distance(event.getX(), mTouchStartPoint.x, event.getY(),
                                mTouchStartPoint.y)) > 25f) {
                    // has to be in no mode, when drawing not enabled, or 3
                    // fingers
                    mSavedMatrix.set(mMatrix);
                    mTouchStartPoint.set(event.getX(), event.getY());

                    mTouchMode = DRAG;
                    mChart.disableScroll();

                } else if (mTouchMode == DRAG) {

                    mMatrix.set(mSavedMatrix);
                    PointF dragPoint = new PointF(event.getX(), event.getY());
                    mMatrix.postTranslate(dragPoint.x - mTouchStartPoint.x, dragPoint.y
                            - mTouchStartPoint.y);
                    
//                    mMatrix.set(mSavedMatrix);
//                    PointF dragPoint = new PointF(event.getX(), event.getY());
//                    mMatrix.postTranslate(dragPoint.x - mTouchStartPoint.x, -(dragPoint.y
//                            - mTouchStartPoint.y));


                } else if (mTouchMode == X_ZOOM || mTouchMode == Y_ZOOM || mTouchMode == PINCH_ZOOM) {

                    // get the distance between the pointers of the touch
                    // event
                    float totalDist = spacing(event);

                    if (totalDist > 10f) {

                        float[] values = new float[9];
                        mMatrix.getValues(values);

                        // get the previous scale factors
                        // float oldScaleX = values[Matrix.MSCALE_X];
                        // float oldScaleY = values[Matrix.MSCALE_Y];

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
                } else if (mTouchMode == LONGPRESS) {
                    mChart.disableScroll();
                }

                break;
        }

        // Perform the transformation
        mMatrix = mChart.refreshTouch(mMatrix);

        return true; // indicate event was handled
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
     * enables drawing by finger on the listener
     * 
     * @param mDrawingEnabled
     */
    public void setDrawingEnabled(boolean mDrawingEnabled) {
        this.mDrawingEnabled = mDrawingEnabled;
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
    private static float distance(float eventX, float startX, float eventY, float startY) {
        float dx = eventX - startX;
        float dy = eventY - startY;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * returns the center point between two pointer touch points
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
        float yTrans = -(mChart.getMeasuredHeight() - y - mChart.getOffsetBottom());

        return new PointF(xTrans, yTrans);
    }

    /**
     * returns the matrix object the listener holds
     * 
     * @return
     */
    public Matrix getMatrix() {
        return mMatrix;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {

        Highlight h = mChart.getHighlightByTouchPoint(e.getX(), e.getY());
        
        if (h == null || h.equalTo(mLastHighlighted)) {
            mChart.highlightValues(null);
            mLastHighlighted = null;
        } else {
            mLastHighlighted = h;
            mChart.highlightValues(new Highlight[] {
                    h
            });
        }

        return super.onSingleTapConfirmed(e);
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {

        PointF trans = getTrans(e.getX(), e.getY());

        mChart.zoomIn(trans.x, trans.y);
        
        Log.i("BarlineChartTouch", "Double-Tap, Zooming In, x: " + trans.x + ", y: " + trans.y);
        
        return super.onDoubleTap(e);
    }

    @Override
    public void onLongPress(MotionEvent e) {

        PointF trans = getTrans(e.getX(), e.getY());

        mChart.zoomOut(trans.x, trans.y);
        
        Log.i("BarlineChartTouch", "Longpress, Zooming Out, x: " + trans.x + ", y: " + trans.y);
    };

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        // ctx.showValue(e, matrix);
        return true;
    }

}
