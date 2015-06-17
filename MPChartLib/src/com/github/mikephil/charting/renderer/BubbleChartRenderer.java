
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Style;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.data.BubbleData;
import com.github.mikephil.charting.data.BubbleDataSet;
import com.github.mikephil.charting.data.BubbleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.BubbleDataProvider;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.List;

/**
 * Bubble chart implementation: Copyright 2015 Pierre-Marc Airoldi Licensed
 * under Apache License 2.0 Ported by Daniel Cohen Gindi
 */
public class BubbleChartRenderer extends DataRenderer {

    protected BubbleDataProvider mChart;

    public BubbleChartRenderer(BubbleDataProvider chart, ChartAnimator animator,
            ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        mChart = chart;

        mRenderPaint.setStyle(Style.FILL);

        mHighlightPaint.setStyle(Style.STROKE);
        mHighlightPaint.setStrokeWidth(Utils.convertDpToPixel(1.5f));
    }

    @Override
    public void initBuffers() {

    }

    @Override
    public void drawData(Canvas c) {

        BubbleData bubbleData = mChart.getBubbleData();

        for (BubbleDataSet set : bubbleData.getDataSets()) {

            if (set.isVisible())
                drawDataSet(c, set);
        }
    }

    private float[] sizeBuffer = new float[4];
    private float[] pointBuffer = new float[2];

    protected float getShapeSize(float entrySize, float maxSize, float reference) {
        final float factor = (maxSize == 0f) ? 1f : (float) Math.sqrt(entrySize / maxSize);
        final float shapeSize = reference * factor;
        return shapeSize;
    }

    protected void drawDataSet(Canvas c, BubbleDataSet dataSet) {

        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        List<BubbleEntry> entries = dataSet.getYVals();

        Entry entryFrom = dataSet.getEntryForXIndex(mMinX);
        Entry entryTo = dataSet.getEntryForXIndex(mMaxX);

        int minx = Math.max(dataSet.getEntryPosition(entryFrom), 0);
        int maxx = Math.min(dataSet.getEntryPosition(entryTo) + 1, entries.size());

        sizeBuffer[0] = 0f;
        sizeBuffer[2] = 1f;

        trans.pointValuesToPixel(sizeBuffer);

        // calcualte the full width of 1 step on the x-axis
        final float maxBubbleWidth = Math.abs(sizeBuffer[2] - sizeBuffer[0]);
        final float maxBubbleHeight = Math.abs(mViewPortHandler.contentBottom() - mViewPortHandler.contentTop());
        final float referenceSize = Math.min(maxBubbleHeight, maxBubbleWidth);

        for (int j = minx; j < maxx; j++) {

            final BubbleEntry entry = entries.get(j);

            pointBuffer[0] = (float) (entry.getXIndex() - minx) * phaseX + (float) minx;
            pointBuffer[1] = (float) (entry.getVal()) * phaseY;
            trans.pointValuesToPixel(pointBuffer);

            float shapeHalf = getShapeSize(entry.getSize(), dataSet.getMaxSize(), referenceSize) / 2f;

            if (!mViewPortHandler.isInBoundsTop(pointBuffer[1] + shapeHalf)
                    || !mViewPortHandler.isInBoundsBottom(pointBuffer[1] - shapeHalf))
                continue;

            if (!mViewPortHandler.isInBoundsLeft(pointBuffer[0] + shapeHalf))
                continue;

            if (!mViewPortHandler.isInBoundsRight(pointBuffer[0] - shapeHalf))
                break;

            final int color = dataSet.getColor(entry.getXIndex());

            mRenderPaint.setColor(color);
            c.drawCircle(pointBuffer[0], pointBuffer[1], shapeHalf, mRenderPaint);
        }
    }

