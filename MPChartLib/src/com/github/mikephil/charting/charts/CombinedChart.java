
package com.github.mikephil.charting.charts;

import android.content.Context;
import android.util.AttributeSet;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.interfaces.BarDataProvider;
import com.github.mikephil.charting.interfaces.CandleDataProvider;
import com.github.mikephil.charting.interfaces.LineDataProvider;
import com.github.mikephil.charting.interfaces.ScatterDataProvider;
import com.github.mikephil.charting.renderer.CombinedChartRenderer;
import com.github.mikephil.charting.utils.FillFormatter;

public class CombinedChart extends BarLineChartBase<CombinedData> implements LineDataProvider,
        BarDataProvider, ScatterDataProvider, CandleDataProvider {

    private float mXAxisInset = 0f;
    
    private FillFormatter mFillFormatter;

    public CombinedChart(Context context) {
        super(context);
    }

    public CombinedChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CombinedChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();

        mFillFormatter = new DefaultFillFormatter();
        // mRenderer = new CombinedChartRenderer(this, mAnimator,
        // mViewPortHandler);
    }

    @Override
    protected void calcMinMax() {
        super.calcMinMax();

        if (getBarData() != null) {
            mDeltaX += 1;
            mXAxisInset = 0.5f;
            mXAxis.setCenterXLabelText(true);
        }
    }

    @Override
    public void setData(CombinedData data) {
        super.setData(data);
        mRenderer = new CombinedChartRenderer(this, mAnimator, mViewPortHandler);
    }

    public void setFillFormatter(FillFormatter formatter) {

        if (formatter == null)
            formatter = new DefaultFillFormatter();
        else
            mFillFormatter = formatter;
    }

    @Override
    public FillFormatter getFillFormatter() {
        return mFillFormatter;
    }

    @Override
    public LineData getLineData() {
        if (mData == null)
            return null;
        return mData.getLineData();
    }

    @Override
    public BarData getBarData() {
        if (mData == null)
            return null;
        return mData.getBarData();
    }

    @Override
    public ScatterData getScatterData() {
        if (mData == null)
            return null;
        return mData.getScatterData();
    }

    @Override
    public CandleData getCandleData() {
        if (mData == null)
            return null;
        return mData.getCandleData();
    }

    @Override
    public boolean isDrawBarShadowEnabled() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isDrawValueAboveBarEnabled() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isDrawHighlightArrowEnabled() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isDrawValuesForWholeStackEnabled() {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public float getXAxisInset() {
        return mXAxisInset;
    }
    
    public void setXAxisInset(float inset) {
        mXAxisInset = inset;
    }
}
