
package com.github.mikephil.charting.charts;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;

import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.renderer.HorizontalBarChartRenderer;
import com.github.mikephil.charting.renderer.XAxisRendererHorizontalBarChart;
import com.github.mikephil.charting.renderer.YAxisRendererHorizontalBarChart;
import com.github.mikephil.charting.utils.Utils;

/**
 * BarChart with horizontal bar orientation. In this implementation, x- and
 * y-axis are switched.
 * 
 * @author Philipp Jahoda
 */
public class HorizontalBarChart extends BarChart {

    public HorizontalBarChart(Context context) {
        super(context);
    }

    public HorizontalBarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HorizontalBarChart(Context context, AttributeSet attrs,
            int defStyle) {
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
    protected void calculateOffsets() {

        float offsetLeft = 0f, offsetRight = 0f, offsetTop = 0f, offsetBottom = 0f;

        // setup offsets for legend
        if (mLegend != null && mLegend.isEnabled()) {

            if (mLegend.getPosition() == LegendPosition.RIGHT_OF_CHART
                    || mLegend.getPosition() == LegendPosition.RIGHT_OF_CHART_CENTER) {

                offsetRight += mLegend.mTextWidthMax + mLegend.getXOffset() * 2f;

            } else if (mLegend.getPosition() == LegendPosition.BELOW_CHART_LEFT
                    || mLegend.getPosition() == LegendPosition.BELOW_CHART_RIGHT
                    || mLegend.getPosition() == LegendPosition.BELOW_CHART_CENTER) {

                offsetBottom += mLegend.mTextHeightMax * 3f;
            }
        }

        // offsets for y-labels
        if (mAxisLeft.isEnabled()) {
            offsetTop += mAxisLeft.getRequiredHeightSpace(mAxisRendererLeft.getAxisPaint());
        }

        if (mAxisRight.isEnabled()) {
            offsetBottom += mAxisRight.getRequiredHeightSpace(mAxisRendererRight.getAxisPaint());
        }

        float xlabelwidth = mXAxis.mLabelWidth;

        if (mXAxis.isEnabled()) {

            // offsets for x-labels
            if (mXAxis.getPosition() == XAxisPosition.BOTTOM) {

                offsetLeft += xlabelwidth;

            } else if (mXAxis.getPosition() == XAxisPosition.TOP) {

                offsetRight += xlabelwidth;

            } else if (mXAxis.getPosition() == XAxisPosition.BOTH_SIDED) {

                offsetLeft += xlabelwidth;
                offsetRight += xlabelwidth;
            }
        }

        float min = Utils.convertDpToPixel(10f);

        mViewPortHandler.restrainViewPort(Math.max(min, offsetLeft), Math.max(min, offsetTop),
                Math.max(min, offsetRight), Math.max(min, offsetBottom));

        if (mLogEnabled) {
            Log.i(LOG_TAG, "offsetLeft: " + offsetLeft + ", offsetTop: " + offsetTop
                    + ", offsetRight: " + offsetRight + ", offsetBottom: " + offsetBottom);
            Log.i(LOG_TAG, "Content: " + mViewPortHandler.getContentRect().toString());
        }

        prepareOffsetMatrix();
        prepareValuePxMatrix();
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
