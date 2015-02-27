
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Paint.Align;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;

public class XAxisRendererHorizontalBarChart extends XAxisRendererBarChart {

    public XAxisRendererHorizontalBarChart(ViewPortHandler viewPortHandler, XAxis xAxis,
            Transformer trans, BarChart chart) {
        super(viewPortHandler, xAxis, trans, chart);
    }
    
    @Override
    public void computeAxis(float xValAverageLength, ArrayList<String> xValues) {
        
        mAxisPaint.setTypeface(mXAxis.getTypeface());
        mAxisPaint.setTextSize(mXAxis.getTextSize());
        mXAxis.setValues(xValues);

        String longest = mXAxis.getLongestLabel();
        mXAxis.mLabelWidth = (int) (Utils.calcTextWidth(mAxisPaint, longest) + mXAxis.getXOffset() * 3.5f);
        mXAxis.mLabelHeight = Utils.calcTextHeight(mAxisPaint, longest);
    }

    @Override
    public void renderAxisLabels(Canvas c) {

        if (!mXAxis.isEnabled() || !mXAxis.isDrawLabelsEnabled())
            return;

        float xoffset = mXAxis.getXOffset();

        mAxisPaint.setTypeface(mXAxis.getTypeface());
        mAxisPaint.setTextSize(mXAxis.getTextSize());
        mAxisPaint.setColor(mXAxis.getTextColor());

        if (mXAxis.getPosition() == XAxisPosition.TOP) {

            mAxisPaint.setTextAlign(Align.LEFT);
            drawLabels(c, mViewPortHandler.contentRight() + xoffset);

        } else if (mXAxis.getPosition() == XAxisPosition.BOTTOM) {

            mAxisPaint.setTextAlign(Align.RIGHT);
            drawLabels(c, mViewPortHandler.contentLeft() - xoffset);

        } else if (mXAxis.getPosition() == XAxisPosition.BOTTOM_INSIDE) {

            mAxisPaint.setTextAlign(Align.LEFT);
            drawLabels(c, mViewPortHandler.contentLeft() + xoffset);

        } else if (mXAxis.getPosition() == XAxisPosition.TOP_INSIDE) {

            mAxisPaint.setTextAlign(Align.RIGHT);
            drawLabels(c, mViewPortHandler.contentRight() - xoffset);

        } else { // BOTH SIDED

            drawLabels(c, mViewPortHandler.contentLeft());
            drawLabels(c, mViewPortHandler.contentRight());
        }
    }

    /**
     * draws the x-labels on the specified y-position
     * 
     * @param pos
     */
    @Override
    protected void drawLabels(Canvas c, float pos) {

        // pre allocate to save performance (dont allocate in loop)
        float[] position = new float[] {
                0f, 0f
        };

        BarData bd = mChart.getData();
        int step = bd.getDataSetCount();

        for (int i = 0; i < mXAxis.getValues().size(); i += mXAxis.mAxisLabelModulus) {

            position[1] = i * step + i * bd.getGroupSpace()
                    + bd.getGroupSpace() / 2f;
            
            // consider groups (center label for each group)
            if (step > 1) {
                position[1] += ((float) step - 1f) / 2f;
            }

            mTrans.pointValuesToPixel(position);

            if (mViewPortHandler.isInBoundsY(position[1])) {

                String label = mXAxis.getValues().get(i);
                c.drawText(label, pos, position[1] + mXAxis.mLabelHeight / 2f,
                        mAxisPaint);
            }
        }
    }

    @Override
    public void renderGridLines(Canvas c) {

        if (!mXAxis.isDrawGridLinesEnabled() || !mXAxis.isEnabled())
            return;

        float[] position = new float[] {
                0f, 0f
        };

        BarData bd = mChart.getData();
        // take into consideration that multiple DataSets increase mDeltaX
        int step = bd.getDataSetCount();

        for (int i = 0; i < mXAxis.getValues().size(); i += mXAxis.mAxisLabelModulus) {

            position[1] = i * step + i * bd.getGroupSpace() - 0.5f;

            mTrans.pointValuesToPixel(position);

            if (mViewPortHandler.isInBoundsY(position[1])) {

                c.drawLine(mViewPortHandler.contentLeft(), position[1],
                        mViewPortHandler.contentRight(), position[1], mGridPaint);
            }
        }
    }

    @Override
    public void renderAxisLine(Canvas c) {

        if (!mXAxis.isDrawAxisLineEnabled() || !mXAxis.isEnabled())
            return;

        mAxisLinePaint.setColor(mXAxis.getAxisLineColor());
        mAxisLinePaint.setStrokeWidth(mXAxis.getAxisLineWidth());

        if (mXAxis.getPosition() == XAxisPosition.TOP
                || mXAxis.getPosition() == XAxisPosition.TOP_INSIDE
                || mXAxis.getPosition() == XAxisPosition.BOTH_SIDED) {
            c.drawLine(mViewPortHandler.contentRight(),
                    mViewPortHandler.contentTop(), mViewPortHandler.contentRight(),
                    mViewPortHandler.contentBottom(), mAxisLinePaint);
        }

        if (mXAxis.getPosition() == XAxisPosition.BOTTOM
                || mXAxis.getPosition() == XAxisPosition.BOTTOM_INSIDE
                || mXAxis.getPosition() == XAxisPosition.BOTH_SIDED) {
            c.drawLine(mViewPortHandler.contentLeft(),
                    mViewPortHandler.contentTop(), mViewPortHandler.contentLeft(),
                    mViewPortHandler.contentBottom(), mAxisLinePaint);
        }
    }
}
