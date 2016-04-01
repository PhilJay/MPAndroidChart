
package com.github.mikephil.charting.data;

import com.github.mikephil.charting.interfaces.datasets.IPieDataSet;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class PieDataSet extends DataSet<Entry> implements IPieDataSet {

    /** the space in pixels between the chart-slices, default 0f */
    private float mSliceSpace = 0f;

    /** indicates the selection distance of a pie slice */
    private float mShift = 18f;

    private ValuePosition mXValuePosition = ValuePosition.INSIDE_SLICE;
    private ValuePosition mYValuePosition = ValuePosition.INSIDE_SLICE;
    private int mValueLineColor = 0xff000000;
    private float mValueLineWidth = 1.0f;
    private float mValueLinePart1OffsetPercentage = 75.f;
    private float mValueLinePart1Length = 0.3f;
    private float mValueLinePart2Length = 0.4f;
    private boolean mValueLineVariableLength = true;

    public PieDataSet(List<Entry> yVals, String label) {
        super(yVals, label);
//        mShift = Utils.convertDpToPixel(12f);
    }

    @Override
    public DataSet<Entry> copy() {

        List<Entry> yVals = new ArrayList<Entry>();

        for (int i = 0; i < mYVals.size(); i++) {
            yVals.add(mYVals.get(i).copy());
        }

        PieDataSet copied = new PieDataSet(yVals, getLabel());
        copied.mColors = mColors;
        copied.mSliceSpace = mSliceSpace;
        copied.mShift = mShift;
        return copied;
    }

    /**
     * Sets the space that is left out between the piechart-slices in dp.
     * Default: 0 --> no space, maximum 20f
     *
     * @param spaceDp
     */
    public void setSliceSpace(float spaceDp) {

        if (spaceDp > 20)
            spaceDp = 20f;
        if (spaceDp < 0)
            spaceDp = 0f;

        mSliceSpace = Utils.convertDpToPixel(spaceDp);
    }

    @Override
    public float getSliceSpace() {
        return mSliceSpace;
    }

    /**
     * sets the distance the highlighted piechart-slice of this DataSet is
     * "shifted" away from the center of the chart, default 12f
     * 
     * @param shift
     */
    public void setSelectionShift(float shift) {
        mShift = Utils.convertDpToPixel(shift);
    }

    @Override
    public float getSelectionShift() {
        return mShift;
    }

    @Override
    public ValuePosition getXValuePosition()
    {
        return mXValuePosition;
    }

    public void setXValuePosition(ValuePosition xValuePosition)
    {
        this.mXValuePosition = xValuePosition;
    }

    @Override
    public ValuePosition getYValuePosition()
    {
        return mYValuePosition;
    }

    public void setYValuePosition(ValuePosition yValuePosition)
    {
        this.mYValuePosition = yValuePosition;
    }

    /** When valuePosition is OutsideSlice, indicates line color */
    @Override
    public int getValueLineColor()
    {
        return mValueLineColor;
    }

    public void setValueLineColor(int valueLineColor)
    {
        this.mValueLineColor = valueLineColor;
    }

    /** When valuePosition is OutsideSlice, indicates line width */
    @Override
    public float getValueLineWidth()
    {
        return mValueLineWidth;
    }

    public void setValueLineWidth(float valueLineWidth)
    {
        this.mValueLineWidth = valueLineWidth;
    }

    /** When valuePosition is OutsideSlice, indicates offset as percentage out of the slice size */
    @Override
    public float getValueLinePart1OffsetPercentage()
    {
        return mValueLinePart1OffsetPercentage;
    }

    public void setValueLinePart1OffsetPercentage(float valueLinePart1OffsetPercentage)
    {
        this.mValueLinePart1OffsetPercentage = valueLinePart1OffsetPercentage;
    }

    /** When valuePosition is OutsideSlice, indicates length of first half of the line */
    @Override
    public float getValueLinePart1Length()
    {
        return mValueLinePart1Length;
    }

    public void setValueLinePart1Length(float valueLinePart1Length)
    {
        this.mValueLinePart1Length = valueLinePart1Length;
    }

    /** When valuePosition is OutsideSlice, indicates length of second half of the line */
    @Override
    public float getValueLinePart2Length()
    {
        return mValueLinePart2Length;
    }

    public void setValueLinePart2Length(float valueLinePart2Length)
    {
        this.mValueLinePart2Length = valueLinePart2Length;
    }

    /** When valuePosition is OutsideSlice, this allows variable line length */
    @Override
    public boolean isValueLineVariableLength()
    {
        return mValueLineVariableLength;
    }

    public void setValueLineVariableLength(boolean valueLineVariableLength)
    {
        this.mValueLineVariableLength = valueLineVariableLength;
    }

    public enum ValuePosition {
        INSIDE_SLICE,
        OUTSIDE_SLICE
    }
}
