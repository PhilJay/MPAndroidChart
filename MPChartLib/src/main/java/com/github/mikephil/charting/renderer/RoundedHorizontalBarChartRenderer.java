package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.BarBuffer;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * @noinspection unused
 */
public class RoundedHorizontalBarChartRenderer extends HorizontalBarChartRenderer {

	private final RectF mBarShadowRectBuffer = new RectF();
	private float roundedShadowRadius = 0f;
	private float roundedPositiveDataSetRadius = 0f;
	private float roundedNegativeDataSetRadius = 0f;

	public void setRoundedNegativeDataSetRadius(float roundedNegativeDataSet) {
		roundedNegativeDataSetRadius = roundedNegativeDataSet;
	}

	public void setRoundedShadowRadius(float roundedShadow) {
		roundedShadowRadius = roundedShadow;
	}

	public void setRoundedPositiveDataSetRadius(float roundedPositiveDataSet) {
		roundedPositiveDataSetRadius = roundedPositiveDataSet;
	}

	public RoundedHorizontalBarChartRenderer(BarDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
		super(chart, animator, viewPortHandler);

		mValuePaint.setTextAlign(Paint.Align.LEFT);
	}

	@Override
	protected void drawDataSet(Canvas c, IBarDataSet dataSet, int index) {
		initBuffers();
		Transformer trans = chart.getTransformer(dataSet.getAxisDependency());
		barBorderPaint.setColor(dataSet.getBarBorderColor());
		barBorderPaint.setStrokeWidth(Utils.convertDpToPixel(dataSet.getBarBorderWidth()));
		shadowPaint.setColor(dataSet.getBarShadowColor());
		boolean drawBorder = dataSet.getBarBorderWidth() > 0f;
		float phaseX = animator.getPhaseX();
		float phaseY = animator.getPhaseY();

		if (chart.isDrawBarShadowEnabled()) {
			shadowPaint.setColor(dataSet.getBarShadowColor());
			BarData barData = chart.getBarData();
			float barWidth = barData.getBarWidth();
			float barWidthHalf = barWidth / 2.0f;
			float x;
			int i = 0;
			double count = Math.min((double) (int) (double) ((float) dataSet.getEntryCount() * phaseX), dataSet.getEntryCount());
			while (i < count) {
				BarEntry e = dataSet.getEntryForIndex(i);
				x = e.getX();
				mBarShadowRectBuffer.top = x - barWidthHalf;
				mBarShadowRectBuffer.bottom = x + barWidthHalf;
				trans.rectValueToPixel(mBarShadowRectBuffer);
				if (!viewPortHandler.isInBoundsTop(mBarShadowRectBuffer.bottom)) {
					i++;
					continue;
				}
				if (!viewPortHandler.isInBoundsBottom(mBarShadowRectBuffer.top)) {
					break;
				}
				mBarShadowRectBuffer.left = viewPortHandler.contentLeft();
				mBarShadowRectBuffer.right = viewPortHandler.contentRight();

				if (roundedShadowRadius > 0) {
					c.drawRoundRect(barRect, roundedShadowRadius, roundedShadowRadius, shadowPaint);
				} else {
					c.drawRect(mBarShadowRectBuffer, shadowPaint);
				}
				i++;
			}
		}

		BarBuffer buffer = barBuffers.get(index);
		buffer.setPhases(phaseX, phaseY);
		buffer.setDataSet(index);
		buffer.setInverted(chart.isInverted(dataSet.getAxisDependency()));
		buffer.setBarWidth(chart.getBarData().getBarWidth());
		buffer.feed(dataSet);
		trans.pointValuesToPixel(buffer.buffer);

		// if multiple colors has been assigned to Bar Chart
		if (dataSet.getColors().size() > 1) {
			for (int j = 0; j < buffer.size(); j += 4) {
				if (!viewPortHandler.isInBoundsTop(buffer.buffer[j + 3])) {
					continue;
				}

				if (!viewPortHandler.isInBoundsBottom(buffer.buffer[j + 1])) {
					break;
				}

				if (chart.isDrawBarShadowEnabled()) {
					if (roundedShadowRadius > 0) {
						c.drawRoundRect(new RectF(buffer.buffer[j], viewPortHandler.contentTop(),
								buffer.buffer[j + 2],
								viewPortHandler.contentBottom()), roundedShadowRadius, roundedShadowRadius, shadowPaint);
					} else {
						c.drawRect(buffer.buffer[j], viewPortHandler.contentTop(),
								buffer.buffer[j + 2],
								viewPortHandler.contentBottom(), shadowPaint);
					}
				}

				// Set the color for the currently drawn value. If the index
				renderPaint.setColor(dataSet.getColor(j / 4));

				if (roundedPositiveDataSetRadius > 0) {
					c.drawRoundRect(new RectF(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
							buffer.buffer[j + 3]), roundedPositiveDataSetRadius, roundedPositiveDataSetRadius, renderPaint);
				} else {
					c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
							buffer.buffer[j + 3], renderPaint);
				}
			}
		} else {
			renderPaint.setColor(dataSet.getColor());

			for (int j = 0; j < buffer.size(); j += 4) {
				if (!viewPortHandler.isInBoundsTop(buffer.buffer[j + 3])) {
					continue;
				}

				if (!viewPortHandler.isInBoundsBottom(buffer.buffer[j + 1])) {
					break;
				}

				if (chart.isDrawBarShadowEnabled()) {
					if (roundedShadowRadius > 0) {
						c.drawRoundRect(new RectF(buffer.buffer[j], viewPortHandler.contentTop(),
								buffer.buffer[j + 2],
								viewPortHandler.contentBottom()), roundedShadowRadius, roundedShadowRadius, shadowPaint);
					} else {
						c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
								buffer.buffer[j + 3], renderPaint);
					}
				}

				if (roundedPositiveDataSetRadius > 0) {
					c.drawRoundRect(new RectF(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
							buffer.buffer[j + 3]), roundedPositiveDataSetRadius, roundedPositiveDataSetRadius, renderPaint);
				} else {
					c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
							buffer.buffer[j + 3], renderPaint);
				}
			}
		}

		boolean isSingleColor = dataSet.getColors().size() == 1;
		if (isSingleColor) {
			renderPaint.setColor(dataSet.getColor(index));
		}

		int j = 0;
		while (j < buffer.size()) {
			if (!viewPortHandler.isInBoundsTop(buffer.buffer[j + 3])) {
				j += 4;
				continue;
			}

			if (!viewPortHandler.isInBoundsBottom(buffer.buffer[j + 1])) {
				break;
			}

			if (!isSingleColor) {
				renderPaint.setColor(dataSet.getColor(j / 4));
			}

			if ((dataSet.getEntryForIndex(j / 4).getY() < 0 && roundedNegativeDataSetRadius > 0)) {
				Path path2 = roundRect(new RectF(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
						buffer.buffer[j + 3]), roundedNegativeDataSetRadius, roundedNegativeDataSetRadius, true, true, true, true);
				c.drawPath(path2, renderPaint);
			} else if ((dataSet.getEntryForIndex(j / 4).getY() > 0 && roundedPositiveDataSetRadius > 0)) {
				Path path2 = roundRect(new RectF(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
						buffer.buffer[j + 3]), roundedPositiveDataSetRadius, roundedPositiveDataSetRadius, true, true, true, true);
				c.drawPath(path2, renderPaint);
			} else {
				c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
						buffer.buffer[j + 3], renderPaint);
			}
			j += 4;
		}
	}

	private Path roundRect(RectF rect, float rx, float ry, boolean tl, boolean tr, boolean br, boolean bl) {
		float top = rect.top;
		float left = rect.left;
		float right = rect.right;
		float bottom = rect.bottom;
		Path path = new Path();
		if (rx < 0) {
			rx = 0;
		}
		if (ry < 0) {
			ry = 0;
		}
		float width = right - left;
		float height = bottom - top;
		if (rx > width / 2) {
			rx = width / 2;
		}
		if (ry > height / 2) {
			ry = height / 2;
		}
		float widthMinusCorners = (width - (2 * rx));
		float heightMinusCorners = (height - (2 * ry));

		path.moveTo(right, top + ry);
		if (tr) {
			path.rQuadTo(0, -ry, -rx, -ry);//top-right corner
		} else {
			path.rLineTo(0, -ry);
			path.rLineTo(-rx, 0);
		}
		path.rLineTo(-widthMinusCorners, 0);
		if (tl) {
			path.rQuadTo(-rx, 0, -rx, ry); //top-left corner
		} else {
			path.rLineTo(-rx, 0);
			path.rLineTo(0, ry);
		}
		path.rLineTo(0, heightMinusCorners);

		if (bl) {
			path.rQuadTo(0, ry, rx, ry);//bottom-left corner
		} else {
			path.rLineTo(0, ry);
			path.rLineTo(rx, 0);
		}

		path.rLineTo(widthMinusCorners, 0);
		if (br) {
			path.rQuadTo(rx, 0, rx, -ry); //bottom-right corner
		} else {
			path.rLineTo(rx, 0);
			path.rLineTo(0, -ry);
		}

		path.rLineTo(0, -heightMinusCorners);
		path.close();
		return path;
	}
}
