
package com.github.mikephil.charting.charts;

import android.content.Context;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.BarDataProvider;
import com.github.mikephil.charting.renderer.BarChartRenderer;
import com.github.mikephil.charting.renderer.XAxisRendererBarChart;
import com.github.mikephil.charting.utils.Highlight;

import java.math.BigDecimal;

/**
 * Chart that draws bars.
 * 
 * @author Philipp Jahoda
 */
public class BarChart extends BarLineChartBase<BarData> implements BarDataProvider {

    // /** indicates the angle of the 3d effect */
    // private float mSkew = 0.3f;
    //
    // /** indicates how much the 3d effect goes back */
    // private float mDepth = 0.3f;
    //
    // /** flag the enables or disables 3d bars */
    // private boolean m3DEnabled = false;

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

        mRenderer = new BarChartRenderer(this, mAnimator, mViewPortHandler);
        mXAxisRenderer = new XAxisRendererBarChart(mViewPortHandler, mXAxis, mLeftAxisTransformer,
                this);

        mXChartMin = -0.5f;
    }

    @Override
    protected void calcMinMax() {
        super.calcMinMax();

        // increase deltax by 1 because the bars have a width of 1
        mDeltaX += 0.5f;

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
        mXChartMax = mDeltaX - mXChartMin;
    }

    // protected float getPositiveYOffset(boolean drawAboveValueBar)
    // {
    // return (mDrawValueAboveBar ? -Utils.convertDpToPixel(5) :
    // Utils.calcTextHeight(mValuePaint,
    // "8") * 1.5f);
    // }
    //
    // protected float getNegativeYOffset(boolean drawAboveValueBar)
    // {
    // return (mDrawValueAboveBar ? Utils.calcTextHeight(mValuePaint, "8") *
    // 1.5f : -Utils
    // .convertDpToPixel(5));
    // }

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

        mLeftAxisTransformer.pixelsToValue(pts);

        // for barchart, we only need x-val
        double xTouchVal = pts[0];
        double base = xTouchVal;

        if (xTouchVal < mXChartMin || xTouchVal > mXChartMax)
            return null;

        Log.i(LOG_TAG, "base: " + base);

        int setCount = mData.getDataSetCount();
        int valCount = setCount * mData.getXValCount();

        if (setCount <= 1) {
            return new Highlight((int) Math.round(base), 0);
        }

        // calculate the amount of bar-space between index 0 and touch position
        float space = (float) ((((float) valCount / (float) setCount) / (mDeltaX / base)));
//        
        float border = (float) setCount + mData.getGroupSpace();
        
        float steps = 0.5f + (int) (((float) base + 0.5f) / ((float) setCount));

        float reduction = (float) (steps) * mData.getGroupSpace();

        Log.i(LOG_TAG, "reduction: " + reduction);

        float beforeRound = (float) ((base - reduction) / setCount);
        Log.i(LOG_TAG, "touch x-index before round: " + beforeRound);
        
        int xIndex = (int) beforeRound;
        Log.i(LOG_TAG, "touch x-index: " + xIndex);

        float dataSetBeforeRound = (float) ((base - reduction) % (setCount - 0.5f));

        Log.i(LOG_TAG, "datasetindex before round: " + dataSetBeforeRound);

        int dataSetIndex = (int) Math.round(dataSetBeforeRound);
        Log.i(LOG_TAG, "touch dataset-index: " + dataSetIndex);
        
        if (dataSetIndex < 0 || dataSetIndex >= mData.getDataSetCount())
            return null;

        if (xIndex < 0)
            xIndex = 0;

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
        float left = x - 0.5f + spaceHalf;
        float right = x + 0.5f - spaceHalf;
        float top = y >= 0 ? y : 0;
        float bottom = y <= 0 ? y : 0;

        RectF bounds = new RectF(left, top, right, bottom);

        getTransformer(set.getAxisDependency()).rectValueToPixel(bounds);

        return bounds;
    }

    // /**
    // * sets the skew (default 0.3f), the skew indicates how much the 3D effect
    // * of the chart is turned to the right
    // *
    // * @param skew
    // */
    // public void setSkew(float skew) {
    // this.mSkew = skew;
    // }
    //
    // /**
    // * returns the skew value that indicates how much the 3D effect is turned
    // to
    // * the right
    // *
    // * @return
    // */
    // public float getSkew() {
    // return mSkew;
    // }
    //
    // /**
    // * set the depth of the chart (default 0.3f), the depth indicates how much
    // * the 3D effect of the chart goes back
    // *
    // * @param depth
    // */
    // public void setDepth(float depth) {
    // this.mDepth = depth;
    // }
    //
    // /**
    // * returhs the depth, which indicates how much the 3D effect goes back
    // *
    // * @return
    // */
    // public float getDepth() {
    // return mDepth;
    // }
    //
    // /**
    // * if enabled, chart will be drawn in 3d
    // *
    // * @param enabled
    // */
    // public void set3DEnabled(boolean enabled) {
    // this.m3DEnabled = enabled;
    // }
    //
    // /**
    // * returns true if 3d bars is enabled, false if not
    // *
    // * @return
    // */
    // public boolean is3DEnabled() {
    // return m3DEnabled;
    // }

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
    public BarData getBarData() {
        return mData;
    }
}
