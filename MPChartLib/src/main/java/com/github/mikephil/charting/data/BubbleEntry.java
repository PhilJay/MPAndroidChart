
package com.github.mikephil.charting.data;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;

import java.util.Locale;

/**
 * Subclass of Entry that holds a value for one entry in a BubbleChart. Bubble
 * chart implementation: Copyright 2015 Pierre-Marc Airoldi Licensed under
 * Apache License 2.0
 *
 * @author Philipp Jahoda
 */
@SuppressLint("ParcelCreator")
public class BubbleEntry extends Entry {

    /**
     * size value
     *
     * Size does not translate directly into the radius of the circle drawn on the chart.
     * If BubbleDataSet mNormalizeSize is true, mSize size is scaled as an area;
     * if it is false mSize is scaled to a diameter value. The actual radius is not determined
     * until the BubbleEntry is rendered.
     */
    private float mSize = 0f;

    /**
     * The size of the rendered circle in data units.
     *
     * These fields are set by the renderer when the chart is rendered (via setDrawnRadii()),
     * and may be unset (via unDraw() if the bubble is not rendered, e.g. it is off screen.
     */
    private float mDrawnXRadius = Float.NaN;
    private float mDrawnYRadius = Float.NaN;

    /**
     * Constructor.
     *
     * @param x The value on the x-axis.
     * @param y The value on the y-axis.
     * @param size The size of the bubble.
     */
    public BubbleEntry(float x, float y, float size) {
        super(x, y);
        this.mSize = size;
    }

    /**
     * Constructor.
     *
     * @param x The value on the x-axis.
     * @param y The value on the y-axis.
     * @param size The size of the bubble.
     * @param data Spot for additional data this Entry represents.
     */
    public BubbleEntry(float x, float y, float size, Object data) {
        super(x, y, data);
        this.mSize = size;
    }

    /**
     * Constructor.
     *
     * @param x The value on the x-axis.
     * @param y The value on the y-axis.
     * @param size The size of the bubble.
     * @param icon Icon image
     */
    public BubbleEntry(float x, float y, float size, Drawable icon) {
        super(x, y, icon);
        this.mSize = size;
    }

    /**
     * Constructor.
     *
     * @param x The value on the x-axis.
     * @param y The value on the y-axis.
     * @param size The size of the bubble.
     * @param icon Icon image
     * @param data Spot for additional data this Entry represents.
     */
    public BubbleEntry(float x, float y, float size, Drawable icon, Object data) {
        super(x, y, icon, data);
        this.mSize = size;
    }

    public BubbleEntry copy() {

        BubbleEntry c = new BubbleEntry(getX(), getY(), mSize, getData());
        return c;
    }

    /**
     * Returns the size of this entry (the size of the bubble).
     *
     * @return
     */
    public float getSize() {
        return mSize;
    }

    public void setSize(float size) {
        this.mSize = size;
    }


    /**
     * Returns true if the entry has been rendered, false if it has not.
     * If true, mDrawnXRadius and mDrawnYRadius will have valid values.
     *
     * @return true if the entry has been rendered
     */
    public boolean isDrawn() {
        return ! Float.isNaN(mDrawnXRadius);
    }

    /**
     * Set the rendered radius of the drawn circle, scaled the same as mSize.
     *
     * @param xRadius
     * @param yRadius
     */
    public void setDrawnRadii(float xRadius, float yRadius) {
        this.mDrawnXRadius = xRadius;
        this.mDrawnYRadius = yRadius;
    }

    /**
     * Clears mDrawnXRadius and mDrawnYRadius if the entry is not rendered,
     * e.g. if it is not drawn because it is offscreen.
     *
     */
    public void unDraw() {
        mDrawnXRadius = Float.NaN;
        mDrawnYRadius = Float.NaN;
    }

    /**
     * Is the given xValue within this entry?
     *
     * @param xValue x in same units as mSize.
     * @return true if the entry contains x
     */
    public boolean containsX(float xValue) {
        if (!isDrawn()) return false;
        if (xValue < getX() - mDrawnXRadius) return false;
        if (xValue > getX() + mDrawnXRadius) return false;
        return true;
    }
    public boolean containsY(float yValue) {
        if (!isDrawn()) return false;
        if (yValue < getY() - mDrawnYRadius) return false;
        if (yValue > getY() + mDrawnYRadius) return false;
        return true;
    }

    public float getDrawnXRadius() {
        return mDrawnXRadius;
    }

    public float getDrawnYRadius() {
        return mDrawnYRadius;
    }

    /**
     * returns a string representation of the entry containing x-index and value
     */
    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "BubbleEntry: x= %.2f, y= %.2f, size= %.2f", getX(), getY(), mSize);
    }

}
