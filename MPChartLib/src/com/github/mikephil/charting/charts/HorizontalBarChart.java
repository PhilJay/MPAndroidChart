
package com.github.mikephil.charting.charts;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;

import com.github.mikephil.charting.renderer.HorizontalBarChartRenderer;
import com.github.mikephil.charting.renderer.XAxisRendererHorizontalBarChart;
import com.github.mikephil.charting.renderer.YAxisRendererHorizontalBarChart;

/**
 * BarChart with horizontal bar orientation. In this implementation, x- and
 * y-axis are switched.
 * 
 * @author Philipp Jahoda
 */
public class HorizontalBarChart extends BarChart {

    public HorizontalBarChart(Context context)
    {
        super(context);
    }

    public HorizontalBarChart(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public HorizontalBarChart(Context context, AttributeSet attrs,
            int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();

        mRenderer = new HorizontalBarChartRenderer(this, mAnimator, mViewPortHandler);
        mAxisRendererLeft = new YAxisRendererHorizontalBarChart(mViewPortHandler, mAxisLeft,
                mLeftAxisTransformer);
        mAxisRendererRight = new YAxisRendererHorizontalBarChart(mViewPortHandler, mAxisRight,
                mRightAxisTransformer);
        mXAxisRenderer = new XAxisRendererHorizontalBarChart(mViewPortHandler, mXAxis,
                mLeftAxisTransformer, this);
    }

    @Override
    protected void prepareValuePxMatrix() {
        mRightAxisTransformer.prepareMatrixValuePx(mAxisRight.mAxisMinimum, mAxisRight.mAxisRange,
                mDeltaX,
                mXChartMin);
        mLeftAxisTransformer.prepareMatrixValuePx(mAxisLeft.mAxisMinimum, mAxisLeft.mAxisRange,
                mDeltaX,
                mXChartMin);
    }
    
    @Override
    protected void calcModulus() {
        float[] values = new float[9];
        mViewPortHandler.getMatrixTouch().getValues(values);

        mXAxis.mAxisLabelModulus = (int) Math
                .ceil((mData.getXValCount() * mXAxis.mLabelHeight)
                        / (mViewPortHandler.contentHeight() * values[Matrix.MSCALE_Y]));
    }
}
