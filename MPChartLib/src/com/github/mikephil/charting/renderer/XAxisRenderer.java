
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;

import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.List;

public class XAxisRenderer extends AxisRenderer {

    protected XAxis mXAxis;

    public XAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans) {
        super(viewPortHandler, trans);

        this.mXAxis = xAxis;

        mAxisLabelPaint.setColor(Color.BLACK);
        mAxisLabelPaint.setTextAlign(Align.CENTER);
        mAxisLabelPaint.setTextSize(Utils.convertDpToPixel(10f));
    }

    public void computeAxis(float xValAverageLength, List<String> xValues) {

        mAxisLabelPaint.setTypeface(mXAxis.getTypeface());
        mAxisLabelPaint.setTextSize(mXAxis.getTextSize());

        StringBuffer a = new StringBuffer();

        int max = (int) Math.round(xValAverageLength
                + mXAxis.getSpaceBetweenLabels());

        for (int i = 0; i < max; i++) {
            a.append('h');
        }

        mXAxis.mLabelWidth = Utils.calcTextWidth(mAxisLabelPaint, a.toString());
        mXAxis.mLabelHeight = Utils.calcTextHeight(mAxisLabelPaint, "Q");
        mXAxis.setValues(xValues);
    }

    @Override
    public void renderAxisLabels(Canvas c) {

        if (!mXAxis.isEnabled() || !mXAxis.isDrawLabelsEnabled())
            return;

        float yoffset = Utils.convertDpToPixel(4f);

        mAxisLabelPaint.setTypeface(mXAxis.getTypeface());
        mAxisLabelPaint.setTextSize(mXAxis.getTextSize());
        mAxisLabelPaint.setColor(mXAxis.getTextColor());

        if (mXAxis.getPosition() == XAxisPosition.TOP) {

            drawLabels(c, mViewPortHandler.offsetTop() - yoffset);

        } else if (mXAxis.getPosition() == XAxisPosition.BOTTOM) {

            drawLabels(c, mViewPortHandler.contentBottom() + mXAxis.mLabelHeight + yoffset * 1.5f);

        } else if (mXAxis.getPosition() == XAxisPosition.BOTTOM_INSIDE) {

            drawLabels(c, mViewPortHandler.contentBottom() - yoffset);

        } else if (mXAxis.getPosition() == XAxisPosition.TOP_INSIDE) {

            drawLabels(c, mViewPortHandler.offsetTop() + yoffset + mXAxis.mLabelHeight);

        } else { // BOTH SIDED

            drawLabels(c, mViewPortHandler.offsetTop() - yoffset);
            drawLabels(c, mViewPortHandler.contentBottom() + mXAxis.mLabelHeight + yoffset * 1.6f);
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
            c.drawLine(mViewPortHandler.contentLeft(),
                    mViewPortHandler.contentTop(), mViewPortHandler.contentRight(),
                    mViewPortHandler.contentTop(), mAxisLinePaint);
        }

        if (mXAxis.getPosition() == XAxisPosition.BOTTOM
                || mXAxis.getPosition() == XAxisPosition.BOTTOM_INSIDE
                || mXAxis.getPosition() == XAxisPosition.BOTH_SIDED) {
            c.drawLine(mViewPortHandler.contentLeft(),
                    mViewPortHandler.contentBottom(), mViewPortHandler.contentRight(),
                    mViewPortHandler.contentBottom(), mAxisLinePaint);
        }
    }

    /**
     * draws the x-labels on the specified y-position
     *
     * @param pos
     */
    protected void drawLabels(Canvas c, float pos) {

        // pre allocate to save performance (dont allocate in loop)
        float[] position = new float[]{
                0f, 0f
        };

        for (int i = mMinX; i <= mMaxX; i += mXAxis.mAxisLabelModulus) {

            position[0] = i;

            mTrans.pointValuesToPixel(position);

            if (mViewPortHandler.isInBoundsX(position[0])) {

                String label = mXAxis.getValues().get(i);

                if (mXAxis.isAvoidFirstLastClippingEnabled()) {

                    // avoid clipping of the last
                    if (i == mXAxis.getValues().size() - 1 && mXAxis.getValues().size() > 1) {
                        float width = Utils.calcTextWidth(mAxisLabelPaint, label);

                        if (width > mViewPortHandler.offsetRight() * 2
                                && position[0] + width > mViewPortHandler.getChartWidth())
                            position[0] -= width / 2;

                        // avoid clipping of the first
                    } else if (i == 0) {

                        float width = Utils.calcTextWidth(mAxisLabelPaint, label);
                        position[0] += width / 2;
                    }
                }

                drawLabel(c, label, i, position[0], pos);
            }
        }
    }

    protected void drawLabel(Canvas c, String label, int xIndex, float x, float y) {
        String formattedLabel = mXAxis.getValueFormatter().getXValue(label, xIndex, mViewPortHandler);
        c.drawText(formattedLabel, x, y, mAxisLabelPaint);
    }

    @Override
    public void renderGridLines(Canvas c) {

        if (!mXAxis.isDrawGridLinesEnabled() || !mXAxis.isEnabled())
            return;

        // pre alloc
        float[] position = new float[]{
                0f, 0f
        };

        mGridPaint.setColor(mXAxis.getGridColor());
        mGridPaint.setStrokeWidth(mXAxis.getGridLineWidth());
        mGridPaint.setPathEffect(mXAxis.getGridDashPathEffect());

        Path gridLinePath = new Path();

        for (int i = mMinX; i <= mMaxX; i += mXAxis.mAxisLabelModulus) {

            position[0] = i;
            mTrans.pointValuesToPixel(position);

            if (position[0] >= mViewPortHandler.offsetLeft()
                    && position[0] <= mViewPortHandler.getChartWidth()) {

                gridLinePath.moveTo(position[0], mViewPortHandler.contentBottom());
                gridLinePath.lineTo(position[0], mViewPortHandler.contentTop());

                // draw a path because lines don't support dashing on lower android versions
                c.drawPath(gridLinePath, mGridPaint);
            }

            gridLinePath.reset();
        }
    }

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

        float[] position = new float[2];

        for (int i = 0; i < limitLines.size(); i++) {

            LimitLine l = limitLines.get(i);

            if (!l.isEnabled())
                continue;

            position[0] = l.getLimit();
            position[1] = 0.f;

            mTrans.pointValuesToPixel(position);

            renderLimitLineLine(c, l, position);
            renderLimitLineLabel(c, l, position, 2.f + l.getYOffset());
        }
    }

    float[] mLimitLineSegmentsBuffer = new float[4];
    private Path mLimitLinePath = new Path();

    public void renderLimitLineLine(Canvas c, LimitLine limitLine, float[] position) {
        mLimitLineSegmentsBuffer[0] = position[0];
        mLimitLineSegmentsBuffer[1] = mViewPortHandler.contentTop();
        mLimitLineSegmentsBuffer[2] = position[0];
        mLimitLineSegmentsBuffer[3] = mViewPortHandler.contentBottom();

        mLimitLinePath.reset();
        mLimitLinePath.moveTo(mLimitLineSegmentsBuffer[0], mLimitLineSegmentsBuffer[1]);
        mLimitLinePath.lineTo(mLimitLineSegmentsBuffer[2], mLimitLineSegmentsBuffer[3]);

        mLimitLinePaint.setStyle(Paint.Style.STROKE);
        mLimitLinePaint.setColor(limitLine.getLineColor());
        mLimitLinePaint.setStrokeWidth(limitLine.getLineWidth());
        mLimitLinePaint.setPathEffect(limitLine.getDashPathEffect());

        c.drawPath(mLimitLinePath, mLimitLinePaint);
    }

    public void renderLimitLineLabel(Canvas c, LimitLine limitLine, float[] position, float yOffset) {
        String label = limitLine.getLabel();

        // if drawing the limit-value label is enabled
        if (label != null && !label.equals("")) {

            mLimitLinePaint.setStyle(limitLine.getTextStyle());
            mLimitLinePaint.setPathEffect(null);
            mLimitLinePaint.setColor(limitLine.getTextColor());
            mLimitLinePaint.setStrokeWidth(0.5f);
            mLimitLinePaint.setTextSize(limitLine.getTextSize());

            float xOffset = limitLine.getLineWidth() + limitLine.getXOffset();

            final LimitLine.LimitLabelPosition labelPosition = limitLine.getLabelPosition();
            final float labelLineHeight = Utils.calcTextHeight(mLimitLinePaint, label);
            final int lineSpacing = 5;
            if (labelPosition == LimitLine.LimitLabelPosition.RIGHT_TOP) {
                mLimitLinePaint.setTextAlign(Align.LEFT);
                int lineCount = 1;
                for (String line : label.split("\n")) {
                    c.drawText(line, position[0] + xOffset, mViewPortHandler.contentTop() + yOffset + labelLineHeight * lineCount + lineSpacing, mLimitLinePaint);
                    lineCount++;
                }
            } else if (labelPosition == LimitLine.LimitLabelPosition.RIGHT_BOTTOM) {

                mLimitLinePaint.setTextAlign(Align.LEFT);
                int lineCount = label.split("\n").length;
                for (String line : label.split("\n")) {
                    c.drawText(line, position[0] + xOffset, mViewPortHandler.contentBottom() - yOffset - labelLineHeight * (lineCount - 1) - lineSpacing, mLimitLinePaint);
                    lineCount--;
                }
            } else if (labelPosition == LimitLine.LimitLabelPosition.LEFT_TOP) {

                mLimitLinePaint.setTextAlign(Align.RIGHT);
                int lineCount = 1;
                for (String line : label.split("\n")) {
                    c.drawText(line, position[0] - xOffset, mViewPortHandler.contentTop() + yOffset + labelLineHeight * lineCount + lineSpacing, mLimitLinePaint);
                    lineCount++;
                }
            } else {
                int lineCount = label.split("\n").length;
                mLimitLinePaint.setTextAlign(Align.RIGHT);
                for (String line : label.split("\n")) {
                    c.drawText(line, position[0] - xOffset, mViewPortHandler.contentBottom() - yOffset - labelLineHeight * (lineCount - 1) - lineSpacing, mLimitLinePaint);
                    lineCount--;
                }
            }
        }
    }

}
