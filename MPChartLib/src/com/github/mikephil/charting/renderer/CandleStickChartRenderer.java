
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.CandleBodyBuffer;
import com.github.mikephil.charting.buffer.CandleShadowBuffer;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.CandleDataProvider;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.List;

public class CandleStickChartRenderer extends DataRenderer {

    protected CandleDataProvider mChart;

    private CandleShadowBuffer[] mShadowBuffers;
    private CandleBodyBuffer[] mBodyBuffers;

    public CandleStickChartRenderer(CandleDataProvider chart, ChartAnimator animator,
            ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        mChart = chart;
    }

    @Override
    public void initBuffers() {
        CandleData candleData = mChart.getCandleData();
        mShadowBuffers = new CandleShadowBuffer[candleData.getDataSetCount()];
        mBodyBuffers = new CandleBodyBuffer[candleData.getDataSetCount()];

        for (int i = 0; i < mShadowBuffers.length; i++) {
            CandleDataSet set = candleData.getDataSetByIndex(i);
            mShadowBuffers[i] = new CandleShadowBuffer(set.getValueCount() * 4);
            mBodyBuffers[i] = new CandleBodyBuffer(set.getValueCount() * 4);
        }
    }

    @Override
    public void drawData(Canvas c) {

        CandleData candleData = mChart.getCandleData();

        for (CandleDataSet set : candleData.getDataSets()) {

            if (set.isVisible())
                drawDataSet(c, set);
        }
    }

    protected void drawDataSet(Canvas c, CandleDataSet dataSet) {

        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        int dataSetIndex = mChart.getCandleData().getIndexOfDataSet(dataSet);

        List<CandleEntry> entries = dataSet.getYVals();

        Entry entryFrom = dataSet.getEntryForXIndex(mMinX);
        Entry entryTo = dataSet.getEntryForXIndex(mMaxX);

        int minx = Math.max(dataSet.getEntryPosition(entryFrom), 0);
        int maxx = Math.min(dataSet.getEntryPosition(entryTo) + 1, entries.size());

        int range = (maxx - minx) * 4;
        int to = (int)Math.ceil((maxx - minx) * phaseX + minx);

        CandleShadowBuffer shadowBuffer = mShadowBuffers[dataSetIndex];
        shadowBuffer.setPhases(phaseX, phaseY);
        shadowBuffer.limitFrom(minx);
        shadowBuffer.limitTo(maxx);
        shadowBuffer.feed(entries);

        trans.pointValuesToPixel(shadowBuffer.buffer);

        mRenderPaint.setStyle(Paint.Style.STROKE);

        // If not set, use default functionality for backward compatibility
        if (dataSet.getShadowColor() == ColorTemplate.COLOR_NONE) {
            mRenderPaint.setColor(dataSet.getColor());
        } else {
            mRenderPaint.setColor(dataSet.getShadowColor());
        }

        mRenderPaint.setStrokeWidth(dataSet.getShadowWidth());

        // draw the shadow
        c.drawLines(shadowBuffer.buffer, 0, range, mRenderPaint);

        CandleBodyBuffer bodyBuffer = mBodyBuffers[dataSetIndex];
        bodyBuffer.setBodySpace(dataSet.getBodySpace());
        bodyBuffer.setPhases(phaseX, phaseY);
        bodyBuffer.limitFrom(minx);
        bodyBuffer.limitTo(maxx);
        bodyBuffer.feed(entries);

        trans.pointValuesToPixel(bodyBuffer.buffer);

        // draw the body
        for (int j = 0; j < range; j += 4) {

            // get the entry
            CandleEntry e = entries.get(j / 4 + minx);

            if (!fitsBounds(e.getXIndex(), mMinX, to))
                continue;

            float leftBody = bodyBuffer.buffer[j];
            float open = bodyBuffer.buffer[j + 1];
            float rightBody = bodyBuffer.buffer[j + 2];
            float close = bodyBuffer.buffer[j + 3];

            // draw body differently for increasing and decreasing entry
            if (open > close) { // decreasing

                if (dataSet.getDecreasingColor() == ColorTemplate.COLOR_NONE) {
                    mRenderPaint.setColor(dataSet.getColor(j / 4 + minx));
                } else {
                    mRenderPaint.setColor(dataSet.getDecreasingColor());
                }

                mRenderPaint.setStyle(dataSet.getDecreasingPaintStyle());
                // draw the body
                c.drawRect(leftBody, close, rightBody, open, mRenderPaint);

            } else if(open < close) {

                if (dataSet.getIncreasingColor() == ColorTemplate.COLOR_NONE) {
                    mRenderPaint.setColor(dataSet.getColor(j / 4 + minx));
                } else {
                    mRenderPaint.setColor(dataSet.getIncreasingColor());
                }

                mRenderPaint.setStyle(dataSet.getIncreasingPaintStyle());
                // draw the body
                c.drawRect(leftBody, open, rightBody, close, mRenderPaint);
            } else { // equal values
                
                mRenderPaint.setColor(Color.BLACK);
                mRenderPaint.setStyle(Paint.Style.STROKE);
                c.drawLine(leftBody, open, rightBody, close, mRenderPaint);
            }
        }
    }

