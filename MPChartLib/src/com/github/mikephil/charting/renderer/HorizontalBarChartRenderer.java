
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Paint.Align;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.BarBuffer;
import com.github.mikephil.charting.buffer.HorizontalBarBuffer;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.List;

/**
 * Renderer for the HorizontalBarChart.
 *
 * @author Philipp Jahoda
 */
public class HorizontalBarChartRenderer extends BarChartRenderer {

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
            IBarDataSet set = barData.getDataSetByIndex(i);
            mBarBuffers[i] = new HorizontalBarBuffer(set.getEntryCount() * 4 * (set.isStacked() ? set.getStackSize() : 1),
                    barData.getGroupSpace(),
                    barData.getDataSetCount(), set.isStacked());
        }
    }

    @Override
    protected void drawDataSet(Canvas c, IBarDataSet dataSet, int index) {

        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        mShadowPaint.setColor(dataSet.getBarShadowColor());
        mBarBorderPaint.setColor(dataSet.getBarBorderColor());
        mBarBorderPaint.setStrokeWidth(Util.convertDpToPixel(dataSet.getBarBorderWidth()));

        final boolean drawBorder = dataSet.getBarBorderWidth() > 0.f;

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        // initialize the buffer
        BarBuffer buffer = mBarBuffers[index];
        buffer.setPhases(phaseX, phaseY);
        buffer.setBarSpace(dataSet.getBarSpace());
        buffer.setDataSet(index);
        buffer.setInverted(mChart.isInverted(dataSet.getAxisDependency()));

        buffer.feed(dataSet);

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
            // is out of bounds, reuse colors.
            mRenderPaint.setColor(dataSet.getColor(j / 4));
            c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                    buffer.buffer[j + 3], mRenderPaint);

            if (drawBorder) {
                c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                        buffer.buffer[j + 3], mBarBorderPaint);
            }
        }
    }

    @Override
    public void drawValues(Canvas c) {
        // if values are drawn
        if (passesCheck()) {

            List<IBarDataSet> dataSets = mChart.getBarData().getDataSets();

            final float valueOffsetPlus = Utils.convertDpToPixel(5f);
            float posOffset = 0f;
            float negOffset = 0f;
            final boolean drawValueAboveBar = mChart.isDrawValueAboveBarEnabled();

            for (int i = 0; i < mChart.getBarData().getDataSetCount(); i++) {

                IBarDataSet dataSet = dataSets.get(i);

                if (!dataSet.isDrawValuesEnabled() || dataSet.getEntryCount() == 0)
                    continue;

                boolean isInverted = mChart.isInverted(dataSet.getAxisDependency());

                // apply the text-styling defined by the DataSet
                applyValueTextStyle(dataSet);
                final float halfTextHeight = Utils.calcTextHeight(mValuePaint, "10") / 2f;

                ValueFormatter formatter = dataSet.getValueFormatter();

                Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

                float[] valuePoints = getTransformedValues(trans, dataSet, i);

                // if only single values are drawn (sum)
                if (!dataSet.isStacked()) {

                    for (int j = 0; j < valuePoints.length * mAnimator.getPhaseX(); j += 2) {

                        if (!mViewPortHandler.isInBoundsTop(valuePoints[j + 1]))
                            break;

                        if (!mViewPortHandler.isInBoundsX(valuePoints[j]))
                            continue;

                        if (!mViewPortHandler.isInBoundsBottom(valuePoints[j + 1]))
                            continue;

                        BarEntry e = dataSet.getEntryForIndex(j / 2);
                        float val = e.getVal();
                        String formattedValue = formatter.getFormattedValue(val, e, i, mViewPortHandler);

                        // calculate the correct offset depending on the draw position of the value
                        float valueTextWidth = Utils.calcTextWidth(mValuePaint, formattedValue);
                        posOffset = (drawValueAboveBar ? valueOffsetPlus : -(valueTextWidth + valueOffsetPlus));
                        negOffset = (drawValueAboveBar ? -(valueTextWidth + valueOffsetPlus) : valueOffsetPlus);

                        if (isInverted) {
                            posOffset = -posOffset - valueTextWidth;
                            negOffset = -negOffset - valueTextWidth;
                        }

                        drawValue(c, formattedValue, valuePoints[j] + (val >= 0 ? posOffset : negOffset),
                                valuePoints[j + 1] + halfTextHeight, dataSet.getValueTextColor(j / 2));
                    }

                    // if each value of a potential stack should be drawn
                } else {

                    for (int j = 0; j < (valuePoints.length - 1) * mAnimator.getPhaseX(); j += 2) {

                        BarEntry e = dataSet.getEntryForIndex(j / 2);

                        float[] vals = e.getVals();

                        // we still draw stacked bars, but there is one
                        // non-stacked
                        // in between
                        if (vals == null) {

                            if (!mViewPortHandler.isInBoundsTop(valuePoints[j + 1]))
                                break;

                            if (!mViewPortHandler.isInBoundsX(valuePoints[j]))
                                continue;

                            if (!mViewPortHandler.isInBoundsBottom(valuePoints[j + 1]))
                                continue;

                            float val = e.getVal();
                            String formattedValue = formatter.getFormattedValue(val, e, i, mViewPortHandler);

                            // calculate the correct offset depending on the draw position of the value
                            float valueTextWidth = Utils.calcTextWidth(mValuePaint, formattedValue);
                            posOffset = (drawValueAboveBar ? valueOffsetPlus : -(valueTextWidth + valueOffsetPlus));
                            negOffset = (drawValueAboveBar ? -(valueTextWidth + valueOffsetPlus) : valueOffsetPlus);

                            if (isInverted) {
                                posOffset = -posOffset - valueTextWidth;
                                negOffset = -negOffset - valueTextWidth;
                            }

                            drawValue(c, formattedValue, valuePoints[j]
                                            + (e.getVal() >= 0 ? posOffset : negOffset),
                                    valuePoints[j + 1] + halfTextHeight, dataSet.getValueTextColor(j / 2));

                        } else {

                            float[] transformed = new float[vals.length * 2];

                            float posY = 0f;
                            float negY = -e.getNegativeSum();

                            for (int k = 0, idx = 0; k < transformed.length; k += 2, idx++) {

                                float value = vals[idx];
                                float y;

                                if (value >= 0f) {
                                    posY += value;
                                    y = posY;
                                } else {
                                    y = negY;
                                    negY -= value;
                                }

                                transformed[k] = y * mAnimator.getPhaseY();
                            }

                            trans.pointValuesToPixel(transformed);

                            for (int k = 0; k < transformed.length; k += 2) {

                                float val = vals[k / 2];
                                String formattedValue = formatter.getFormattedValue(val, e, i, mViewPortHandler);

                                // calculate the correct offset depending on the draw position of the value
                                float valueTextWidth = Utils.calcTextWidth(mValuePaint, formattedValue);
                                posOffset = (drawValueAboveBar ? valueOffsetPlus : -(valueTextWidth + valueOffsetPlus));
                                negOffset = (drawValueAboveBar ? -(valueTextWidth + valueOffsetPlus) : valueOffsetPlus);

                                if (isInverted) {
                                    posOffset = -posOffset - valueTextWidth;
                                    negOffset = -negOffset - valueTextWidth;
                                }

                                float x = transformed[k]
                                        + (val >= 0 ? posOffset : negOffset);
                                float y = valuePoints[j + 1];

                                if (!mViewPortHandler.isInBoundsTop(y))
                                    break;

                                if (!mViewPortHandler.isInBoundsX(x))
                                    continue;

                                if (!mViewPortHandler.isInBoundsBottom(y))
                                    continue;

                                drawValue(c, formattedValue, x, y + halfTextHeight, dataSet.getValueTextColor(j / 2));
                            }
                        }
                    }
                }
            }
        }
    }

    protected void drawValue(Canvas c, String valueText, float x, float y, int color) {
        mValuePaint.setColor(color);
        c.drawText(valueText, x, y, mValuePaint);
    }

    @Override
    protected void prepareBarHighlight(float x, float y1, float y2, float barspaceHalf,
                                       Transformer trans) {

        float top = x - 0.5f + barspaceHalf;
        float bottom = x + 0.5f - barspaceHalf;
        float left = y1;
        float right = y2;

        mBarRect.set(left, top, right, bottom);

        trans.rectValueToPixelHorizontal(mBarRect, mAnimator.getPhaseY());
    }

    @Override
    public float[] getTransformedValues(Transformer trans, IBarDataSet data,
                                        int dataSetIndex) {
        return trans.generateTransformedValuesHorizontalBarChart(data, dataSetIndex,
                mChart.getBarData(), mAnimator.getPhaseY());
    }

    @Override
    protected boolean passesCheck() {
        return mChart.getBarData().getYValCount() < mChart.getMaxVisibleCount()
                * mViewPortHandler.getScaleY();
    }
}
