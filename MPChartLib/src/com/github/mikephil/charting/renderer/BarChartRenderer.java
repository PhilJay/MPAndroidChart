
package com.github.mikephil.charting.renderer;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;


public class BarChartRenderer extends DataRenderer {

    protected BarChart mChart;
    
    /** the rect object that is used for drawing the bar shadow */
    protected RectF mBarShadow = new RectF();

    /** the rect object that is used for drawing the bars */
    protected RectF mBarRect = new RectF();

    public BarChartRenderer(BarChart chart, ChartAnimator animator,
            ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        this.mChart = chart;
        
        mHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHighlightPaint.setStyle(Paint.Style.FILL);
        mHighlightPaint.setColor(Color.rgb(0, 0, 0));
        // set alpha after color
        mHighlightPaint.setAlpha(120);
    }

    @Override
    public void drawData(Canvas c) {
        
        BarData barData = mChart.getData();
        
        for(int i = 0; i < barData.getDataSetCount(); i++) {
            
            BarDataSet set = barData.getDataSetByIndex(i);
            
            if(set.isVisible()) {
                drawDataSet(c, set, i);
            }
        }
    }
    
    private void drawDataSet(Canvas c, BarDataSet dataSet, int index) {
        
        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());
        
        // the space between bar-groups
        float space = mChart.getData().getGroupSpace();
        
        boolean noStacks = dataSet.getStackSize() == 1 ? true : false;

        ArrayList<BarEntry> entries = dataSet.getYVals();

