
package com.github.mikephil.charting.utils;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;

/**
 * Class representing the legend of the chart.
 * 
 * @author Philipp Jahoda
 */
public class Legend {

    public enum LegendPosition {
        LEFT_OF_CHART, BELOW_CHART
    }

    public enum LegendForm {
        SQUARE, CIRCLE, LINE
    }

    /** the legend colors */
    private int[] mColors;

    /** the legend labels */
    private String[] mLegendLabels;

    /** the position relative to the chart the legend is drawn on */
    private LegendPosition mPosition = LegendPosition.BELOW_CHART;

    /** the shape/form the legend colors are drawn in */
    private LegendForm mShape = LegendForm.SQUARE;

    /** the typeface used for the legend labels */
    private Typeface mTypeface = null;

    /** the size of the legend forms/shapes */
    private float mFormSize = 8f;

    /** the space between the legend entries on a vertical or horizontal axis */
    private float mEntrySpace;

    /** the space between the form and the actual label/text */
    private float mFormToTextSpace;

    /** default constructor */
    public Legend() {

        mFormSize = Utils.convertDpToPixel(mFormSize);
        mEntrySpace = mFormSize * 2f;
        mFormToTextSpace = mFormSize * 2f;
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
     * returns the maximum length in pixels over all legend labels + their forms
     * 
     * @param p the paint object used for rendering the text
     * @return
     */
    public int getMaximumEntryLength(Paint p) {

        int max = 0;

        for (int i = 0; i < mLegendLabels.length; i++) {
            
            if(mLegendLabels[i] != null) {
                
                int length = Utils.calcTextWidth(p, mLegendLabels[i]);

                if (length > max)
                    max = length;
            }
        }

        return max + (int) mFormSize * 4;
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
     * returns the space between the legend entries on a vertical or horizontal
     * axis in pixels
     * 
     * @return
     */
    public float getEntrySpace() {
        return mEntrySpace;
    }

    /**
     * sets the space between the legend entries on a vertical or horizontal
     * axis in pixels, converts to dp internally
     * 
     * @param space
     */
    public void setEntrySpace(float space) {
        mEntrySpace = Utils.convertDpToPixel(space);
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

    // /**
    // * Draws the legend.
    // * @param drawCanvas
    // * @param labelPaint
    // * @param formPaint
    // */
    // public void draw(Canvas drawCanvas, Paint labelPaint, Paint formPaint) {
    //
    // if(mTypeface != null) labelPaint.setTypeface(mTypeface);
    //
    // switch(mPosition) {
    // case BELOW_CHART:
    //
    // for(int i = 0; i < mLegendLabels.length; i++) {
    //
    // formPaint.setColor(mColors[i]);
    //
    // drawCanvas.drawRect(left, top, right, bottom, formPaint);
    // }
    //
    //
    // break;
    // case LEFT_OF_CHART:
    // break;
    // }
    // }
}
