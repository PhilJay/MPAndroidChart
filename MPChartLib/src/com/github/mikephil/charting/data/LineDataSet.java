package com.github.mikephil.charting.data;

import android.graphics.DashPathEffect;

import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;

public class LineDataSet extends DataSet {    

    /** the radius of the circle-shaped value indicators */
    private float mCircleSize = 4f;

    /** the width of the drawn data lines */
    private float mLineWidth = 1f;
    
    /** the path effect of this DataSet that makes dashed lines possible */
    private DashPathEffect mDashPathEffect = null;

    public LineDataSet(ArrayList<Entry> yVals, String label) {
        super(yVals, label);        

        mCircleSize = Utils.convertDpToPixel(mCircleSize);
    }

    @Override
    public DataSet copy() {
        
        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (int i = 0; i < mYVals.size(); i++) {
            yVals.add(mYVals.get(i).copy());
        }

        LineDataSet copied = new LineDataSet(yVals, getLabel());
        return copied;
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
     * sets the size (radius) of the circle shpaed value indicators, default
     * size = 4f
     * 
     * @param size
     */
    public void setCircleSize(float size) {
        mCircleSize = Utils.convertDpToPixel(size);
    }

    /**
     * returns the circlesize
     */
    public float getCircleSize() {
        return mCircleSize;
    }
    

    /**
     * Enables the line to be drawn in dashed mode, e.g. like this "- - - - - -"
     * 
     * @param lineLength the length of the line pieces
     * @param spaceLength the length of space inbetween the pieces
     * @param phase offset, in degrees (normally, use 0)
     */
    public void enableDashedLine(float lineLength, float spaceLength, float phase) {
        mDashPathEffect = new DashPathEffect(new float[] {
                lineLength, spaceLength
        }, phase);
    }

    /**
     * Disables the line to be drawn in dashed mode.
     */
    public void disableDashedLine() {
        mDashPathEffect = null;
    }

    /**
     * Returns true if the dashed-line effect is enabled, false if not.
     * 
     * @return
     */
    public boolean isDashedLineEnabled() {
        return mDashPathEffect == null ? false : true;
    }
    
    /**
     * returns the DashPathEffect that is set for this DataSet
     * @return
     */
    public DashPathEffect getDashPathEffect() {
        return mDashPathEffect;
    }
}
