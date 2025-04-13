
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.RectF;

import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition;
import com.github.mikephil.charting.utils.MPPointD;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.List;

public class YAxisRendererHorizontalBarChart extends YAxisRenderer {

    public YAxisRendererHorizontalBarChart(ViewPortHandler viewPortHandler, YAxis yAxis,
                                           Transformer trans) {
        super(viewPortHandler, yAxis, trans);

        limitLinePaint.setTextAlign(Align.LEFT);
    }

    /**
     * Computes the axis values.
     *
     * @param yMin - the minimum y-value in the data object for this axis
     * @param yMax - the maximum y-value in the data object for this axis
     */
    @Override
    public void computeAxis(float yMin, float yMax, boolean inverted) {

        // calculate the starting and entry point of the y-labels (depending on
        // zoom / contentrect bounds)
        if (viewPortHandler.contentHeight() > 10 && !viewPortHandler.isFullyZoomedOutX()) {

            MPPointD p1 = transformer.getValuesByTouchPoint(viewPortHandler.contentLeft(),
                    viewPortHandler.contentTop());
            MPPointD p2 = transformer.getValuesByTouchPoint(viewPortHandler.contentRight(),
                    viewPortHandler.contentTop());

            if (!inverted) {
                yMin = (float) p1.x;
                yMax = (float) p2.x;
            } else {
                yMin = (float) p2.x;
                yMax = (float) p1.x;
            }

            MPPointD.recycleInstance(p1);
            MPPointD.recycleInstance(p2);
        }

        computeAxisValues(yMin, yMax);
    }

    /**
     * draws the y-axis labels to the screen
     */
    @Override
    public void renderAxisLabels(Canvas c) {

        if (!yAxis.isEnabled() || !yAxis.isDrawLabelsEnabled())
            return;

        float[] positions = getTransformedPositions();

        paintAxisLabels.setTypeface(yAxis.getTypeface());
        paintAxisLabels.setTextSize(yAxis.getTextSize());
        paintAxisLabels.setColor(yAxis.getTextColor());
        paintAxisLabels.setTextAlign(Align.CENTER);

        float baseYOffset = Utils.convertDpToPixel(2.5f);
        float textHeight = Utils.calcTextHeight(paintAxisLabels, "Q");

        AxisDependency dependency = yAxis.getAxisDependency();
        YAxisLabelPosition labelPosition = yAxis.getLabelPosition();

        float yPos = 0f;

        if (dependency == AxisDependency.LEFT) {

            if (labelPosition == YAxisLabelPosition.OUTSIDE_CHART) {
                yPos = viewPortHandler.contentTop() - baseYOffset;
            } else {
                yPos = viewPortHandler.contentTop() - baseYOffset;
            }

        } else {

            if (labelPosition == YAxisLabelPosition.OUTSIDE_CHART) {
                yPos = viewPortHandler.contentBottom() + textHeight + baseYOffset;
            } else {
                yPos = viewPortHandler.contentBottom() + textHeight + baseYOffset;
            }
        }

        drawYLabels(c, yPos, positions, yAxis.getYOffset());
    }

