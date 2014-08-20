
package com.github.mikephil.charting.charts;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;

/**
 * Chart that draws candle-sticks.
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

        mRenderPaint.setStyle(Paint.Style.STROKE);

        for (int i = 0; i < mCurrentData.getDataSetCount(); i++) {

            CandleDataSet dataSet = dataSets.get(i);
            ArrayList<? extends Entry> entries = dataSet.getYVals();

            float[] valuePoints = generateTransformedValues(entries, 0f);

            for (int j = 0; j < (valuePoints.length - 2) * mPhaseX; j += 2) {

                // get the color that is specified for this position from
                // the DataSet, this will reuse colors, if the index is out
                // of bounds
                mRenderPaint.setColor(dataSet.getColor(j / 2));

                if (isOffContentRight(valuePoints[j]))
                    break;

                // make sure the lines don't do shitty things outside bounds
                if (j != 0 && isOffContentLeft(valuePoints[j - 1])
                        && isOffContentTop(valuePoints[j + 1])
                        && isOffContentBottom(valuePoints[j + 1]))
                    continue;

                mDrawCanvas.drawLine(valuePoints[j], valuePoints[j + 1], valuePoints[j + 2],
                        valuePoints[j + 3], mRenderPaint);
            }
        }
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
