
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
public class CandleStickChart extends BarLineChartBase {

    public CandleStickChart(Context context) {
        super(context);
    }

    public CandleStickChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CandleStickChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Sets a CandleData object for the CandleStickChart.
     * 
     * @param data
     */
    public void setData(CandleData data) {
        super.setData(data);
    }

    @Override
    protected void drawData() {

        ArrayList<CandleDataSet> dataSets = (ArrayList<CandleDataSet>) mCurrentData.getDataSets();

        mRenderPaint.setStyle(Paint.Style.FILL);

        for (int i = 0; i < mCurrentData.getDataSetCount(); i++) {

            CandleDataSet dataSet = dataSets.get(i);
            ArrayList<CandleEntry> entries = (ArrayList<CandleEntry>) dataSet.getYVals();
            
            mRenderPaint.setStrokeWidth(dataSet.getShadowWidth());

            float[] shadowPoints = new float[4];
            float[] bodyPoints = new float[4];

            for (int j = 0; j < entries.size() * mPhaseX; j++) {

                // get the color that is specified for this position from
                // the DataSet, this will reuse colors, if the index is out
                // of bounds
                mRenderPaint.setColor(dataSet.getColor(j));

                CandleEntry e = entries.get(j);

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

                // draw the body
                mDrawCanvas.drawRect(leftBody, open, rightBody, close, mRenderPaint);
            }
        }
    }

    /**
     * Transforms the values of an entry in order to draw the candle-body.
     * 
     * @param bodyPoints
     * @param e
     * @param bodySpace
     * 
     */
    private void transformBody(float[] bodyPoints, CandleEntry e, float bodySpace) {

        bodyPoints[0] = e.getXIndex() + bodySpace;
        bodyPoints[1] = e.getClose();
        bodyPoints[2] = e.getXIndex() + (1f - bodySpace);
        bodyPoints[3] = e.getOpen();

        transformPointArray(bodyPoints);
    }

    /**
     * Transforms the values of an entry in order to draw the candle-shadow.
     * 
     * @param shadowPoints
     * @param e
     */
    private void transformShadow(float[] shadowPoints, CandleEntry e) {

        shadowPoints[0] = e.getXIndex() + 0.5f;
        shadowPoints[1] = e.getShadowHigh();
        shadowPoints[2] = e.getXIndex() + 0.5f;
        shadowPoints[3] = e.getShadowLow();

        transformPointArray(shadowPoints);
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
        // TODO Auto-generated method stub

    }

}
