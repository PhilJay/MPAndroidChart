
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;

public class XAxisRendererBarChart extends XAxisRenderer {

    private BarChart mChart;

    public XAxisRendererBarChart(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans,
            BarChart chart) {
        super(viewPortHandler, xAxis, trans);

        this.mChart = chart;
    }

    /**
     * draws the x-labels on the specified y-position
     * 
     * @param yPos
     */
    @Override
    protected void drawLabels(Canvas c, float yPos) {

        // pre allocate to save performance (dont allocate in loop)
        float[] position = new float[] {
                0f, 0f
        };

        BarData bd = mChart.getData();
        int step = bd.getDataSetCount();

        for (int i = 0; i < mXAxis.getValues().size(); i += mXAxis.mXAxisLabelModulus) {

            position[0] = i * step + i * bd.getGroupSpace()
                    + bd.getGroupSpace() / 2f;

            // center the text
            if (mXAxis.isCenterXLabelsEnabled())
                position[0] += (step / 2f);

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
                        yPos,
                        mAxisPaint);
            }
        }
    }

    @Override
    public void renderGridLines(Canvas c) {

        if (!mXAxis.isDrawGridLinesEnabled())
            return;

        float[] position = new float[] {
                0f, 0f
        };

        BarData bd = mChart.getData();
        // take into consideration that multiple DataSets increase mDeltaX
        int step = bd.getDataSetCount();

        for (int i = 0; i < mXAxis.getValues().size(); i += mXAxis.mXAxisLabelModulus) {

            position[0] = i * step + i * bd.getGroupSpace();

            mTrans.pointValuesToPixel(position);

            if (mViewPortHandler.isInBoundsX(position[0])) {

                c.drawLine(position[0], mViewPortHandler.offsetTop(), position[0],
                        mViewPortHandler.contentBottom(), mGridPaint);
            }
        }
    }
}
