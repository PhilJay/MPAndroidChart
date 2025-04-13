
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.RectF;

import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.utils.FSize;
import com.github.mikephil.charting.utils.MPPointD;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.List;

public class XAxisRenderer extends AxisRenderer {

    protected XAxis mXAxis;

    public XAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans) {
        super(viewPortHandler, trans, xAxis);

        this.mXAxis = xAxis;

        paintAxisLabels.setColor(Color.BLACK);
        paintAxisLabels.setTextAlign(Align.CENTER);
        paintAxisLabels.setTextSize(Utils.convertDpToPixel(10f));
    }

    protected void setupGridPaint() {
        paintGrid.setColor(mXAxis.getGridColor());
        paintGrid.setStrokeWidth(mXAxis.getGridLineWidth());
        paintGrid.setPathEffect(mXAxis.getGridDashPathEffect());
    }

    @Override
    public void computeAxis(float min, float max, boolean inverted) {

        // calculate the starting and entry point of the y-labels (depending on
        // zoom / contentrect bounds)
        if (viewPortHandler.contentWidth() > 10 && !viewPortHandler.isFullyZoomedOutX()) {

            MPPointD p1 = transformer.getValuesByTouchPoint(viewPortHandler.contentLeft(), viewPortHandler.contentTop());
            MPPointD p2 = transformer.getValuesByTouchPoint(viewPortHandler.contentRight(), viewPortHandler.contentTop());

            if (inverted) {

                min = (float) p2.x;
                max = (float) p1.x;
            } else {

                min = (float) p1.x;
                max = (float) p2.x;
            }

            MPPointD.recycleInstance(p1);
            MPPointD.recycleInstance(p2);
        }

        computeAxisValues(min, max);
    }

    @Override
    protected void computeAxisValues(float min, float max) {
        super.computeAxisValues(min, max);

        computeSize();
    }

    protected void computeSize() {

        String longest = mXAxis.getLongestLabel();

        paintAxisLabels.setTypeface(mXAxis.getTypeface());
        paintAxisLabels.setTextSize(mXAxis.getTextSize());

        final FSize labelSize = Utils.calcTextSize(paintAxisLabels, longest);

        final float labelWidth = labelSize.width;
        final float labelHeight = Utils.calcTextHeight(paintAxisLabels, "Q");

        final FSize labelRotatedSize = Utils.getSizeOfRotatedRectangleByDegrees(
                labelWidth,
                labelHeight,
                mXAxis.getLabelRotationAngle());


        mXAxis.mLabelWidth = Math.round(labelRotatedSize.width);
        mXAxis.mLabelHeight = Math.round(labelRotatedSize.height);

        FSize.recycleInstance(labelRotatedSize);
        FSize.recycleInstance(labelSize);
    }

    @Override
    public void renderAxisLabels(Canvas c) {

        if (!mXAxis.isEnabled() || !mXAxis.isDrawLabelsEnabled())
            return;

        float yoffset = mXAxis.getYOffset();

        paintAxisLabels.setColor(mXAxis.getTextColor());

        MPPointF pointF = MPPointF.getInstance(0,0);
        if (mXAxis.getPosition() == XAxisPosition.TOP) {
            pointF.x = 0.5f;
            pointF.y = 1.0f;
            drawLabels(c, viewPortHandler.contentTop() - yoffset, pointF);

        } else if (mXAxis.getPosition() == XAxisPosition.TOP_INSIDE) {
            pointF.x = 0.5f;
            pointF.y = 1.0f;
            drawLabels(c, viewPortHandler.contentTop() + yoffset + mXAxis.mLabelHeight, pointF);

        } else if (mXAxis.getPosition() == XAxisPosition.BOTTOM) {
            pointF.x = 0.5f;
            pointF.y = 0.0f;
            drawLabels(c, viewPortHandler.contentBottom() + yoffset, pointF);

        } else if (mXAxis.getPosition() == XAxisPosition.BOTTOM_INSIDE) {
            pointF.x = 0.5f;
            pointF.y = 0.0f;
            drawLabels(c, viewPortHandler.contentBottom() - yoffset - mXAxis.mLabelHeight, pointF);

        } else { // BOTH SIDED
            pointF.x = 0.5f;
            pointF.y = 1.0f;
            drawLabels(c, viewPortHandler.contentTop() - yoffset, pointF);
            pointF.x = 0.5f;
            pointF.y = 0.0f;
            drawLabels(c, viewPortHandler.contentBottom() + yoffset, pointF);
        }
        MPPointF.recycleInstance(pointF);
    }

    @Override
    public void renderAxisLine(Canvas c) {

        if (!mXAxis.isDrawAxisLineEnabled() || !mXAxis.isEnabled())
            return;

        paintAxisLine.setColor(mXAxis.getAxisLineColor());
        paintAxisLine.setStrokeWidth(mXAxis.getAxisLineWidth());
        paintAxisLine.setPathEffect(mXAxis.getAxisLineDashPathEffect());

        if (mXAxis.getPosition() == XAxisPosition.TOP
                || mXAxis.getPosition() == XAxisPosition.TOP_INSIDE
                || mXAxis.getPosition() == XAxisPosition.BOTH_SIDED) {
            c.drawLine(viewPortHandler.contentLeft(),
                    viewPortHandler.contentTop(), viewPortHandler.contentRight(),
                    viewPortHandler.contentTop(), paintAxisLine);
        }

        if (mXAxis.getPosition() == XAxisPosition.BOTTOM
                || mXAxis.getPosition() == XAxisPosition.BOTTOM_INSIDE
                || mXAxis.getPosition() == XAxisPosition.BOTH_SIDED) {
            c.drawLine(viewPortHandler.contentLeft(),
                    viewPortHandler.contentBottom(), viewPortHandler.contentRight(),
                    viewPortHandler.contentBottom(), paintAxisLine);
        }
    }

    /**
     * draws the x-labels on the specified y-position
     *
     * @param pos
     */
    protected void drawLabels(Canvas c, float pos, MPPointF anchor) {

        final float labelRotationAngleDegrees = mXAxis.getLabelRotationAngle();
        boolean centeringEnabled = mXAxis.isCenterAxisLabelsEnabled();

        float[] positions;

        if (mXAxis.isShowSpecificPositions()) {
            positions = new float[mXAxis.getSpecificPositions().length * 2];
            for (int i = 0; i < positions.length; i += 2) {
                positions[i] = mXAxis.getSpecificPositions()[i / 2];
            }
        } else {
            positions = new float[mXAxis.mEntryCount * 2];
            for (int i = 0; i < positions.length; i += 2) {
                // only fill x values
                if (centeringEnabled) {
                    positions[i] = mXAxis.mCenteredEntries[i / 2];
                } else {
                    positions[i] = mXAxis.mEntries[i / 2];
                }
            }
        }

        transformer.pointValuesToPixel(positions);

        for (int i = 0; i < positions.length; i += 2) {

            float x = positions[i];

            if (viewPortHandler.isInBoundsX(x)) {

                String label = mXAxis.isShowSpecificPositions() ?
                        mXAxis.getValueFormatter().getFormattedValue(mXAxis.getSpecificPositions()[i / 2], mXAxis)
                        : mXAxis.getValueFormatter().getFormattedValue(mXAxis.mEntries[i / 2], mXAxis);

                if (mXAxis.isAvoidFirstLastClippingEnabled()) {

                    // avoid clipping of the last
                    if (i / 2 == mXAxis.mEntryCount - 1 && mXAxis.mEntryCount > 1) {
                        float width = Utils.calcTextWidth(paintAxisLabels, label);

                        if (width > viewPortHandler.offsetRight() * 2
                                && x + width > viewPortHandler.getChartWidth())
                            x -= width / 2;

                        // avoid clipping of the first
                    } else if (i == 0) {

                        float width = Utils.calcTextWidth(paintAxisLabels, label);
                        x += width / 2;
                    }
                }

                drawLabel(c, label, x, pos, anchor, labelRotationAngleDegrees);
            }
        }
    }

    protected void drawLabel(Canvas c, String formattedLabel, float x, float y, MPPointF anchor, float angleDegrees) {
        Utils.drawXAxisValue(c, formattedLabel, x, y, paintAxisLabels, anchor, angleDegrees);
    }
    protected Path mRenderGridLinesPath = new Path();
    protected float[] mRenderGridLinesBuffer = new float[2];
    @Override
    public void renderGridLines(Canvas c) {

        if (!mXAxis.isDrawGridLinesEnabled() || !mXAxis.isEnabled())
            return;

        int clipRestoreCount = c.save();
        c.clipRect(getGridClippingRect());
        
        if (axis.isShowSpecificPositions()) {
            if (mRenderGridLinesBuffer.length != axis.getSpecificPositions().length * 2) {
                mRenderGridLinesBuffer = new float[mXAxis.getSpecificPositions().length * 2];
            }
        } else {
            if (mRenderGridLinesBuffer.length != axis.mEntryCount * 2) {
                mRenderGridLinesBuffer = new float[mXAxis.mEntryCount * 2];
            }
        }
        float[] positions = mRenderGridLinesBuffer;

        for (int i = 0; i < positions.length; i += 2) {
            if (axis.isShowSpecificPositions()) {
                positions[i] = mXAxis.getSpecificPositions()[i / 2];
                positions[i + 1] = mXAxis.getSpecificPositions()[i / 2];
            } else {
                positions[i] = mXAxis.mEntries[i / 2];
                positions[i + 1] = mXAxis.mEntries[i / 2];
            }
        }

        transformer.pointValuesToPixel(positions);

        setupGridPaint();

        Path gridLinePath = mRenderGridLinesPath;
        gridLinePath.reset();

        for (int i = 0; i < positions.length; i += 2) {
            drawGridLine(c, positions[i], positions[i + 1], gridLinePath);
        }

        c.restoreToCount(clipRestoreCount);
    }

    protected RectF mGridClippingRect = new RectF();

    public RectF getGridClippingRect() {
        mGridClippingRect.set(viewPortHandler.getContentRect());
        mGridClippingRect.inset(-axis.getGridLineWidth(), 0.f);
        return mGridClippingRect;
    }

    /**
     * Draws the grid line at the specified position using the provided path.
     *
     * @param c
     * @param x
     * @param y
     * @param gridLinePath
     */
    protected void drawGridLine(Canvas c, float x, float y, Path gridLinePath) {

        gridLinePath.moveTo(x, viewPortHandler.contentBottom());
        gridLinePath.lineTo(x, viewPortHandler.contentTop());

        // draw a path because lines don't support dashing on lower android versions
        c.drawPath(gridLinePath, paintGrid);

        gridLinePath.reset();
    }

    protected float[] mRenderLimitLinesBuffer = new float[2];
    protected RectF mLimitLineClippingRect = new RectF();

    /**
     * Draws the LimitLines associated with this axis to the screen.
     *
     * @param c
     */
    @Override
    public void renderLimitLines(Canvas c) {

        List<LimitLine> limitLines = mXAxis.getLimitLines();

        if (limitLines == null || limitLines.size() <= 0)
            return;

        float[] position = mRenderLimitLinesBuffer;
        position[0] = 0;
        position[1] = 0;

        for (int i = 0; i < limitLines.size(); i++) {

            LimitLine l = limitLines.get(i);

            if (!l.isEnabled())
                continue;

            int clipRestoreCount = c.save();
            mLimitLineClippingRect.set(viewPortHandler.getContentRect());
            mLimitLineClippingRect.inset(-l.getLineWidth(), 0.f);
            c.clipRect(mLimitLineClippingRect);

            position[0] = l.getLimit();
            position[1] = 0.f;

            transformer.pointValuesToPixel(position);

            renderLimitLineLine(c, l, position);
            renderLimitLineLabel(c, l, position, 2.f + l.getYOffset());

            c.restoreToCount(clipRestoreCount);
        }
    }

    float[] mLimitLineSegmentsBuffer = new float[4];
    private Path mLimitLinePath = new Path();

    public void renderLimitLineLine(Canvas c, LimitLine limitLine, float[] position) {
        mLimitLineSegmentsBuffer[0] = position[0];
        mLimitLineSegmentsBuffer[1] = viewPortHandler.contentTop();
        mLimitLineSegmentsBuffer[2] = position[0];
        mLimitLineSegmentsBuffer[3] = viewPortHandler.contentBottom();

        mLimitLinePath.reset();
        mLimitLinePath.moveTo(mLimitLineSegmentsBuffer[0], mLimitLineSegmentsBuffer[1]);
        mLimitLinePath.lineTo(mLimitLineSegmentsBuffer[2], mLimitLineSegmentsBuffer[3]);

        limitLinePaint.setStyle(Paint.Style.STROKE);
        limitLinePaint.setColor(limitLine.getLineColor());
        limitLinePaint.setStrokeWidth(limitLine.getLineWidth());
        limitLinePaint.setPathEffect(limitLine.getDashPathEffect());

        c.drawPath(mLimitLinePath, limitLinePaint);
    }

    public void renderLimitLineLabel(Canvas c, LimitLine limitLine, float[] position, float yOffset) {
        String label = limitLine.getLabel();

        // if drawing the limit-value label is enabled
        if (label != null && !label.equals("")) {

            limitLinePaint.setStyle(limitLine.getTextStyle());
            limitLinePaint.setPathEffect(null);
            limitLinePaint.setColor(limitLine.getTextColor());
            limitLinePaint.setStrokeWidth(0.5f);
            limitLinePaint.setTextSize(limitLine.getTextSize());


            float xOffset = limitLine.getLineWidth() + limitLine.getXOffset();

            final LimitLine.LimitLabelPosition labelPosition = limitLine.getLabelPosition();

            if (labelPosition == LimitLine.LimitLabelPosition.RIGHT_TOP) {

                final float labelLineHeight = Utils.calcTextHeight(limitLinePaint, label);
                limitLinePaint.setTextAlign(Align.LEFT);
                c.drawText(label, position[0] + xOffset, viewPortHandler.contentTop() + yOffset + labelLineHeight,
                        limitLinePaint);
            } else if (labelPosition == LimitLine.LimitLabelPosition.RIGHT_BOTTOM) {

                limitLinePaint.setTextAlign(Align.LEFT);
                c.drawText(label, position[0] + xOffset, viewPortHandler.contentBottom() - yOffset, limitLinePaint);
            } else if (labelPosition == LimitLine.LimitLabelPosition.LEFT_TOP) {

                limitLinePaint.setTextAlign(Align.RIGHT);
                final float labelLineHeight = Utils.calcTextHeight(limitLinePaint, label);
                c.drawText(label, position[0] - xOffset, viewPortHandler.contentTop() + yOffset + labelLineHeight,
                        limitLinePaint);
            } else {

                limitLinePaint.setTextAlign(Align.RIGHT);
                c.drawText(label, position[0] - xOffset, viewPortHandler.contentBottom() - yOffset, limitLinePaint);
            }
        }
    }
}
