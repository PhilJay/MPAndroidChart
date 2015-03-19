
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.BarBuffer;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.BarDataProvider;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.List;

public class BarChartRenderer extends DataRenderer {

    protected BarDataProvider mChart;

    /** the rect object that is used for drawing the bars */
    protected RectF mBarRect = new RectF();

    protected BarBuffer[] mBarBuffers;

    protected Paint mShadowPaint;

    public BarChartRenderer(BarDataProvider chart, ChartAnimator animator,
            ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        this.mChart = chart;

        mHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHighlightPaint.setStyle(Paint.Style.FILL);
        mHighlightPaint.setColor(Color.rgb(0, 0, 0));
        // set alpha after color
        mHighlightPaint.setAlpha(120);

        mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mShadowPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void initBuffers() {

        BarData barData = mChart.getBarData();
        mBarBuffers = new BarBuffer[barData.getDataSetCount()];

        for (int i = 0; i < mBarBuffers.length; i++) {
            BarDataSet set = barData.getDataSetByIndex(i);
            mBarBuffers[i] = new BarBuffer(set.getValueCount() * 4 * set.getStackSize(),
                    barData.getGroupSpace(),
                    barData.getDataSetCount(), set.isStacked());
        }
    }

    @Override
    public void drawData(Canvas c) {

        BarData barData = mChart.getBarData();

        for (int i = 0; i < barData.getDataSetCount(); i++) {

            BarDataSet set = barData.getDataSetByIndex(i);

            if (set.isVisible()) {
                drawDataSet(c, set, i);
            }
        }
    }

    protected void drawDataSet(Canvas c, BarDataSet dataSet, int index) {

        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());
        calcXBounds(trans);

        mShadowPaint.setColor(dataSet.getBarShadowColor());

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        List<BarEntry> entries = dataSet.getYVals();

        // initialize the buffer
        BarBuffer buffer = mBarBuffers[index];
        buffer.setPhases(phaseX, phaseY);
        buffer.setBarSpace(dataSet.getBarSpace());
        buffer.setDataSet(index);

        buffer.feed(entries);

        trans.pointValuesToPixel(buffer.buffer);

        // if multiple colors
        if (dataSet.getColors().size() > 1) {

            for (int j = 0; j < buffer.size(); j += 4) {

                if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2]))
                    continue;

