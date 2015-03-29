
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Paint.Align;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.BarBuffer;
import com.github.mikephil.charting.buffer.HorizontalBarBuffer;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.BarDataProvider;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.List;

/**
 * Renderer for the HorizontalBarChart.
 * 
 * @author Philipp Jahoda
 */
public class HorizontalBarChartRenderer extends BarChartRenderer {

    private float mYOffset = 0f;

    public HorizontalBarChartRenderer(BarDataProvider chart, ChartAnimator animator,
            ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);

        mValuePaint.setTextAlign(Align.LEFT);
    }

    @Override
    public void initBuffers() {

        BarData barData = mChart.getBarData();
        mBarBuffers = new HorizontalBarBuffer[barData.getDataSetCount()];

        for (int i = 0; i < mBarBuffers.length; i++) {
            BarDataSet set = barData.getDataSetByIndex(i);
            mBarBuffers[i] = new HorizontalBarBuffer(set.getValueCount() * 4 * set.getStackSize(),
                    barData.getGroupSpace(),
                    barData.getDataSetCount(), set.isStacked());
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

        for (int j = 0; j < buffer.size(); j += 4) {

            if (!mViewPortHandler.isInBoundsTop(buffer.buffer[j + 3]))
                break;

            if (!mViewPortHandler.isInBoundsBottom(buffer.buffer[j + 1]))
                continue;

            if (mChart.isDrawBarShadowEnabled()) {
                c.drawRect(mViewPortHandler.contentLeft(), buffer.buffer[j + 1],
                        mViewPortHandler.contentRight(),
                        buffer.buffer[j + 3], mShadowPaint);
            }

            // Set the color for the currently drawn value. If the index
            // is
            // out of bounds, reuse colors.
            mRenderPaint.setColor(dataSet.getColor(j / 4));
            c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                    buffer.buffer[j + 3], mRenderPaint);
        }
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
            negOffset = drawValueAboveBar ? -Utils.convertDpToPixel(5) : Utils.convertDpToPixel(5);
            posOffset = drawValueAboveBar ? Utils.convertDpToPixel(5) : -Utils.convertDpToPixel(5);

            if (drawValueAboveBar)
                mValuePaint.setTextAlign(Align.LEFT);
            else
                mValuePaint.setTextAlign(Align.RIGHT);

            for (int i = 0; i < mChart.getBarData().getDataSetCount(); i++) {

                BarDataSet dataSet = dataSets.get(i);

                if (!dataSet.isDrawValuesEnabled())
                    continue;

                // apply the text-styling defined by the DataSet
                applyValueTextStyle(dataSet);

                mYOffset = Utils.calcTextHeight(mValuePaint, "10") / 2f;

                ValueFormatter formatter = dataSet.getValueFormatter();

                Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

                List<BarEntry> entries = dataSet.getYVals();

                float[] valuePoints = getTransformedValues(trans, entries, i);

                // if only single values are drawn (sum)
                if (!mChart.isDrawValuesForWholeStackEnabled()) {

                    for (int j = 0; j < valuePoints.length * mAnimator.getPhaseX(); j += 2) {

                        if (!mViewPortHandler.isInBoundsX(valuePoints[j]))
                            continue;

                        if (!mViewPortHandler.isInBoundsTop(valuePoints[j + 1]))
                            break;

                        if (!mViewPortHandler.isInBoundsBottom(valuePoints[j + 1]))
                            continue;

                        float val = entries.get(j / 2).getVal();

                        drawValue(c, val, valuePoints[j] + (val >= 0 ? posOffset : negOffset),
                                valuePoints[j + 1], formatter);
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

                            if (!mViewPortHandler.isInBoundsX(valuePoints[j]))
                                continue;

                            if (!mViewPortHandler.isInBoundsTop(valuePoints[j + 1]))
                                break;

                            if (!mViewPortHandler.isInBoundsBottom(valuePoints[j + 1]))
                                continue;

                            drawValue(c, e.getVal(), valuePoints[j]
                                    + (e.getVal() >= 0 ? posOffset : negOffset),
                                    valuePoints[j + 1],
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

                                float x = transformed[k]
                                        + (vals[k / 2] >= 0 ? posOffset : negOffset);
                                float y = valuePoints[j + 1];

                                if (!mViewPortHandler.isInBoundsX(x))
                                    continue;

                                if (!mViewPortHandler.isInBoundsTop(y))
                                    break;

                                if (!mViewPortHandler.isInBoundsBottom(y))
                                    continue;

                                drawValue(c, vals[k / 2], x, y, formatter);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void prepareBarHighlight(float x, float y, float barspaceHalf, float from,
            Transformer trans) {

        float top = x - 0.5f + barspaceHalf;
        float bottom = x + 0.5f - barspaceHalf;
        float left = y >= from ? y : from;
        float right = y <= from ? y : from;

        mBarRect.set(left, top, right, bottom);

        trans.rectValueToPixelHorizontal(mBarRect, mAnimator.getPhaseY());
    }

    @Override
    public float[] getTransformedValues(Transformer trans, List<BarEntry> entries,
            int dataSetIndex) {
        return trans.generateTransformedValuesHorizontalBarChart(entries, dataSetIndex,
                mChart.getBarData(), mAnimator.getPhaseY());
    }

    @Override
    protected void drawValue(Canvas c, float val, float xPos, float yPos, ValueFormatter formatter) {
        super.drawValue(c, val, xPos, yPos + mYOffset, formatter);
    }

    @Override
    protected boolean passesCheck() {
        return mChart.getBarData().getYValCount() < mChart.getMaxVisibleCount()
                * mViewPortHandler.getScaleY();
    }
}
