
package com.github.mikephil.charting.components;

import android.graphics.Color;
import android.graphics.DashPathEffect;

import com.github.mikephil.charting.utils.Utils;

/**
 * The limit line is an additional feature for all Line-, Bar- and
 * ScatterCharts. It allows the displaying of an additional line in the chart
 * that marks a certain maximum / limit.
 * 
 * @author Philipp Jahoda
 */
public class LimitLine {

    /** limit / maximum (the y-position) */
    private float mLimit = 0f;

    /** the width of the limit line */
    private float mLineWidth = 2f;

    /** the color of the limit line */
    private int mLineColor = Color.rgb(237, 91, 91);

    /** the color of the value-text */
    private int mValueTextColor = Color.BLACK;

    /** the size of the label text */
    private float mTextSize = 13f;

    /** label string that is drawn next to the limit line */
    private String mLabel = "";

    /** the path effect of this LimitLine that makes dashed lines possible */
    private DashPathEffect mDashPathEffect = null;

    /** indicates the position of the LimitLine label */
    private LimitLabelPosition mLabelPosition = LimitLabelPosition.POS_RIGHT;

    /** enum that indicates the position of the LimitLine label */
    public enum LimitLabelPosition {
        POS_LEFT, POS_RIGHT
    }

    /**
     * Constructor with limit.
     * 
     * @param limit - the position (the value) on the y-axis where this line
     *            should appear
     */
    public LimitLine(float limit) {
        mLimit = limit;
    }

    /**
     * Constructor with limit and label.
     * 
     * @param limit - the position (the value) on the y-axis where this line
     *            should appear
     * @param label - provide "" if no label is required
     */
    public LimitLine(float limit, String label) {
        mLimit = limit;
        mLabel = label;
    }

    /**
     * Returns the limit that is set for this line.
     * 
     * @return
     */
    public float getLimit() {
        return mLimit;
    }

    /**
     * set the line width of the chart (min = 0.2f, max = 12f); default 2f NOTE:
     * thinner line == better performance, thicker line == worse performance
     * 
     * @param width
     */
    public void setLineWidth(float width) {

        if (width < 0.2f)
            width = 0.2f;
        if (width > 12.0f)
            width = 12.0f;
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
     * Returns the color that is used for this LimitLine
     * 
     * @return
     */
    public int getLineColor() {
        return mLineColor;
    }

    /**
     * Enables the line to be drawn in dashed mode, e.g. like this "- - - - - -"
     * 
     * @param lineLength the length of the line pieces
     * @param spaceLength the length of space inbetween the pieces
     * @param phase offset, in degrees (normally, use 0)
     */
    public void enableDashedLine(float lineLength, float spaceLength, float phase) {
        mDashPathEffect = new DashPathEffect(new float[] {
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
     * 
     * @param color
     */
    public void setTextColor(int color) {
        mValueTextColor = color;
    }

    /**
     * Returns the color of the value-text that is drawn next to the LimitLine.
     * 
     * @return
     */
    public int getTextColor() {
        return mValueTextColor;
    }

    /**
     * Sets the position of the LimitLine value label (either on the right or on
     * the left edge of the chart). Not supported for RadarChart.
     * 
     * @param pos
     */
    public void setLabelPosition(LimitLabelPosition pos) {
        mLabelPosition = pos;
    }

    /**
     * Returns the position of the LimitLine label (value).
     * 
     * @return
     */
    public LimitLabelPosition getLabelPosition() {
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

    /**
     * Sets the size of the label-text.
     * 
     * @param size
     */
    public void setTextSize(float size) {
        mTextSize = Utils.convertDpToPixel(size);
    }

    /**
     * Returns the size of the label text.
     * 
     * @return
     */
    public float getTextSize() {
        return mTextSize;
    }
}
