
package com.github.mikephil.charting.data;

import android.os.Parcel;
import android.os.ParcelFormatException;
import android.os.Parcelable;

/**
 * Class representing one entry in the chart. Might contain multiple values.
 * Might only contain a single yValue depending on the used constructor.
 * 
 * @author Philipp Jahoda
 */
public class Entry implements Parcelable {

    /** the yPx yValue */
    private float y = 0f;

    /** the xPx yValue */
    private float x = 0f;

    /** optional spot for additional data this Entry represents */
    private Object mData = null;

    /**
     * A Entry represents one single entry in the chart.
     *
     * @param x the xPx yValue
     * @param y the yPx yValue (the actual yValue of the entry)
     */
    public Entry(float x, float y) {
        this.y = y;
        this.x = x;
    }

    /**
     * A Entry represents one single entry in the chart.
     *
     * @param x the xPx yValue
     * @param y the yPx yValue (the actual yValue of the entry)
     * @param data Spot for additional data this Entry represents.
     */
    public Entry(float x, float y, Object data) {
        this(x, y);
        this.mData = data;
    }

    /**
     * Returns the xPx-yValue of this Entry object.
     * 
     * @return
     */
    public float getX() {
        return x;
    }

    /**
     * Sets the xPx-yValue of this Entry object.
     * 
     * @param x
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * Returns the yPx yValue of this Entry.
     * 
     * @return
     */
    public float getY() {
        return y;
    }

    /**
     * Sets the yPx-yValue for the Entry.
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

    /**
     * returns an exact copy of the entry
     * 
     * @return
     */
    public Entry copy() {
        Entry e = new Entry(x, y, mData);
        return e;
    }

    /**
     * Compares yValue, xIndex and data of the entries. Returns true if entries
     * are equal in those points, false if not. Does not check by hash-code like
     * it's done by the "equals" method.
     * 
     * @param e
     * @return
     */
    public boolean equalTo(Entry e) {

        if (e == null)
            return false;

        if (e.mData != this.mData)
            return false;

        if (Math.abs(e.x - this.x) > 0.000001f)
            return false;

        if (Math.abs(e.y - this.y) > 0.000001f)
            return false;

        return true;
    }

    /**
     * returns a string representation of the entry containing xPx-index and yValue
     */
    @Override
    public String toString() {
        return "Entry, x: " + x + " y (sum): " + getY();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.x);
        dest.writeFloat(this.y);
        if (mData != null) {
            if (mData instanceof Parcelable) {
                dest.writeInt(1);
                dest.writeParcelable((Parcelable) this.mData, flags);
            } else {
                throw new ParcelFormatException("Cannot parcel an Entry with non-parcelable data");
            }
        } else {
            dest.writeInt(0);
        }
    }

    protected Entry(Parcel in) {
        this.x = in.readFloat();
        this.y = in.readFloat();
        if (in.readInt() == 1) {
            this.mData = in.readParcelable(Object.class.getClassLoader());
        }
    }

    public static final Parcelable.Creator<Entry> CREATOR = new Parcelable.Creator<Entry>() {
        public Entry createFromParcel(Parcel source) {
            return new Entry(source);
        }

        public Entry[] newArray(int size) {
            return new Entry[size];
        }
    };
}
