
package com.github.mikephil.charting.charts;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Paint.Align;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.github.mikephil.charting.listener.PieRadarChartTouchListener;
import com.github.mikephil.charting.utils.Legend.LegendPosition;
import com.github.mikephil.charting.utils.Utils;

/**
 * Baseclass of PieChart and RadarChart.
 * 
 * @author Philipp Jahoda
 */
public abstract class PieRadarChartBase extends Chart {

    /** holds the current rotation angle of the chart */
    protected float mChartAngle = 270f;

    /** flag that indicates if rotation is enabled or not */
    private boolean mRotateEnabled = true;

    /** the pie- and radarchart touchlistener */
    private OnTouchListener mListener;

    public PieRadarChartBase(Context context) {
        super(context);
    }

    public PieRadarChartBase(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PieRadarChartBase(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();

        mListener = new PieRadarChartTouchListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // use the pie- and radarchart listener own listener
        if (mTouchEnabled && mListener != null)
            return mListener.onTouch(this, event);
        else
            return super.onTouchEvent(event);
    }

    @Override
    public void prepare() {

        if (mDataNotSet)
            return;

        calcMinMax(false);

        // calculate how many digits are needed
        calcFormats();

        prepareLegend();
    }

    /**
     * calculates the required number of digits for the y-legend and for the
     * values that might be drawn in the chart (if enabled)
     */
    private void calcFormats() {

        // -1 means calculate digits
        if (mValueDigitsToUse == -1) {
            if (mOriginalData.getXValCount() <= 1)
                mValueFormatDigits = 0;
            else
                mValueFormatDigits = Utils.getPieFormatDigits(mDeltaY);
        }

        else
            mValueFormatDigits = mValueDigitsToUse;
    }

    @Override
    public void notifyDataSetChanged() {
        // TODO Auto-generated method stub
    }

    @Override
    protected void drawAdditional() {
        // TODO Auto-generated method stub
    }

    @Override
    protected void calculateOffsets() {

        if (mLegend == null)
            return;

        // setup offsets for legend
        if (mLegend.getPosition() == LegendPosition.RIGHT_OF_CHART) {

            mLegend.setOffsetRight(mLegend.getMaximumEntryLength(mLegendLabelPaint));
            mLegendLabelPaint.setTextAlign(Align.LEFT);

        } else if (mLegend.getPosition() == LegendPosition.BELOW_CHART_LEFT
                || mLegend.getPosition() == LegendPosition.BELOW_CHART_RIGHT
                || mLegend.getPosition() == LegendPosition.BELOW_CHART_CENTER) {

            mLegend.setOffsetBottom(mLegendLabelPaint.getTextSize() * 3.5f);
        }

        if (mDrawLegend) {

            mOffsetBottom = Math.max(mOffsetBottom, mLegend.getOffsetBottom());
            mOffsetRight = Math.max(mOffsetRight, mLegend.getOffsetRight() / 3 * 2);
        }

        mLegend.setOffsetTop(mOffsetTop);
        mLegend.setOffsetLeft(mOffsetLeft);

        prepareContentRect();

        float scaleX = (float) ((getWidth() - mOffsetLeft - mOffsetRight) / mDeltaX);
        float scaleY = (float) ((getHeight() - mOffsetBottom - mOffsetTop) / mDeltaY);

        Matrix val = new Matrix();
        val.postTranslate(0, -mYChartMin);
        val.postScale(scaleX, -scaleY);

        mMatrixValueToPx.set(val);

        Matrix offset = new Matrix();
        offset.postTranslate(mOffsetLeft, getHeight() - mOffsetBottom);

        mMatrixOffset.set(offset);
    }

    /** the angle where the dragging started */
    private float mStartAngle = 0f;

    /**
     * sets the starting angle of the rotation, this is only used by the touch
     * listener, x and y is the touch position
     * 
     * @param x
     * @param y
     */
    public void setStartAngle(float x, float y) {

        mStartAngle = getAngleForPoint(x, y);

        // take the current angle into consideration when starting a new drag
        mStartAngle -= mChartAngle;
    }

    /**
     * updates the view rotation depending on the given touch position, also
     * takes the starting angle into consideration
     * 
     * @param x
     * @param y
     */
    public void updateRotation(float x, float y) {

        mChartAngle = getAngleForPoint(x, y);

        // take the offset into consideration
        mChartAngle -= mStartAngle;

        // keep the angle >= 0 and <= 360
        mChartAngle = (mChartAngle + 360f) % 360f;
    }

    /**
     * returns the angle relative to the chart center for the given point on the
     * chart in degrees. The angle is always between 0 and 360°, 0° is NORTH, 90°
     * is EAST, ...
     * 
     * @param x
     * @param y
     * @return
     */
    public float getAngleForPoint(float x, float y) {

        PointF c = getCenter();

        double tx = x - c.x, ty = y - c.y;
        double length = Math.sqrt(tx * tx + ty * ty);
        double r = Math.acos(ty / length);

        float angle = (float) Math.toDegrees(r);

        if (x > c.x)
            angle = 360f - angle;

        // add 90° because chart starts EAST
        angle = angle + 90f;

        // neutralize overflow
        if (angle > 360f)
            angle = angle - 360f;

        return angle;
    }

    /**
     * Returns the distance of a certain point on the chart to the center of the
     * chart.
     * 
     * @param x
     * @param y
     * @return
     */
    public float distanceToCenter(float x, float y) {

        PointF c = getCenter();

        float dist = 0f;

        float xDist = 0f;
        float yDist = 0f;

        if (x > c.x) {
            xDist = x - c.x;
        } else {
            xDist = c.x - x;
        }

        if (y > c.y) {
            yDist = y - c.y;
        } else {
            yDist = c.y - y;
        }

        // pythagoras
        dist = (float) Math.sqrt(Math.pow(xDist, 2.0) + Math.pow(yDist, 2.0));

        return dist;
    }

    /**
     * Returns the xIndex for the given angle around the center of the chart.
     * Returns -1 if not found / outofbounds.
     * 
     * @param angle
     * @return
     */
    public abstract int getIndexForAngle(float angle);

    /**
     * Set an offset for the rotation of the RadarChart in degrees. Default 270f
     * --> top (NORTH)
     * 
     * @param angle
     */
    public void setRotationAngle(int angle) {

        angle = (int) Math.abs(angle % 360);
        mChartAngle = angle;
    }

    /**
     * gets the current rotation angle of the pie chart
     * 
     * @return
     */
    public float getRotationAngle() {
        return mChartAngle;
    }

    /**
     * Set this to true to enable the rotation / spinning of the chart by touch.
     * Set it to false to disable it. Default: true
     * 
     * @param enabled
     */
    public void setRotationEnabled(boolean enabled) {
        mRotateEnabled = enabled;
    }

    /**
     * Returns true if rotation of the chart by touch is enabled, false if not.
     * 
     * @return
     */
    public boolean isRotationEnabled() {
        return mRotateEnabled;
    }

    /**
     * returns the diameter of the pie- or radar-chart
     * 
     * @return
     */
    public float getDiameter() {
        if (mContentRect == null)
            return 0;
        else
            return Math.min(mContentRect.width(), mContentRect.height());
    }

    /**
     * Returns the radius of the chart in pixels.
     * 
     * @return
     */
    public abstract float getRadius();

    /**
     * set a new (e.g. custom) charttouchlistener NOTE: make sure to
     * setTouchEnabled(true); if you need touch gestures on the chart
     * 
     * @param l
     */
    public void setOnTouchListener(OnTouchListener l) {
        this.mListener = l;
    }
}
