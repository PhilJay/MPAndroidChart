
package com.github.mikephil.charting.components;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;

import com.github.mikephil.charting.utils.Utils;

/**
 * The limit line is an additional feature for all Line-, Bar- and
 * ScatterCharts. It allows the displaying of an additional line in the chart
 * that marks a certain maximum / limit on the specified axis (x- or y-axis).
 */
public class LimitRange extends ComponentBase {

	public static class Range {
		private final float mLow;
		private final float mHigh;

		Range(float r1, float r2) {
			if (r1 < r2) {
				mLow = r1;
				mHigh = r2;
			} else {
				mLow = r2;
				mHigh = r1;
			}
		}

		public float getLow() {
			return mLow;
		}

		public float getHigh() {
			return mHigh;
		}
	}

	/**
	 * limit / maximum (the y-value or xIndex)
	 */
	private Range mLimit;

	/**
	 * the width of the limit line
	 */
	private float mLineWidth = 0f;

	/**
	 * the color of the limit line
	 */
	private int mLineColor = Color.rgb(237, 91, 91);

	/**
	 * the color of the Range
	 */
	private int mRangeColor = Color.rgb(128, 128, 128);

	/**
	 * the style of the label text
	 */
	private Paint.Style mTextStyle = Paint.Style.FILL;

	/**
	 * label string that is drawn next to the limit line
	 */
	private String mLabel = "";

	/**
	 * the path effect of this LimitLine that makes dashed lines possible
	 */
	private DashPathEffect mDashPathEffect = null;

	/**
	 * indicates the position of the LimitLine label
	 */
	private LimitLine.LimitLabelPosition mLabelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP;

	/**
	 * Constructor with limit.
	 *
	 * @param firstLimit  - the position (the value) on the y-axis (y-value) or x-axis
	 *                    (xIndex) where this line should appear
	 * @param secondLimit - the position (the value) on the y-axis (y-value) or x-axis
	 *                    (xIndex) where this line should appear
	 */
	public LimitRange(float firstLimit, float secondLimit) {
		mLimit = new Range(firstLimit, secondLimit);
	}

	/**
	 * Constructor with limit and label.
	 *
	 * @param firstLimit  - the position (the value) on the y-axis (y-value) or x-axis
	 *                    (xIndex) where this line should appear
	 * @param secondLimit - the position (the value) on the y-axis (y-value) or x-axis
	 *                    (xIndex) where this line should appear
	 * @param label       - provide "" if no label is required
	 */
	public LimitRange(float firstLimit, float secondLimit, String label) {
		mLimit = new Range(firstLimit, secondLimit);
		mLabel = label;
	}

	/**
	 * Returns the limit that is set for this line.
	 *
	 * @return
	 */
	public Range getLimit() {
		return mLimit;
	}

	/**
	 * set the line width of the chart (min = 0.2f, max = 12f); default 2f NOTE:
	 * thinner line == better performance, thicker line == worse performance
	 *
	 * @param width
	 */
	public void setLineWidth(float width) {
        if (width > 12.0f) {
            width = 12.0f;
        }
		mLineWidth = Utils.convertDpToPixel(width);
	}

	/**
	 * returns the width of limit line
	 *
	 * @return
	 */
	public float getLineWidth() {
		return mLineWidth;
	}

	/**
	 * Sets the linecolor for this LimitLine. Make sure to use
	 * getResources().getColor(...)
	 *
	 * @param color
	 */
	public void setLineColor(int color) {
		mLineColor = color;
	}

	/**
	 * Sets the range color for this LimitRange. Make sure to use
	 * getResources().getColor(...)
	 *
	 * @param color
	 */
	public void setRangeColor(int color) {
		mRangeColor = color;
	}

	/**
	 * Returns the color that is used for this LimitLine
	 *
	 * @return
	 */
	public int getLineColor() {
		return mLineColor;
	}

	/**
	 * Returns the color that is used for this LimitRange
	 *
	 * @return
	 */
	public int getRangeColor() {
		return mRangeColor;
	}

	/**
	 * Enables the line to be drawn in dashed mode, e.g. like this "- - - - - -"
	 *
	 * @param lineLength  the length of the line pieces
	 * @param spaceLength the length of space inbetween the pieces
	 * @param phase       offset, in degrees (normally, use 0)
	 */
	public void enableDashedLine(float lineLength, float spaceLength, float phase) {
		mDashPathEffect = new DashPathEffect(new float[]{
				lineLength, spaceLength
		}, phase);
	}

	/**
	 * Disables the line to be drawn in dashed mode.
	 */
	public void disableDashedLine() {
		mDashPathEffect = null;
	}

	/**
	 * Returns true if the dashed-line effect is enabled, false if not. Default:
	 * disabled
	 *
	 * @return
	 */
	public boolean isDashedLineEnabled() {
		return mDashPathEffect == null ? false : true;
	}

	/**
	 * returns the DashPathEffect that is set for this LimitLine
	 *
	 * @return
	 */
	public DashPathEffect getDashPathEffect() {
		return mDashPathEffect;
	}

	/**
	 * Sets the color of the value-text that is drawn next to the LimitLine.
	 * Default: Paint.Style.FILL_AND_STROKE
	 *
	 * @param style
	 */
	public void setTextStyle(Paint.Style style) {
		this.mTextStyle = style;
	}

	/**
	 * Returns the color of the value-text that is drawn next to the LimitLine.
	 *
	 * @return
	 */
	public Paint.Style getTextStyle() {
		return mTextStyle;
	}

	/**
	 * Sets the position of the LimitLine value label (either on the right or on
	 * the left edge of the chart). Not supported for RadarChart.
	 *
	 * @param pos
	 */
	public void setLabelPosition(LimitLine.LimitLabelPosition pos) {
		mLabelPosition = pos;
	}

	/**
	 * Returns the position of the LimitLine label (value).
	 *
	 * @return
	 */
	public LimitLine.LimitLabelPosition getLabelPosition() {
		return mLabelPosition;
	}

	/**
	 * Sets the label that is drawn next to the limit line. Provide "" if no
	 * label is required.
	 *
	 * @param label
	 */
	public void setLabel(String label) {
		mLabel = label;
	}

	/**
	 * Returns the label that is drawn next to the limit line.
	 *
	 * @return
	 */
	public String getLabel() {
		return mLabel;
	}
}
