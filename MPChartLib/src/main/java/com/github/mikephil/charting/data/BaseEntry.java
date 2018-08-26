package com.github.mikephil.charting.data;

import android.graphics.drawable.Drawable;

/**
 * Created by Philipp Jahoda on 02/06/16.
 */
public abstract class BaseEntry {

    /** the y value */
    private double y = 0d;

    /** optional spot for additional data this Entry represents */
    private Object mData = null;

    /** optional icon image */
    private Drawable mIcon = null;

    public BaseEntry() {

    }

    public BaseEntry(double y) {
        this.y = y;
    }

    public BaseEntry(double y, Object data) {
        this(y);
        this.mData = data;
    }

    public BaseEntry(double y, Drawable icon) {
        this(y);
        this.mIcon = icon;
    }

    public BaseEntry(double y, Drawable icon, Object data) {
        this(y);
        this.mIcon = icon;
        this.mData = data;
    }

    /**
     * Returns the y value of this Entry.
     *
     * @return
     */
    public double getY() {
        return y;
    }

    /**
     * Returns the y value of this Entry.
     *
     * @return
     */
    public float getFloatY() {
        return (float)y;
    }

    /**
     * Sets the icon drawable
     *
     * @param icon
     */
    public void setIcon(Drawable icon) {
        this.mIcon = icon;
    }

    /**
     * Returns the icon of this Entry.
     *
     * @return
     */
    public Drawable getIcon() {
        return mIcon;
    }

    /**
     * Sets the y-value for the Entry.
     *
     * @param y
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Returns the data, additional information that this Entry represents, or
     * null, if no data has been specified.
     *
     * @return
     */
    public Object getData() {
        return mData;
    }

    /**
     * Sets additional data this Entry should represent.
     *
     * @param data
     */
    public void setData(Object data) {
        this.mData = data;
    }
}
