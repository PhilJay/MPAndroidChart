
package com.github.mikephil.charting.charts;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
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

    /** indicates the angle of the 3d effect */
    private float mSkew = 0.3f;

    /** indicates how much the 3d effect goes back */
    private float mDepth = 0.3f;

    /** flag the enables or disables 3d bars */
    private boolean m3DEnabled = false;

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
    private boolean mDrawBarShadow = true;

    /** the rect object that is used for drawing the bar shadow */
    private RectF mBarShadow = new RectF();

    /** the rect object that is used for drawing the bars */
    private RectF mBarRect = new RectF();

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

        mHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHighlightPaint.setStyle(Paint.Style.FILL);
        mHighlightPaint.setColor(Color.rgb(0, 0, 0));
        // set alpha after color
        mHighlightPaint.setAlpha(120);

        // calculate3DColors();
    }

    @Override
    protected void calcMinMax(boolean fixedValues) {
        super.calcMinMax(fixedValues);

        // increase deltax by 1 because the bars have a width of 1
        mDeltaX++;

        // extend xDelta to make space for multiple datasets (if ther are one)
        mDeltaX *= mOriginalData.getDataSetCount();

        int maxEntry = 0;

        for (int i = 0; i < mOriginalData.getDataSetCount(); i++) {

            DataSet<? extends Entry> set = mOriginalData.getDataSetByIndex(i);

            if (maxEntry < set.getEntryCount())
                maxEntry = set.getEntryCount();
        }

        float groupSpace = mOriginalData.getGroupSpace();
        mDeltaX += maxEntry * groupSpace;
    }

    @Override
    protected void drawHighlights() {

        int setCount = mOriginalData.getDataSetCount();

        for (int i = 0; i < mIndicesToHightlight.length; i++) {

            Highlight h = mIndicesToHightlight[i];
            int index = h.getXIndex();

            int dataSetIndex = h.getDataSetIndex();
            BarDataSet ds = (BarDataSet) mCurrentData.getDataSetByIndex(dataSetIndex);

            mHighlightPaint.setColor(ds.getHighLightColor());
            mHighlightPaint.setAlpha(ds.getHighLightAlpha());

            // check outofbounds
            if (index < mCurrentData.getYValCount() && index >= 0
                    && index < (mDeltaX * mPhaseX) / mOriginalData.getDataSetCount()) {

                Entry e = getEntryByDataSetIndex(index, dataSetIndex);

                if (e == null)
                    continue;

                // calculate the correct x-position
                float x = index * setCount + dataSetIndex + mOriginalData.getGroupSpace() / 2f
                        + mOriginalData.getGroupSpace() * index;
                float y = e.getVal();

                prepareBar(x, y, ds.getBarSpace());

                mDrawCanvas.drawRect(mBarRect, mHighlightPaint);

                if (mDrawHighlightArrow) {

                    mHighlightPaint.setAlpha(255);

                    // distance between highlight arrow and bar
                    float offsetY = mDeltaY * 0.07f;

                    Path arrow = new Path();
                    arrow.moveTo(x + 0.5f, y + offsetY * 0.3f);
                    arrow.lineTo(x + 0.2f, y + offsetY);
                    arrow.lineTo(x + 0.8f, y + offsetY);

                    transformPath(arrow);
                    mDrawCanvas.drawPath(arrow, mHighlightPaint);
                }
            }
        }
    }

    @Override
    protected void drawData() {

        ArrayList<BarDataSet> dataSets = mOriginalData.getDataSets();
        int setCount = mOriginalData.getDataSetCount();

        // the space between bar-groups
        float space = mOriginalData.getGroupSpace();

        // 2D drawing
        for (int i = 0; i < setCount; i++) {

            BarDataSet dataSet = dataSets.get(i);
            boolean noStacks = dataSet.getStackSize() == 1 ? true : false;

            ArrayList<BarEntry> entries = dataSet.getYVals();

            // do the drawing
            for (int j = 0; j < dataSet.getEntryCount() * mPhaseX; j++) {

                BarEntry e = entries.get(j);

                // calculate the x-position, depending on datasetcount
                float x = e.getXIndex() + j * (setCount - 1) + i + space * j + space / 2f;
                float y = e.getVal();

                // no stacks
                if (noStacks) {

                    prepareBar(x, y, dataSet.getBarSpace());

                    // avoid drawing outofbounds values
                    if (isOffContentRight(mBarRect.left))
                        break;

                    if (isOffContentLeft(mBarRect.right)) {
                        continue;
                    }

                    // if drawing the bar shadow is enabled
                    if (mDrawBarShadow) {
                        mRenderPaint.setColor(dataSet.getBarShadowColor());
                        mDrawCanvas.drawRect(mBarShadow, mRenderPaint);
                    }

                    // Set the color for the currently drawn value. If the index
                    // is
                    // out of bounds, reuse colors.
                    mRenderPaint.setColor(dataSet.getColor(j));
                    mDrawCanvas.drawRect(mBarRect, mRenderPaint);

                } else { // stacked bars

                    float[] vals = e.getVals();

                    // we still draw stacked bars, but there could be one
                    // non-stacked
                    // in between
                    if (vals == null) {

                        prepareBar(x, y, dataSet.getBarSpace());

                        // if drawing the bar shadow is enabled
                        if (mDrawBarShadow) {
                            mRenderPaint.setColor(dataSet.getBarShadowColor());
                            mDrawCanvas.drawRect(mBarShadow, mRenderPaint);
                        }

                        mRenderPaint.setColor(dataSet.getColor(0));
                        mDrawCanvas.drawRect(mBarRect, mRenderPaint);

                    } else {

                        float all = e.getVal();

                        // if drawing the bar shadow is enabled
                        if (mDrawBarShadow) {

                            prepareBar(x, y, dataSet.getBarSpace());
                            mRenderPaint.setColor(dataSet.getBarShadowColor());
                            mDrawCanvas.drawRect(mBarShadow, mRenderPaint);
                        }

                        // draw the stack
                        for (int k = 0; k < vals.length; k++) {

                            all -= vals[k];

                            prepareBar(x, vals[k] + all, dataSet.getBarSpace());

                            mRenderPaint.setColor(dataSet.getColor(k));
                            mDrawCanvas.drawRect(mBarRect, mRenderPaint);
                        }
                    }

                    // avoid drawing outofbounds values
                    if (isOffContentRight(mBarRect.left))
                        break;
                }
            }
        }
    }

    /**
     * Prepares a bar for drawing on the specified x-index and y-position. Also
     * prepares the shadow-bar if enabled.
     * 
     * @param x the x-position
     * @param y the y-position
     * @param barspace the space between bars
     */
    private void prepareBar(float x, float y, float barspace) {

        float spaceHalf = barspace / 2f;
        float left = x + spaceHalf;
        float right = x + 1f - spaceHalf;
        float top = y >= 0 ? y : 0;
        float bottom = y <= 0 ? y : 0;

        mBarRect.set(left, top, right, bottom);

        transformRectWithPhase(mBarRect);

        // if a shadow is drawn, prepare it too
        if (mDrawBarShadow) {
            mBarShadow.set(mBarRect.left, mOffsetTop, mBarRect.right, getHeight() - mOffsetBottom);
        }
    }

    @Override
    protected void drawXLabels(float yPos) {

        // pre allocate to save performance (dont allocate in loop)
        float[] position = new float[] {
                0f, 0f
        };

        int step = mCurrentData.getDataSetCount();

        for (int i = 0; i < mCurrentData.getXValCount(); i += mXLabels.mXAxisLabelModulus) {

            position[0] = i * step + i * mOriginalData.getGroupSpace()
                    + mOriginalData.getGroupSpace() / 2f;

            // center the text
            if (mXLabels.isCenterXLabelsEnabled())
                position[0] += (step / 2f);

            transformPointArray(position);

            if (position[0] >= mOffsetLeft && position[0] <= getWidth() - mOffsetRight) {

                String label = mCurrentData.getXVals().get(i);

                if (mXLabels.isAvoidFirstLastClippingEnabled()) {

                    // avoid clipping of the last
                    if (i == mCurrentData.getXValCount() - 1) {
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

        if (!mDrawVerticalGrid || mCurrentData == null)
            return;

        float[] position = new float[] {
                0f, 0f
        };

        // take into consideration that multiple DataSets increase mDeltaX
        int step = mCurrentData.getDataSetCount();

        for (int i = 0; i < mCurrentData.getXValCount(); i += mXLabels.mXAxisLabelModulus) {

            position[0] = i * step + i * mOriginalData.getGroupSpace();

            transformPointArray(position);

            if (position[0] >= mOffsetLeft && position[0] <= getWidth()) {

                mDrawCanvas.drawLine(position[0], mOffsetTop, position[0], getHeight()
                        - mOffsetBottom, mGridPaint);
            }
        }
    }
    
    @Override
    protected void drawValues() {

        // if values are drawn
        if (mDrawYValues && mCurrentData.getYValCount() < mMaxVisibleCount * mScaleX) {

            ArrayList<BarDataSet> dataSets = ((BarData) mCurrentData).getDataSets();

            float offset = 0f;

            // calculate the correct offset depending on the draw position of
            // the value
            if (mDrawValueAboveBar)
                offset = -Utils.convertDpToPixel(5);
            else
                offset = Utils.calcTextHeight(mValuePaint, "8") * 1.5f;

            for (int i = 0; i < mCurrentData.getDataSetCount(); i++) {

                BarDataSet dataSet = dataSets.get(i);
                ArrayList<BarEntry> entries = dataSet.getYVals();

                float[] valuePoints = generateTransformedValuesBarChart(entries, i);

                // if only single values are drawn (sum)
                if (!mDrawValuesForWholeStack) {

                    for (int j = 0; j < valuePoints.length * mPhaseX; j += 2) {

                        if (isOffContentRight(valuePoints[j]))
                            break;

                        if (isOffContentLeft(valuePoints[j]) || isOffContentTop(valuePoints[j + 1])
                                || isOffContentBottom(valuePoints[j + 1]))
                            continue;

                        float val = entries.get(j / 2).getVal();

                        drawValue(mValueFormatter.getFormattedValue(val), valuePoints[j],
                                valuePoints[j + 1] + offset);
                    }

                    // if each value of a potential stack should be drawn
                } else {

                    for (int j = 0; j < (valuePoints.length - 1) * mPhaseX; j += 2) {

                        if (isOffContentRight(valuePoints[j]))
                            break;

                        if (isOffContentLeft(valuePoints[j]) || isOffContentTop(valuePoints[j + 1])
                                || isOffContentBottom(valuePoints[j + 1]))
                            continue;

                        BarEntry e = entries.get(j / 2);

                        float[] vals = e.getVals();

                        // we still draw stacked bars, but there is one
                        // non-stacked
                        // in between
                        if (vals == null) {

                            drawValue(mValueFormatter.getFormattedValue(e.getVal()), valuePoints[j],
                                    valuePoints[j + 1] + offset);

                        } else {

                            float[] transformed = new float[vals.length * 2];
                            int cnt = 0;
                            float add = e.getVal();

                            for (int k = 0; k < transformed.length; k += 2) {

                                add -= vals[cnt];
                                transformed[k + 1] = (vals[cnt] + add) * mPhaseY;
                                cnt++;
                            }

                            transformPointArray(transformed);

                            for (int k = 0; k < transformed.length; k += 2) {

                                drawValue(mValueFormatter.getFormattedValue(vals[k / 2]), valuePoints[j],
                                        transformed[k + 1] + offset);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Draws a value at the specified x and y position.
     * 
     * @param value
     * @param xPos
     * @param yPos
     */
    private void drawValue(String val, float xPos, float yPos) {

        if (mDrawUnitInChart) {

            mDrawCanvas.drawText(val + mUnit, xPos, yPos,
                    mValuePaint);
        } else {

            mDrawCanvas.drawText(val, xPos, yPos,
                    mValuePaint);
        }
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

        if (mDataNotSet || mCurrentData == null) {
            Log.e(LOG_TAG, "Can't select by touch. No data set.");
            return null;
        }

        // create an array of the touch-point
        float[] pts = new float[2];
        pts[0] = x;
        pts[1] = y;

        Matrix tmp = new Matrix();

        // invert all matrixes to convert back to the original value
        mMatrixOffset.invert(tmp);
        tmp.mapPoints(pts);

        mMatrixTouch.invert(tmp);
        tmp.mapPoints(pts);

        mMatrixValueToPx.invert(tmp);
        tmp.mapPoints(pts);

        // for barchart, we only need x-val
        double xTouchVal = pts[0];
        double base = xTouchVal;

        if (xTouchVal < 0 || xTouchVal > mDeltaX)
            return null;

        if (base < 0)
            base = 0;
        if (base >= mDeltaX)
            base = mDeltaX - 1;

        int setCount = mOriginalData.getDataSetCount();
        int valCount = setCount * mOriginalData.getXValCount();

        // calculate the amount of bar-space between index 0 and touch position
        float space = (float) (((float) valCount / (float) setCount) / (mDeltaX / base));

        float reduction = (float) space * mOriginalData.getGroupSpace();

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

        BarDataSet set = mOriginalData.getDataSetForEntry(e);

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

        transformRect(bounds);

        return bounds;
    }

    /**
     * sets the skew (default 0.3f), the skew indicates how much the 3D effect
     * of the chart is turned to the right
     * 
     * @param skew
     */
    public void setSkew(float skew) {
        this.mSkew = skew;
    }

    /**
     * returns the skew value that indicates how much the 3D effect is turned to
     * the right
     * 
     * @return
     */
    public float getSkew() {
        return mSkew;
    }

    /**
     * set the depth of the chart (default 0.3f), the depth indicates how much
     * the 3D effect of the chart goes back
     * 
     * @param depth
     */
    public void setDepth(float depth) {
        this.mDepth = depth;
    }

    /**
     * returhs the depth, which indicates how much the 3D effect goes back
     * 
     * @return
     */
    public float getDepth() {
        return mDepth;
    }

    /**
     * if enabled, chart will be drawn in 3d
     * 
     * @param enabled
     */
    public void set3DEnabled(boolean enabled) {
        this.m3DEnabled = enabled;
    }

    /**
     * returns true if 3d bars is enabled, false if not
     * 
     * @return
     */
    public boolean is3DEnabled() {
        return m3DEnabled;
    }

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
