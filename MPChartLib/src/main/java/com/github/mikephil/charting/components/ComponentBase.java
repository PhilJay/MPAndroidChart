
package com.github.mikephil.charting.components;

import android.graphics.Color;
import android.graphics.Typeface;

import com.github.mikephil.charting.utils.Utils;

/**
 * This class encapsulates everything both Axis, Legend and LimitLines have in common.
 *
 * @author Philipp Jahoda
 */
public abstract class ComponentBase {

    /**
     * flag that indicates if this axis / legend is enabled or not
     */
    protected boolean mEnabled = true;

    /**
     * the offset in pixels this component has on the x-axis
     */
    protected float mXOffset = 5f;

    /**
     * the offset in pixels this component has on the Y-axis
     */
    protected float mYOffset = 5f;

    /**
     * the typeface used for the labels
     */
    protected Typeface mTypeface = null;

    /**
     * the text size of the labels
     */
    protected float mTextSize = Utils.convertDpToPixel(10f);

    /**
     * the text color to use for the labels
     */
    protected int mTextColor = Color.BLACK;


    public ComponentBase() {

    }

    /**
     * Returns the used offset on the x-axis for drawing the axis or legend
     * labels. This offset is applied before and after the label.
     *
     * @return
     */
    public float getXOffset() {
        return mXOffset;
    }

    /**
     * Sets the used x-axis offset for the labels on this axis.
     *
     * @param xOffset
     */
    public void setXOffset(float xOffset) {
        mXOffset = Utils.convertDpToPixel(xOffset);
    }

    /**
     * Returns the used offset on the x-axis for drawing the axis labels. This
     * offset is applied before and after the label.
     *
     * @return
     */
    public float getYOffset() {
        return mYOffset;
    }

    /**
     * Sets the used y-axis offset for the labels on this axis. For the legend,
     * higher offset means the legend as a whole will be placed further away
     * from the top.
     *
     * @param yOffset
     */
    public void setYOffset(float yOffset) {
        mYOffset = Utils.convertDpToPixel(yOffset);
    }

    /**
     * returns the Typeface used for the labels, returns null if none is set
     *
     * @return
     */
    public Typeface getTypeface() {
        return mTypeface;
    }

    /**
     * sets a specific Typeface for the labels
     *
     * @param tf
     */
    public void setTypeface(Typeface tf) {
        mTypeface = tf;
    }

    /**
     * sets the size of the label text in density pixels min = 6f, max = 24f, default
     * 10f
     *
     * @param size the text size, in DP
     */
    public void setTextSize(float size) {

        if (size > 24f)
            size = 24f;
        if (size < 6f)
            size = 6f;

        mTextSize = Utils.convertDpToPixel(size);
    }

    /**
     * returns the text size that is currently set for the labels, in pixels
     *
     * @return
     */
    public float getTextSize() {
        return mTextSize;
    }


    /**
     * Sets the text color to use for the labels. Make sure to use
     * getResources().getColor(...) when using a color from the resources.
     *
     * @param color
     */
    public void setTextColor(int color) {
        mTextColor = color;
    }

    /**
     * Returns the text color that is set for the labels.
     *
     * @return
     */
    public int getTextColor() {
        return mTextColor;
    }

    /**
     * Set this to true if this component should be enabled (should be drawn),
     * false if not. If disabled, nothing of this component will be drawn.
     * Default: true
     *
     * @param enabled
     */
    public void setEnabled(boolean enabled) {
        mEnabled = enabled;
    }

    /**
     * Returns true if this comonent is enabled (should be drawn), false if not.
     *
     * @return
     */
    public boolean isEnabled() {
        return mEnabled;
    }
}
