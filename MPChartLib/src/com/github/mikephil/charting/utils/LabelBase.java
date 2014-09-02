
package com.github.mikephil.charting.utils;

import android.graphics.Color;
import android.graphics.Typeface;

/**
 * Baseclass of all labels.
 * 
 * @author Philipp Jahoda
 */
public abstract class LabelBase {

    /** the typeface to use for the labels */
    private Typeface mTypeface;

    /** the size of the label text */
    private float mTextSize = 10f;

    /** the text color to use */
    private int mTextColor = Color.BLACK;

    /** default constructor */
    public LabelBase() {
        mTextSize = Utils.convertDpToPixel(10f);
    }

    /**
     * sets the size of the label text in pixels min = 6f, max = 16f, default
     * 10f
     * 
     * @param size
     */
    public void setTextSize(float size) {

        if (size > 16f)
            size = 16f;
        if (size < 6f)
            size = 6f;

        mTextSize = Utils.convertDpToPixel(size);
    }

    /**
     * returns the text size that is currently set for the labels
     * 
     * @return
     */
    public float getTextSize() {
        return mTextSize;
    }

    /**
     * sets the typeface that should be used for the labels
     * 
     * @param t
     */
    public void setTypeface(Typeface t) {
        mTypeface = t;
    }

    /**
     * returns the typeface that is used for the labels
     * 
     * @return
     */
    public Typeface getTypeface() {
        return mTypeface;
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
}
