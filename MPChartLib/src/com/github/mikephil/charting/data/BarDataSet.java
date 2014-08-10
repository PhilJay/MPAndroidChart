
package com.github.mikephil.charting.data;

import android.graphics.Color;

import java.util.ArrayList;

public class BarDataSet extends DataSet {

    /** space indicator between the bars 0.1f == 10 % */
    private float mBarSpace = 0.15f;

    /**
     * the maximum number of bars that are stacked upon each other, this value
     * is calculated from the Entries that are added to the DataSet
     */
    private int mStackSize = 1;

    /** the color used for drawing the bar shadows */
    private int mBarShadowColor = Color.rgb(215, 215, 215);

    /**
     * the overall entry count, including counting each stack-value individually
     */
    private int mEntryCountStacks = 0;

    /**
     * array of labels used to describe the different values of the stacked bars
     */
    private String[] mStackLabels = new String[] { "Stack" };
    

    public BarDataSet(ArrayList<Entry> yVals, String label) {
        super(yVals, label);

        calcStackSize();
        calcEntryCountIncludingStacks();
    }

    @Override
    public DataSet copy() {

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (int i = 0; i < mYVals.size(); i++) {
            yVals.add(mYVals.get(i).copy());
        }

        BarDataSet copied = new BarDataSet(yVals, getLabel());
        copied.mStackSize = mStackSize;
        copied.mBarSpace = mBarSpace;
        copied.mEntryCountStacks = mEntryCountStacks;

        return copied;
    }

    /**
     * Calculates the total number of entries this DataSet represents, including
     * stacks. All values belonging to a stack are calculated separately.
     */
    private void calcEntryCountIncludingStacks() {

        mEntryCountStacks = 0;

        for (int i = 0; i < mYVals.size(); i++) {

            float[] vals = mYVals.get(i).getVals();

            if (vals == null)
                mEntryCountStacks++;
            else
                mEntryCountStacks += vals.length;
        }
    }

    /**
     * calculates the maximum stacksize that occurs in the Entries array of this
     * DataSet
     */
    private void calcStackSize() {

        for (int i = 0; i < mYVals.size(); i++) {

            float[] vals = mYVals.get(i).getVals();

            if (vals != null && vals.length > mStackSize)
                mStackSize = vals.length;
        }
    }

    /**
     * Returns the maximum number of bars that can be stacked upon another in
     * this DataSet.
     * 
     * @return
     */
    public int getStackSize() {
        return mStackSize;
    }

    /**
     * returns the overall entry count, including counting each stack-value
     * individually
     * 
     * @return
     */
    public int getEntryCountStacks() {
        return mEntryCountStacks;
    }

    /**
     * returns the space between bars in percent of the whole width of one value
     * 
     * @return
     */
    public float getBarSpacePercent() {
        return mBarSpace * 100f;
    }

    /**
     * returns the space between bars as the actual value (0 - 1.0f)
     * 
     * @return
     */
    public float getBarSpace() {
        return mBarSpace;
    }

    /**
     * sets the space between the bars in percent of the total bar width
     * 
     * @param percent
     */
    public void setBarSpacePercent(float percent) {
        mBarSpace = percent / 100f;
    }

    /**
     * Sets the color used for drawing the bar-shadows. The bar shadows is a
     * surface behind the bar that indicates the maximum value. Don't for get to
     * use getResources().getColor(...) to set this. Or Color.rgb(...).
     * 
     * @param color
     */
    public void setBarShadowColor(int color) {
        mBarShadowColor = color;
    }

    /**
     * Returns the color used for drawing the bar-shadows. The bar shadows is a
     * surface behind the bar that indicates the maximum value.
     * 
     * @return
     */
    public int getBarShadowColor() {
        return mBarShadowColor;
    }

    /**
     * Sets labels for different values of bar-stacks, in case there are one.
     * 
     * @param labels
     */
    public void setStackLabels(String[] labels) {
        mStackLabels = labels;
    }

    /**
     * returns the labels used for the different value-stacks
     * 
     * @return
     */
    public String[] getStackLabels() {
        return mStackLabels;
    }
}
