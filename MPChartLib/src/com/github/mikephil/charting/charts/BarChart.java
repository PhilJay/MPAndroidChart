
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
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;

/**
 * Chart that draws bars.
 * 
 * @author Philipp Jahoda
 */
public class BarChart extends BarLineChartBase {

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

    /**
     * Sets a BarData object as a model for the BarChart.
     * 
     * @param data
     */
    public void setData(BarData data) {
        super.setData(data);
    }

    // @Override
    // public void setColorTemplate(ColorTemplate ct) {
    // super.setColorTemplate(ct);
    //
    // calculate3DColors();
    // }

    // /** array that holds all the colors for the top 3D effect */
    // private ArrayList<ArrayList<Integer>> mTopColors;
    //
    // /** array that holds all the colors for the side 3D effect */
    // private ArrayList<ArrayList<Integer>> mSideColors;
    //
    // /**
    // * calculates the 3D color arrays
    // */
    // protected void calculate3DColors() {
    //
    // // generate the colors for the 3D effect
    // mTopColors = new ArrayList<ArrayList<Integer>>();
    // mSideColors = new ArrayList<ArrayList<Integer>>();
    //
    // float[] hsv = new float[3];
    //
    // for (int i = 0; i < mCt.getColors().size(); i++) {
    //
    // // Get the colors for the DataSet at the current index. If the index
    // // is out of bounds, reuse DataSet colors.
    // ArrayList<Integer> colors = mCt.getDataSetColors(i);
    // ArrayList<Integer> topColors = new ArrayList<Integer>();
    // ArrayList<Integer> sideColors = new ArrayList<Integer>();
    //
    // for (int j = 0; j < colors.size(); j++) {
    //
    // // extract the color
    // int c = colors.get(j);
    // Color.colorToHSV(c, hsv); // convert to hsv
    //
    // // make brighter
    // hsv[1] = hsv[1] - 0.1f; // less saturation
    // hsv[2] = hsv[2] + 0.1f; // more brightness
    //
    // // convert back
    // c = Color.HSVToColor(hsv);
    //
    // // assign
    // topColors.add(c);
    //
    // // get color again
    // c = colors.get(j);
    //
    // // convert
    // Color.colorToHSV(c, hsv);
    //
    // // make darker
    // hsv[1] = hsv[1] + 0.1f; // more saturation
    // hsv[2] = hsv[2] - 0.1f; // less brightness
    //
    // // reassing
    // c = Color.HSVToColor(hsv);
    //
    // sideColors.add(c);
    // }
    //
    // mTopColors.add(topColors);
    // mSideColors.add(sideColors);
    // }
    // }

    @Override
    protected void calcMinMax(boolean fixedValues) {
        super.calcMinMax(fixedValues);

        if (!mStartAtZero && getYMin() >= 0f) {
            mYChartMin = getYMin();
            mDeltaY = Math.abs(mYChartMax - mYChartMin);
        }

        // increase deltax by 1 because the bars have a width of 1
        mDeltaX++;
    }

    @Override
    protected void drawHighlights() {

        // if there are values to highlight and highlighnting is enabled, do it
        if (mHighlightEnabled && mHighLightIndicatorEnabled && valuesToHighlight()) {

            // distance between highlight arrow and bar
            float offsetY = mDeltaY * 0.04f;

            for (int i = 0; i < mIndicesToHightlight.length; i++) {

                int index = mIndicesToHightlight[i].getXIndex();

                int dataSetIndex = mIndicesToHightlight[i].getDataSetIndex();
                BarDataSet ds = (BarDataSet) mCurrentData.getDataSetByIndex(dataSetIndex);

                // check outofbounds
                if (index < mCurrentData.getYValCount() && index >= 0) {

                    mHighlightPaint.setAlpha(120);

                    float y = getYValueByDataSetIndex(index, dataSetIndex);
                    float left = index + ds.getBarSpace() / 2f;
                    float right = index + 1f - ds.getBarSpace() / 2f;
                    float top = y >= 0 ? y : 0;
                    float bottom = y <= 0 ? y : 0;

                    RectF highlight = new RectF(left, top, right, bottom);
                    transformRect(highlight);

                    mDrawCanvas.drawRect(highlight, mHighlightPaint);

                    if (mDrawHighlightArrow) {

                        mHighlightPaint.setAlpha(200);

                        Path arrow = new Path();
                        arrow.moveTo(index + 0.5f, y + offsetY * 0.3f);
                        arrow.lineTo(index + 0.2f, y + offsetY);
                        arrow.lineTo(index + 0.8f, y + offsetY);

                        transformPath(arrow);
                        mDrawCanvas.drawPath(arrow, mHighlightPaint);
                    }
                }
            }
        }
    }

