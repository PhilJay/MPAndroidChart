
package com.github.mikephil.charting.utils;

import android.graphics.Typeface;

/**
 * Class representing the legend of the chart.
 * 
 * @author PHilipp Jahoda
 */
public class Legend {

    public enum LegendPosition {
        LEFT_OF_CHART, BELOW_CHART
    }

    public enum LegendShape {
        SQUARE, CIRCLE
    }

    /** the legend colors */
    private int[] mColors;

    /** the legend labels */
    private String[] mLegendLabels;

    /** the position relative to the chart the legend is drawn on */
    private LegendPosition mPosition = LegendPosition.BELOW_CHART;

    /** the shape the legend colors are drawn in */
    private LegendShape mShape = LegendShape.SQUARE;

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

    public int[] getColors() {
        return mColors;
    }

    public String[] getLegendLabels() {
        return mLegendLabels;
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
     * returns the current shape that is set for the legend
     * 
     * @return
     */
    public LegendShape getShape() {
        return mShape;
    }

    /**
     * sets the shape of the legend forms
     * 
     * @param shape
     */
    public void setShape(LegendShape shape) {
        mShape = shape;
    }

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
     * @return
     */
    public float getFormToTextSpace() {
        return mFormToTextSpace;
    }
    
    /**
     * sets the space between the form and the actual label/text, converts to dp internally
     * @param mFormToTextSpace
     */
    public void setFormToTextSpace(float space) {
        this.mFormToTextSpace = Utils.convertDpToPixel(space);;
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