    @Override
    public void renderAxisLine(Canvas c) {

        if (!yAxis.isEnabled() || !yAxis.isDrawAxisLineEnabled())
            return;

        paintAxisLine.setColor(yAxis.getAxisLineColor());
        paintAxisLine.setStrokeWidth(yAxis.getAxisLineWidth());

        if (yAxis.getAxisDependency() == AxisDependency.LEFT) {
            c.drawLine(viewPortHandler.contentLeft(),
                    viewPortHandler.contentTop(), viewPortHandler.contentRight(),
                    viewPortHandler.contentTop(), paintAxisLine);
        } else {
            c.drawLine(viewPortHandler.contentLeft(),
                    viewPortHandler.contentBottom(), viewPortHandler.contentRight(),
                    viewPortHandler.contentBottom(), paintAxisLine);
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

        paintAxisLabels.setTypeface(yAxis.getTypeface());
        paintAxisLabels.setTextSize(yAxis.getTextSize());
        paintAxisLabels.setColor(yAxis.getTextColor());

        final int from = yAxis.isDrawBottomYLabelEntryEnabled() ? 0 : 1;
        final int to = yAxis.isDrawTopYLabelEntryEnabled()
                ? yAxis.mEntryCount
                : (yAxis.mEntryCount - 1);

        float xOffset = yAxis.getLabelXOffset();

        for (int i = from; i < to; i++) {

            String text = yAxis.getFormattedLabel(i);

            c.drawText(text,
                    positions[i * 2],
                    fixedPosition - offset + xOffset,
                    paintAxisLabels);
        }
    }

    @Override
    protected float[] getTransformedPositions() {

        if(mGetTransformedPositionsBuffer.length != yAxis.mEntryCount * 2) {
            mGetTransformedPositionsBuffer = new float[yAxis.mEntryCount * 2];
        }
        float[] positions = mGetTransformedPositionsBuffer;

        for (int i = 0; i < positions.length; i += 2) {
            // only fill x values, y values are not needed for x-labels
            positions[i] = yAxis.mEntries[i / 2];
        }

        transformer.pointValuesToPixel(positions);
        return positions;
    }

    @Override
    public RectF getGridClippingRect() {
        mGridClippingRect.set(viewPortHandler.getContentRect());
        mGridClippingRect.inset(-axis.getGridLineWidth(), 0.f);
        return mGridClippingRect;
    }

    @Override
    protected Path linePath(Path p, int i, float[] positions) {

        p.moveTo(positions[i], viewPortHandler.contentTop());
        p.lineTo(positions[i], viewPortHandler.contentBottom());

        return p;
    }

    protected Path mDrawZeroLinePathBuffer = new Path();

    @Override
    protected void drawZeroLine(Canvas c) {

        int clipRestoreCount = c.save();
        zeroLineClippingRect.set(viewPortHandler.getContentRect());
        zeroLineClippingRect.inset(-yAxis.getZeroLineWidth(), 0.f);
        c.clipRect(limitLineClippingRect);

        // draw zero line
        MPPointD pos = transformer.getPixelForValues(0f, 0f);

        zeroLinePaint.setColor(yAxis.getZeroLineColor());
        zeroLinePaint.setStrokeWidth(yAxis.getZeroLineWidth());

        Path zeroLinePath = mDrawZeroLinePathBuffer;
        zeroLinePath.reset();

        zeroLinePath.moveTo((float) pos.x - 1, viewPortHandler.contentTop());
        zeroLinePath.lineTo((float) pos.x - 1, viewPortHandler.contentBottom());

        // draw a path because lines don't support dashing on lower android versions
        c.drawPath(zeroLinePath, zeroLinePaint);

        c.restoreToCount(clipRestoreCount);
    }

    protected Path mRenderLimitLinesPathBuffer = new Path();
    protected float[] mRenderLimitLinesBuffer = new float[4];
    /**
     * Draws the LimitLines associated with this axis to the screen.
     * This is the standard XAxis renderer using the YAxis limit lines.
     *
     * @param c
     */
    @Override
    public void renderLimitLines(Canvas c) {

        List<LimitLine> limitLines = yAxis.getLimitLines();

        if (limitLines == null || limitLines.size() <= 0)
            return;

        float[] pts = mRenderLimitLinesBuffer;
        pts[0] = 0;
        pts[1] = 0;
        pts[2] = 0;
        pts[3] = 0;
        Path limitLinePath = mRenderLimitLinesPathBuffer;
        limitLinePath.reset();

        for (int i = 0; i < limitLines.size(); i++) {

            LimitLine l = limitLines.get(i);

            if (!l.isEnabled())
                continue;

            int clipRestoreCount = c.save();
            limitLineClippingRect.set(viewPortHandler.getContentRect());
            limitLineClippingRect.inset(-l.getLineWidth(), 0.f);
            c.clipRect(limitLineClippingRect);

            pts[0] = l.getLimit();
            pts[2] = l.getLimit();

            transformer.pointValuesToPixel(pts);

            pts[1] = viewPortHandler.contentTop();
            pts[3] = viewPortHandler.contentBottom();

            limitLinePath.moveTo(pts[0], pts[1]);
            limitLinePath.lineTo(pts[2], pts[3]);

            limitLinePaint.setStyle(Paint.Style.STROKE);
            limitLinePaint.setColor(l.getLineColor());
            limitLinePaint.setPathEffect(l.getDashPathEffect());
            limitLinePaint.setStrokeWidth(l.getLineWidth());

            c.drawPath(limitLinePath, limitLinePaint);
            limitLinePath.reset();

            String label = l.getLabel();

            // if drawing the limit-value label is enabled
            if (label != null && !label.equals("")) {

                limitLinePaint.setStyle(l.getTextStyle());
                limitLinePaint.setPathEffect(null);
                limitLinePaint.setColor(l.getTextColor());
                limitLinePaint.setTypeface(l.getTypeface());
                limitLinePaint.setStrokeWidth(0.5f);
                limitLinePaint.setTextSize(l.getTextSize());

                float xOffset = l.getLineWidth() + l.getXOffset();
                float yOffset = Utils.convertDpToPixel(2f) + l.getYOffset();

                final LimitLine.LimitLabelPosition position = l.getLabelPosition();

                if (position == LimitLine.LimitLabelPosition.RIGHT_TOP) {

                    final float labelLineHeight = Utils.calcTextHeight(limitLinePaint, label);
                    limitLinePaint.setTextAlign(Align.LEFT);
                    c.drawText(label, pts[0] + xOffset, viewPortHandler.contentTop() + yOffset + labelLineHeight, limitLinePaint);
                } else if (position == LimitLine.LimitLabelPosition.RIGHT_BOTTOM) {

                    limitLinePaint.setTextAlign(Align.LEFT);
                    c.drawText(label, pts[0] + xOffset, viewPortHandler.contentBottom() - yOffset, limitLinePaint);
                } else if (position == LimitLine.LimitLabelPosition.LEFT_TOP) {

                    limitLinePaint.setTextAlign(Align.RIGHT);
                    final float labelLineHeight = Utils.calcTextHeight(limitLinePaint, label);
                    c.drawText(label, pts[0] - xOffset, viewPortHandler.contentTop() + yOffset + labelLineHeight, limitLinePaint);
                } else {

                    limitLinePaint.setTextAlign(Align.RIGHT);
                    c.drawText(label, pts[0] - xOffset, viewPortHandler.contentBottom() - yOffset, limitLinePaint);
                }
            }

            c.restoreToCount(clipRestoreCount);
        }
    }
}