                if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j]))
                    break;

                if (mChart.isDrawBarShadowEnabled()) {
                    c.drawRect(buffer.buffer[j], mViewPortHandler.contentTop(),
                            buffer.buffer[j + 2],
                            mViewPortHandler.contentBottom(), mShadowPaint);
                }

                // Set the color for the currently drawn value. If the index
                // is
                // out of bounds, reuse colors.
                mRenderPaint.setColor(dataSet.getColor(j / 4));
                c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                        buffer.buffer[j + 3], mRenderPaint);
            }
        } else {

            mRenderPaint.setColor(dataSet.getColor());

            for (int j = 0; j < buffer.size(); j += 4) {

                if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2]))
                    continue;

                if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j]))
                    break;

                if (mChart.isDrawBarShadowEnabled()) {
                    c.drawRect(buffer.buffer[j], mViewPortHandler.contentTop(),
                            buffer.buffer[j + 2],
                            mViewPortHandler.contentBottom(), mShadowPaint);
                }

                c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                        buffer.buffer[j + 3], mRenderPaint);
            }
        }
    }

    /**
     * Prepares a bar for being highlighted.
     * 
     * @param x the x-position
     * @param y the y-position
     * @param barspace the space between bars
     * @param from
     * @param trans
     */
    protected void prepareBarHighlight(float x, float y, float barspace, float from,
            Transformer trans) {

        float barWidth = 0.5f;

        float spaceHalf = barspace / 2f;
        float left = x - barWidth + spaceHalf;
        float right = x + barWidth - spaceHalf;
        float top = y >= from ? y : from;
        float bottom = y <= from ? y : from;

        mBarRect.set(left, top, right, bottom);

        trans.rectValueToPixel(mBarRect, mAnimator.getPhaseY());
    }

    @Override
    public void drawValues(Canvas c) {
        // if values are drawn
        if (passesCheck()) {

            List<BarDataSet> dataSets = mChart.getBarData().getDataSets();

            float posOffset = 0f;
            float negOffset = 0f;
            boolean drawValueAboveBar = mChart.isDrawValueAboveBarEnabled();

            // calculate the correct offset depending on the draw position of
            // the value
            float plus = Utils.convertDpToPixel(6f);

            posOffset = (drawValueAboveBar ? -Utils.convertDpToPixel(5) : Utils.calcTextHeight(
                    mValuePaint,
                    "8") + plus);
            negOffset = (drawValueAboveBar ? Utils.calcTextHeight(mValuePaint, "8") + plus : -Utils
                    .convertDpToPixel(5));

            for (int i = 0; i < mChart.getBarData().getDataSetCount(); i++) {

                BarDataSet dataSet = dataSets.get(i);

                if (!dataSet.isDrawValuesEnabled())
                    continue;

                // apply the text-styling defined by the DataSet
                applyValueTextStyle(dataSet);

                ValueFormatter formatter = dataSet.getValueFormatter();

                Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

                List<BarEntry> entries = dataSet.getYVals();

                float[] valuePoints = getTransformedValues(trans, entries, i);

                // if only single values are drawn (sum)
                if (!mChart.isDrawValuesForWholeStackEnabled()) {

                    for (int j = 0; j < valuePoints.length * mAnimator.getPhaseX(); j += 2) {

                        if (!mViewPortHandler.isInBoundsRight(valuePoints[j]))
                            break;

                        if (!mViewPortHandler.isInBoundsY(valuePoints[j + 1])
                                || !mViewPortHandler.isInBoundsLeft(valuePoints[j]))
                            continue;

                        float val = entries.get(j / 2).getVal();

                        drawValue(c, val, valuePoints[j],
                                valuePoints[j + 1] + (val >= 0 ? posOffset : negOffset), formatter);
                    }

                    // if each value of a potential stack should be drawn
                } else {

                    for (int j = 0; j < (valuePoints.length - 1) * mAnimator.getPhaseX(); j += 2) {

                        BarEntry e = entries.get(j / 2);

                        float[] vals = e.getVals();

                        // we still draw stacked bars, but there is one
                        // non-stacked
                        // in between
                        if (vals == null) {

                            if (!mViewPortHandler.isInBoundsRight(valuePoints[j]))
                                break;

                            if (!mViewPortHandler.isInBoundsY(valuePoints[j + 1])
                                    || !mViewPortHandler.isInBoundsLeft(valuePoints[j]))
                                continue;

                            drawValue(c, e.getVal(), valuePoints[j],
                                    valuePoints[j + 1] + (e.getVal() >= 0 ? posOffset : negOffset),
                                    formatter);

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

                                float x = valuePoints[j];
                                float y = transformed[k + 1]
                                        + (vals[k / 2] >= 0 ? posOffset : negOffset);

                                if (!mViewPortHandler.isInBoundsRight(x))
                                    break;

                                if (!mViewPortHandler.isInBoundsY(y)
                                        || !mViewPortHandler.isInBoundsLeft(x))
                                    continue;

                                drawValue(c, vals[k / 2], x, y, formatter);
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
     * @formatter
     */
    protected void drawValue(Canvas c, float val, float xPos, float yPos, ValueFormatter formatter) {

        String value = formatter.getFormattedValue(val);
        c.drawText(value, xPos, yPos,
                mValuePaint);
    }

    @Override
    public void drawExtras(Canvas c) {
    }

    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {

        int setCount = mChart.getBarData().getDataSetCount();

        for (int i = 0; i < indices.length; i++) {

            Highlight h = indices[i];
            int index = h.getXIndex();

            int dataSetIndex = h.getDataSetIndex();
            BarDataSet set = mChart.getBarData().getDataSetByIndex(dataSetIndex);

            if (set == null)
                continue;

            Transformer trans = mChart.getTransformer(set.getAxisDependency());

            mHighlightPaint.setColor(set.getHighLightColor());
            mHighlightPaint.setAlpha(set.getHighLightAlpha());

            // check outofbounds
            if (index < mChart.getBarData().getYValCount() && index >= 0
                    && index < (mChart.getXChartMax() * mAnimator.getPhaseX()) / setCount) {

                BarEntry e = mChart.getBarData().getDataSetByIndex(dataSetIndex)
                        .getEntryForXIndex(index);

                if (e == null)
                    continue;

                float groupspace = mChart.getBarData().getGroupSpace();
                boolean isStack = h.getStackIndex() < 0 ? false : true;

                // calculate the correct x-position
                float x = index * setCount + dataSetIndex + groupspace / 2f
                        + groupspace * index;
                float y = isStack ? e.getVals()[h.getStackIndex()]
                        + e.getBelowSum(h.getStackIndex()) : e.getVal();

                // this is where the bar starts
                float from = isStack ? e.getBelowSum(h.getStackIndex()) : 0f;

                prepareBarHighlight(x, y, set.getBarSpace(), from, trans);

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

    public float[] getTransformedValues(Transformer trans, List<BarEntry> entries,
            int dataSetIndex) {
        return trans.generateTransformedValuesBarChart(entries, dataSetIndex,
                mChart.getBarData(),
                mAnimator.getPhaseY());
    }

    protected boolean passesCheck() {
        return mChart.getBarData().getYValCount() < mChart.getMaxVisibleCount()
                * mViewPortHandler.getScaleX();
    }
}