    @Override
    protected void drawData() {

        ArrayList<BarDataSet> dataSets = (ArrayList<BarDataSet>) mCurrentData.getDataSets();

        // 2D drawing
        for (int i = 0; i < mCurrentData.getDataSetCount(); i++) {

            BarDataSet dataSet = dataSets.get(i);
            ArrayList<Entry> entries = dataSet.getYVals();

            // do the drawing
            for (int j = 0; j < dataSet.getEntryCount(); j++) {

                Entry e = entries.get(j);

                // no stacks
                if (dataSet.getStackSize() == 1) {

                    prepareBar(e.getXIndex(), e.getVal(), dataSet.getBarSpace());

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

                        prepareBar(e.getXIndex(), e.getVal(), dataSet.getBarSpace());

                        // if drawing the bar shadow is enabled
                        if (mDrawBarShadow) {
                            mRenderPaint.setColor(dataSet.getBarShadowColor());
                            mDrawCanvas.drawRect(mBarShadow, mRenderPaint);
                        }

                        mRenderPaint.setColor(dataSet.getColor(0));
                        mDrawCanvas.drawRect(mBarRect, mRenderPaint);

                    } else {

                        float all = e.getSum();

                        for (int k = 0; k < vals.length; k++) {

                            all -= vals[k];

                            prepareBar(e.getXIndex(), vals[k] + all, dataSet.getBarSpace());

                            // if drawing the bar shadow is enabled
                            if (mDrawBarShadow) {
                                mRenderPaint.setColor(dataSet.getBarShadowColor());
                                mDrawCanvas.drawRect(mBarShadow, mRenderPaint);
                            }

                            mRenderPaint.setColor(dataSet.getColor(k));
                            mDrawCanvas.drawRect(mBarRect, mRenderPaint);
                        }
                    }

                    // avoid drawing outofbounds values
                    if (isOffContentRight(mBarRect.left))
                        break;

                    if (isOffContentLeft(mBarRect.right)) {
                        continue;
                    }
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
     * @param space the space between bars
     */
    private void prepareBar(float x, float y, float space) {

        float left = x + space / 2f;
        float right = x + 1f - space / 2f;
        float top = y >= 0 ? y : 0;
        float bottom = y <= 0 ? y : 0;

        mBarRect.set(left, top, right, bottom);

        transformRect(mBarRect);

        // if a shadow is drawn, prepare it too
        if (mDrawBarShadow) {
            mBarShadow.set(mBarRect.left, mOffsetTop, mBarRect.right, getHeight() - mOffsetBottom);
        }
    }

    // @Override
    // protected void drawData() {
    //
    // ArrayList<Path> topPaths = new ArrayList<Path>();
    // ArrayList<Path> sidePaths = new ArrayList<Path>();
    //
    // ArrayList<BarDataSet> dataSets = (ArrayList<BarDataSet>)
    // mCurrentData.getDataSets();
    //
    // // preparations for 3D bars
    // if (m3DEnabled) {
    //
    // float[] pts = new float[] {
    // 0f, 0f, 1f, 0f
    // };
    //
    // // calculate the depth depending on scale
    //
    // transformPointArray(pts);
    //
    // pts[3] = pts[2] - pts[0];
    // pts[2] = 0f;
    // pts[1] = 0f;
    // pts[0] = 0f;
    //
    // Matrix invert = new Matrix();
    //
    // mMatrixOffset.invert(invert);
    // invert.mapPoints(pts);
    //
    // mMatrixTouch.invert(invert);
    // invert.mapPoints(pts);
    //
    // mMatrixValueToPx.invert(invert);
    // invert.mapPoints(pts);
    //
    // float depth = Math.abs(pts[3] - pts[1]) * mDepth;
    //
    // for (int i = 0; i < mCurrentData.getDataSetCount(); i++) {
    //
    // DataSet dataSet = dataSets.get(i);
    // ArrayList<Entry> series = dataSet.getYVals();
    //
    // for (int j = 0; j < series.size(); j++) {
    //
    // float x = series.get(j).getXIndex();
    // float y = series.get(j).getVal();
    // float left = x + mBarSpace / 2f;
    // float right = x + 1f - mBarSpace / 2f;
    // float top = y >= 0 ? y : 0;
    //
    // // create the 3D effect paths for the top and side
    // Path topPath = new Path();
    // topPath.moveTo(left, top);
    // topPath.lineTo(left + mSkew, top + depth);
    // topPath.lineTo(right + mSkew, top + depth);
    // topPath.lineTo(right, top);
    //
    // topPaths.add(topPath);
    //
    // Path sidePath = new Path();
    // sidePath.moveTo(right, top);
    // sidePath.lineTo(right + mSkew, top + depth);
    // sidePath.lineTo(right + mSkew, depth);
    // sidePath.lineTo(right, 0);
    //
    // sidePaths.add(sidePath);
    // }
    // }
    //
    // transformPaths(topPaths);
    // transformPaths(sidePaths);
    // }
    //
    // int cnt = 0;
    //
    // // 2D drawing
    // for (int i = 0; i < mCurrentData.getDataSetCount(); i++) {
    //
    // BarDataSet dataSet = dataSets.get(i);
    // ArrayList<Entry> series = dataSet.getYVals();
    //
    // // Get the colors for the DataSet at the current index. If the
    // // index
    // // is out of bounds, reuse DataSet colors.
    // ArrayList<Integer> colors = mCt.getDataSetColors(i %
    // mCt.getColors().size());
    // ArrayList<Integer> colors3DTop = mTopColors.get(i %
    // mCt.getColors().size());
    // ArrayList<Integer> colors3DSide = mSideColors.get(i %
    // mCt.getColors().size());
    //
    // // do the drawing
    // for (int j = 0; j < dataSet.getEntryCount(); j++) {
    //
    // // Set the color for the currently drawn value. If the index
    // // is
    // // out of bounds, reuse colors.
    // mRenderPaint.setColor(colors.get(j % colors.size()));
    //
    // int x = series.get(j).getXIndex();
    // float y = series.get(j).getVal();
    // float left = x + mBarSpace / 2f;
    // float right = x + 1f - mBarSpace / 2f;
    // float top = y >= 0 ? y : 0;
    // float bottom = y <= 0 ? y : 0;
    //
    // mBarRect.set(left, top, right, bottom);
    //
    // transformRect(mBarRect);
    //
    // // avoid drawing outofbounds values
    // if (isOffContentRight(mBarRect.left))
    // break;
    //
    // if (isOffContentLeft(mBarRect.right)) {
    // cnt++;
    // continue;
    // }
    //
    // mDrawCanvas.drawRect(mBarRect, mRenderPaint);
    //
    // // 3D drawing
    // if (m3DEnabled) {
    //
    // mRenderPaint.setColor(colors3DTop.get(j % colors3DTop.size()));
    // mDrawCanvas.drawPath(topPaths.get(cnt), mRenderPaint);
    //
    // mRenderPaint.setColor(colors3DSide.get(j % colors3DSide.size()));
    // mDrawCanvas.drawPath(sidePaths.get(cnt), mRenderPaint);
    // }
    //
    // cnt++;
    // }
    // }
    // }

    @Override
    protected void drawValues() {

        long starttime = System.currentTimeMillis();
        // if values are drawn
        if (mDrawYValues && mCurrentData.getYValCount() < mMaxVisibleCount * mScaleX) {

            ArrayList<BarDataSet> dataSets = (ArrayList<BarDataSet>) mCurrentData.getDataSets();

            float offset = 0f;

            // calculate the correct offset depending on the draw position of
            // the value
            if (mDrawValueAboveBar)
                offset = -Utils.convertDpToPixel(5);
            else
                offset = Utils.calcTextHeight(mValuePaint, "8") * 1.5f;

            for (int i = 0; i < mCurrentData.getDataSetCount(); i++) {

                BarDataSet dataSet = dataSets.get(i);
                ArrayList<Entry> entries = dataSet.getYVals();

                float[] valuePoints = generateTransformedValues(entries, 0.5f);

                if (!mDrawValuesForWholeStack) {

                    for (int j = 0; j < valuePoints.length; j += 2) {

                        if (isOffContentRight(valuePoints[j]))
                            break;

                        if (isOffContentLeft(valuePoints[j]))
                            continue;

                        float val = entries.get(j / 2).getSum();

                        drawValue(mFormatValue.format(val), valuePoints[j],
                                valuePoints[j + 1] + offset);
                    }

                    // if each value of a potential stack should be drawn
                } else {

                    for (int j = 0; j < valuePoints.length - 1; j += 2) {

                        if (isOffContentRight(valuePoints[j]))
                            break;

                        if (isOffContentLeft(valuePoints[j]))
                            continue;

                        Entry e = entries.get(j / 2);

                        float[] vals = e.getVals();

                        // we still draw stacked bars, but there is one
                        // non-stacked
                        // in between
                        if (vals == null) {

                            drawValue(mFormatValue.format(e.getVal()), valuePoints[j],
                                    valuePoints[j + 1] + offset);

                        } else {

                            float[] transformed = new float[vals.length * 2];
                            int cnt = 0;
                            float add = e.getSum();

                            for (int k = 0; k < transformed.length; k += 2) {

                                add -= vals[cnt];
                                transformed[k + 1] = vals[cnt] + add;
                                cnt++;
                            }

                            transformPointArray(transformed);

                            for (int k = 0; k < transformed.length; k += 2) {

                                drawValue(mFormatValue.format(vals[k / 2]), valuePoints[j],
                                        transformed[k + 1] + offset);
                            }
                        }
                    }
                }
            }
        }

        Log.i(LOG_TAG, "DrawValues time: " + (System.currentTimeMillis() - starttime) + "ms");
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

    @Override
    public void setPaint(Paint p, int which) {
        super.setPaint(p, which);

        switch (which) {
            case PAINT_HIGHLIGHT_BAR:
                mHighlightPaint = p;
                break;
        }
    }

    @Override
    public Paint getPaint(int which) {
        super.getPaint(which);

        switch (which) {
            case PAINT_HIGHLIGHT_BAR:
                return mHighlightPaint;
        }

        return null;
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
     * if set to true, a grey area is drawn behind each bar that indicates the
     * maximum value
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
