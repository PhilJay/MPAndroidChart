
package com.github.mikephil.charting.charts;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;

/**
 * Chart that draws bars.
 * 
 * @author Philipp Jahoda
 */
public class BarChart extends BarLineChartBase<BarData> {

//    /** indicates the angle of the 3d effect */
//    private float mSkew = 0.3f;
//
//    /** indicates how much the 3d effect goes back */
//    private float mDepth = 0.3f;
//
//    /** flag the enables or disables 3d bars */
//    private boolean m3DEnabled = false;

    /** flag that enables or disables the highlighting arrow */
    private boolean mDrawHighlightArrow = false;

    /**
     * if set to true, all values are drawn above their bars, instead of below
     * their top
     */
    private boolean mDrawValueAboveBar = true;

    /**
     * if set to true, all values of a stack are drawn individually, and not
     * just their sum
     */
    private boolean mDrawValuesForWholeStack = true;

    /**
     * if set to true, a grey area is darawn behind each bar that indicates the
     * maximum value
     */
    protected boolean mDrawBarShadow = true;

    public BarChart(Context context) {
        super(context);
    }

    public BarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BarChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void calcMinMax(boolean fixedValues) {
        super.calcMinMax(fixedValues);

        // increase deltax by 1 because the bars have a width of 1
        mDeltaX++;

        // extend xDelta to make space for multiple datasets (if ther are one)
        mDeltaX *= mData.getDataSetCount();

        int maxEntry = 0;

        for (int i = 0; i < mData.getDataSetCount(); i++) {

            DataSet<? extends Entry> set = mData.getDataSetByIndex(i);

            if (maxEntry < set.getEntryCount())
                maxEntry = set.getEntryCount();
        }

        float groupSpace = mData.getGroupSpace();
        mDeltaX += maxEntry * groupSpace;
    }

    @Override
    protected void drawHighlights() {

    }

    @Override
    protected void drawDataSet(int index) {
        
        
    }

    @Override
    protected void drawXLabels(float yPos) {

        // pre allocate to save performance (dont allocate in loop)
        float[] position = new float[] {
                0f, 0f
        };

        int step = mData.getDataSetCount();

        for (int i = 0; i < mData.getXValCount(); i += mXAxis.mXAxisLabelModulus) {

            position[0] = i * step + i * mData.getGroupSpace()
                    + mData.getGroupSpace() / 2f;

            // center the text
            if (mXAxis.isCenterXLabelsEnabled())
                position[0] += (step / 2f);

            mTrans.pointValuesToPixel(position);

            if (position[0] >= mOffsetLeft && position[0] <= getWidth() - mOffsetRight) {

                String label = mData.getXVals().get(i);

                if (mXAxis.isAvoidFirstLastClippingEnabled()) {

                    // avoid clipping of the last
                    if (i == mData.getXValCount() - 1) {
                        float width = Utils.calcTextWidth(mXLabelPaint, label);

                        if (width > getOffsetRight() * 2 && position[0] + width > getWidth())
                            position[0] -= width / 2;

                        // avoid clipping of the first
                    } else if (i == 0) {

                        float width = Utils.calcTextWidth(mXLabelPaint, label);
                        position[0] += width / 2;
                    }
                }

                mDrawCanvas.drawText(label, position[0],
                        yPos,
                        mXLabelPaint);
            }
        }
    }

    @Override
    protected void drawVerticalGrid() {

        if (!mDrawVerticalGrid || mData == null)
            return;

        float[] position = new float[] {
                0f, 0f
        };

        // take into consideration that multiple DataSets increase mDeltaX
        int step = mData.getDataSetCount();

        for (int i = 0; i < mData.getXValCount(); i += mXAxis.mXAxisLabelModulus) {

            position[0] = i * step + i * mData.getGroupSpace();

            mTrans.pointValuesToPixel(position);

            if (position[0] >= mOffsetLeft && position[0] <= getWidth()) {

                mDrawCanvas.drawLine(position[0], mOffsetTop, position[0], getHeight()
                        - mOffsetBottom, mGridPaint);
            }
        }
    }

    @Override
    protected void drawValues() {

       
    }

    protected float getPositiveYOffset(boolean drawAboveValueBar)
    {
        return (mDrawValueAboveBar ? -Utils.convertDpToPixel(5) : Utils.calcTextHeight(mValuePaint,
                "8") * 1.5f);
    }

    protected float getNegativeYOffset(boolean drawAboveValueBar)
    {
        return (mDrawValueAboveBar ? Utils.calcTextHeight(mValuePaint, "8") * 1.5f : -Utils
                .convertDpToPixel(5));
    }

