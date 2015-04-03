
package com.github.mikephil.charting.data;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;

import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * DataSet for the CandleStickChart.
 * 
 * @author Philipp Jahoda
 */
public class CandleDataSet extends BarLineScatterCandleDataSet<CandleEntry> {

    /** the width of the shadow of the candle */
    private float mShadowWidth = 3f;

    /** the space between the candle entries, default 0.1f (10%) */
    private float mBodySpace = 0.1f;

    /** paint style when open <= close */
    protected Paint.Style mPaintStyle = Paint.Style.FILL;

    /** paint style when open > close */
    protected Paint.Style mDecreasingPaintStyle = Paint.Style.STROKE;

    /** List representing all colors that are used for this DataSet when open > close */
    protected List<Integer> mDecreasingColors;

    /** shadow line color, set -1 for backward compatibility and uses default color */
    protected int mShadowColor = -1;

    public CandleDataSet(List<CandleEntry> yVals, String label) {
        super(yVals, label);
        mDecreasingColors = new ArrayList<Integer>();
    }

    @Override
    public DataSet<CandleEntry> copy() {

        List<CandleEntry> yVals = new ArrayList<CandleEntry>();

        for (int i = 0; i < mYVals.size(); i++) {
            yVals.add(((CandleEntry) mYVals.get(i)).copy());
        }

        CandleDataSet copied = new CandleDataSet(yVals, getLabel());
        copied.mColors = mColors;
        copied.mShadowWidth = mShadowWidth;
        copied.mBodySpace = mBodySpace;
        copied.mHighLightColor = mHighLightColor;
        copied.mDecreasingColors = mDecreasingColors;
        copied.mPaintStyle = mPaintStyle;
        copied.mDecreasingPaintStyle = mDecreasingPaintStyle;
        copied.mShadowColor = mShadowColor;

        return copied;
    }

    @Override
    protected void calcMinMax() {
        // super.calcMinMax();

        if (mYVals.size() == 0) {
            return;
        }

        List<CandleEntry> entries = mYVals;

        mYMin = entries.get(0).getLow();
        mYMax = entries.get(0).getHigh();

        for (int i = 0; i < entries.size(); i++) {

            CandleEntry e = entries.get(i);

            if (e.getLow() < mYMin)
                mYMin = e.getLow();

            if (e.getHigh() > mYMax)
                mYMax = e.getHigh();
        }
    }

    /**
     * Sets the space that is left out on the left and right side of each
     * candle, default 0.1f (10%), max 0.45f, min 0f
     * 
     * @param space
     */
    public void setBodySpace(float space) {

        if (space < 0f)
            space = 0f;
        if (space > 0.45f)
            space = 0.45f;

        mBodySpace = space;
    }

    /**
     * Returns the space that is left out on the left and right side of each
     * candle.
     * 
     * @return
     */
    public float getBodySpace() {
        return mBodySpace;
    }

    /**
     * Sets the width of the candle-shadow-line in pixels. Default 3f.
     * 
     * @param width
     */
    public void setShadowWidth(float width) {
        mShadowWidth = Utils.convertDpToPixel(width);
    }

    /**
     * Returns the width of the candle-shadow-line in pixels.
     * 
     * @return
     */
    public float getShadowWidth() {
        return mShadowWidth;
    }

    //TODO
    /**
     * It is necessary to implement ColorsList class that will encapsulate colors list functionality,
     * because It's wrong to copy paste setColor, addColor, ... resetColors for each time when we want
     * to add a coloring options for one of objects
     * @author Mesrop
     */

    /** BELOW THIS COLOR HANDLING */

    /**
     * Sets the colors that should be used fore this DataSet when open > close . Colors are reused
     * as soon as the number of Entries the DataSet represents is higher than
     * the size of the colors array. If you are using colors from the resources,
     * make sure that the colors are already prepared (by calling
     * getResources().getColor(...)) before adding them to the DataSet.
     *
     * @param colors
     */
    public void setDecreasingColors(List<Integer> colors) {
        this.mDecreasingColors = colors;
    }

    /**
     * Sets the colors that should be used fore this DataSet when open > close. Colors are reused
     * as soon as the number of Entries the DataSet represents is higher than
     * the size of the colors array. If you are using colors from the resources,
     * make sure that the colors are already prepared (by calling
     * getResources().getColor(...)) before adding them to the DataSet.
     *
     * @param colors
     */
    public void setDecreasingColors(int[] colors) {
        this.mDecreasingColors = ColorTemplate.createColors(colors);
    }

    /**
     * Sets the colors that should be used fore this DataSet when open > close. Colors are reused
     * as soon as the number of Entries the DataSet represents is higher than
     * the size of the colors array. You can use
     * "new int[] { R.color.red, R.color.green, ... }" to provide colors for
     * this method. Internally, the colors are resolved using
     * getResources().getColor(...)
     *
     * @param colors
     */
    public void setDecreasingColors(int[] colors, Context c) {

        List<Integer> clrs = new ArrayList<Integer>();

        for (int color : colors) {
            clrs.add(c.getResources().getColor(color));
        }

        mDecreasingColors = clrs;
    }

    /**
     * Adds a new color to the colors array of the DataSet for open > close.
     *
     * @param color
     */
    public void addColor(int color) {
        if (mDecreasingColors == null)
            mDecreasingColors = new ArrayList<Integer>();
        mDecreasingColors.add(color);
    }

    /**
     * Sets the one and ONLY color that should be used for this DataSet when open > close.
     * Internally, this recreates the colors array and adds the specified color.
     *
     * @param color
     */
    public void setDecreasingColor(int color) {
        resetDecreasingColors();
        mDecreasingColors.add(color);
    }

    /**
     * returns all the decreasing colors that are set for this DataSet
     *
     * @return
     */
    public List<Integer> getDecreasingColors() {
        return mDecreasingColors;
    }

    /**
     * Returns the decreasing color at the given index of the DataSet's color array.
     * Performs a IndexOutOfBounds check by modulus.
     *
     * @param index
     * @return
     */
    public int getDecreasingColor(int index) {
        return mDecreasingColors.get(index % mDecreasingColors.size());
    }

    /**
     * Returns the first color (index 0) of the decreasing colors-array this DataSet
     * contains.
     *
     * @return
     */
    public int getDecreasingColor() {
        return mDecreasingColors.get(0);
    }

    /**
     * Resets all colors of this DataSet and recreates the colors array.
     */
    public void resetDecreasingColors() {
        mDecreasingColors = new ArrayList<Integer>();
    }

    /**
     * Returns paint style when open > close
     * @return
     */
    public Paint.Style getDecreasingPaintStyle() {
        return mDecreasingPaintStyle;
    }

    /**
     * Sets paint style when open > close
     * @param decreasingPaintStyle
     */
    public void setDecreasingPaintStyle(Paint.Style decreasingPaintStyle) {
        this.mDecreasingPaintStyle = decreasingPaintStyle;
    }

    /**
     * Returns paint style when open <= close
     * @return
     */
    public Paint.Style getPaintStyle() {
        return mPaintStyle;
    }

    /**
     * Sets paint style when open <= close
     * @param paintStyle
     */
    public void setPaintStyle(Paint.Style paintStyle) {
        this.mPaintStyle = paintStyle;
    }


    //TODO: Here temporary use only one color until ColorsList will be implemented
    /**
     * Returns shadow color for all entries
     * @return
     */
    public int getShadowColor() {
        return mShadowColor;
    }

    /**
     * Sets shadow color for all entries
     * @param shadowColor
     */
    public void setShadowColor(int shadowColor) {
        this.mShadowColor = shadowColor;
    }
}
