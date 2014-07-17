
package com.github.mikephil.charting.utils;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;

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

    private Typeface mTypeface = null;
    
    private float mFormSize = 12f;

    /**
     * Constructor. Provide colors and labels for the legend.
     * 
     * @param colors
     * @param labels
     */
    public Legend(int[] colors, String[] labels) {

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

    public LegendPosition getPosition() {
        return mPosition;
    }

    public void setPosition(LegendPosition pos) {
        mPosition = pos;
    }

    public LegendShape getShape() {
        return mShape;
    }

    public void setShape(LegendShape shape) {
        mShape = shape;
    }

    public Typeface getTypeface() {
        return mTypeface;
    }

    public void setTypeface(Typeface tf) {
        mTypeface = tf;
    }
    
    public void setFormSize(float size) {
        mFormSize = size;
    }
    
    public float getFormSize() {
        return mFormSize;
    }

//    /**
//     * Draws the legend.
//     * @param drawCanvas
//     * @param labelPaint
//     * @param formPaint
//     */
//    public void draw(Canvas drawCanvas, Paint labelPaint, Paint formPaint) {
//        
//        if(mTypeface != null) labelPaint.setTypeface(mTypeface);
//        
//        switch(mPosition) {
//            case BELOW_CHART: 
//                
//                for(int i = 0; i < mLegendLabels.length; i++) {
//                    
//                    formPaint.setColor(mColors[i]);
//                    
//                    drawCanvas.drawRect(left, top, right, bottom, formPaint);
//                }
//                
//                
//                break;
//            case LEFT_OF_CHART:
//                break;
//        }
//    }
}
