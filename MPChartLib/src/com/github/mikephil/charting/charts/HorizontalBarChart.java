
package com.github.mikephil.charting.charts;

import android.content.Context;
import android.util.AttributeSet;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.renderer.HorizontalBarChartRenderer;
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
    }

    @Override
    protected void prepareValuePxMatrix() {
        mRightAxisTransformer.prepareMatrixValuePx(mXChartMin, mAxisRight.mAxisRange, mDeltaX,
                mAxisRight.mAxisMinimum);
        mLeftAxisTransformer.prepareMatrixValuePx(mXChartMin, mAxisLeft.mAxisRange, mDeltaX,
                mAxisLeft.mAxisMinimum);
    }

    private class XLabelsAsYLabels extends YAxis
    {
        public XLabelsAsYLabels(AxisDependency position) {
            super(position);
            // TODO Auto-generated constructor stub
        }

        /**
         * Returns the longest formatted label (in terms of characters) the
         * y-labels contain.
         *
         * @return
         */
        @Override
        public String getLongestLabel()
        {
            String longest = "";

            for (int i = 0; i < mData.getXValCount(); i++)
            {
                String text = mData.getXVals().get(i);

                if (longest.length() < text.length())
                    longest = text;
            }

            return longest;
        }

        /**
         * Returns the formatted y-label at the specified index. This will
         * either use the auto-formatter or the custom formatter (if one is
         * set).
         *
         * @param index
         * @return
         */
        @Override
        public String getFormattedLabel(int index)
        {
            super.getFormattedLabel(index);
            if (index < 0)
                return "";

            return mData.getXVals().get(index);
        }
    }
}
