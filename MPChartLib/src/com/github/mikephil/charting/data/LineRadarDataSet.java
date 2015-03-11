
package com.github.mikephil.charting.data;

import android.graphics.Color;

import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;

/**
 * Base dataset for line and radar DataSets.
 * 
 * @author Philipp Jahoda
 */
public abstract class LineRadarDataSet<T extends Entry> extends BarLineScatterCandleDataSet<T> {

    /** the color that is used for filling the line surface */
    private int mFillColor = Color.rgb(140, 234, 255);

    /** transparency used for filling line surface */
    private int mFillAlpha = 85;

    /** the width of the drawn data lines */
    private float mLineWidth = 2.5f;

    /** if true, the data will also be drawn filled */
    private boolean mDrawFilled = false;

    // private Shader mShader;

    public LineRadarDataSet(ArrayList<T> yVals, String label) {
        super(yVals, label);
    }

    /**
     * returns the color that is used for filling the line surface
     * 
     * @return
     */
    public int getFillColor() {
        return mFillColor;
    }

    /**
     * sets the color that is used for filling the line surface
     * 
     * @param color
     */
    public void setFillColor(int color) {
        mFillColor = color;
    }

    /**
     * returns the alpha value that is used for filling the line surface,
     * default: 85
     * 
     * @return
     */
    public int getFillAlpha() {
        return mFillAlpha;
    }

    /**
     * sets the alpha value (transparency) that is used for filling the line
     * surface (0-255), default: 85
     * 
     * @param color
     */
    public void setFillAlpha(int alpha) {
        mFillAlpha = alpha;
    }

    /**
     * set the line width of the chart (min = 0.2f, max = 10f); default 1f NOTE:
     * thinner line == better performance, thicker line == worse performance
     * 
     * @param width
     */
    public void setLineWidth(float width) {

        if (width < 0.2f)
            width = 0.5f;
        if (width > 10.0f)
            width = 10.0f;
        mLineWidth = Utils.convertDpToPixel(width);
    }

    /**
     * returns the width of the drawn chart line
     * 
     * @return
     */
    public float getLineWidth() {
        return mLineWidth;
    }

    /**
     * Set to true if the DataSet should be drawn filled (surface), and not just
     * as a line, disabling this will give great performance boost! default:
     * false
     * 
     * @param filled
     */
    public void setDrawFilled(boolean filled) {
        mDrawFilled = filled;
    }

    /**
     * returns true if filled drawing is enabled, false if not
     * 
     * @return
     */
    public boolean isDrawFilledEnabled() {
        return mDrawFilled;
    }
}
