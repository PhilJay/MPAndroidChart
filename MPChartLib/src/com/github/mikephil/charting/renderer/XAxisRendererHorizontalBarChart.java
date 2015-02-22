
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Paint.Align;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;

public class XAxisRendererHorizontalBarChart extends XAxisRendererBarChart {

    public XAxisRendererHorizontalBarChart(ViewPortHandler viewPortHandler, XAxis xAxis,
            Transformer trans, BarChart chart) {
        super(viewPortHandler, xAxis, trans, chart);
    }

    @Override
    public void renderAxis(Canvas c) {

        if (!mXAxis.isEnabled())
            return;

        float xoffset = Utils.convertDpToPixel(4f);

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

        drawAxisLine(c);
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
    protected void drawAxisLine(Canvas c) {

        if (!mXAxis.isDrawAxisLineEnabled())
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
