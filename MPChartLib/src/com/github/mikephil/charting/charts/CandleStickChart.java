
package com.github.mikephil.charting.charts;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;

import java.util.ArrayList;

/**
 * Financial chart type that draws candle-sticks.
 * 
 * @author Philipp Jahoda
 */
public class CandleStickChart extends BarLineChartBase<CandleData> {

    public CandleStickChart(Context context) {
        super(context);
    }

    public CandleStickChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CandleStickChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void calcMinMax(boolean fixedValues) {
        super.calcMinMax(fixedValues);

        // increase deltax by 1 because the candles have a width of 1
        mDeltaX++;
    }

    @Override
    protected void drawData() {

        ArrayList<CandleDataSet> dataSets = mData.getDataSets();

        // pre allocate
        float[] shadowPoints = new float[4];
        float[] bodyPoints = new float[4];

        for (int i = 0; i < mData.getDataSetCount(); i++) {

            CandleDataSet dataSet = dataSets.get(i);
            ArrayList<CandleEntry> entries = dataSet.getYVals();

            mRenderPaint.setStrokeWidth(dataSet.getShadowWidth());

            for (int j = 0; j < entries.size() * mPhaseX; j++) {

                // get the color that is specified for this position from
                // the DataSet, this will reuse colors, if the index is out
                // of bounds
                mRenderPaint.setColor(dataSet.getColor(j));

                // get the entry
                CandleEntry e = entries.get(j);

                // transform the entries values for shadow and body
                transformShadow(shadowPoints, e);
                transformBody(bodyPoints, e, dataSet.getBodySpace());

                float xShadow = shadowPoints[0];
                float leftBody = bodyPoints[0];
                float rightBody = bodyPoints[2];

                float high = shadowPoints[1];
                float low = shadowPoints[3];

                float open = bodyPoints[1];
                float close = bodyPoints[3];

                if (isOffContentRight(leftBody))
                    break;

                // make sure the lines don't do shitty things outside bounds
                if (isOffContentLeft(rightBody)
                        && isOffContentTop(low)
                        && isOffContentBottom(high))
                    continue;

                // draw the shadow
                mDrawCanvas.drawLine(xShadow, low, xShadow, high, mRenderPaint);

                // decide weather the body is hollow or filled
                if (open > close) {

                    mRenderPaint.setStyle(Paint.Style.FILL);
                    // draw the body
                    mDrawCanvas.drawRect(leftBody, close, rightBody, open, mRenderPaint);

                } else {

                    mRenderPaint.setStyle(Paint.Style.STROKE);
                    // draw the body
                    mDrawCanvas.drawRect(leftBody, open, rightBody, close, mRenderPaint);
                }
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
    private void transformBody(float[] bodyPoints, CandleEntry e, float bodySpace) {

        bodyPoints[0] = e.getXIndex() + bodySpace;
        bodyPoints[1] = e.getClose() * mPhaseY;
        bodyPoints[2] = e.getXIndex() + (1f - bodySpace);
        bodyPoints[3] = e.getOpen() * mPhaseY;

        mTrans.pointValuesToPixel(bodyPoints);
    }

    /**
     * Transforms the values of an entry in order to draw the candle-shadow.
     * 
     * @param shadowPoints
     * @param e
     */
    private void transformShadow(float[] shadowPoints, CandleEntry e) {

        shadowPoints[0] = e.getXIndex() + 0.5f;
        shadowPoints[1] = e.getHigh() * mPhaseY;
        shadowPoints[2] = e.getXIndex() + 0.5f;
        shadowPoints[3] = e.getLow() * mPhaseY;

        mTrans.pointValuesToPixel(shadowPoints);
    }

    @Override
    protected void drawValues() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void drawAdditional() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void drawHighlights() {

        for (int i = 0; i < mIndicesToHightlight.length; i++) {

            int xIndex = mIndicesToHightlight[i].getXIndex(); // get the
                                                              // x-position

            CandleDataSet set = mData.getDataSetByIndex(mIndicesToHightlight[i].getDataSetIndex());
            
            if (set == null)
                continue;
            
            mHighlightPaint.setColor(set.getHighLightColor());
            
            CandleEntry e = set.getEntryForXIndex(xIndex);
            
            if(e == null) continue;

            float low = e.getLow() * mPhaseY;
            float high = e.getHigh() * mPhaseY;

            float[] vertPts = new float[] {
                    xIndex, mYChartMax, xIndex, mYChartMin, xIndex + 1f, mYChartMax, xIndex + 1f,
                    mYChartMin
            };

            float[] horPts = new float[] {
                    0, low, mDeltaX, low, 0, high, mDeltaX, high
            };

            mTrans.pointValuesToPixel(vertPts);
            mTrans.pointValuesToPixel(horPts);

            // draw the vertical highlight lines
            mDrawCanvas.drawLines(vertPts, mHighlightPaint);

            // draw the horizontal highlight lines
            mDrawCanvas.drawLines(horPts, mHighlightPaint);
        }
    }

}
