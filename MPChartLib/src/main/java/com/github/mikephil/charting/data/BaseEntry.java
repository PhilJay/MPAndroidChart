package com.github.mikephil.charting.data;

import android.graphics.drawable.Drawable;

import com.github.mikephil.charting.utils.Utils;

/**
 * Created by Philipp Jahoda on 02/06/16.
 */
public abstract class BaseEntry {

    /** the y value */
    private float y = 0f;

    /** optional spot for additional data this Entry represents */
    private Object mData = null;

    /** optional icon image */
    private Drawable mIcon = null;

    public BaseEntry() {

    }

    public BaseEntry(float y) {
        this.y = y;
    }

    public BaseEntry(float y, Object data) {
        this(y);
        this.mData = data;
    }

    public BaseEntry(float y, Drawable icon) {
        this(y);
        this.mIcon = icon;
    }

    public BaseEntry(float y, Drawable icon, Object data) {
        this(y);
        this.mIcon = icon;
        this.mData = data;
    }

    /**
     * Returns the y value of this Entry.
     *
     * @return
     */
    public float getY() {
        return y;
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
    public void setY(float y) {
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

    @Override
    public int hashCode() {
      int hashCode = 5;
      int multiplier = 17;

      hashCode = hashCode * multiplier + ((getY() == 0.0f) ? 0 : Float.floatToIntBits(getY()));
      hashCode = hashCode * multiplier + ((getData() == null) ? 0 : getData().hashCode());
      hashCode = hashCode * multiplier + ((getIcon() == null) ? 0 : getIcon().hashCode());

      return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == null) {
        return false;
      }

      if (obj == this) {
        return true;
      }

      if (!getClass().isAssignableFrom(obj.getClass())) {
        return false;
      }

      BaseEntry other = (BaseEntry) obj;

      if (Math.abs(other.getY() - this.getY()) > Utils.FLOAT_EPSILON) {
        return false;
      }

      if ((other.getData() != null && getData() == null) && !other.getData().equals(getData())) {
        return false;
      }

      if ((other.getIcon() != null && getIcon() != null) && !other.getIcon().equals(getIcon())) {
        return false;
      }

      return true;
    }

}
