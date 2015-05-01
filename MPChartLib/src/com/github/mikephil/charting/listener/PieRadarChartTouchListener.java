
package com.github.mikephil.charting.listener;

import android.annotation.SuppressLint;
import android.graphics.PointF;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.PieRadarChartBase;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.SelInfo;
import com.github.mikephil.charting.utils.Utils;

import java.util.List;

/**
 * Touchlistener for the PieChart.
 * 
 * @author Philipp Jahoda
 */
public class PieRadarChartTouchListener extends SimpleOnGestureListener implements OnTouchListener {

    private static final int NONE = 0;
    private static final int ROTATE = 1;

    private PointF mTouchStartPoint = new PointF();

    private PieRadarChartBase<?> mChart;

    private int mTouchMode = NONE;

    private GestureDetector mGestureDetector;

    /** used for tracking velocity of dragging */
    private VelocityTracker mVelocityTracker;

    private long mDecelarationLastTime = 0;
    private float mDecelarationAngularVelocity = 0.f;

    public PieRadarChartTouchListener(PieRadarChartBase<?> ctx) {
        this.mChart = ctx;

        mGestureDetector = new GestureDetector(ctx.getContext(), this);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        if (event.getActionMasked() == MotionEvent.ACTION_CANCEL) {
            if (mVelocityTracker != null) {
                mVelocityTracker.recycle();
                mVelocityTracker = null;
            }
        }

        if (mGestureDetector.onTouchEvent(event))
            return true;

        // if rotation by touch is enabled
        if (mChart.isRotationEnabled()) {

            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    mChart.setGestureStartAngle(x, y);
                    mTouchStartPoint.x = x;
                    mTouchStartPoint.y = y;
                    break;
                case MotionEvent.ACTION_MOVE:

                    if (mTouchMode == NONE
                            && distance(x, mTouchStartPoint.x, y, mTouchStartPoint.y)
                            > Utils.convertDpToPixel(8f)) {
                        mTouchMode = ROTATE;
                        mChart.disableScroll();
                    } else if (mTouchMode == ROTATE) {
                        mChart.updateRotation(x, y);
                        mChart.invalidate();
                    }

                    break;
                case MotionEvent.ACTION_UP:

                    final VelocityTracker velocityTracker = mVelocityTracker;
                    final int pointerId = event.getPointerId(0);
                    velocityTracker.computeCurrentVelocity(1000, Utils.getMaximumFlingVelocity());
                    final float velocityY = velocityTracker.getYVelocity(pointerId);
                    final float velocityX = velocityTracker.getXVelocity(pointerId);

                    if (Math.abs(velocityX) > Utils.getMinimumFlingVelocity() ||
                            Math.abs(velocityY) > Utils.getMinimumFlingVelocity()) {

                        if (mChart.isDragDecelerationEnabled()) {
                            stopDeceleration();

                            mDecelarationLastTime = AnimationUtils.currentAnimationTimeMillis();

                            float newAngle = mChart.getAngleForPoint(event.getX(), event.getY());
                            float previousAngle = mChart.getAngleForPoint(event.getX() - mVelocityTracker.getXVelocity(),
                                    event.getY() - mVelocityTracker.getYVelocity());

                            if (previousAngle >= 270.f && newAngle <= 90.f) {
                                // This is the wrapping point between 360 and 0,
                                // which prevents us from knowing if it's clockwise or counter.
                                newAngle += 360.f;
                            } else if (previousAngle <= 90.f && newAngle >= 270.f) {
                                // This is the wrapping point between 0 and 360,
                                // which prevents us from knowing if it's clockwise or counter.
                                previousAngle += 360.f;
                            }

                            mDecelarationAngularVelocity = newAngle - previousAngle;

                            Utils.postInvalidateOnAnimation(mChart); // This causes computeScroll to fire, recommended for this by Google
                        }
                    }

                    mChart.enableScroll();
                    mTouchMode = NONE;

                    if (mVelocityTracker != null) {
                        mVelocityTracker.recycle();
                        mVelocityTracker = null;
                    }

                    break;
            }
        }

        return true;
    }

    @Override
    public void onLongPress(MotionEvent me) {
        OnChartGestureListener l = mChart.getOnChartGestureListener();

        if (l != null) {
            l.onChartLongPressed(me);
        }
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return true;
    }

    /** reference to the last highlighted object */
    private Highlight mLastHighlight = null;

    @Override
    public boolean onSingleTapUp(MotionEvent e) {

        OnChartGestureListener l = mChart.getOnChartGestureListener();

        if (l != null) {
            l.onChartSingleTapped(e);
        }

        float distance = mChart.distanceToCenter(e.getX(), e.getY());

        // check if a slice was touched
        if (distance > mChart.getRadius()) {

            // if no slice was touched, highlight nothing
            mChart.highlightValues(null);
            mLastHighlight = null;

        } else {

            float angle = mChart.getAngleForPoint(e.getX(), e.getY());

            if (mChart instanceof PieChart) {
                angle /= mChart.getAnimator().getPhaseY();
            }

            int index = mChart.getIndexForAngle(angle);

            // check if the index could be found
            if (index < 0) {

                mChart.highlightValues(null);
                mLastHighlight = null;

            } else {

                List<SelInfo> valsAtIndex = mChart.getYValsAtIndex(index);

                int dataSetIndex = 0;

                // get the dataset that is closest to the selection (PieChart
                // only
                // has one DataSet)
                if (mChart instanceof RadarChart) {

                    dataSetIndex = Utils.getClosestDataSetIndex(valsAtIndex, distance
                            / ((RadarChart) mChart).getFactor(), null);
                }

                Highlight h = new Highlight(index, dataSetIndex);

                if (h.equalTo(mLastHighlight)) {

                    mChart.highlightTouch(null);
                    mLastHighlight = null;
                } else {

                    mChart.highlightTouch(h);
                    mLastHighlight = h;
                }
            }
        }

        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        OnChartGestureListener l = mChart.getOnChartGestureListener();

        if (l != null) {
            l.onChartDoubleTapped(e);
        }
        return super.onDoubleTap(e);
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

    public void stopDeceleration() {
        mDecelarationAngularVelocity = 0.f;
    }

    public void computeScroll() {

        if (mDecelarationAngularVelocity == 0.f)
            return; // There's no deceleration in progress

        final long currentTime = AnimationUtils.currentAnimationTimeMillis();

        mDecelarationAngularVelocity *= mChart.getDragDecelerationFrictionCoef();

        final float timeInterval = (float)(currentTime - mDecelarationLastTime) / 1000.f;

        mChart.setRotationAngle(mChart.getRotationAngle() + mDecelarationAngularVelocity * timeInterval);

        mDecelarationLastTime = currentTime;

        if (Math.abs(mDecelarationAngularVelocity) >= 0.001)
            Utils.postInvalidateOnAnimation(mChart); // This causes computeScroll to fire, recommended for this by Google
        else
            stopDeceleration();
    }
}