    /**
     * Returns the Highlight object (contains x-index and DataSet index) of the
     * selected value at the given touch point inside the BarChart.
     * 
     * @param x
     * @param y
     * @return
     */
    @Override
    public Highlight getHighlightByTouchPoint(float x, float y) {

        if (mDataNotSet || mData == null) {
            Log.e(LOG_TAG, "Can't select by touch. No data set.");
            return null;
        }

        // create an array of the touch-point
        float[] pts = new float[2];
        pts[0] = x;
        pts[1] = y;

        mTrans.pixelsToValue(pts);

        // for barchart, we only need x-val
        double xTouchVal = pts[0];
        double base = xTouchVal;

        if (xTouchVal < 0 || xTouchVal > mDeltaX)
            return null;

        if (base < 0)
            base = 0;
        if (base >= mDeltaX)
            base = mDeltaX - 1;

        int setCount = mData.getDataSetCount();
        int valCount = setCount * mData.getXValCount();

        // calculate the amount of bar-space between index 0 and touch position
        float space = (float) (((float) valCount / (float) setCount) / (mDeltaX / base));

        float reduction = (float) space * mData.getGroupSpace();

        int xIndex = (int) ((base - reduction) / setCount);

        int dataSetIndex = ((int) (base - reduction)) % setCount;

        if (dataSetIndex == -1)
            return null;

        return new Highlight(xIndex, dataSetIndex);
    }

    /**
     * Returns the bounding box of the specified Entry in the specified DataSet.
     * Returns null if the Entry could not be found in the charts data.
     * 
     * @param e
     * @param dataSetIndex
     * @return
     */
    public RectF getBarBounds(BarEntry e) {

        BarDataSet set = mData.getDataSetForEntry(e);

        if (set == null)
            return null;

        float barspace = set.getBarSpace();
        float y = e.getVal();
        float x = e.getXIndex();

        float spaceHalf = barspace / 2f;
        float left = x + spaceHalf;
        float right = x + 1f - spaceHalf;
        float top = y >= 0 ? y : 0;
        float bottom = y <= 0 ? y : 0;

        RectF bounds = new RectF(left, top, right, bottom);

        getTransformer(set.getAxisDependency()).rectValueToPixel(bounds);

        return bounds;
    }

//    /**
//     * sets the skew (default 0.3f), the skew indicates how much the 3D effect
//     * of the chart is turned to the right
//     * 
//     * @param skew
//     */
//    public void setSkew(float skew) {
//        this.mSkew = skew;
//    }
//
//    /**
//     * returns the skew value that indicates how much the 3D effect is turned to
//     * the right
//     * 
//     * @return
//     */
//    public float getSkew() {
//        return mSkew;
//    }
//
//    /**
//     * set the depth of the chart (default 0.3f), the depth indicates how much
//     * the 3D effect of the chart goes back
//     * 
//     * @param depth
//     */
//    public void setDepth(float depth) {
//        this.mDepth = depth;
//    }
//
//    /**
//     * returhs the depth, which indicates how much the 3D effect goes back
//     * 
//     * @return
//     */
//    public float getDepth() {
//        return mDepth;
//    }
//
//    /**
//     * if enabled, chart will be drawn in 3d
//     * 
//     * @param enabled
//     */
//    public void set3DEnabled(boolean enabled) {
//        this.m3DEnabled = enabled;
//    }
//
//    /**
//     * returns true if 3d bars is enabled, false if not
//     * 
//     * @return
//     */
//    public boolean is3DEnabled() {
//        return m3DEnabled;
//    }

    /**
     * set this to true to draw the highlightning arrow
     * 
     * @param enabled
     */
    public void setDrawHighlightArrow(boolean enabled) {
        mDrawHighlightArrow = enabled;
    }

    /**
     * returns true if drawing the highlighting arrow is enabled, false if not
     * 
     * @return
     */
    public boolean isDrawHighlightArrowEnabled() {
        return mDrawHighlightArrow;
    }

    /**
     * If set to true, all values are drawn above their bars, instead of below
     * their top.
     * 
     * @param enabled
     */
    public void setDrawValueAboveBar(boolean enabled) {
        mDrawValueAboveBar = enabled;
    }

    /**
     * returns true if drawing values above bars is enabled, false if not
     * 
     * @return
     */
    public boolean isDrawValueAboveBarEnabled() {
        return mDrawValueAboveBar;
    }

    /**
     * if set to true, all values of a stack are drawn individually, and not
     * just their sum
     * 
     * @param enabled
     */
    public void setDrawValuesForWholeStack(boolean enabled) {
        mDrawValuesForWholeStack = enabled;
    }

    /**
     * returns true if all values of a stack are drawn, and not just their sum
     * 
     * @return
     */
    public boolean isDrawValuesForWholeStackEnabled() {
        return mDrawValuesForWholeStack;
    }

    /**
     * If set to true, a grey area is drawn behind each bar that indicates the
     * maximum value. Enabling his will reduce performance by about 50%.
     * 
     * @param enabled
     */
    public void setDrawBarShadow(boolean enabled) {
        mDrawBarShadow = enabled;
    }

    /**
     * returns true if drawing shadows (maxvalue) for each bar is enabled, false
     * if not
     * 
     * @return
     */
    public boolean isDrawBarShadowEnabled() {
        return mDrawBarShadow;
    }

    @Override
    protected void drawAdditional() {
    }
}
