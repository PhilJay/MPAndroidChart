
package com.github.mikephil.charting.charts;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Paint.Align;
import android.util.AttributeSet;

import com.github.mikephil.charting.utils.Legend.LegendPosition;

/**
 * Baseclass of PieChart and RadarChart.
 * 
 * @author Philipp Jahoda
 */
public abstract class PieRadarChartBase extends Chart {

    public PieRadarChartBase(Context context) {
        super(context);
    }

    public PieRadarChartBase(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PieRadarChartBase(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void prepare() {
        
        if (mDataNotSet)
            return;

        calcMinMax(false);

        prepareLegend();
    }

    @Override
    public void notifyDataSetChanged() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void calculateOffsets() {

        if (mLegend == null)
            return;

        // setup offsets for legend
        if (mLegend.getPosition() == LegendPosition.RIGHT_OF_CHART) {

            mLegend.setOffsetRight(mLegend.getMaximumEntryLength(mLegendLabelPaint));
            mLegendLabelPaint.setTextAlign(Align.LEFT);

        } else if (mLegend.getPosition() == LegendPosition.BELOW_CHART_LEFT
                || mLegend.getPosition() == LegendPosition.BELOW_CHART_RIGHT
                || mLegend.getPosition() == LegendPosition.BELOW_CHART_CENTER) {

            mLegend.setOffsetBottom(mLegendLabelPaint.getTextSize() * 3.5f);
        }

        if (mDrawLegend) {

            mOffsetBottom = Math.max(mOffsetBottom, mLegend.getOffsetBottom());
            mOffsetRight = Math.max(mOffsetRight, mLegend.getOffsetRight() / 3 * 2);
        }

        mLegend.setOffsetTop(mOffsetTop);
        mLegend.setOffsetLeft(mOffsetLeft);

        prepareContentRect();

        float scaleX = (float) ((getWidth() - mOffsetLeft - mOffsetRight) / mDeltaX);
        float scaleY = (float) ((getHeight() - mOffsetBottom - mOffsetTop) / mDeltaY);

        Matrix val = new Matrix();
        val.postTranslate(0, -mYChartMin);
        val.postScale(scaleX, -scaleY);

        mMatrixValueToPx.set(val);

        Matrix offset = new Matrix();
        offset.postTranslate(mOffsetLeft, getHeight() - mOffsetBottom);

        mMatrixOffset.set(offset);
    }
}
