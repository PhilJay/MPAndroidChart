
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.interfaces.CandleDataProvider;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.Transformer;

import java.util.ArrayList;

public class CandleStickChartRenderer extends DataRenderer {

    protected CandleDataProvider mChart;

    public CandleStickChartRenderer(CandleDataProvider chart, ChartAnimator animator,
            ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        mChart = chart;
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

        // pre allocate
        float[] shadowPoints = new float[4];
        float[] bodyPoints = new float[4];

        ArrayList<CandleEntry> entries = dataSet.getYVals();

        mRenderPaint.setStrokeWidth(dataSet.getShadowWidth());

        for (int j = 0; j < entries.size() * mAnimator.getPhaseX(); j++) {

            // get the color that is specified for this position from
            // the DataSet, this will reuse colors, if the index is out
            // of bounds
            mRenderPaint.setColor(dataSet.getColor(j));

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

            // draw the shadow
            c.drawLine(xShadow, low, xShadow, high, mRenderPaint);

            // decide weather the body is hollow or filled
            if (open > close) {

                mRenderPaint.setStyle(Paint.Style.FILL);
                // draw the body
                c.drawRect(leftBody, close, rightBody, open, mRenderPaint);

            } else {

                mRenderPaint.setStyle(Paint.Style.STROKE);
                // draw the body
                c.drawRect(leftBody, open, rightBody, close, mRenderPaint);
            }
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

        bodyPoints[0] = e.getXIndex() + bodySpace;
        bodyPoints[1] = e.getClose() * phase;
        bodyPoints[2] = e.getXIndex() + (1f - bodySpace);
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

        shadowPoints[0] = e.getXIndex() + 0.5f;
        shadowPoints[1] = e.getHigh() * phase;
        shadowPoints[2] = e.getXIndex() + 0.5f;
        shadowPoints[3] = e.getLow() * phase;

        trans.pointValuesToPixel(shadowPoints);
    }

    @Override
    public void drawValues(Canvas c) {
        // TODO Auto-generated method stub

    }

    @Override
    public void drawExtras(Canvas c) {
    }

    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {

        for (int i = 0; i < indices.length; i++) {

            int xIndex = indices[i].getXIndex(); // get the
                                                 // x-position

            CandleDataSet set = mChart.getCandleData().getDataSetByIndex(indices[i].getDataSetIndex());

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
                    xIndex, max, xIndex, min, xIndex + 1f, max, xIndex + 1f,
                    min
            };

            float[] horPts = new float[] {
                    0, low, mChart.getDeltaX(), low, 0, high, mChart.getDeltaX(), high
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
