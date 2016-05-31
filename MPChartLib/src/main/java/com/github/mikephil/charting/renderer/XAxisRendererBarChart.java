
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.PointF;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class XAxisRendererBarChart extends XAxisRenderer {

    protected BarChart mChart;

    public XAxisRendererBarChart(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans,
            BarChart chart) {
        super(viewPortHandler, xAxis, trans);

        this.mChart = chart;
    }

    /**
     * draws the x-labels on the specified y-position
     * 
     * @param pos
     */
    @Override
    protected void drawLabels(Canvas c, float pos, PointF anchor) {

        final float labelRotationAngleDegrees = mXAxis.getLabelRotationAngle();

        // pre allocate to save performance (dont allocate in loop)
        float[] position = new float[] {
                0f, 0f
        };

        BarData bd = mChart.getData();
        int step = bd.getDataSetCount();

//        for (int i = mMinX; i <= mMaxX; i += mXAxis.mAxisLabelModulus) {
//
//            position[0] = i * step + i * bd.getGroupSpace()
//                    + bd.getGroupSpace() / 2f;
//
//            // consider groups (center label for each group)
//            if (step > 1) {
//                position[0] += ((float) step - 1f) / 2f;
//            }
//
//            mTrans.pointValuesToPixel(position);
//
//            if (mViewPortHandler.isInBoundsX(position[0]) && i >= 0
//                    && i < mXAxis.getValues().size()) {
//
//                String label = mXAxis.getValues().get(i).getLabel();
//
//                if (mXAxis.isAvoidFirstLastClippingEnabled()) {
//
//                    // avoid clipping of the last
//                    if (i == mXAxis.getValues().size() - 1) {
//                        float width = Utils.calcTextWidth(mAxisLabelPaint, label);
//
//                        if (position[0] + width / 2.f > mViewPortHandler.contentRight())
//                            position[0] = mViewPortHandler.contentRight() - (width / 2.f);
//
//                        // avoid clipping of the first
//                    } else if (i == 0) {
//
//                        float width = Utils.calcTextWidth(mAxisLabelPaint, label);
//
//                        if (position[0] - width / 2.f < mViewPortHandler.contentLeft())
//                            position[0] = mViewPortHandler.contentLeft() + (width / 2.f);
//                    }
//                }
//
//                drawLabel(c, label, i, position[0], pos, anchor, labelRotationAngleDegrees);
//            }
//        }
    }

    @Override
    public void renderGridLines(Canvas c) {

        if (!mXAxis.isDrawGridLinesEnabled() || !mXAxis.isEnabled())
            return;

        float[] position = new float[] {
                0f, 0f
        };

        mGridPaint.setColor(mXAxis.getGridColor());
        mGridPaint.setStrokeWidth(mXAxis.getGridLineWidth());

        BarData bd = mChart.getData();
        int step = bd.getDataSetCount();

//        for (int i = mMinX; i < mMaxX; i += mXAxis.mAxisLabelModulus) {
//
//            position[0] = i * step + i * bd.getGroupSpace() - 0.5f;
//
//            mTrans.pointValuesToPixel(position);
//
//            if (mViewPortHandler.isInBoundsX(position[0])) {
//
//                c.drawLine(position[0], mViewPortHandler.offsetTop(), position[0],
//                        mViewPortHandler.contentBottom(), mGridPaint);
//            }
//        }
    }
}
