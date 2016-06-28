
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.PointF;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.utils.FSize;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.PointD;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.List;

public class XAxisRendererHorizontalBarChart extends XAxisRenderer {

    protected BarChart mChart;

    public XAxisRendererHorizontalBarChart(ViewPortHandler viewPortHandler, XAxis xAxis,
            Transformer trans, BarChart chart) {
        super(viewPortHandler, xAxis, trans);

        this.mChart = chart;
    }

    @Override
    public void computeAxis(float min, float max, boolean inverted) {

        // calculate the starting and entry point of the y-labels (depending on
        // zoom / contentrect bounds)
        if (mViewPortHandler.contentWidth() > 10 && !mViewPortHandler.isFullyZoomedOutY()) {

            PointD p1 = mTrans.getValuesByTouchPoint(mViewPortHandler.contentLeft(), mViewPortHandler.contentBottom());
            PointD p2 = mTrans.getValuesByTouchPoint(mViewPortHandler.contentLeft(), mViewPortHandler.contentTop());

            if (inverted) {

                min = (float) p2.y;
                max = (float) p1.y;
            } else {

                min = (float) p1.y;
                max = (float) p2.y;
            }

            PointD.recycleInstance(p1);
            PointD.recycleInstance(p2);
        }

        computeAxisValues(min, max);
    }
    
    @Override
    protected void computeSize() {
        
        mAxisLabelPaint.setTypeface(mXAxis.getTypeface());
        mAxisLabelPaint.setTextSize(mXAxis.getTextSize());

        String longest = mXAxis.getLongestLabel();

        final FSize labelSize = Utils.calcTextSize(mAxisLabelPaint, longest);

        final float labelWidth = (int)(labelSize.width + mXAxis.getXOffset() * 3.5f);
        final float labelHeight = labelSize.height;

        final FSize labelRotatedSize = Utils.getSizeOfRotatedRectangleByDegrees(
                labelSize.width,
                labelHeight,
                mXAxis.getLabelRotationAngle());

        mXAxis.mLabelWidth = Math.round(labelWidth);
        mXAxis.mLabelHeight = Math.round(labelHeight);
        mXAxis.mLabelRotatedWidth = (int)(labelRotatedSize.width + mXAxis.getXOffset() * 3.5f);
        mXAxis.mLabelRotatedHeight = Math.round(labelRotatedSize.height);

        FSize.recycleInstance(labelRotatedSize);
    }

    @Override
    public void renderAxisLabels(Canvas c) {

        if (!mXAxis.isEnabled() || !mXAxis.isDrawLabelsEnabled())
            return;

        float xoffset = mXAxis.getXOffset();

        mAxisLabelPaint.setTypeface(mXAxis.getTypeface());
        mAxisLabelPaint.setTextSize(mXAxis.getTextSize());
        mAxisLabelPaint.setColor(mXAxis.getTextColor());

        MPPointF pointF = MPPointF.getInstance(0,0);

        if (mXAxis.getPosition() == XAxisPosition.TOP) {
            pointF.x = 0.0f;
            pointF.y = 0.5f;
            drawLabels(c, mViewPortHandler.contentRight() + xoffset, pointF);

        } else if (mXAxis.getPosition() == XAxisPosition.TOP_INSIDE) {
            pointF.x = 1.0f;
            pointF.y = 0.5f;
            drawLabels(c, mViewPortHandler.contentRight() - xoffset, pointF);

        } else if (mXAxis.getPosition() == XAxisPosition.BOTTOM) {
            pointF.x = 1.0f;
            pointF.y = 0.5f;
            drawLabels(c, mViewPortHandler.contentLeft() - xoffset, pointF);

        } else if (mXAxis.getPosition() == XAxisPosition.BOTTOM_INSIDE) {
            pointF.x = 1.0f;
            pointF.y = 0.5f;
            drawLabels(c, mViewPortHandler.contentLeft() + xoffset, pointF);

        } else { // BOTH SIDED
            pointF.x = 0.0f;
            pointF.y = 0.5f;
            drawLabels(c, mViewPortHandler.contentRight() + xoffset, pointF);
            pointF.x = 1.0f;
            pointF.y = 0.5f;
            drawLabels(c, mViewPortHandler.contentLeft() - xoffset, pointF);
        }

        MPPointF.recycleInstance(pointF);
    }

