
package com.github.mikephil.charting.utils;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import java.util.ArrayList;

/**
 * Class representing the legend of the chart. The legend will contain one entry
 * per color and DataSet. Multiple colors in one DataSet are grouped together.
 * The legend object is NOT available before setting data to the chart.
 * 
 * @author Philipp Jahoda
 */
public class Legend {

    public enum LegendPosition {
        RIGHT_OF_CHART, RIGHT_OF_CHART_CENTER, RIGHT_OF_CHART_INSIDE, BELOW_CHART_LEFT, BELOW_CHART_RIGHT, BELOW_CHART_CENTER, PIECHART_CENTER, NONE
    }

    public enum LegendForm {
        SQUARE, CIRCLE, LINE
    }

    /** offsets for the legend */
    private float mLegendOffsetBottom = 12f, mLegendOffsetRight = 12f, mLegendOffsetLeft = 12f,
            mLegendOffsetTop = 12f;

    /** the legend colors */
    private int[] mColors;

    /** the legend labels */
    private String[] mLegendLabels;

    /** the position relative to the chart the legend is drawn on */
    private LegendPosition mPosition = LegendPosition.BELOW_CHART_LEFT;

    /** the shape/form the legend colors are drawn in */
    private LegendForm mShape = LegendForm.SQUARE;

    /** the typeface used for the legend labels */
    private Typeface mTypeface = null;

    /** the text size of the legend labels */
    private float mTextSize = 9f;

    /** the text color to use */
    private int mTextColor = Color.BLACK;

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
        mTextSize = Utils.convertDpToPixel(9f);
        mStackSpace = Utils.convertDpToPixel(3f);
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
        this.mLegendLabels = labels;
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
        this.mLegendLabels = Utils.convertStrings(labels);
    }

    /**
     * returns the maximum length in pixels over all legend labels + their forms
     * 
     * @param p the paint object used for rendering the text
     * @return
     */
    public int getMaximumEntryLength(Paint p) {

        int max = 0;

        for (int i = 0; i < mLegendLabels.length; i++) {

            if (mLegendLabels[i] != null) {

                int length = Utils.calcTextWidth(p, mLegendLabels[i]);

                if (length > max)
                    max = length;
            }
        }

        return max + (int) mFormSize;
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
        return mLegendLabels;
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

        this.mLegendLabels = labels;
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
     * returns the typeface used for the legend labels, returns null if none is
     * set
     * 
     * @return
     */
    public Typeface getTypeface() {
        return mTypeface;
    }

    /**
     * sets a specific typeface for the legend labels
     * 
     * @param tf
     */
    public void setTypeface(Typeface tf) {
        mTypeface = tf;
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
     * draws the form at the given position with the color at the given index
     * 
     * @param c canvas to draw with
     * @param x
     * @param y
     * @param p paint to use for drawing
     * @param index the index of the color to use (in the colors array)
     */
    public void drawForm(Canvas c, float x, float y, Paint p, int index) {

        if (mColors[index] == -2)
            return;

        p.setColor(mColors[index]);

        float half = mFormSize / 2f;

        switch (getForm()) {
            case CIRCLE:
                c.drawCircle(x + half, y + half, half, p);
                break;
            case SQUARE:
                c.drawRect(x, y, x + mFormSize, y + mFormSize, p);
                break;
            case LINE:
                c.drawLine(x - half, y + half, x + half, y + half, p);
                break;
        }
    }

    /**
     * draws the label at the given index in the labels array at the given
     * position
     * 
     * @param c canvas to draw with
     * @param x
     * @param y
     * @param p paint to use for drawing
     * @param index index in the labels-array
     */
    public void drawLabel(Canvas c, float x, float y, Paint p, int index) {

        c.drawText(mLegendLabels[index], x, y, p);
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

        // apply offsets
        mLegendOffsetBottom = l.mLegendOffsetBottom;
        mLegendOffsetLeft = l.mLegendOffsetLeft;
        mLegendOffsetRight = l.mLegendOffsetRight;
        mLegendOffsetTop = l.mLegendOffsetTop;
    }

    /**
     * returns the bottom offset
     * 
     * @return
     */
    public float getOffsetBottom() {
        return mLegendOffsetBottom;
    }

    /**
     * returns the right offset
     * 
     * @return
     */
    public float getOffsetRight() {
        return mLegendOffsetRight;
    }

    /**
     * sets the bottom offset
     * 
     * @param off
     */
    public void setOffsetBottom(float off) {
        mLegendOffsetBottom = off;
    }

    /**
     * sets the right offset
     * 
     * @param off
     */
    public void setOffsetRight(float off) {
        mLegendOffsetRight = off;
    }

    /**
     * returns the bottom offset
     * 
     * @return
     */
    public float getOffsetTop() {
        return mLegendOffsetTop;
    }

    /**
     * returns the left offset
     * 
     * @return
     */
    public float getOffsetLeft() {
        return mLegendOffsetLeft;
    }

    /**
     * sets the bottom offset
     * 
     * @param off
     */
    public void setOffsetTop(float off) {
        mLegendOffsetTop = off;
    }

    /**
     * sets the left offset
     * 
     * @param off
     */
    public void setOffsetLeft(float off) {
        mLegendOffsetLeft = off;
    }

    /**
     * sets the text size of the legend labels, default 9f
     * 
     * @param size
     */
    public void setTextSize(float size) {
        mTextSize = Utils.convertDpToPixel(size);
    }

    /**
     * returns the text size of the legend labels
     * 
     * @return
     */
    public float getTextSize() {
        return mTextSize;
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

        for (int i = 0; i < mLegendLabels.length; i++) {

            // grouped forms have null labels
            if (mLegendLabels[i] != null) {

                // make a step to the left
                if (mColors[i] != -2)
                    width += mFormSize + mFormToTextSpace;

                width += Utils.calcTextWidth(labelpaint, mLegendLabels[i])
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

        for (int i = 0; i < mLegendLabels.length; i++) {

            // grouped forms have null labels
            if (mLegendLabels[i] != null) {

                height += Utils.calcTextHeight(labelpaint, mLegendLabels[i])
                        + mYEntrySpace;
            }
        }

        return height;
    }

    /**
     * Sets the text color to use for the legend labels. Make sure to use
     * getResources().getColor(...) when using a color from the resources.
     * 
     * @param color
     */
    public void setTextColor(int color) {
        mTextColor = color;
    }

    /**
     * Returns the text color that is set for the legend labels.
     * 
     * @return
     */
    public int getTextColor() {
        return mTextColor;
    }
}
