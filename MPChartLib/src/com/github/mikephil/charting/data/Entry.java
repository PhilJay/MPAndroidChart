
package com.github.mikephil.charting.data;

import android.os.Parcel;
import android.os.ParcelFormatException;
import android.os.Parcelable;

/**
 * Class representing one entry in the chart. Might contain multiple values.
 * Might only contain a single value depending on the used constructor.
 * 
 * @author Philipp Jahoda
 */
public class Entry implements Parcelable {

    /** the actual value */
    private float mVal = 0f;

    /** the index on the x-axis */
    private int mXIndex = 0;

    /** optional spot for additional data this Entry represents */
    private Object mData = null;

    /**
     * A Entry represents one single entry in the chart.
     * 
     * @param val the y value (the actual value of the entry)
     * @param xIndex the corresponding index in the x value array (index on the
     *            x-axis of the chart, must NOT be higher than the length of the
     *            x-values String array)
     */
    public Entry(float val, int xIndex) {
        mVal = val;
        mXIndex = xIndex;
    }

    /**
     * A Entry represents one single entry in the chart.
     * 
     * @param val the y value (the actual value of the entry)
     * @param xIndex the corresponding index in the x value array (index on the
     *            x-axis of the chart, must NOT be higher than the length of the
     *            x-values String array)
     * @param data Spot for additional data this Entry represents.
     */
    public Entry(float val, int xIndex, Object data) {
        this(val, xIndex);

        this.mData = data;
    }

    /**
     * returns the x-index the value of this object is mapped to
     * 
     * @return
     */
    public int getXIndex() {
        return mXIndex;
    }

    /**
     * sets the x-index for the entry
     * 
     * @param x
     */
    public void setXIndex(int x) {
        this.mXIndex = x;
    }

    /**
     * Returns the total value the entry represents.
     * 
     * @return
     */
    public float getVal() {
        return mVal;
    }

    /**
     * Sets the value for the entry.
     * 
     * @param val
     */
    public void setVal(float val) {
        this.mVal = val;
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
        Entry e = new Entry(mVal, mXIndex, mData);
        return e;
    }

    /**
     * Compares value, xIndex and data of the entries. Returns true if entries
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
        if (e.mXIndex != this.mXIndex)
            return false;

        if (Math.abs(e.mVal - this.mVal) > 0.00001f)
            return false;

        return true;
    }

    /**
     * returns a string representation of the entry containing x-index and value
     */
    @Override
    public String toString() {
        return "Entry, xIndex: " + mXIndex + " val (sum): " + getVal();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.mVal);
        dest.writeInt(this.mXIndex);
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
        this.mVal = in.readFloat();
        this.mXIndex = in.readInt();
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