    @Override
    protected void drawLabels(Canvas c, float pos, MPPointF anchor) {

        final float labelRotationAngleDegrees = mXAxis.getLabelRotationAngle();
        boolean centeringEnabled = mXAxis.isCenterAxisLabelsEnabled();

        float[] positions = new float[mXAxis.mEntryCount * 2];

        for (int i = 0; i < positions.length; i += 2) {

            // only fill x values
            if (centeringEnabled) {
                positions[i + 1] = mXAxis.mCenteredEntries[i / 2];
            } else {
                positions[i + 1] = mXAxis.mEntries[i / 2];
            }
        }

        mTrans.pointValuesToPixel(positions);

        for (int i = 0; i < positions.length; i += 2) {

            float y = positions[i + 1];

            if (mViewPortHandler.isInBoundsY(y)) {

                String label = mXAxis.getValueFormatter().getFormattedValue(mXAxis.mEntries[i / 2], mXAxis);
                drawLabel(c, label, pos, y, anchor, labelRotationAngleDegrees);
            }
        }
    }

    @Override
    protected void drawGridLine(Canvas c, float x, float y, Path gridLinePath) {

        gridLinePath.moveTo(mViewPortHandler.contentRight(), y);
        gridLinePath.lineTo(mViewPortHandler.contentLeft(), y);

        // draw a path because lines don't support dashing on lower android versions
        c.drawPath(gridLinePath, mGridPaint);

        gridLinePath.reset();
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

	/**
	 * Draws the LimitLines associated with this axis to the screen.
	 * This is the standard YAxis renderer using the XAxis limit lines.
	 *
	 * @param c
	 */
	@Override
	public void renderLimitLines(Canvas c) {

		List<LimitLine> limitLines = mXAxis.getLimitLines();

		if (limitLines == null || limitLines.size() <= 0)
			return;

		float[] pts = new float[2];
		Path limitLinePath = new Path();

		for (int i = 0; i < limitLines.size(); i++) {

			LimitLine l = limitLines.get(i);

            if(!l.isEnabled())
                continue;

			mLimitLinePaint.setStyle(Paint.Style.STROKE);
			mLimitLinePaint.setColor(l.getLineColor());
			mLimitLinePaint.setStrokeWidth(l.getLineWidth());
			mLimitLinePaint.setPathEffect(l.getDashPathEffect());

			pts[1] = l.getLimit();

			mTrans.pointValuesToPixel(pts);

			limitLinePath.moveTo(mViewPortHandler.contentLeft(), pts[1]);
			limitLinePath.lineTo(mViewPortHandler.contentRight(), pts[1]);

			c.drawPath(limitLinePath, mLimitLinePaint);
			limitLinePath.reset();
			// c.drawLines(pts, mLimitLinePaint);

			String label = l.getLabel();

			// if drawing the limit-value label is enabled
			if (label != null && !label.equals("")) {

				mLimitLinePaint.setStyle(l.getTextStyle());
				mLimitLinePaint.setPathEffect(null);
				mLimitLinePaint.setColor(l.getTextColor());
				mLimitLinePaint.setStrokeWidth(0.5f);
				mLimitLinePaint.setTextSize(l.getTextSize());

                final float labelLineHeight = Utils.calcTextHeight(mLimitLinePaint, label);
                float xOffset = Utils.convertDpToPixel(4f) + l.getXOffset();
                float yOffset = l.getLineWidth() + labelLineHeight + l.getYOffset();

                final LimitLine.LimitLabelPosition position = l.getLabelPosition();

				if (position == LimitLine.LimitLabelPosition.RIGHT_TOP) {

					mLimitLinePaint.setTextAlign(Align.RIGHT);
					c.drawText(label,
                            mViewPortHandler.contentRight() - xOffset,
							pts[1] - yOffset + labelLineHeight, mLimitLinePaint);

				} else if (position == LimitLine.LimitLabelPosition.RIGHT_BOTTOM) {

                    mLimitLinePaint.setTextAlign(Align.RIGHT);
                    c.drawText(label,
                            mViewPortHandler.contentRight() - xOffset,
                            pts[1] + yOffset, mLimitLinePaint);

                } else if (position == LimitLine.LimitLabelPosition.LEFT_TOP) {

                    mLimitLinePaint.setTextAlign(Align.LEFT);
                    c.drawText(label,
                            mViewPortHandler.contentLeft() + xOffset,
                            pts[1] - yOffset + labelLineHeight, mLimitLinePaint);

                } else {

					mLimitLinePaint.setTextAlign(Align.LEFT);
					c.drawText(label,
                            mViewPortHandler.offsetLeft() + xOffset,
							pts[1] + yOffset, mLimitLinePaint);
				}
			}
		}
	}
}
