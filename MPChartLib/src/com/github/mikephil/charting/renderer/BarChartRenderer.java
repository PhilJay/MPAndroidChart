
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.BarBuffer;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
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
            IBarDataSet set = barData.getDataSetByIndex(i);
            mBarBuffers[i] = new BarBuffer(set.getEntryCount() * 4 * (set.isStacked() ? set.getStackSize() : 1),
                    barData.getGroupSpace(),
                    barData.getDataSetCount(), set.isStacked());
        }
    }

    @Override
    public void drawData(Canvas c) {

        BarData barData = mChart.getBarData();

        for (int i = 0; i < barData.getDataSetCount(); i++) {

            IBarDataSet set = barData.getDataSetByIndex(i);

            if (set.isVisible() && set.getEntryCount() > 0) {
                drawDataSet(c, set, i);
            }
        }
    }

    protected void drawDataSet(Canvas c, IBarDataSet dataSet, int index) {

        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        mShadowPaint.setColor(dataSet.getBarShadowColor());

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

        // draw the bar shadow before the values
        if (mChart.isDrawBarShadowEnabled()) {

            for (int j = 0; j < buffer.size(); j += 4) {

                if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2]))
                    continue;

                if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j]))
                    break;

                c.drawRect(buffer.buffer[j], mViewPortHandler.contentTop(),
                        buffer.buffer[j + 2],
                        mViewPortHandler.contentBottom(), mShadowPaint);
            }
        }

        // if multiple colors
        if (dataSet.getColors().size() > 1) {

            for (int j = 0; j < buffer.size(); j += 4) {

                if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2]))
                    continue;

                if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j]))
                    break;

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

                c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                        buffer.buffer[j + 3], mRenderPaint);
            }
        }
    }

    /**
     * Prepares a bar for being highlighted.
     * 
     * @param x the x-position
     * @param y1 the y1-position
     * @param y2 the y2-position
     * @param barspaceHalf the space between bars
     * @param trans
     */
    protected void prepareBarHighlight(float x, float y1, float y2, float barspaceHalf,
            Transformer trans) {

        float barWidth = 0.5f;

        float left = x - barWidth + barspaceHalf;
        float right = x + barWidth - barspaceHalf;
        float top = y1;
        float bottom = y2;

        mBarRect.set(left, top, right, bottom);

        trans.rectValueToPixel(mBarRect, mAnimator.getPhaseY());
    }

    @Override
    public void drawValues(Canvas c) {
        // if values are drawn
        if (passesCheck()) {

            List<IBarDataSet> dataSets = mChart.getBarData().getDataSets();

            final float valueOffsetPlus = Utils.convertDpToPixel(4.5f);
            float posOffset = 0f;
            float negOffset = 0f;
            boolean drawValueAboveBar = mChart.isDrawValueAboveBarEnabled();

            for (int i = 0; i < mChart.getBarData().getDataSetCount(); i++) {

                IBarDataSet dataSet = dataSets.get(i);

                if (!dataSet.isDrawValuesEnabled() || dataSet.getEntryCount() == 0)
                    continue;

                // apply the text-styling defined by the DataSet
                applyValueTextStyle(dataSet);

                boolean isInverted = mChart.isInverted(dataSet.getAxisDependency());

                // calculate the correct offset depending on the draw position of
                // the value
                float valueTextHeight = Utils.calcTextHeight(mValuePaint, "8");
                posOffset = (drawValueAboveBar ? -valueOffsetPlus : valueTextHeight + valueOffsetPlus);
                negOffset = (drawValueAboveBar ? valueTextHeight + valueOffsetPlus : -valueOffsetPlus);

                if (isInverted) {
                    posOffset = -posOffset - valueTextHeight;
                    negOffset = -negOffset - valueTextHeight;
                }

                Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

                float[] valuePoints = getTransformedValues(trans, dataSet, i);

                // if only single values are drawn (sum)
                if (!dataSet.isStacked()) {

                    for (int j = 0; j < valuePoints.length * mAnimator.getPhaseX(); j += 2) {

                        if (!mViewPortHandler.isInBoundsRight(valuePoints[j]))
                            break;

                        if (!mViewPortHandler.isInBoundsY(valuePoints[j + 1])
                                || !mViewPortHandler.isInBoundsLeft(valuePoints[j]))
                            continue;

                        BarEntry entry = dataSet.getEntryForIndex(j / 2);
                        float val = entry.getVal();

                        drawValue(c, dataSet.getValueFormatter(), val, entry, i, valuePoints[j],
                                valuePoints[j + 1] + (val >= 0 ? posOffset : negOffset), dataSet.getValueTextColor(j / 2));
                    }

                    // if we have stacks
                } else {

                    for (int j = 0; j < (valuePoints.length - 1) * mAnimator.getPhaseX(); j += 2) {

                        BarEntry entry = dataSet.getEntryForIndex(j / 2);

                        float[] vals = entry.getVals();

                        // we still draw stacked bars, but there is one
                        // non-stacked
                        // in between
                        if (vals == null) {

                            if (!mViewPortHandler.isInBoundsRight(valuePoints[j]))
                                break;

                            if (!mViewPortHandler.isInBoundsY(valuePoints[j + 1])
                                    || !mViewPortHandler.isInBoundsLeft(valuePoints[j]))
                                continue;

                            drawValue(c, dataSet.getValueFormatter(), entry.getVal(), entry, i, valuePoints[j],
                                    valuePoints[j + 1] + (entry.getVal() >= 0 ? posOffset : negOffset), dataSet.getValueTextColor(j / 2));

                            // draw stack values
                        } else {

                            int color = dataSet.getValueTextColor(j / 2);

                            float[] transformed = new float[vals.length * 2];

                            float posY = 0f;
                            float negY = -entry.getNegativeSum();

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

                                transformed[k + 1] = y * mAnimator.getPhaseY();
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

                                drawValue(c, dataSet.getValueFormatter(), vals[k / 2], entry, i, x, y, color);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {

        int setCount = mChart.getBarData().getDataSetCount();

        for (int i = 0; i < indices.length; i++) {

            Highlight h = indices[i];
            int index = h.getXIndex();

            int dataSetIndex = h.getDataSetIndex();
            IBarDataSet set = mChart.getBarData().getDataSetByIndex(dataSetIndex);

            if (set == null || !set.isHighlightEnabled())
                continue;

            float barspaceHalf = set.getBarSpace() / 2f;
            
            Transformer trans = mChart.getTransformer(set.getAxisDependency());

            mHighlightPaint.setColor(set.getHighLightColor());
            mHighlightPaint.setAlpha(set.getHighLightAlpha());

            // check outofbounds
            if (index >= 0
                    && index < (mChart.getXChartMax() * mAnimator.getPhaseX()) / setCount) {

                BarEntry e = set.getEntryForXIndex(index);

                if (e == null || e.getXIndex() != index)
                    continue;

                float groupspace = mChart.getBarData().getGroupSpace();
                boolean isStack = h.getStackIndex() < 0 ? false : true;

                // calculate the correct x-position
                float x = index * setCount + dataSetIndex + groupspace / 2f
                        + groupspace * index;

                final float y1;
                final float y2;

                if (isStack) {
                    y1 = h.getRange().from;
                    y2 = h.getRange().to;
                } else {
                    y1 = e.getVal();
                    y2 = 0.f;
                }

                prepareBarHighlight(x, y1, y2, barspaceHalf, trans);

                c.drawRect(mBarRect, mHighlightPaint);

                if (mChart.isDrawHighlightArrowEnabled()) {

                    mHighlightPaint.setAlpha(255);

                    // distance between highlight arrow and bar
                    float offsetY = mAnimator.getPhaseY() * 0.07f;

                    float[] values = new float[9];
                    trans.getPixelToValueMatrix().getValues(values);
                    final float xToYRel = Math.abs(values[Matrix.MSCALE_Y] / values[Matrix.MSCALE_X]);

                    final float arrowWidth = set.getBarSpace() / 2.f;
                    final float arrowHeight = arrowWidth * xToYRel;

                    final float yArrow = (y1 > -y2 ? y1 : y1) * mAnimator.getPhaseY();

                    Path arrow = new Path();
                    arrow.moveTo(x + 0.4f, yArrow + offsetY);
                    arrow.lineTo(x + 0.4f + arrowWidth, yArrow + offsetY - arrowHeight);
                    arrow.lineTo(x + 0.4f + arrowWidth, yArrow + offsetY + arrowHeight);

                    trans.pathValueToPixel(arrow);
                    c.drawPath(arrow, mHighlightPaint);
                }
            }
        }
    }

    public float[] getTransformedValues(Transformer trans, IBarDataSet data,
            int dataSetIndex) {
        return trans.generateTransformedValuesBarChart(data, dataSetIndex,
                mChart.getBarData(),
                mAnimator.getPhaseY());
    }

    protected boolean passesCheck() {
        return mChart.getBarData().getYValCount() < mChart.getMaxVisibleCount()
                * mViewPortHandler.getScaleX();
    }

    @Override
    public void drawExtras(Canvas c) { }
}
