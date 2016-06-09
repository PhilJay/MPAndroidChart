
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
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.utils.FSize;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.List;

public class XAxisRendererHorizontalBarChart extends XAxisRendererBarChart {

    public XAxisRendererHorizontalBarChart(ViewPortHandler viewPortHandler, XAxis xAxis,
            Transformer trans, BarChart chart) {
        super(viewPortHandler, xAxis, trans, chart);
    }
    
    @Override
    public void computeAxis(float xValAverageLength, List<String> xValues) {
        
        mAxisLabelPaint.setTypeface(mXAxis.getTypeface());
        mAxisLabelPaint.setTextSize(mXAxis.getTextSize());
        mAxisLabelPaint.setFakeBoldText(mXAxis.getTextBold());
        mXAxis.setValues(xValues);

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
    }

    @Override
    public void renderAxisLabels(Canvas c) {

        if (!mXAxis.isEnabled() || !mXAxis.isDrawLabelsEnabled())
            return;

        float xoffset = mXAxis.getXOffset();

        mAxisLabelPaint.setTypeface(mXAxis.getTypeface());
        mAxisLabelPaint.setTextSize(mXAxis.getTextSize());
        mAxisLabelPaint.setFakeBoldText(mXAxis.getTextBold());
        mAxisLabelPaint.setColor(mXAxis.getTextColor());

        if (mXAxis.getPosition() == XAxisPosition.TOP) {

            drawLabels(c, mViewPortHandler.contentRight() + xoffset,
                    new PointF(0.0f, 0.5f));

        } else if (mXAxis.getPosition() == XAxisPosition.TOP_INSIDE) {

            drawLabels(c, mViewPortHandler.contentRight() - xoffset,
                    new PointF(1.0f, 0.5f));

        } else if (mXAxis.getPosition() == XAxisPosition.BOTTOM) {

            drawLabels(c, mViewPortHandler.contentLeft() - xoffset,
                    new PointF(1.0f, 0.5f));

        } else if (mXAxis.getPosition() == XAxisPosition.BOTTOM_INSIDE) {

            drawLabels(c, mViewPortHandler.contentLeft() + xoffset,
                    new PointF(0.0f, 0.5f));

        } else { // BOTH SIDED

            drawLabels(c, mViewPortHandler.contentRight() + xoffset,
                    new PointF(0.0f, 0.5f));
            drawLabels(c, mViewPortHandler.contentLeft() - xoffset,
                    new PointF(1.0f, 0.5f));
        }
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

        for (int i = mMinX; i <= mMaxX; i += mXAxis.mAxisLabelModulus) {

            position[1] = i * step + i * bd.getGroupSpace()
                    + bd.getGroupSpace() / 2f;
            
            // consider groups (center label for each group)
            if (step > 1) {
                position[1] += ((float) step - 1f) / 2f;
            }

            mTrans.pointValuesToPixel(position);

            if (mViewPortHandler.isInBoundsY(position[1])) {

                String label = mXAxis.getValues().get(i);
                drawLabel(c, label, i, pos, position[1], anchor, labelRotationAngleDegrees);
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

        for (int i = mMinX; i <= mMaxX; i += mXAxis.mAxisLabelModulus) {

            position[1] = i * step + i * bd.getGroupSpace() - 0.5f;

            mTrans.pointValuesToPixel(position);

            if (mViewPortHandler.isInBoundsY(position[1])) {

                c.drawLine(mViewPortHandler.contentLeft(), position[1],
                        mViewPortHandler.contentRight(), position[1], mGridPaint);
            }
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
                mLimitLinePaint.setFakeBoldText(l.getTextBold());

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
