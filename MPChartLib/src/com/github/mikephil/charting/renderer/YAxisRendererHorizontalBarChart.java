
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Paint.Align;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition;
import com.github.mikephil.charting.utils.PointD;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;

public class YAxisRendererHorizontalBarChart extends YAxisRenderer {

    public YAxisRendererHorizontalBarChart(ViewPortHandler viewPortHandler, YAxis yAxis,
            Transformer trans) {
        super(viewPortHandler, yAxis, trans);
    }

    /**
     * Computes the axis values.
     * 
     * @param yMin - the minimum y-value in the data object for this axis
     * @param yMax - the maximum y-value in the data object for this axis
     */
    public void computeAxis(float yMin, float yMax) {

        // calculate the starting and entry point of the y-labels (depending on
        // zoom / contentrect bounds)
        if (mViewPortHandler.contentHeight() > 10 && !mViewPortHandler.isFullyZoomedOutX()) {

            PointD p1 = mTrans.getValuesByTouchPoint(mViewPortHandler.contentLeft(),
                    mViewPortHandler.contentTop());
            PointD p2 = mTrans.getValuesByTouchPoint(mViewPortHandler.contentRight(),
                    mViewPortHandler.contentTop());

            if (!mYAxis.isInverted()) {
                yMin = (float) p1.x;
                yMax = (float) p2.x;
            } else {

                if (!mYAxis.isStartAtZeroEnabled())
                    yMin = (float) Math.min(p1.x, p2.x);
                else
                    yMin = 0;
                yMax = (float) Math.max(p1.x, p2.x);
            }
        }

        computeAxisValues(yMin, yMax);
    }

    /**
     * draws the y-axis labels to the screen
     */
    @Override
    public void renderAxis(Canvas c) {

        if (!mYAxis.isEnabled())
            return;

        float[] positions = new float[mYAxis.mEntryCount * 2];

        for (int i = 0; i < positions.length; i += 2) {
            // only fill y values, x values are not needed since the y-labels
            // are
            // static on the x-axis
            positions[i] = mYAxis.mEntries[i / 2];
        }

        mTrans.pointValuesToPixel(positions);

        mAxisPaint.setTypeface(mYAxis.getTypeface());
        mAxisPaint.setTextSize(mYAxis.getTextSize());
        mAxisPaint.setColor(mYAxis.getTextColor());
        mAxisPaint.setTextAlign(Align.CENTER);

        float yoffset = Utils.calcTextHeight(mAxisPaint, "A") * 1.3f;

        AxisDependency dependency = mYAxis.getAxisDependency();
        YAxisLabelPosition labelPosition = mYAxis.getLabelPosition();

        float yPos = 0f;

        if (dependency == AxisDependency.LEFT) {

            if (labelPosition == YAxisLabelPosition.OUTSIDE_CHART) {
                yoffset = Utils.convertDpToPixel(3f);
                yPos = mViewPortHandler.contentTop();
            } else {
                yoffset = yoffset * -1f;
                yPos = mViewPortHandler.contentTop();
            }

        } else {

            if (labelPosition == YAxisLabelPosition.OUTSIDE_CHART) {
                yoffset = yoffset * -1f;
                yPos = mViewPortHandler.contentBottom();
            } else {
                yoffset = Utils.convertDpToPixel(4f);
                yPos = mViewPortHandler.contentBottom();
            }
        }

        drawYLabels(c, yPos, positions, yoffset);

        drawAxisLine(c);
    }

    @Override
    protected void drawAxisLine(Canvas c) {

        if (!mYAxis.isDrawAxisLineEnabled())
            return;

        mAxisLinePaint.setColor(mYAxis.getAxisLineColor());
        mAxisLinePaint.setStrokeWidth(mYAxis.getAxisLineWidth());

        if (mYAxis.getAxisDependency() == AxisDependency.LEFT) {
            c.drawLine(mViewPortHandler.contentLeft(),
                    mViewPortHandler.contentTop(), mViewPortHandler.contentRight(),
                    mViewPortHandler.contentTop(), mAxisLinePaint);
        } else {
            c.drawLine(mViewPortHandler.contentLeft(),
                    mViewPortHandler.contentBottom(), mViewPortHandler.contentRight(),
                    mViewPortHandler.contentBottom(), mAxisLinePaint);
        }
    }

    /**
     * draws the y-labels on the specified x-position
     * 
     * @param fixedPosition
     * @param positions
     */
    @Override
    protected void drawYLabels(Canvas c, float fixedPosition, float[] positions, float offset) {

        for (int i = 0; i < mYAxis.mEntryCount; i++) {

            String text = mYAxis.getFormattedLabel(i);

            if (!mYAxis.isDrawTopYLabelEntryEnabled() && i >= mYAxis.mEntryCount - 1)
                return;

            c.drawText(text, positions[i * 2], fixedPosition - offset, mAxisPaint);
        }
    }

    @Override
    public void renderGridLines(Canvas c) {

        if (!mYAxis.isDrawGridLinesEnabled() || !mYAxis.isEnabled())
            return;

        // pre alloc
        float[] position = new float[2];

        mGridPaint.setColor(mYAxis.getGridColor());

        // draw the horizontal grid
        for (int i = 0; i < mYAxis.mEntryCount; i++) {

            position[0] = mYAxis.mEntries[i];
            mTrans.pointValuesToPixel(position);

            c.drawLine(position[0], mViewPortHandler.contentTop(), position[0],
                    mViewPortHandler.contentBottom(),
                    mGridPaint);
        }
    }
}
