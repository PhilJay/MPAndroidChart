
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Paint.Align;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.BarDataProvider;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ValueFormatter;

import java.util.ArrayList;

/**
 * Renderer for the HorizontalBarChart.
 * 
 * @author Philipp Jahoda
 */
public class HorizontalBarChartRenderer extends BarChartRenderer {

    private float xOffset = 0f;
    private float yOffset = 0f;

    public HorizontalBarChartRenderer(BarDataProvider chart, ChartAnimator animator,
            ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);

        mValuePaint.setTextAlign(Align.LEFT);
        yOffset = Utils.calcTextHeight(mValuePaint, "Q");
        xOffset = Utils.convertDpToPixel(4f);
    }

    protected void drawDataSet(Canvas c, BarDataSet dataSet, int index) {

        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        // the space between bar-groups
        float space = mChart.getBarData().getGroupSpace();

        boolean noStacks = dataSet.getStackSize() == 1 ? true : false;

        ArrayList<BarEntry> entries = dataSet.getYVals();

        // do the drawing
        for (int j = 0; j < dataSet.getEntryCount() * mAnimator.getPhaseX(); j++) {

            BarEntry e = entries.get(j);

            // calculate the x-position, depending on datasetcount
            float x = e.getXIndex() + j * (mChart.getBarData().getDataSetCount() - 1) + index
                    + space * j + space / 2f;
            float y = e.getVal();

            // no stacks
            if (noStacks) {

                prepareBar(x, y, dataSet.getBarSpace(), trans);
                
                // avoid drawing outofbounds values
                if (!mViewPortHandler.isInBoundsTop(mBarRect.bottom))
                    break;

                if (!mViewPortHandler.isInBoundsBottom(mBarRect.top))
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
                if (!mViewPortHandler.isInBoundsTop(mBarRect.bottom))
                    break;
            }
        }
    }

    @Override
    public void drawValues(Canvas c) {
        // if values are drawn
        if (passesCheck()) {

            ArrayList<BarDataSet> dataSets = mChart.getBarData().getDataSets();

            float posOffset = 0f;
            float negOffset = 0f;
            boolean drawValueAboveBar = mChart.isDrawValueAboveBarEnabled();

            // calculate the correct offset depending on the draw position of
            // the value
            posOffset = (drawValueAboveBar ? -Utils.convertDpToPixel(5) : Utils.calcTextHeight(
                    mValuePaint,
                    "8") * 1.5f);
            negOffset = (drawValueAboveBar ? Utils.calcTextHeight(mValuePaint, "8") * 1.5f : -Utils
                    .convertDpToPixel(5));

            for (int i = 0; i < mChart.getBarData().getDataSetCount(); i++) {

                BarDataSet dataSet = dataSets.get(i);

                if (!dataSet.isDrawValuesEnabled())
                    continue;

                // apply the text-styling defined by the DataSet
                applyValueTextStyle(dataSet);

                ValueFormatter formatter = dataSet.getValueFormatter();

                Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

                ArrayList<BarEntry> entries = dataSet.getYVals();

                float[] valuePoints = getTransformedValues(trans, entries, i);

                // if only single values are drawn (sum)
                if (!mChart.isDrawValuesForWholeStackEnabled()) {

                    for (int j = 0; j < valuePoints.length * mAnimator.getPhaseX(); j += 2) {

                        if (!mViewPortHandler.isInBoundsX(valuePoints[j]))
                            continue;

                        if (!mViewPortHandler.isInBoundsTop(valuePoints[j + 1]))
                            break;
                        
                        if(!mViewPortHandler.isInBoundsBottom(valuePoints[j + 1])) 
                            continue;

                        float val = entries.get(j / 2).getVal();

                        drawValue(c, val, valuePoints[j],
                                valuePoints[j + 1] + (val >= 0 ? posOffset : negOffset), formatter);
                    }

                    // if each value of a potential stack should be drawn
                } else {

                    for (int j = 0; j < (valuePoints.length - 1) * mAnimator.getPhaseX(); j += 2) {

                        if (!mViewPortHandler.isInBoundsX(valuePoints[j]))
                            continue;

                        if (!mViewPortHandler.isInBoundsTop(valuePoints[j + 1]))
                            break;
                        
                        if(!mViewPortHandler.isInBoundsBottom(valuePoints[j + 1])) 
                            continue;

                        BarEntry e = entries.get(j / 2);

                        float[] vals = e.getVals();

                        // we still draw stacked bars, but there is one
                        // non-stacked
                        // in between
                        if (vals == null) {

                            drawValue(c, e.getVal(), valuePoints[j],
                                    valuePoints[j + 1] + (e.getVal() >= 0 ? posOffset : negOffset),
                                    formatter);

                        } else {

                            float[] transformed = new float[vals.length * 2];
                            int cnt = 0;
                            float add = e.getVal();

                            for (int k = 0; k < transformed.length; k += 2) {

                                add -= vals[cnt];
                                transformed[k] = (vals[cnt] + add) * mAnimator.getPhaseY();
                                cnt++;
                            }

                            trans.pointValuesToPixel(transformed);

                            for (int k = 0; k < transformed.length; k += 2) {

                                drawValue(c, vals[k / 2],
                                        transformed[k], valuePoints[j+1]
                                                + (vals[k / 2] >= 0 ? posOffset : negOffset),
                                        formatter);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void prepareBar(float x, float y, float barspace, Transformer trans) {

        float spaceHalf = barspace / 2f;

        float top = x - 0.5f + spaceHalf;
        float bottom = x + 0.5f - spaceHalf;
        float left = y >= 0 ? y : 0;
        float right = y <= 0 ? y : 0;

        mBarRect.set(left, top, right, bottom);

        trans.rectValueToPixelHorizontal(mBarRect, mAnimator.getPhaseY());

        // if a shadow is drawn, prepare it too
        if (mChart.isDrawBarShadowEnabled()) {
            mBarShadow.set(mViewPortHandler.contentLeft(), mBarRect.top,
                    mViewPortHandler.contentRight(),
                    mBarRect.bottom);
        }
    }

    @Override
    public float[] getTransformedValues(Transformer trans, ArrayList<BarEntry> entries,
            int dataSetIndex) {
        return trans.generateTransformedValuesHorizontalBarChart(entries, dataSetIndex,
                mChart.getBarData(), mAnimator.getPhaseY());
    }

    @Override
    protected void drawValue(Canvas c, float val, float xPos, float yPos, ValueFormatter formatter) {
        super.drawValue(c, val, xPos + xOffset, yPos + yOffset, formatter);
    }

    @Override
    protected boolean passesCheck() {
        return mChart.getBarData().getYValCount() < mChart.getMaxVisibleCount()
                * mViewPortHandler.getScaleY();
    }
}
