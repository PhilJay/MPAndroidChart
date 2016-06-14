
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;

import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition;
import com.github.mikephil.charting.utils.PointD;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.List;

public class YAxisRendererHorizontalBarChart extends YAxisRenderer {

    public YAxisRendererHorizontalBarChart(ViewPortHandler viewPortHandler, YAxis yAxis,
                                           Transformer trans) {
        super(viewPortHandler, yAxis, trans);

        mLimitLinePaint.setTextAlign(Align.LEFT);
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
                yMin = (float) p2.x;
                yMax = (float) p1.x;
            }
        }

        computeAxisValues(yMin, yMax);
    }

    /**
     * draws the y-axis labels to the screen
     */
    @Override
    public void renderAxisLabels(Canvas c) {

        if (!mYAxis.isEnabled() || !mYAxis.isDrawLabelsEnabled())
            return;

        float[] positions = new float[mYAxis.mEntryCount * 2];

        for (int i = 0; i < positions.length; i += 2) {
            // only fill y values, x values are not needed since the y-labels
            // are
            // static on the x-axis
            positions[i] = mYAxis.mEntries[i / 2];
        }

        mTrans.pointValuesToPixel(positions);

        mAxisLabelPaint.setTypeface(mYAxis.getTypeface());
        mAxisLabelPaint.setTextSize(mYAxis.getTextSize());
        mAxisLabelPaint.setFakeBoldText(mYAxis.getTextBold());
        mAxisLabelPaint.setColor(mYAxis.getTextColor());
        mAxisLabelPaint.setTextAlign(Align.CENTER);

        float baseYOffset = Utils.convertDpToPixel(2.5f);
        float textHeight = Utils.calcTextHeight(mAxisLabelPaint, "Q");

        AxisDependency dependency = mYAxis.getAxisDependency();
        YAxisLabelPosition labelPosition = mYAxis.getLabelPosition();

        float yPos = 0f;

        if (dependency == AxisDependency.LEFT) {

            if (labelPosition == YAxisLabelPosition.OUTSIDE_CHART) {
                yPos = mViewPortHandler.contentTop() - baseYOffset;
            } else {
                yPos = mViewPortHandler.contentTop() - baseYOffset;
            }

        } else {

            if (labelPosition == YAxisLabelPosition.OUTSIDE_CHART) {
                yPos = mViewPortHandler.contentBottom() + textHeight + baseYOffset;
            } else {
                yPos = mViewPortHandler.contentBottom() + textHeight + baseYOffset;
            }
        }

        drawYLabels(c, yPos, positions, mYAxis.getYOffset());
    }

    @Override
    public void renderAxisLine(Canvas c) {

        if (!mYAxis.isEnabled() || !mYAxis.isDrawAxisLineEnabled())
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

        mAxisLabelPaint.setTypeface(mYAxis.getTypeface());
        mAxisLabelPaint.setTextSize(mYAxis.getTextSize());
        mAxisLabelPaint.setFakeBoldText(mYAxis.getTextBold());
        mAxisLabelPaint.setColor(mYAxis.getTextColor());

        for (int i = 0; i < mYAxis.mEntryCount; i++) {

            String text = mYAxis.getFormattedLabel(i);

            if (!mYAxis.isDrawTopYLabelEntryEnabled() && i >= mYAxis.mEntryCount - 1)
                return;

            c.drawText(text, positions[i * 2], fixedPosition - offset, mAxisLabelPaint);
        }
    }

    @Override
    public void renderGridLines(Canvas c) {

        if (!mYAxis.isEnabled())
            return;

        // pre alloc
        float[] position = new float[2];

        if (mYAxis.isDrawGridLinesEnabled()) {

            mGridPaint.setColor(mYAxis.getGridColor());
            mGridPaint.setStrokeWidth(mYAxis.getGridLineWidth());

            // draw the horizontal grid
            for (int i = 0; i < mYAxis.mEntryCount; i++) {

                position[0] = mYAxis.mEntries[i];
                mTrans.pointValuesToPixel(position);

                c.drawLine(position[0], mViewPortHandler.contentTop(), position[0],
                        mViewPortHandler.contentBottom(),
                        mGridPaint);
            }
        }

        if (mYAxis.isDrawZeroLineEnabled()) {

            // draw zero line
            position[0] = 0f;
            mTrans.pointValuesToPixel(position);

            drawZeroLine(c, position[0]+1, position[0]+1, mViewPortHandler.contentTop(), mViewPortHandler.contentBottom());
        }
    }

    /**
     * Draws the LimitLines associated with this axis to the screen.
     * This is the standard XAxis renderer using the YAxis limit lines.
     *
     * @param c
     */
    @Override
    public void renderLimitLines(Canvas c) {

        List<LimitLine> limitLines = mYAxis.getLimitLines();

        if (limitLines == null || limitLines.size() <= 0)
            return;

        float[] pts = new float[4];
        Path limitLinePath = new Path();

        for (int i = 0; i < limitLines.size(); i++) {

            LimitLine l = limitLines.get(i);

            if (!l.isEnabled())
                continue;

            pts[0] = l.getLimit();
            pts[2] = l.getLimit();

            mTrans.pointValuesToPixel(pts);

            pts[1] = mViewPortHandler.contentTop();
            pts[3] = mViewPortHandler.contentBottom();

            limitLinePath.moveTo(pts[0], pts[1]);
            limitLinePath.lineTo(pts[2], pts[3]);

            mLimitLinePaint.setStyle(Paint.Style.STROKE);
            mLimitLinePaint.setColor(l.getLineColor());
            mLimitLinePaint.setPathEffect(l.getDashPathEffect());
            mLimitLinePaint.setStrokeWidth(l.getLineWidth());

            c.drawPath(limitLinePath, mLimitLinePaint);
            limitLinePath.reset();

            String label = l.getLabel();

            // if drawing the limit-value label is enabled
            if (label != null && !label.equals("")) {

                mLimitLinePaint.setStyle(l.getTextStyle());
                mLimitLinePaint.setPathEffect(null);
                mLimitLinePaint.setColor(l.getTextColor());
                mLimitLinePaint.setTypeface(l.getTypeface());
                mLimitLinePaint.setStrokeWidth(0.5f);
                mLimitLinePaint.setTextSize(l.getTextSize());
                mLimitLinePaint.setFakeBoldText(l.getTextBold());

                float xOffset = l.getLineWidth() + l.getXOffset();
                float yOffset = Utils.convertDpToPixel(2f) + l.getYOffset();

                final LimitLine.LimitLabelPosition position = l.getLabelPosition();

                if (position == LimitLine.LimitLabelPosition.RIGHT_TOP) {

                    final float labelLineHeight = Utils.calcTextHeight(mLimitLinePaint, label);
                    mLimitLinePaint.setTextAlign(Align.LEFT);
                    c.drawText(label, pts[0] + xOffset, mViewPortHandler.contentTop() + yOffset + labelLineHeight, mLimitLinePaint);
                } else if (position == LimitLine.LimitLabelPosition.RIGHT_BOTTOM) {

                    mLimitLinePaint.setTextAlign(Align.LEFT);
                    c.drawText(label, pts[0] + xOffset, mViewPortHandler.contentBottom() - yOffset, mLimitLinePaint);
                } else if (position == LimitLine.LimitLabelPosition.LEFT_TOP) {

                    mLimitLinePaint.setTextAlign(Align.RIGHT);
                    final float labelLineHeight = Utils.calcTextHeight(mLimitLinePaint, label);
                    c.drawText(label, pts[0] - xOffset, mViewPortHandler.contentTop() + yOffset + labelLineHeight, mLimitLinePaint);
                } else {

                    mLimitLinePaint.setTextAlign(Align.RIGHT);
                    c.drawText(label, pts[0] - xOffset, mViewPortHandler.contentBottom() - yOffset, mLimitLinePaint);
                }
            }
        }
    }
}
