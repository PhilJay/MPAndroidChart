
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.util.Log;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;

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
    protected void drawLabels(Canvas c, float pos) {

        // pre allocate to save performance (dont allocate in loop)
        float[] position = new float[] {
                0f, 0f
        };

        BarData bd = mChart.getData();
        int step = bd.getDataSetCount();
        float div = (float) step + (step > 1 ? bd.getGroupSpace() : 0f);

        float min = (float) (mTrans.getValuesByTouchPoint(mViewPortHandler.contentLeft(), 0).x)
                / div;
        float max = (float) (mTrans.getValuesByTouchPoint(mViewPortHandler.contentRight(), 0).x)
                / div;
        
        for (int i = (int) min; i < max; i += mXAxis.mAxisLabelModulus) {

            if (!fitsBounds(i, min - 1f, max + 0.5f))
                continue;

            position[0] = i * step + i * bd.getGroupSpace()
                    + bd.getGroupSpace() / 2f;

            // consider groups (center label for each group)
            if (step > 1) {
                position[0] += ((float) step - 1f) / 2f;
            }

            mTrans.pointValuesToPixel(position);

            if (mViewPortHandler.isInBoundsX(position[0])) {

                String label = mXAxis.getValues().get(i);

                if (mXAxis.isAvoidFirstLastClippingEnabled()) {

                    // avoid clipping of the last
                    if (i == mXAxis.getValues().size() - 1) {
                        float width = Utils.calcTextWidth(mAxisPaint, label);

                        if (width > mViewPortHandler.offsetRight() * 2
                                && position[0] + width > mViewPortHandler.getChartWidth())
                            position[0] -= width / 2;

                        // avoid clipping of the first
                    } else if (i == 0) {

                        float width = Utils.calcTextWidth(mAxisPaint, label);
                        position[0] += width / 2;
                    }
                }

                c.drawText(label, position[0],
                        pos,
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

        mGridPaint.setColor(mXAxis.getGridColor());
        mGridPaint.setStrokeWidth(mXAxis.getGridLineWidth());

        BarData bd = mChart.getData();
        // take into consideration that multiple DataSets increase mDeltaX
        int step = bd.getDataSetCount();

        for (int i = 0; i < mXAxis.getValues().size(); i += mXAxis.mAxisLabelModulus) {

            position[0] = i * step + i * bd.getGroupSpace() - 0.5f;

            mTrans.pointValuesToPixel(position);

            if (mViewPortHandler.isInBoundsX(position[0])) {

                c.drawLine(position[0], mViewPortHandler.offsetTop(), position[0],
                        mViewPortHandler.contentBottom(), mGridPaint);
            }
        }
    }
}
