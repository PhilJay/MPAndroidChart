
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.List;

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
    protected void drawLabels(Canvas c, float pos, PointF anchor, XAxis.XAxisPosition axisPosition) {

        final float labelRotationAngleDegrees = mXAxis.getLabelRotationAngle();

        // pre allocate to save performance (dont allocate in loop)
        float[] position = new float[] {
                0f, 0f
        };

        BarData bd = mChart.getData();
        int step = bd.getDataSetCount();

        for (int i = mMinX; i <= mMaxX; i += mXAxis.mAxisLabelModulus) {

            position[0] = i * step + i * bd.getGroupSpace()
                    + bd.getGroupSpace() / 2f;

            // consider groups (center label for each group)
            if (step > 1) {
                position[0] += ((float) step - 1f) / 2f;
            }

            mTrans.pointValuesToPixel(position);

            if (mViewPortHandler.isInBoundsX(position[0]) && i >= 0
                    && i < mXAxis.getValues().size()) {

                String label = mXAxis.getValues().get(i);
                float width = Utils.calcTextWidth(mAxisLabelPaint, label);

                if (mXAxis.isAvoidFirstLastClippingEnabled()) {

                    // avoid clipping of the last
                    if (i == mXAxis.getValues().size() - 1) {
                        if (position[0] + width / 2.f > mViewPortHandler.contentRight())
                            position[0] = mViewPortHandler.contentRight() - (width / 2.f);

                        // avoid clipping of the first
                    } else if (i == 0) {
                        if (position[0] - width / 2.f < mViewPortHandler.contentLeft())
                            position[0] = mViewPortHandler.contentLeft() + (width / 2.f);
                    }
                }

                List<Integer> formColors = mXAxis.getFormColors();
                if (formColors != null) {
                    int formColor = formColors.get(i);
                    float formSize = mXAxis.getFormSize();
                    float formRadius = mXAxis.getFormRadius();
                    float formOffset = mXAxis.getFormOffset();
                    float textHeight = Utils.calcTextHeight(mAxisLabelPaint, label);
                    float height = Math.max(formSize, textHeight);

                    float formYPos = pos;
                    float textYPos = pos;
                    switch (axisPosition) {
                        case TOP:
                            formYPos = pos - height + (height - formSize) / 2;
                            textYPos = pos - height + (height - textHeight) / 2 + textHeight;
                            break;

                        case BOTTOM_INSIDE:
                            formYPos = pos - height + (height - formSize) / 2;
                            textYPos = pos - height + (height - textHeight) / 2;
                            break;

                        case TOP_INSIDE:
                            formYPos = pos + (height - formSize) / 2;
                            textYPos = pos + (height - textHeight) / 2 + textHeight;
                            break;

                        case BOTTOM:
                            formYPos = pos + (height - formSize) / 2;
                            textYPos = pos + (height - textHeight) / 2;
                            break;
                    }

                    mAxisFormPaint.setColor(formColor);

                    if (formColor != ColorTemplate.COLOR_SKIP) {
                        position[0] -= width + formOffset / 2;

                        c.drawRoundRect(new RectF(position[0], formYPos, position[0] + formSize, formYPos + formSize), formRadius, formRadius, mAxisFormPaint);
                        drawLabel(c, label, i, position[0] + formSize + formOffset + width / 2, textYPos, anchor, labelRotationAngleDegrees);
                    } else {
                        drawLabel(c, label, i, position[0], textYPos, anchor, labelRotationAngleDegrees);
                    }
                } else {
                    drawLabel(c, label, i, position[0], pos, anchor, labelRotationAngleDegrees);
                }
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
        int step = bd.getDataSetCount();

        for (int i = mMinX; i < mMaxX; i += mXAxis.mAxisLabelModulus) {

            position[0] = i * step + i * bd.getGroupSpace() - 0.5f;

            mTrans.pointValuesToPixel(position);

            if (mViewPortHandler.isInBoundsX(position[0])) {

                c.drawLine(position[0], mViewPortHandler.offsetTop(), position[0],
                        mViewPortHandler.contentBottom(), mGridPaint);
            }
        }
    }
}
