package com.github.mikephil.charting.charts;

import android.content.Context;
import android.util.AttributeSet;

import com.github.mikephil.charting.data.OHLCData;
import com.github.mikephil.charting.interfaces.OHLCDataProvider;
import com.github.mikephil.charting.renderer.OHLCChartRenderer;

/**
 * Financial chart type that draws ohlc-bars.
 *
 * @author Maximilian Peitz
 */
public class OHLCChart extends BarLineChartBase<OHLCData> implements OHLCDataProvider {

    public OHLCChart(Context context) {
        super(context);
    }

    public OHLCChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OHLCChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();

        mRenderer = new OHLCChartRenderer(this, mAnimator, mViewPortHandler);
        mXChartMin = -0.5f;
    }

    @Override
    protected void calcMinMax() {
        super.calcMinMax();

        mXChartMax += 0.5f;
        mDeltaX = Math.abs(mXChartMax - mXChartMin);
    }

    @Override
    public OHLCData getOHLCData() {
        return mData;
    }
}
