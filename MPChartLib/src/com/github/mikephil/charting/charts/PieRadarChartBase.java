
package com.github.mikephil.charting.charts;

import android.content.Context;
import android.graphics.Paint.Align;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.listener.PieRadarChartTouchListener;
import com.github.mikephil.charting.utils.SelInfo;
import com.github.mikephil.charting.utils.Utils;
import com.nineoldandroids.animation.ObjectAnimator;

import java.util.ArrayList;

/**
 * Baseclass of PieChart and RadarChart.
 * 
 * @author Philipp Jahoda
 */
public abstract class PieRadarChartBase<T extends ChartData<? extends DataSet<? extends Entry>>>
        extends Chart<T> {

    /** holds the current rotation angle of the chart */
    protected float mRotationAngle = 270f;

    /** flag that indicates if rotation is enabled or not */
    protected boolean mRotateEnabled = true;

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
    protected void calcMinMax() {
        mDeltaX = mData.getXVals().size() - 1;
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
    public void notifyDataSetChanged() {
        if (mDataNotSet)
            return;

        calcMinMax();

        prepareLegend();

        calculateOffsets();
    }

    @Override
    protected void calculateOffsets() {

        float legendRight = 0f, legendBottom = 0f, legendTop = 0f;

        if (mDrawLegend && mLegend != null && mLegend.getPosition() != LegendPosition.NONE) {

            if (mLegend.getPosition() == LegendPosition.RIGHT_OF_CHART_CENTER) {

                // this is the space between the legend and the chart
                float spacing = Utils.convertDpToPixel(13f);

                legendRight = getFullLegendWidth() + spacing;

                mLegendLabelPaint.setTextAlign(Align.LEFT);
                // legendTop = mLegend.getFullHeight(mLegendLabelPaint);

            } else if (mLegend.getPosition() == LegendPosition.RIGHT_OF_CHART) {

                // this is the space between the legend and the chart
                float spacing = Utils.convertDpToPixel(13f);

                float legendWidth = getFullLegendWidth() + spacing;

                float legendHeight = mLegend.getFullHeight(mLegendLabelPaint);// +
                                                                              // mOffsetTop;

                PointF c = getCenter();

                PointF bottomRight = new PointF(getWidth() - legendWidth, legendHeight);
                PointF reference = getPosition(c, getRadius(), 320);

                float distLegend = distanceToCenter(bottomRight.x, bottomRight.y);
                float distReference = distanceToCenter(reference.x, reference.y);
                float min = Utils.convertDpToPixel(5f);

                if (distLegend < distReference) {

                    float diff = distReference - distLegend;

                    legendRight = min + diff;
                    legendTop = min + diff;
                }

                if (bottomRight.y >= c.y) {
                    legendRight = legendWidth;
                }

                mLegendLabelPaint.setTextAlign(Align.LEFT);

            } else if (mLegend.getPosition() == LegendPosition.BELOW_CHART_LEFT
                    || mLegend.getPosition() == LegendPosition.BELOW_CHART_RIGHT
                    || mLegend.getPosition() == LegendPosition.BELOW_CHART_CENTER) {

                legendBottom = getRequiredBottomOffset();
            }

            legendRight += getRequiredBaseOffset();
            legendTop += getRequiredBaseOffset();

            mLegend.setOffsetBottom(mLegendLabelPaint.getTextSize() * 4f);
            mLegend.setOffsetRight(legendRight);
        }

        float min = Utils.convertDpToPixel(11f);

        if (mLegend != null) {
            mLegend.setOffsetLeft(min);
        }

        float offsetLeft = Math.max(min, getRequiredBaseOffset());
        float offsetTop = Math.max(min, legendTop);
        float offsetRight = Math.max(min, legendRight);
        float offsetBottom = Math.max(min, Math.max(getRequiredBaseOffset(), legendBottom));

        mViewPortHandler.restrainViewPort(offsetLeft, offsetTop, offsetRight, offsetBottom);

            Log.i(LOG_TAG, "offsetLeft: " + offsetLeft + ", offsetTop: " + offsetTop
                    + ", offsetRight: " + offsetRight + ", offsetBottom: " + offsetBottom);
    }

    // /**
    // * Applys the newly calculated offsets to the matrices.
    // */
    // private void applyCalculatedOffsets() {
    //
    // float scaleX = (float) ((getWidth() - mOffsetLeft - mOffsetRight) /
    // mDeltaX);
    // float scaleY = (float) ((getHeight() - mOffsetBottom - mOffsetTop) /
    // mDeltaY);
    //
    // Matrix val = new Matrix();
    // val.postTranslate(0, -mYChartMin);
    // val.postScale(scaleX, -scaleY);
    //
    // mTrans.getValueMatrix().set(val);
    //
    // Matrix offset = new Matrix();
    // offset.postTranslate(mOffsetLeft, getHeight() - mOffsetBottom);
    //
    // mTrans.getOffsetMatrix().set(offset);
    // }

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
        mStartAngle -= mRotationAngle;
    }

    /**
     * updates the view rotation depending on the given touch position, also
     * takes the starting angle into consideration
     * 
     * @param x
     * @param y
     */
    public void updateRotation(float x, float y) {

        mRotationAngle = getAngleForPoint(x, y);

        // take the offset into consideration
        mRotationAngle -= mStartAngle;

        // keep the angle >= 0 and <= 360
        mRotationAngle = (mRotationAngle + 360f) % 360f;
    }

    /**
     * returns the angle relative to the chart center for the given point on the
     * chart in degrees. The angle is always between 0 and 360°, 0° is NORTH,
     * 90° is EAST, ...
     * 
     * @param x
     * @param y
     * @return
     */
    public float getAngleForPoint(float x, float y) {

        PointF c = getCenterOffsets();

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
     * Calculates the position around a center point, depending on the distance
     * from the center, and the angle of the position around the center.
     * 
     * @param center
     * @param dist
     * @param angle in degrees, converted to radians internally
     * @return
     */
    protected PointF getPosition(PointF center, float dist, float angle) {

        PointF p = new PointF((float) (center.x + dist * Math.cos(Math.toRadians(angle))),
                (float) (center.y + dist * Math.sin(Math.toRadians(angle))));
        return p;
    }

    /**
     * Returns the distance of a certain point on the chart to the center of the
     * chart.
     * 
     * @param c the center
     * @param x
     * @param y
     * @return
     */
    public float distanceToCenter(float x, float y) {

        PointF c = getCenterOffsets();

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
    public void setRotationAngle(float angle) {

        angle = (int) Math.abs(angle % 360);
        mRotationAngle = angle;
    }

    /**
     * gets the current rotation angle of the pie chart
     * 
     * @return
     */
    public float getRotationAngle() {
        return mRotationAngle;
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
        RectF content = mViewPortHandler.getContentRect();
        return Math.min(content.width(), content.height());
    }

    /**
     * Returns the radius of the chart in pixels.
     * 
     * @return
     */
    public abstract float getRadius();

    /**
     * Returns the required bottom offset for the chart.
     * 
     * @return
     */
    protected abstract float getRequiredBottomOffset();

    /**
     * Returns the base offset needed for the chart without calculating the
     * legend size.
     * 
     * @return
     */
    protected abstract float getRequiredBaseOffset();

    /**
     * Returns the required right offset for the chart.
     * 
     * @return
     */
    private float getFullLegendWidth() {
        return mLegend.getMaximumEntryLength(mLegendLabelPaint)
                + mLegend.getFormSize() + mLegend.getFormToTextSpace();
    }

    /**
     * set a new (e.g. custom) charttouchlistener NOTE: make sure to
     * setTouchEnabled(true); if you need touch gestures on the chart
     * 
     * @param l
     */
    public void setOnTouchListener(OnTouchListener l) {
        this.mListener = l;
    }

    @Override
    public float getYChartMax() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public float getYChartMin() {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * Returns an array of SelInfo objects for the given x-index. The SelInfo
     * objects give information about the value at the selected index and the
     * DataSet it belongs to. INFORMATION: This method does calculations at
     * runtime. Do not over-use in performance critical situations.
     *
     * @return
     */
    public ArrayList<SelInfo> getYValsAtIndex(int xIndex) {

        ArrayList<SelInfo> vals = new ArrayList<SelInfo>();

        for (int i = 0; i < mData.getDataSetCount(); i++) {
            
            DataSet<?> dataSet = mData.getDataSetByIndex(i);

            // extract all y-values from all DataSets at the given x-index
            float yVal = dataSet.getYValForXIndex(xIndex);
            
            if (!Float.isNaN(yVal)) {
                vals.add(new SelInfo(yVal, i, dataSet));
            }
        }

        return vals;
    }
    
    /**
     * ################ ################ ################ ################
     */
    /** CODE BELOW THIS RELATED TO ANIMATION */

    /** objectanimator used for animating values on y-axis */
    private ObjectAnimator mSpinAnimator;

    /**
     * Applys a spin animation to the Chart.
     * 
     * @param durationmillis
     * @param fromangle
     * @param toangle
     */
    public void spin(int durationmillis, float fromangle, float toangle) {

        mRotationAngle = fromangle;

        mSpinAnimator = ObjectAnimator.ofFloat(this, "rotationAngle", fromangle, toangle);
        mSpinAnimator.setDuration(durationmillis);
        mSpinAnimator.addUpdateListener(this);
        mSpinAnimator.start();
    }
}