    // /**
    // * Transforms the values of an entry in order to draw the candle-body.
    // *
    // * @param bodyPoints
    // * @param e
    // * @param bodySpace
    // */
    // private void transformBody(float[] bodyPoints, CandleEntry e, float
    // bodySpace, Transformer trans) {
    //
    // float phase = mAnimator.getPhaseY();
    //
    // bodyPoints[0] = e.getXIndex() - 0.5f + bodySpace;
    // bodyPoints[1] = e.getClose() * phase;
    // bodyPoints[2] = e.getXIndex() + 0.5f - bodySpace;
    // bodyPoints[3] = e.getOpen() * phase;
    //
    // trans.pointValuesToPixel(bodyPoints);
    // }
    //
    // /**
    // * Transforms the values of an entry in order to draw the candle-shadow.
    // *
    // * @param shadowPoints
    // * @param e
    // */
    // private void transformShadow(float[] shadowPoints, CandleEntry e,
    // Transformer trans) {
    //
    // float phase = mAnimator.getPhaseY();
    //
    // shadowPoints[0] = e.getXIndex();
    // shadowPoints[1] = e.getHigh() * phase;
    // shadowPoints[2] = e.getXIndex();
    // shadowPoints[3] = e.getLow() * phase;
    //
    // trans.pointValuesToPixel(shadowPoints);
    // }

    @Override
    public void drawValues(Canvas c) {

        // if values are drawn
        if (mChart.getCandleData().getYValCount() < mChart.getMaxVisibleCount()
                * mViewPortHandler.getScaleX()) {

            List<CandleDataSet> dataSets = mChart.getCandleData().getDataSets();

            for (int i = 0; i < dataSets.size(); i++) {

                CandleDataSet dataSet = dataSets.get(i);

                if (!dataSet.isDrawValuesEnabled())
                    continue;

                // apply the text-styling defined by the DataSet
                applyValueTextStyle(dataSet);

                Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

                List<CandleEntry> entries = dataSet.getYVals();

                Entry entryFrom = dataSet.getEntryForXIndex(mMinX);
                Entry entryTo = dataSet.getEntryForXIndex(mMaxX);

                int minx = Math.max(dataSet.getEntryPosition(entryFrom), 0);
                int maxx = Math.min(dataSet.getEntryPosition(entryTo) + 1, entries.size());

                float[] positions = trans.generateTransformedValuesCandle(
                        entries, mAnimator.getPhaseX(), mAnimator.getPhaseY(), minx, maxx);

                float yOffset = Utils.convertDpToPixel(5f);

                for (int j = 0; j < positions.length; j += 2) {

                    float x = positions[j];
                    float y = positions[j + 1];

                    if (!mViewPortHandler.isInBoundsRight(x))
                        break;

                    if (!mViewPortHandler.isInBoundsLeft(x) || !mViewPortHandler.isInBoundsY(y))
                        continue;

                    float val = entries.get(j / 2 + minx).getHigh();

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

            CandleDataSet set = mChart.getCandleData().getDataSetByIndex(
                    indices[i].getDataSetIndex());

            if (set == null || !set.isHighlightEnabled())
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
                    mChart.getXChartMin(), low, mChart.getXChartMax(), low, mChart.getXChartMin(), high, mChart.getXChartMax(), high
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
