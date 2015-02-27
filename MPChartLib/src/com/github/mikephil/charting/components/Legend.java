
package com.github.mikephil.charting.components;

import android.graphics.Paint;

import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;

/**
 * Class representing the legend of the chart. The legend will contain one entry
 * per color and DataSet. Multiple colors in one DataSet are grouped together.
 * The legend object is NOT available before setting data to the chart.
 * 
 * @author Philipp Jahoda
 */
public class Legend extends ComponentBase {

    public enum LegendPosition {
        RIGHT_OF_CHART, RIGHT_OF_CHART_CENTER, RIGHT_OF_CHART_INSIDE, BELOW_CHART_LEFT, BELOW_CHART_RIGHT, BELOW_CHART_CENTER, PIECHART_CENTER
    }

    public enum LegendForm {
        SQUARE, CIRCLE, LINE
    }

    /** the legend colors */
    private int[] mColors;

    /** the legend labels */
    private String[] mLabels;

    /** the position relative to the chart the legend is drawn on */
    private LegendPosition mPosition = LegendPosition.BELOW_CHART_LEFT;

    /** the shape/form the legend colors are drawn in */
    private LegendForm mShape = LegendForm.SQUARE;

    /** the size of the legend forms/shapes */
    private float mFormSize = 8f;

    /**
     * the space between the legend entries on a horizontal axis, default 6f
     */
    private float mXEntrySpace = 6f;

    /**
     * the space between the legend entries on a vertical axis, default 5f
     */
    private float mYEntrySpace = 5f;

    /**
     * the space between the legend entries on a vertical axis, default 2f
     * private float mYEntrySpace = 2f; /** the space between the form and the
     * actual label/text
     */
    private float mFormToTextSpace = 5f;

    /** the space that should be left between stacked forms */
    private float mStackSpace = 3f;    

    /** default constructor */
    public Legend() {

        mFormSize = Utils.convertDpToPixel(8f);
        mXEntrySpace = Utils.convertDpToPixel(6f);
        mYEntrySpace = Utils.convertDpToPixel(5f);
        mFormToTextSpace = Utils.convertDpToPixel(5f);
        mTextSize = Utils.convertDpToPixel(10f);
        mStackSpace = Utils.convertDpToPixel(3f);
        this.mXOffset = Utils.convertDpToPixel(5f);
        this.mYOffset = Utils.convertDpToPixel(6f);
    }

    /**
     * Constructor. Provide colors and labels for the legend.
     * 
     * @param colors
     * @param labels
     */
    public Legend(int[] colors, String[] labels) {
        this();

        if (colors == null || labels == null) {
            throw new IllegalArgumentException("colors array or labels array is NULL");
        }

        if (colors.length != labels.length) {
            throw new IllegalArgumentException(
                    "colors array and labels array need to be of same size");
        }

        this.mColors = colors;
        this.mLabels = labels;
    }

    /**
     * Constructor. Provide colors and labels for the legend.
     * 
     * @param colors
     * @param labels
     */
    public Legend(ArrayList<Integer> colors, ArrayList<String> labels) {
        this();

        if (colors == null || labels == null) {
            throw new IllegalArgumentException("colors array or labels array is NULL");
        }

        if (colors.size() != labels.size()) {
            throw new IllegalArgumentException(
                    "colors array and labels array need to be of same size");
        }

        this.mColors = Utils.convertIntegers(colors);
        this.mLabels = Utils.convertStrings(labels);
    }

    /**
     * returns the maximum length in pixels across all legend labels + formsize
     * + formtotextspace
     * 
     * @param p the paint object used for rendering the text
     * @return
     */
    public float getMaximumEntryWidth(Paint p) {

        float max = 0f;

        for (int i = 0; i < mLabels.length; i++) {

            if (mLabels[i] != null) {

                float length = (float) Utils.calcTextWidth(p, mLabels[i]);

                if (length > max)
                    max = length;
            }
        }

        return max + mFormSize + mFormToTextSpace;
    }

    /**
     * returns the maximum height in pixels across all legend labels
     * 
     * @param p the paint object used for rendering the text
     * @return
     */
    public float getMaximumEntryHeight(Paint p) {

        float max = 0f;

        for (int i = 0; i < mLabels.length; i++) {

            if (mLabels[i] != null) {

                float length = (float) Utils.calcTextHeight(p, mLabels[i]);

                if (length > max)
                    max = length;
            }
        }

        return max;
    }

    /**
     * returns all the colors the legend uses
     * 
     * @return
     */
    public int[] getColors() {
        return mColors;
    }

    /**
     * returns all the labels the legend uses
     * 
     * @return
     */
    public String[] getLegendLabels() {
        return mLabels;
    }

    /**
     * Sets a custom array of labels for the legend. Make sure the labels array
     * has the same length as the colors array.
     * 
     * @param labels
     */
    public void setLegendLabels(String[] labels) {

        if (mColors.length != labels.length) {
            throw new IllegalArgumentException(
                    "colors array and labels array need to be of same size");
        }

        this.mLabels = labels;
    }

    /**
     * Returns the legend-label at the given index.
     * 
     * @param index
     * @return
     */
    public String getLabel(int index) {
        return mLabels[index];
    }

    /**
     * returns the position of the legend relative to the chart
     * 
     * @return
     */
    public LegendPosition getPosition() {
        return mPosition;
    }

