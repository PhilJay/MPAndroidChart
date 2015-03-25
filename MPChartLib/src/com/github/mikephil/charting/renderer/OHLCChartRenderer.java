package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.OHLCData;
import com.github.mikephil.charting.data.OHLCDataSet;
import com.github.mikephil.charting.interfaces.OHLCDataProvider;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.List;

public class OHLCChartRenderer extends DataRenderer {

    protected OHLCDataProvider mChart;

    public OHLCChartRenderer(OHLCDataProvider chart, ChartAnimator animator,
                             ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        mChart = chart;
    }

    @Override
    public void initBuffers() {

    }

    @Override
    public void drawData(Canvas c) {

        OHLCData ohlcData = mChart.getOHLCData();

        for (OHLCDataSet set : ohlcData.getDataSets()) {

            if (set.isVisible())
                drawDataSet(c, set);
        }
    }

    protected void drawDataSet(Canvas c, OHLCDataSet dataSet) {

        // pre allocate
        float[] shadowPoints = new float[4];
        float[] bodyPoints = new float[4];

        List<CandleEntry> entries = dataSet.getYVals();

        mRenderPaint.setStrokeWidth(dataSet.getShadowWidth());

        for (int j = 0; j < entries.size() * mAnimator.getPhaseX(); j++) {

            // get the entry
            CandleEntry e = entries.get(j);

            Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

            // transform the entries values for shadow and body
            transformShadow(shadowPoints, e, trans);
            transformBody(bodyPoints, e, dataSet.getBodySpace(), trans);

            float xShadow = shadowPoints[0];
            float leftBody = bodyPoints[0];
            float rightBody = bodyPoints[2];

            float high = shadowPoints[1];
            float low = shadowPoints[3];

            float open = bodyPoints[1];
            float close = bodyPoints[3];

            if (!mViewPortHandler.isInBoundsRight(leftBody))
                break;

            // make sure the lines don't do shitty things outside
            // bounds
            if (j != 0 && !mViewPortHandler.isInBoundsLeft(rightBody)
                    && !mViewPortHandler.isInBoundsTop(low)
                    && !mViewPortHandler.isInBoundsBottom(high))
                continue;

            // set the colors for positive and negative sticks, depending on open to close ratio

            if(close > open) {
                mRenderPaint.setColor(dataSet.getColor(1));
            } else {
                mRenderPaint.setColor(dataSet.getColor(0));
            }

            // draw the shadow
            c.drawLine(xShadow, low, xShadow, high, mRenderPaint);

            // draw the close line
            c.drawLine(leftBody, close, xShadow, close, mRenderPaint);

            // draw the open line
            c.drawLine(xShadow, open, rightBody, open, mRenderPaint);
        }
    }

    /**
     * Transforms the values of an entry in order to draw the candle-body.
     *
     * @param bodyPoints
     * @param e
     * @param bodySpace
     */
    private void transformBody(float[] bodyPoints, CandleEntry e, float bodySpace, Transformer trans) {

        float phase = mAnimator.getPhaseY();

        bodyPoints[0] = e.getXIndex() - 0.5f + bodySpace;
        bodyPoints[1] = e.getClose() * phase;
        bodyPoints[2] = e.getXIndex() + 0.5f - bodySpace;
        bodyPoints[3] = e.getOpen() * phase;

        trans.pointValuesToPixel(bodyPoints);
    }

    /**
     * Transforms the values of an entry in order to draw the candle-shadow.
     *
     * @param shadowPoints
     * @param e
     */
    private void transformShadow(float[] shadowPoints, CandleEntry e, Transformer trans) {

        float phase = mAnimator.getPhaseY();

        shadowPoints[0] = e.getXIndex();
        shadowPoints[1] = e.getHigh() * phase;
        shadowPoints[2] = e.getXIndex();
        shadowPoints[3] = e.getLow() * phase;

        trans.pointValuesToPixel(shadowPoints);
    }

    @Override
    public void drawValues(Canvas c) {

        // if values are drawn
        if (mChart.getOHLCData().getYValCount() < mChart.getMaxVisibleCount()
                * mViewPortHandler.getScaleX()) {

            List<OHLCDataSet> dataSets = mChart.getOHLCData().getDataSets();

            for (int i = 0; i < dataSets.size(); i++) {

                CandleDataSet dataSet = dataSets.get(i);

                if (!dataSet.isDrawValuesEnabled())
                    continue;

                // apply the text-styling defined by the DataSet
                applyValueTextStyle(dataSet);

                Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

                List<CandleEntry> entries = dataSet.getYVals();

                float[] positions = trans.generateTransformedValuesCandle(
                        entries, mAnimator.getPhaseY());

                float yOffset = Utils.convertDpToPixel(5f);

                for (int j = 0; j < positions.length * mAnimator.getPhaseX(); j += 2) {

                    float x = positions[j];
                    float y = positions[j + 1];

                    if (!mViewPortHandler.isInBoundsRight(x))
                        break;

                    if (!mViewPortHandler.isInBoundsLeft(x) || !mViewPortHandler.isInBoundsY(y))
                        continue;

                    float val = entries.get(j / 2).getHigh();

                    c.drawText(dataSet.getValueFormatter().getFormattedValue(val), x, y - yOffset,
                            mValuePaint);
                }
            }
        }
    }

    @Override
    public void drawExtras(Canvas c) {
    }

    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {

        for (int i = 0; i < indices.length; i++) {

            int xIndex = indices[i].getXIndex(); // get the
            // x-position

            CandleDataSet set = mChart.getOHLCData().getDataSetByIndex(
                    indices[i].getDataSetIndex());

            if (set == null)
                continue;

            mHighlightPaint.setColor(set.getHighLightColor());

            CandleEntry e = set.getEntryForXIndex(xIndex);

            if (e == null)
                continue;

            float low = e.getLow() * mAnimator.getPhaseY();
            float high = e.getHigh() * mAnimator.getPhaseY();

            float min = mChart.getYChartMin();
            float max = mChart.getYChartMax();

            float[] vertPts = new float[] {
                    xIndex - 0.5f, max, xIndex - 0.5f, min, xIndex + 0.5f, max, xIndex + 0.5f,
                    min
            };

            float[] horPts = new float[] {
                    0, low, mChart.getXChartMax(), low, 0, high, mChart.getXChartMax(), high
            };

            mChart.getTransformer(set.getAxisDependency()).pointValuesToPixel(vertPts);
            mChart.getTransformer(set.getAxisDependency()).pointValuesToPixel(horPts);

            // draw the vertical highlight lines
            c.drawLines(vertPts, mHighlightPaint);

            // draw the horizontal highlight lines
            c.drawLines(horPts, mHighlightPaint);
        }
    }
}
