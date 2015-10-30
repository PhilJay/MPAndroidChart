package com.github.mikephil.charting.data;

import android.graphics.Color;
import android.graphics.Typeface;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datainterfaces.datasets.IDataSet;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Philipp Jahoda on 21/10/15.
 * This is the base dataset of all DataSets. It's purpose is to implement critical methods
 * provided by the IDataSet interface.
 */
public abstract class BaseDataSet<T extends Entry> implements IDataSet<T> {

    /**
     * List representing all colors that are used for this DataSet
     */
    protected List<Integer> mColors = null;

    /**
     * label that describes the DataSet or the data the DataSet represents
     */
    private String mLabel = "DataSet";

    /**
     * this specifies which axis this DataSet should be plotted against
     */
    protected YAxis.AxisDependency mAxisDependency = YAxis.AxisDependency.LEFT;

    /**
     * the last start value used for calcMinMax
     */
    protected int mLastStart = 0;

    /**
     * the last end value used for calcMinMax
     */
    protected int mLastEnd = 0;

    /**
     * if true, value highlightning is enabled
     */
    protected boolean mHighlightEnabled = true;

    /**
     * custom formatter that is used instead of the auto-formatter if set
     */
    protected transient ValueFormatter mValueFormatter;

    /**
     * the color used for the value-text
     */
    protected int mValueColor = Color.BLACK;

    /**
     * the typeface used for the value text
     */
    protected Typeface mValueTypeface;

    /**
     * if true, y-values are drawn on the chart
     */
    protected boolean mDrawValues = true;

    /**
     * the size of the value-text labels
     */
    protected float mValueTextSize = 17f;

    /**
     * flag that indicates if the DataSet is visible or not
     */
    protected boolean mVisible = true;

    /**
     * Default constructor.
     */
    public BaseDataSet() {

    }

    /**
     * Constructor with label.
     *
     * @param label
     */
    public BaseDataSet(String label) {
        this.mLabel = label;
    }

    /**
     * Use this method to tell the data set that the underlying data has changed
     */
    public void notifyDataSetChanged() {
        calcMinMax(getYVals(), mLastStart, mLastEnd);
    }


    /**
     * ###### ###### COLOR RELATED METHODS ##### ######
     */

    @Override
    public List<Integer> getColors() {
        return mColors;
    }

    @Override
    public int getColor() {
        return mColors.get(0);
    }

    @Override
    public int getColor(int index) {
        return mColors.get(index % mColors.size());
    }

    /** ###### ###### OTHER STYLING RELATED METHODS ##### ###### */

    /**
     * Sets the label string that describes the DataSet.
     *
     * @return
     */
    public void setLabel(String label) {
        mLabel = label;
    }

    @Override
    public String getLabel() {
        return mLabel;
    }

    @Override
    public void setHighlightEnabled(boolean enabled) {
        mHighlightEnabled = enabled;
    }

    @Override
    public boolean isHighlightEnabled() {
        return mHighlightEnabled;
    }

    @Override
    public void setValueFormatter(ValueFormatter f) {

        if (f == null)
            return;
        else
            mValueFormatter = f;
    }

    @Override
    public ValueFormatter getValueFormatter() {
        if (mValueFormatter == null)
            return new DefaultValueFormatter(1);
        return mValueFormatter;
    }

    @Override
    public void setValueTextColor(int color) {
        mValueColor = color;
    }

    @Override
    public void setValueTypeface(Typeface tf) {
        mValueTypeface = tf;
    }

    @Override
    public void setValueTextSize(float size) {
        mValueTextSize = Utils.convertDpToPixel(size);
    }

    @Override
    public int getValueTextColor() {
        return mValueColor;
    }

    @Override
    public Typeface getValueTypeface() {
        return mValueTypeface;
    }

    @Override
    public float getValueTextSize() {
        return mValueTextSize;
    }

    @Override
    public void setDrawValues(boolean enabled) {
        this.mDrawValues = enabled;
    }

    @Override
    public boolean isDrawValuesEnabled() {
        return mDrawValues;
    }

    @Override
    public void setVisible(boolean visible) {
        mVisible = visible;
    }

    @Override
    public boolean isVisible() {
        return mVisible;
    }

    @Override
    public YAxis.AxisDependency getAxisDependency() {
        return mAxisDependency;
    }

    /**
     * Set the y-axis this DataSet should be plotted against (either LEFT or
     * RIGHT). Default: LEFT
     *
     * @param dependency
     */
    public void setAxisDependency(YAxis.AxisDependency dependency) {
        mAxisDependency = dependency;
    }
}