        // do the drawing
        for (int j = 0; j < dataSet.getEntryCount() * mAnimator.getPhaseX(); j++) {

            BarEntry e = entries.get(j);

            // calculate the x-position, depending on datasetcount
            float x = e.getXIndex() + j * (mChart.getData().getDataSetCount() - 1) + index + space * j + space / 2f;
            float y = e.getVal();

            // no stacks
            if (noStacks) {

                prepareBar(x, y, dataSet.getBarSpace(), trans);

                // avoid drawing outofbounds values
                if (!mViewPortHandler.isInBoundsRight(mBarRect.left))
                    break;

                if (!mViewPortHandler.isInBoundsLeft(mBarRect.right))
                    continue;

                // if drawing the bar shadow is enabled
                if (mChart.isDrawBarShadowEnabled()) {
                    mRenderPaint.setColor(dataSet.getBarShadowColor());
                    c.drawRect(mBarShadow, mRenderPaint);
                }

                // Set the color for the currently drawn value. If the index
                // is
                // out of bounds, reuse colors.
                mRenderPaint.setColor(dataSet.getColor(j));
                c.drawRect(mBarRect, mRenderPaint);

            } else { // stacked bars

                float[] vals = e.getVals();

                // we still draw stacked bars, but there could be one
                // non-stacked
                // in between
                if (vals == null) {

                    prepareBar(x, y, dataSet.getBarSpace(), trans);

                    // if drawing the bar shadow is enabled
                    if (mChart.isDrawBarShadowEnabled()) {
                        mRenderPaint.setColor(dataSet.getBarShadowColor());
                        c.drawRect(mBarShadow, mRenderPaint);
                    }

                    mRenderPaint.setColor(dataSet.getColor(0));
                    c.drawRect(mBarRect, mRenderPaint);

                } else {

                    float all = e.getVal();

                    // if drawing the bar shadow is enabled
                    if (mChart.isDrawBarShadowEnabled()) {

                        prepareBar(x, y, dataSet.getBarSpace(), trans);
                        mRenderPaint.setColor(dataSet.getBarShadowColor());
                        c.drawRect(mBarShadow, mRenderPaint);
                    }

                    // draw the stack
                    for (int k = 0; k < vals.length; k++) {

                        all -= vals[k];

                        prepareBar(x, vals[k] + all, dataSet.getBarSpace(), trans);

                        mRenderPaint.setColor(dataSet.getColor(k));
                        c.drawRect(mBarRect, mRenderPaint);
                    }
                }

                // avoid drawing outofbounds values
                if (!mViewPortHandler.isInBoundsRight(mBarRect.left))
                    break;
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
    protected void prepareBar(float x, float y, float barspace, Transformer trans) {

        float spaceHalf = barspace / 2f;
        float left = x + spaceHalf;
        float right = x + 1f - spaceHalf;
        float top = y >= 0 ? y : 0;
        float bottom = y <= 0 ? y : 0;

        mBarRect.set(left, top, right, bottom);

        trans.rectValueToPixel(mBarRect, mAnimator.getPhaseY());

        // if a shadow is drawn, prepare it too
        if (mChart.isDrawBarShadowEnabled()) {
            mBarShadow.set(mBarRect.left, mViewPortHandler.offsetTop(), mBarRect.right, mViewPortHandler.contentBottom());
        }
    }

    @Override
    public void drawValues(Canvas c) {
        // if values are drawn
        if (mChart.getData().getYValCount() < mChart.getMaxVisibleCount()
                * mViewPortHandler.getScaleX()) {

            ArrayList<BarDataSet> dataSets = mChart.getData().getDataSets();

            float posOffset = 0f;
            float negOffset = 0f;

            // calculate the correct offset depending on the draw position of
            // the value
            posOffset = (mChart.isDrawValueAboveBarEnabled() ? -Utils.convertDpToPixel(5) : Utils.calcTextHeight(mValuePaint,
                    "8") * 1.5f);
            negOffset = (mChart.isDrawValueAboveBarEnabled() ? Utils.calcTextHeight(mValuePaint, "8") * 1.5f : -Utils
                    .convertDpToPixel(5));

            for (int i = 0; i < mChart.getData().getDataSetCount(); i++) {

                BarDataSet dataSet = dataSets.get(i);
                
                if(!dataSet.isDrawValuesEnabled()) 
                    continue;
                
                Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());
                
                ArrayList<BarEntry> entries = dataSet.getYVals();

                float[] valuePoints = trans.generateTransformedValuesBarChart(entries, i, mChart.getData(),
                        mAnimator.getPhaseY());

                // if only single values are drawn (sum)
                if (!mChart.isDrawValuesForWholeStackEnabled()) {

                    for (int j = 0; j < valuePoints.length * mAnimator.getPhaseX(); j += 2) {

                        if (!mViewPortHandler.isInBoundsRight(valuePoints[j])) 
                            break;

                        if (!mViewPortHandler.isInBoundsY(valuePoints[j + 1]) || !mViewPortHandler.isInBoundsLeft(valuePoints[j])) 
                            continue;

                        float val = entries.get(j / 2).getVal();

                        drawValue(c, val, valuePoints[j],
                                valuePoints[j + 1] + (val >= 0 ? posOffset : negOffset));
                    }

                    // if each value of a potential stack should be drawn
                } else {

                    for (int j = 0; j < (valuePoints.length - 1) * mAnimator.getPhaseX(); j += 2) {

                        if (!mViewPortHandler.isInBoundsRight(valuePoints[j])) 
                            break;

                        if (!mViewPortHandler.isInBoundsY(valuePoints[j + 1]) || !mViewPortHandler.isInBoundsLeft(valuePoints[j])) 
                            continue;

                        BarEntry e = entries.get(j / 2);

                        float[] vals = e.getVals();

                        // we still draw stacked bars, but there is one
                        // non-stacked
                        // in between
                        if (vals == null) {

                            drawValue(c, e.getVal(), valuePoints[j],
                                    valuePoints[j + 1] + (e.getVal() >= 0 ? posOffset : negOffset));

                        } else {

                            float[] transformed = new float[vals.length * 2];
                            int cnt = 0;
                            float add = e.getVal();

                            for (int k = 0; k < transformed.length; k += 2) {

                                add -= vals[cnt];
                                transformed[k + 1] = (vals[cnt] + add) * mAnimator.getPhaseY();
                                cnt++;
                            }

                            trans.pointValuesToPixel(transformed);

                            for (int k = 0; k < transformed.length; k += 2) {

                                drawValue(c, vals[k / 2], valuePoints[j],
                                        transformed[k + 1]
                                                + (vals[k / 2] >= 0 ? posOffset : negOffset));
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
    private void drawValue(Canvas c, float val, float xPos, float yPos) {

        String value = mChart.getValueFormatter().getFormattedValue(val);
        c.drawText(value, xPos, yPos,
                    mValuePaint);
    }

    @Override
    public void drawExtras(Canvas c) { }

    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {
        
        int setCount = mChart.getData().getDataSetCount();

        for (int i = 0; i < indices.length; i++) {

            Highlight h = indices[i];
            int index = h.getXIndex();

            int dataSetIndex = h.getDataSetIndex();
            BarDataSet set = mChart.getData().getDataSetByIndex(dataSetIndex);

            if (set == null)
                continue;
            
            Transformer trans = mChart.getTransformer(set.getAxisDependency());

            mHighlightPaint.setColor(set.getHighLightColor());
            mHighlightPaint.setAlpha(set.getHighLightAlpha());

            // check outofbounds
            if (index < mChart.getData().getYValCount() && index >= 0
                    && index < (mChart.getDeltaX() * mAnimator.getPhaseX()) / setCount) {

                Entry e = mChart.getEntryByDataSetIndex(index, dataSetIndex);

                if (e == null)
                    continue;
                
                float groupspace = mChart.getData().getGroupSpace();

                // calculate the correct x-position
                float x = index * setCount + dataSetIndex + groupspace / 2f
                        + groupspace * index;
                float y = e.getVal();

                prepareBar(x, y, set.getBarSpace(), trans);

                c.drawRect(mBarRect, mHighlightPaint);

                if (mChart.isDrawHighlightArrowEnabled()) {

                    mHighlightPaint.setAlpha(255);

                    // distance between highlight arrow and bar
                    float offsetY = mAnimator.getPhaseY() * 0.07f;

                    Path arrow = new Path();
                    arrow.moveTo(x + 0.5f, y + offsetY * 0.3f);
                    arrow.lineTo(x + 0.2f, y + offsetY);
                    arrow.lineTo(x + 0.8f, y + offsetY);

                    trans.pathValueToPixel(arrow);
                    c.drawPath(arrow, mHighlightPaint);
                }
            }
        }
    }
}
