
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Style;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.data.BubbleData;
import com.github.mikephil.charting.data.BubbleEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.BubbleDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBubbleDataSet;
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

        for (IBubbleDataSet set : bubbleData.getDataSets()) {

            if (set.isVisible() && set.getEntryCount() > 0)
                drawDataSet(c, set);
        }
    }

    private float[] sizeBuffer = new float[4];
    private float[] pointBuffer = new float[2];

    protected float getShapeSize(float entrySize,
                                 float maxSize,
                                 float reference,
                                 boolean normalizeSize) {
        final float factor = normalizeSize
                ? ((maxSize == 0f) ? 1f : (float) Math.sqrt(entrySize / maxSize))
                : entrySize;
        final float shapeSize = reference * factor;
        return shapeSize;
    }

    protected void drawDataSet(Canvas c, IBubbleDataSet dataSet) {

        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        float phaseX = Math.max(0.f, Math.min(1.f, mAnimator.getPhaseX()));
        float phaseY = mAnimator.getPhaseY();

        BubbleEntry entryFrom = dataSet.getEntryForXIndex(mMinX);
        BubbleEntry entryTo = dataSet.getEntryForXIndex(mMaxX);

        int minx = Math.max(dataSet.getEntryIndex(entryFrom), 0);
        int maxx = Math.min(dataSet.getEntryIndex(entryTo) + 1, dataSet.getEntryCount());

        sizeBuffer[0] = 0f;
        sizeBuffer[2] = 1f;

        trans.pointValuesToPixel(sizeBuffer);

        boolean normalizeSize = dataSet.isNormalizeSizeEnabled();

        // calcualte the full width of 1 step on the x-axis
        final float maxBubbleWidth = Math.abs(sizeBuffer[2] - sizeBuffer[0]);
        final float maxBubbleHeight = Math.abs(mViewPortHandler.contentBottom() - mViewPortHandler.contentTop());
        final float referenceSize = Math.min(maxBubbleHeight, maxBubbleWidth);

        for (int j = minx; j < maxx; j++) {

            final BubbleEntry entry = dataSet.getEntryForIndex(j);

            pointBuffer[0] = (float) (entry.getX() - minx) * phaseX + (float) minx;
            pointBuffer[1] = (float) (entry.getY()) * phaseY;
            trans.pointValuesToPixel(pointBuffer);

            float shapeHalf = getShapeSize(entry.getSize(), dataSet.getMaxSize(), referenceSize, normalizeSize) / 2f;

            if (!mViewPortHandler.isInBoundsTop(pointBuffer[1] + shapeHalf)
                    || !mViewPortHandler.isInBoundsBottom(pointBuffer[1] - shapeHalf))
                continue;

            if (!mViewPortHandler.isInBoundsLeft(pointBuffer[0] + shapeHalf))
                continue;

            if (!mViewPortHandler.isInBoundsRight(pointBuffer[0] - shapeHalf))
                break;

            final int color = dataSet.getColor(entry.getX());

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

            final List<IBubbleDataSet> dataSets = bubbleData.getDataSets();

            float lineHeight = Utils.calcTextHeight(mValuePaint, "1");

            for (int i = 0; i < dataSets.size(); i++) {

                IBubbleDataSet dataSet = dataSets.get(i);

                if (!dataSet.isDrawValuesEnabled() || dataSet.getEntryCount() == 0)
                    continue;

                // apply the text-styling defined by the DataSet
                applyValueTextStyle(dataSet);

                final float phaseX = Math.max(0.f, Math.min(1.f, mAnimator.getPhaseX()));
                final float phaseY = mAnimator.getPhaseY();

                BubbleEntry entryFrom = dataSet.getEntryForXIndex(mMinX);
                BubbleEntry entryTo = dataSet.getEntryForXIndex(mMaxX);

                int minx = dataSet.getEntryIndex(entryFrom);
                int maxx = Math.min(dataSet.getEntryIndex(entryTo) + 1, dataSet.getEntryCount());

                final float[] positions = mChart.getTransformer(dataSet.getAxisDependency())
                        .generateTransformedValuesBubble(dataSet, phaseX, phaseY, minx, maxx);

                final float alpha = phaseX == 1 ? phaseY : phaseX;

                for (int j = 0; j < positions.length; j += 2) {

                    int valueTextColor = dataSet.getValueTextColor(j / 2 + minx);
                    valueTextColor = Color.argb(Math.round(255.f * alpha), Color.red(valueTextColor),
                            Color.green(valueTextColor), Color.blue(valueTextColor));

                    float x = positions[j];
                    float y = positions[j + 1];

                    if (!mViewPortHandler.isInBoundsRight(x))
                        break;

                    if ((!mViewPortHandler.isInBoundsLeft(x) || !mViewPortHandler.isInBoundsY(y)))
                        continue;

                    BubbleEntry entry = dataSet.getEntryForIndex(j / 2 + minx);

                    drawValue(c, dataSet.getValueFormatter(), entry.getSize(), entry, i, x,
                            y + (0.5f * lineHeight), valueTextColor);
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

        float phaseX = Math.max(0.f, Math.min(1.f, mAnimator.getPhaseX()));
        float phaseY = mAnimator.getPhaseY();

        for (Highlight high : indices) {

            final int minDataSetIndex = high.getDataSetIndex() == -1
                    ? 0
                    : high.getDataSetIndex();
            final int maxDataSetIndex = high.getDataSetIndex() == -1
                    ? bubbleData.getDataSetCount()
                    : (high.getDataSetIndex() + 1);
            if (maxDataSetIndex - minDataSetIndex < 1) continue;

            for (int dataSetIndex = minDataSetIndex;
                 dataSetIndex < maxDataSetIndex;
                 dataSetIndex++) {

                IBubbleDataSet dataSet = bubbleData.getDataSetByIndex(dataSetIndex);

                if (dataSet == null || !dataSet.isHighlightEnabled())
                    continue;

                final BubbleEntry entry = (BubbleEntry) bubbleData.getEntryForHighlight(high);
                if (entry == null || entry.getX() != high.getXIndex())
                    continue;

                BubbleEntry entryFrom = dataSet.getEntryForXIndex(mMinX);
                BubbleEntry entryTo = dataSet.getEntryForXIndex(mMaxX);

                int minx = dataSet.getEntryIndex(entryFrom);
                int maxx = Math.min(dataSet.getEntryIndex(entryTo) + 1, dataSet.getEntryCount());

                Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

                sizeBuffer[0] = 0f;
                sizeBuffer[2] = 1f;

                trans.pointValuesToPixel(sizeBuffer);

                boolean normalizeSize = dataSet.isNormalizeSizeEnabled();

                // calcualte the full width of 1 step on the x-axis
                final float maxBubbleWidth = Math.abs(sizeBuffer[2] - sizeBuffer[0]);
                final float maxBubbleHeight = Math.abs(
                        mViewPortHandler.contentBottom() - mViewPortHandler.contentTop());
                final float referenceSize = Math.min(maxBubbleHeight, maxBubbleWidth);

                pointBuffer[0] = (float) (entry.getX() - minx) * phaseX + (float) minx;
                pointBuffer[1] = (float) (entry.getY()) * phaseY;
                trans.pointValuesToPixel(pointBuffer);

                float shapeHalf = getShapeSize(entry.getSize(),
                        dataSet.getMaxSize(),
                        referenceSize,
                        normalizeSize) / 2f;

                if (!mViewPortHandler.isInBoundsTop(pointBuffer[1] + shapeHalf)
                        || !mViewPortHandler.isInBoundsBottom(pointBuffer[1] - shapeHalf))
                    continue;

                if (!mViewPortHandler.isInBoundsLeft(pointBuffer[0] + shapeHalf))
                    continue;

                if (!mViewPortHandler.isInBoundsRight(pointBuffer[0] - shapeHalf))
                    break;

                if (high.getXIndex() < minx || high.getXIndex() >= maxx)
                    continue;

                final int originalColor = dataSet.getColor(entry.getX());

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
}
