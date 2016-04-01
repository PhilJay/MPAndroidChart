
package com.github.mikephil.charting.charts;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.listener.PieRadarChartTouchListener;
import com.github.mikephil.charting.utils.SelectionDetail;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Baseclass of PieChart and RadarChart.
 * 
 * @author Philipp Jahoda
 */
public abstract class PieRadarChartBase<T extends ChartData<? extends IDataSet<? extends Entry>>>
        extends Chart<T> {

    /** holds the normalized version of the current rotation angle of the chart */
    private float mRotationAngle = 270f;

    /** holds the raw version of the current rotation angle of the chart */
    private float mRawRotationAngle = 270f;

    /** flag that indicates if rotation is enabled or not */
    protected boolean mRotateEnabled = true;

    /** Sets the minimum offset (padding) around the chart, defaults to 0.f */
    protected float mMinOffset = 0.f;

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

        mChartTouchListener = new PieRadarChartTouchListener(this);
    }

    @Override
    protected void calcMinMax() {
        mXAxis.mAxisRange = mData.getXVals().size() - 1;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // use the pie- and radarchart listener own listener
        if (mTouchEnabled && mChartTouchListener != null)
            return mChartTouchListener.onTouch(this, event);
        else
            return super.onTouchEvent(event);
    }

    @Override
    public void computeScroll() {

        if (mChartTouchListener instanceof PieRadarChartTouchListener)
            ((PieRadarChartTouchListener) mChartTouchListener).computeScroll();
    }

    @Override
    public void notifyDataSetChanged() {
        if (mData == null)
            return;

        calcMinMax();

        if (mLegend != null)
            mLegendRenderer.computeLegend(mData);

        calculateOffsets();
    }

    @Override
    public void calculateOffsets() {

        float legendLeft = 0f, legendRight = 0f, legendBottom = 0f, legendTop = 0f;

        if (mLegend != null && mLegend.isEnabled()) {
            
            float fullLegendWidth = Math.min(mLegend.mNeededWidth, 
                    mViewPortHandler.getChartWidth() * mLegend.getMaxSizePercent()) + 
                    mLegend.getFormSize() + mLegend.getFormToTextSpace();

            if (mLegend.getPosition() == LegendPosition.RIGHT_OF_CHART_CENTER) {

                // this is the space between the legend and the chart
                float spacing = Utils.convertDpToPixel(13f);

                legendRight = fullLegendWidth + spacing;

            } else if (mLegend.getPosition() == LegendPosition.RIGHT_OF_CHART) {

                // this is the space between the legend and the chart
                float spacing = Utils.convertDpToPixel(8f);

                float legendWidth = fullLegendWidth + spacing;

                float legendHeight = mLegend.mNeededHeight + mLegend.mTextHeightMax;

                PointF c = getCenter();

                PointF bottomRight = new PointF(getWidth() - legendWidth + 15, legendHeight + 15);
                float distLegend = distanceToCenter(bottomRight.x, bottomRight.y);

                PointF reference = getPosition(c, getRadius(),
                        getAngleForPoint(bottomRight.x, bottomRight.y));

                float distReference = distanceToCenter(reference.x, reference.y);
                float min = Utils.convertDpToPixel(5f);

                if (distLegend < distReference) {

                    float diff = distReference - distLegend;
                    legendRight = min + diff;
                }

                if (bottomRight.y >= c.y && getHeight() - legendWidth > getWidth()) {
                    legendRight = legendWidth;
                }

            } else if (mLegend.getPosition() == LegendPosition.LEFT_OF_CHART_CENTER) {

                // this is the space between the legend and the chart
                float spacing = Utils.convertDpToPixel(13f);

                legendLeft = fullLegendWidth + spacing;

            } else if (mLegend.getPosition() == LegendPosition.LEFT_OF_CHART) {

                // this is the space between the legend and the chart
                float spacing = Utils.convertDpToPixel(8f);

                float legendWidth = fullLegendWidth + spacing;

                float legendHeight = mLegend.mNeededHeight + mLegend.mTextHeightMax;

                PointF c = getCenter();

                PointF bottomLeft = new PointF(legendWidth - 15, legendHeight + 15);
                float distLegend = distanceToCenter(bottomLeft.x, bottomLeft.y);

                PointF reference = getPosition(c, getRadius(),
                        getAngleForPoint(bottomLeft.x, bottomLeft.y));

                float distReference = distanceToCenter(reference.x, reference.y);
                float min = Utils.convertDpToPixel(5f);

                if (distLegend < distReference) {

                    float diff = distReference - distLegend;
                    legendLeft = min + diff;
                }

                if (bottomLeft.y >= c.y && getHeight() - legendWidth > getWidth()) {
                    legendLeft = legendWidth;
                }

            } else if (mLegend.getPosition() == LegendPosition.BELOW_CHART_LEFT
                    || mLegend.getPosition() == LegendPosition.BELOW_CHART_RIGHT
                    || mLegend.getPosition() == LegendPosition.BELOW_CHART_CENTER) {

                // It's possible that we do not need this offset anymore as it
                //   is available through the extraOffsets, but changing it can mean
                //   changing default visibility for existing apps.
                float yOffset = getRequiredLegendOffset();

                legendBottom = Math.min(mLegend.mNeededHeight + yOffset, mViewPortHandler.getChartHeight() * mLegend.getMaxSizePercent());

            } else if (mLegend.getPosition() == LegendPosition.ABOVE_CHART_LEFT
                    || mLegend.getPosition() == LegendPosition.ABOVE_CHART_RIGHT
                    || mLegend.getPosition() == LegendPosition.ABOVE_CHART_CENTER) {

                // It's possible that we do not need this offset anymore as it
                //   is available through the extraOffsets, but changing it can mean
                //   changing default visibility for existing apps.
                float yOffset = getRequiredLegendOffset();

                legendTop = Math.min(mLegend.mNeededHeight + yOffset, mViewPortHandler.getChartHeight() * mLegend.getMaxSizePercent());

            }

            legendLeft += getRequiredBaseOffset();
            legendRight += getRequiredBaseOffset();
            legendTop += getRequiredBaseOffset();
        }

        float minOffset = Utils.convertDpToPixel(mMinOffset);

        if (this instanceof RadarChart) {
            XAxis x = ((RadarChart) this).getXAxis();

            if (x.isEnabled() && x.isDrawLabelsEnabled()) {
                minOffset = Math.max(minOffset, x.mLabelRotatedWidth);
            }
        }

        legendTop += getExtraTopOffset();
        legendRight += getExtraRightOffset();
        legendBottom += getExtraBottomOffset();
        legendLeft += getExtraLeftOffset();

        float offsetLeft = Math.max(minOffset, legendLeft);
        float offsetTop = Math.max(minOffset, legendTop);
        float offsetRight = Math.max(minOffset, legendRight);
        float offsetBottom = Math.max(minOffset, Math.max(getRequiredBaseOffset(), legendBottom));

        mViewPortHandler.restrainViewPort(offsetLeft, offsetTop, offsetRight, offsetBottom);

        if (mLogEnabled)
            Log.i(LOG_TAG, "offsetLeft: " + offsetLeft + ", offsetTop: " + offsetTop
                    + ", offsetRight: " + offsetRight + ", offsetBottom: " + offsetBottom);
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
        mRawRotationAngle = angle;
        mRotationAngle = Utils.getNormalizedAngle(mRawRotationAngle);
    }

    /**
     * gets the raw version of the current rotation angle of the pie chart the
     * returned value could be any value, negative or positive, outside of the
     * 360 degrees. this is used when working with rotation direction, mainly by
     * gestures and animations.
     *
     * @return
     */
    public float getRawRotationAngle() {
        return mRawRotationAngle;
    }

    /**
     * gets a normalized version of the current rotation angle of the pie chart,
     * which will always be between 0.0 < 360.0
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

    /** Gets the minimum offset (padding) around the chart, defaults to 0.f */
    public float getMinOffset() {
        return mMinOffset;
    }

    /** Sets the minimum offset (padding) around the chart, defaults to 0.f */
    public void setMinOffset(float minOffset) {
        mMinOffset = minOffset;
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
     * Returns the required offset for the chart legend.
     * 
     * @return
     */
    protected abstract float getRequiredLegendOffset();

    /**
     * Returns the base offset needed for the chart without calculating the
     * legend size.
     * 
     * @return
     */
    protected abstract float getRequiredBaseOffset();

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
     * Returns an array of SelectionDetail objects for the given x-index. The SelectionDetail
     * objects give information about the value at the selected index and the
     * DataSet it belongs to. INFORMATION: This method does calculations at
     * runtime. Do not over-use in performance critical situations.
     *
     * @return
     */
    public List<SelectionDetail> getSelectionDetailsAtIndex(int xIndex) {

        List<SelectionDetail> vals = new ArrayList<SelectionDetail>();

        for (int i = 0; i < mData.getDataSetCount(); i++) {

            IDataSet<?> dataSet = mData.getDataSetByIndex(i);

            // extract all y-values from all DataSets at the given x-index
            final float yVal = dataSet.getYValForXIndex(xIndex);
            if (yVal == Float.NaN)
                continue;

            vals.add(new SelectionDetail(yVal, i, dataSet));
        }

        return vals;
    }

    /**
     * ################ ################ ################ ################
     */
    /** CODE BELOW THIS RELATED TO ANIMATION */

    /**
     * Applys a spin animation to the Chart.
     * 
     * @param durationmillis
     * @param fromangle
     * @param toangle
     */
    @SuppressLint("NewApi")
    public void spin(int durationmillis, float fromangle, float toangle, Easing.EasingOption easing) {

        if (android.os.Build.VERSION.SDK_INT < 11)
            return;

        setRotationAngle(fromangle);

        ObjectAnimator spinAnimator = ObjectAnimator.ofFloat(this, "rotationAngle", fromangle,
                toangle);
        spinAnimator.setDuration(durationmillis);
        spinAnimator.setInterpolator(Easing.getEasingFunctionFromOption(easing));

        spinAnimator.addUpdateListener(new AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                postInvalidate();
            }
        });
        spinAnimator.start();
    }
}