    /**
     * sets the position of the legend relative to the whole chart
     * 
     * @param pos
     */
    public void setPosition(LegendPosition pos) {
        mPosition = pos;
    }

    /**
     * returns the current form/shape that is set for the legend
     * 
     * @return
     */
    public LegendForm getForm() {
        return mShape;
    }

    /**
     * sets the form/shape of the legend forms
     * 
     * @param shape
     */
    public void setForm(LegendForm shape) {
        mShape = shape;
    }

    /**
     * sets the size in pixels of the legend forms, this is internally converted
     * in dp, default 8f
     * 
     * @param size
     */
    public void setFormSize(float size) {
        mFormSize = Utils.convertDpToPixel(size);
    }

    /**
     * returns the size in dp of the legend forms
     * 
     * @return
     */
    public float getFormSize() {
        return mFormSize;
    }

    /**
     * returns the space between the legend entries on a horizontal axis in
     * pixels
     * 
     * @return
     */
    public float getXEntrySpace() {
        return mXEntrySpace;
    }

    /**
     * sets the space between the legend entries on a horizontal axis in pixels,
     * converts to dp internally
     * 
     * @param space
     */
    public void setXEntrySpace(float space) {
        mXEntrySpace = Utils.convertDpToPixel(space);
    }

    /**
     * returns the space between the legend entries on a vertical axis in pixels
     * 
     * @return
     */
    public float getYEntrySpace() {
        return mYEntrySpace;
    }

    /**
     * sets the space between the legend entries on a vertical axis in pixels,
     * converts to dp internally
     * 
     * @param space
     */
    public void setYEntrySpace(float space) {
        mYEntrySpace = Utils.convertDpToPixel(space);
    }

    /**
     * returns the space between the form and the actual label/text
     * 
     * @return
     */
    public float getFormToTextSpace() {
        return mFormToTextSpace;
    }

    /**
     * sets the space between the form and the actual label/text, converts to dp
     * internally
     * 
     * @param mFormToTextSpace
     */
    public void setFormToTextSpace(float space) {
        this.mFormToTextSpace = Utils.convertDpToPixel(space);
    }

    /**
     * applies the state from the legend in the parameter to this legend (except
     * colors, labels and offsets)
     * 
     * @param l
     */
    public void apply(Legend l) {

        mPosition = l.mPosition;
        mShape = l.mShape;
        mTypeface = l.mTypeface;
        mFormSize = l.mFormSize;
        mXEntrySpace = l.mXEntrySpace;
        mYEntrySpace = l.mYEntrySpace;
        mFormToTextSpace = l.mFormToTextSpace;
        mTextSize = l.mTextSize;
        mStackSpace = l.mStackSpace;
        mTextColor = l.mTextColor;
        mEnabled = l.mEnabled;
        mXOffset = l.mXOffset;
        mYOffset = l.mYOffset;
    }

    /**
     * returns the space that is left out between stacked forms (with no label)
     * 
     * @return
     */
    public float getStackSpace() {
        return mStackSpace;
    }

    /**
     * sets the space that is left out between stacked forms (with no label)
     * 
     * @param space
     */
    public void setStackSpace(float space) {
        mStackSpace = space;
    }

    /**
     * calculates the full width the fully drawn legend will use in pixels
     * 
     * @return
     */
    public float getFullWidth(Paint labelpaint) {

        float width = 0f;

        for (int i = 0; i < mLabels.length; i++) {

            // grouped forms have null labels
            if (mLabels[i] != null) {

                // make a step to the left
                if (mColors[i] != -2)
                    width += mFormSize + mFormToTextSpace;

                width += Utils.calcTextWidth(labelpaint, mLabels[i])
                        + mXEntrySpace;
            } else {
                width += mFormSize + mStackSpace;
            }
        }

        return width;
    }

    /**
     * Calculates the full height of the drawn legend.
     * 
     * @param mLegendLabelPaint
     * @return
     */
    public float getFullHeight(Paint labelpaint) {

        float height = 0f;

        for (int i = 0; i < mLabels.length; i++) {

            // grouped forms have null labels
            if (mLabels[i] != null) {

                height += Utils.calcTextHeight(labelpaint, mLabels[i])
                        + mYEntrySpace;
            }
        }

        return height;
    }

    /** the total width of the legend (needed width space) */
    public float mNeededWidth = 0f;

    /** the total height of the legend (needed height space) */
    public float mNeededHeight = 0f;
    
    public float mTextHeightMax = 0f;
    
    public float mTextWidthMax = 0f;

    /**
     * Calculates the dimensions of the Legend. This includes the maximum width
     * and height of a single entry, as well as the total width and height of
     * the Legend.
     * 
     * @param labelpaint
     */
    public void calculateDimensions(Paint labelpaint) {

        if (mPosition == LegendPosition.RIGHT_OF_CHART
                || mPosition == LegendPosition.RIGHT_OF_CHART_CENTER
                || mPosition == LegendPosition.PIECHART_CENTER) {
            mNeededWidth = getMaximumEntryWidth(labelpaint);
            mNeededHeight = getFullHeight(labelpaint);
            mTextWidthMax = mNeededWidth;
            mTextHeightMax = getMaximumEntryHeight(labelpaint);

        } else {

            mNeededWidth = getFullWidth(labelpaint);
            mNeededHeight = getMaximumEntryHeight(labelpaint);
            mTextWidthMax = getMaximumEntryWidth(labelpaint);
            mTextHeightMax = mNeededHeight;
        }
    }
}
