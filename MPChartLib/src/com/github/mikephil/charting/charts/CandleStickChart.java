
package com.github.mikephil.charting.charts;

import android.content.Context;
import android.util.AttributeSet;

import com.github.mikephil.charting.data.CandleData;

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
        // TODO Auto-generated method stub

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
