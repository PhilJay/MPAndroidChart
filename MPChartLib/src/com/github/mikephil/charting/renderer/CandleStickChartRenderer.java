
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.CandleBodyBuffer;
import com.github.mikephil.charting.buffer.CandleShadowBuffer;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.CandleDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.List;

public class CandleStickChartRenderer extends LineScatterCandleRadarRenderer {

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
            ICandleDataSet set = candleData.getDataSetByIndex(i);
            mShadowBuffers[i] = new CandleShadowBuffer(set.getEntryCount() * 4);
            mBodyBuffers[i] = new CandleBodyBuffer(set.getEntryCount() * 4);
        }
    }

    @Override
    public void drawData(Canvas c) {

        CandleData candleData = mChart.getCandleData();

        for (ICandleDataSet set : candleData.getDataSets()) {

            if (set.isVisible() && set.getEntryCount() > 0)
                drawDataSet(c, set);
        }
    }

    protected void drawDataSet(Canvas c, ICandleDataSet dataSet) {

        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        int dataSetIndex = mChart.getCandleData().getIndexOfDataSet(dataSet);

        int minx = Math.max(mMinX, 0);
        int maxx = Math.min(mMaxX + 1, dataSet.getEntryCount());

        int range = (maxx - minx) * 4;
        int to = (int) Math.ceil((maxx - minx) * phaseX + minx);

        CandleBodyBuffer bodyBuffer = mBodyBuffers[dataSetIndex];
        bodyBuffer.setBodySpace(dataSet.getBodySpace());
        bodyBuffer.setPhases(phaseX, phaseY);
        bodyBuffer.limitFrom(minx);
        bodyBuffer.limitTo(maxx);
        bodyBuffer.feed(dataSet);

        trans.pointValuesToPixel(bodyBuffer.buffer);

        CandleShadowBuffer shadowBuffer = mShadowBuffers[dataSetIndex];
        shadowBuffer.setPhases(phaseX, phaseY);
        shadowBuffer.limitFrom(minx);
        shadowBuffer.limitTo(maxx);
        shadowBuffer.feed(dataSet);

        trans.pointValuesToPixel(shadowBuffer.buffer);

        mRenderPaint.setStrokeWidth(dataSet.getShadowWidth());

        // draw the body
        for (int j = 0; j < range; j += 4) {

            // get the entry
            CandleEntry e = dataSet.getEntryForIndex(j / 4 + minx);

            if (!fitsBounds(e.getXIndex(), mMinX, to))
                continue;

            if (dataSet.getShadowColorSameAsCandle()) {

                if (e.getOpen() > e.getClose())
                    mRenderPaint.setColor(
                            dataSet.getDecreasingColor() == ColorTemplate.COLOR_NONE ?
                                    dataSet.getColor(j) :
                                    dataSet.getDecreasingColor()
                    );

                else if (e.getOpen() < e.getClose())
                    mRenderPaint.setColor(
                            dataSet.getIncreasingColor() == ColorTemplate.COLOR_NONE ?
                                    dataSet.getColor(j) :
                                    dataSet.getIncreasingColor()
                    );

                else
                    mRenderPaint.setColor(
                            dataSet.getShadowColor() == ColorTemplate.COLOR_NONE ?
                                    dataSet.getColor(j) :
                                    dataSet.getShadowColor()
                    );

            } else {
                mRenderPaint.setColor(
                        dataSet.getShadowColor() == ColorTemplate.COLOR_NONE ?
                                dataSet.getColor(j) :
                                dataSet.getShadowColor()
                );
            }

            mRenderPaint.setStyle(Paint.Style.STROKE);

            // draw the shadow
            c.drawLine(shadowBuffer.buffer[j], shadowBuffer.buffer[j + 1],
                    shadowBuffer.buffer[j + 2], shadowBuffer.buffer[j + 3],
                    mRenderPaint);

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

            } else if (open < close) {

                if (dataSet.getIncreasingColor() == ColorTemplate.COLOR_NONE) {
                    mRenderPaint.setColor(dataSet.getColor(j / 4 + minx));
                } else {
                    mRenderPaint.setColor(dataSet.getIncreasingColor());
                }

                mRenderPaint.setStyle(dataSet.getIncreasingPaintStyle());
                // draw the body
                c.drawRect(leftBody, open, rightBody, close, mRenderPaint);
            } else { // equal values

                mRenderPaint.setColor(dataSet.getShadowColor());
                c.drawLine(leftBody, open, rightBody, close, mRenderPaint);
            }
        }
    }

    @Override
    public void drawValues(Canvas c) {

        // if values are drawn
        if (mChart.getCandleData().getYValCount() < mChart.getMaxVisibleCount()
                * mViewPortHandler.getScaleX()) {

            List<ICandleDataSet> dataSets = mChart.getCandleData().getDataSets();

            for (int i = 0; i < dataSets.size(); i++) {

                ICandleDataSet dataSet = dataSets.get(i);

                if (!dataSet.isDrawValuesEnabled() || dataSet.getEntryCount() == 0)
                    continue;

                // apply the text-styling defined by the DataSet
                applyValueTextStyle(dataSet);

                Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

                int minx = Math.max(mMinX, 0);
                int maxx = Math.min(mMaxX + 1, dataSet.getEntryCount());

                float[] positions = trans.generateTransformedValuesCandle(
                        dataSet, mAnimator.getPhaseX(), mAnimator.getPhaseY(), minx, maxx);

                float yOffset = Utils.convertDpToPixel(5f);

                for (int j = 0; j < positions.length; j += 2) {

                    float x = positions[j];
                    float y = positions[j + 1];

                    if (!mViewPortHandler.isInBoundsRight(x))
                        break;

                    if (!mViewPortHandler.isInBoundsLeft(x) || !mViewPortHandler.isInBoundsY(y))
                        continue;

                    CandleEntry entry = dataSet.getEntryForIndex(j / 2 + minx);

                    drawValue(c, dataSet.getValueFormatter(), entry.getHigh(), entry, i, x, y - yOffset);
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

            ICandleDataSet set = mChart.getCandleData().getDataSetByIndex(
                    indices[i].getDataSetIndex());

            if (set == null || !set.isHighlightEnabled())
                continue;

            CandleEntry e = set.getEntryForXIndex(xIndex);

            if (e == null || e.getXIndex() != xIndex)
                continue;

            float low = e.getLow() * mAnimator.getPhaseY();
            float high = e.getHigh() * mAnimator.getPhaseY();
            float y = (low + high) / 2f;

            float min = mChart.getYChartMin();
            float max = mChart.getYChartMax();


            float[] pts = new float[]{
                    xIndex, y
            };

            mChart.getTransformer(set.getAxisDependency()).pointValuesToPixel(pts);

            // draw the lines
            drawHighlightLines(c, pts, set);
        }
    }

}
