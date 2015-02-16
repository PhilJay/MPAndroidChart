
package com.github.mikephil.charting.charts;

import android.content.Context;
import android.util.AttributeSet;

import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.interfaces.CandleDataProvider;
import com.github.mikephil.charting.renderer.CandleStickChartRenderer;

/**
 * Financial chart type that draws candle-sticks.
 * 
 * @author Philipp Jahoda
 */
public class CandleStickChart extends BarLineChartBase<CandleData> implements CandleDataProvider {

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
    protected void init() {
        super.init();
        
        mRenderer = new CandleStickChartRenderer(this, mAnimator, mViewPortHandler);
    }

    @Override
    protected void calcMinMax() {
        super.calcMinMax();

        // increase deltax by 1 because the candles have a width of 1
        mDeltaX++;
    }
    
    @Override
    public CandleData getCandleData() {
        return mData;
    }
}