    @Override
    public void drawValues(Canvas c) {

        BubbleData bubbleData = mChart.getBubbleData();

        if (bubbleData == null)
            return;

        // if values are drawn
        if (bubbleData.getYValCount() < (int) (Math.ceil((float) (mChart.getMaxVisibleCount())
                * mViewPortHandler.getScaleX()))) {

            final List<BubbleDataSet> dataSets = bubbleData.getDataSets();

            float lineHeight = Utils.calcTextHeight(mValuePaint, "1");

            for (BubbleDataSet dataSet : dataSets) {

                if (!dataSet.isDrawValuesEnabled())
                    continue;

                // apply the text-styling defined by the DataSet
                applyValueTextStyle(dataSet);

                final float phaseX = mAnimator.getPhaseX();
                final float phaseY = mAnimator.getPhaseY();

                final float alpha = phaseX == 1 ? phaseY : phaseX;
                int valueTextColor = dataSet.getValueTextColor();
                valueTextColor = Color.argb(Math.round(255.f * alpha), Color.red(valueTextColor),
                        Color.green(valueTextColor), Color.blue(valueTextColor));

                mValuePaint.setColor(valueTextColor);

                final List<BubbleEntry> entries = dataSet.getYVals();

                Entry entryFrom = dataSet.getEntryForXIndex(mMinX);
                Entry entryTo = dataSet.getEntryForXIndex(mMaxX);

                int minx = dataSet.getEntryPosition(entryFrom);
                int maxx = Math.min(dataSet.getEntryPosition(entryTo) + 1, dataSet.getEntryCount());

                final float[] positions = mChart.getTransformer(dataSet.getAxisDependency())
                        .generateTransformedValuesBubble(entries, phaseX, phaseY, minx, maxx);

                for (int j = 0; j < positions.length; j += 2) {

                    float x = positions[j];
                    float y = positions[j + 1];

                    if (!mViewPortHandler.isInBoundsRight(x))
                        break;

                    if ((!mViewPortHandler.isInBoundsLeft(x) || !mViewPortHandler.isInBoundsY(y)))
                        continue;

                    final BubbleEntry entry = entries.get(j / 2 + minx);

                    final float val = entry.getSize();

                    c.drawText(dataSet.getValueFormatter().getFormattedValue(val),
                            x,
                            y + (0.5f * lineHeight),
                            mValuePaint);
                }
            }
        }

    }

    @Override
    public void drawExtras(Canvas c) {
    }

    private float[] _hsvBuffer = new float[3];

    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {

        BubbleData bubbleData = mChart.getBubbleData();

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        for (Highlight indice : indices) {

            BubbleDataSet dataSet = bubbleData.getDataSetByIndex(indice.getDataSetIndex());

            if (dataSet == null || !dataSet.isHighlightEnabled())
                continue;

            Entry entryFrom = dataSet.getEntryForXIndex(mMinX);
            Entry entryTo = dataSet.getEntryForXIndex(mMaxX);

            int minx = dataSet.getEntryPosition(entryFrom);
            int maxx = Math.min(dataSet.getEntryPosition(entryTo) + 1, dataSet.getEntryCount());

            final BubbleEntry entry = (BubbleEntry) bubbleData.getEntryForHighlight(indice);
            if (entry == null || entry.getXIndex() != indice.getXIndex())
                continue;

            Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());
            
            sizeBuffer[0] = 0f;
            sizeBuffer[2] = 1f;

            trans.pointValuesToPixel(sizeBuffer);
            
            // calcualte the full width of 1 step on the x-axis
            final float maxBubbleWidth = Math.abs(sizeBuffer[2] - sizeBuffer[0]);
            final float maxBubbleHeight = Math.abs(mViewPortHandler.contentBottom() - mViewPortHandler.contentTop());
            final float referenceSize = Math.min(maxBubbleHeight, maxBubbleWidth);

            pointBuffer[0] = (float) (entry.getXIndex() - minx) * phaseX + (float) minx;
            pointBuffer[1] = (float) (entry.getVal()) * phaseY;
            trans.pointValuesToPixel(pointBuffer);

            float shapeHalf = getShapeSize(entry.getSize(), dataSet.getMaxSize(), referenceSize) / 2f;

            if (!mViewPortHandler.isInBoundsTop(pointBuffer[1] + shapeHalf)
                    || !mViewPortHandler.isInBoundsBottom(pointBuffer[1] - shapeHalf))
                continue;

            if (!mViewPortHandler.isInBoundsLeft(pointBuffer[0] + shapeHalf))
                continue;

            if (!mViewPortHandler.isInBoundsRight(pointBuffer[0] - shapeHalf))
                break;

            if (indice.getXIndex() < minx || indice.getXIndex() >= maxx)
                continue;

            final int originalColor = dataSet.getColor(entry.getXIndex());

            Color.RGBToHSV(Color.red(originalColor), Color.green(originalColor),
                    Color.blue(originalColor), _hsvBuffer);
            _hsvBuffer[2] *= 0.5f;
            final int color = Color.HSVToColor(Color.alpha(originalColor), _hsvBuffer);

            mHighlightPaint.setColor(color);
            mHighlightPaint.setStrokeWidth(dataSet.getHighlightCircleWidth());
            c.drawCircle(pointBuffer[0], pointBuffer[1], shapeHalf, mHighlightPaint);
        }
    }
}
