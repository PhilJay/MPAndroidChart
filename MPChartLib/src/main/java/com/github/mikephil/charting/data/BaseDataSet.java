package com.github.mikephil.charting.data;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
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
     * List representing all colors that are used for drawing the actual values for this DataSet
     */
    protected List<Integer> mValueColors = null;

    /**
     * label that describes the DataSet or the data the DataSet represents
     */
    private String mLabel = "DataSet";

    /**
     * this specifies which axis this DataSet should be plotted against
     */
    protected YAxis.AxisDependency mAxisDependency = YAxis.AxisDependency.LEFT;

    /**
     * if true, value highlightning is enabled
     */
    protected boolean mHighlightEnabled = true;

    /**
     * custom formatter that is used instead of the auto-formatter if set
     */
    protected transient ValueFormatter mValueFormatter;

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
        mColors = new ArrayList<Integer>();
        mValueColors = new ArrayList<Integer>();

        // default color
        mColors.add(Color.rgb(140, 234, 255));
        mValueColors.add(Color.BLACK);
    }

    /**
     * Constructor with label.
     *
     * @param label
     */
    public BaseDataSet(String label) {
        this();
        this.mLabel = label;
    }

    /**
     * Use this method to tell the data set that the underlying data has changed.
     */
    public void notifyDataSetChanged() {
        calcMinMax();
    }


    /**
     * ###### ###### COLOR GETTING RELATED METHODS ##### ######
     */

    @Override
    public List<Integer> getColors() {
        return mColors;
    }

    public List<Integer> getValueColors() {
        return mValueColors;
    }

    @Override
    public int getColor() {
        return mColors.get(0);
    }

    @Override
    public int getColor(int index) {
        return mColors.get(index % mColors.size());
    }

    /**
     * ###### ###### COLOR SETTING RELATED METHODS ##### ######
     */

    /**
     * Sets the colors that should be used fore this DataSet. Colors are reused
     * as soon as the number of Entries the DataSet represents is higher than
     * the size of the colors array. If you are using colors from the resources,
     * make sure that the colors are already prepared (by calling
     * getResources().getColor(...)) before adding them to the DataSet.
     *
     * @param colors
     */
    public void setColors(List<Integer> colors) {
        this.mColors = colors;
    }

    /**
     * Sets the colors that should be used fore this DataSet. Colors are reused
     * as soon as the number of Entries the DataSet represents is higher than
     * the size of the colors array. If you are using colors from the resources,
     * make sure that the colors are already prepared (by calling
     * getResources().getColor(...)) before adding them to the DataSet.
     *
     * @param colors
     */
    public void setColors(int... colors) {
        this.mColors = ColorTemplate.createColors(colors);
    }

    /**
     * Sets the colors that should be used fore this DataSet. Colors are reused
     * as soon as the number of Entries the DataSet represents is higher than
     * the size of the colors array. You can use
     * "new int[] { R.color.red, R.color.green, ... }" to provide colors for
     * this method. Internally, the colors are resolved using
     * getResources().getColor(...)
     *
     * @param colors
     */
    public void setColors(int[] colors, Context c) {

        if(mColors == null){
            mColors = new ArrayList<>();
        }

        mColors.clear();

        for (int color : colors) {
            mColors.add(c.getResources().getColor(color));
        }
    }

    /**
     * Adds a new color to the colors array of the DataSet.
     *
     * @param color
     */
    public void addColor(int color) {
        if (mColors == null)
            mColors = new ArrayList<Integer>();
        mColors.add(color);
    }

    /**
     * Sets the one and ONLY color that should be used for this DataSet.
     * Internally, this recreates the colors array and adds the specified color.
     *
     * @param color
     */
    public void setColor(int color) {
        resetColors();
        mColors.add(color);
    }

    /**
     * Sets a color with a specific alpha value.
     *
     * @param color
     * @param alpha from 0-255
     */
    public void setColor(int color, int alpha) {
        setColor(Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color)));
    }

    /**
     * Sets colors with a specific alpha value.
     *
     * @param colors
     * @param alpha
     */
    public void setColors(int[] colors, int alpha) {
        resetColors();
        for (int color : colors) {
            addColor(Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color)));
        }
    }

    /**
     * Resets all colors of this DataSet and recreates the colors array.
     */
    public void resetColors() {
        if(mColors == null) {
            mColors = new ArrayList<Integer>();
        }
        mColors.clear();
    }

    /**
     * ###### ###### OTHER STYLING RELATED METHODS ##### ######
     */

    @Override
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
        if (needsFormatter())
            return new DefaultValueFormatter(1);
        return mValueFormatter;
    }

    @Override
    public boolean needsFormatter() {
        return mValueFormatter == null;
    }

    @Override
    public void setValueTextColor(int color) {
        mValueColors.clear();
        mValueColors.add(color);
    }

    @Override
    public void setValueTextColors(List<Integer> colors) {
        mValueColors = colors;
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
        return mValueColors.get(0);
    }

    @Override
    public int getValueTextColor(int index) {
        return mValueColors.get(index % mValueColors.size());
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

    @Override
    public void setAxisDependency(YAxis.AxisDependency dependency) {
        mAxisDependency = dependency;
    }


    /**
     * ###### ###### DATA RELATED METHODS ###### ######
     */

    @Override
    public int getIndexInEntries(int xIndex) {

        for (int i = 0; i < getEntryCount(); i++) {
            if (xIndex == getEntryForIndex(i).getX())
                return i;
        }

        return -1;
    }

    @Override
    public boolean removeFirst() {

        if (getEntryCount() > 0) {

            T entry = getEntryForIndex(0);
            return removeEntry(entry);
        } else
            return false;
    }

    @Override
    public boolean removeLast() {

        if (getEntryCount() > 0) {

            T e = getEntryForIndex(getEntryCount() - 1);
            return removeEntry(e);
        } else
            return false;
    }

    @Override
    public boolean removeEntryByXValue(float xValue) {

        T e = getEntryForXValue(xValue);
        return removeEntry(e);
    }

    @Override
    public boolean removeEntry(int index) {

        T e = getEntryForIndex(index);
        return removeEntry(e);
    }

    @Override
    public boolean contains(T e) {

        for (int i = 0; i < getEntryCount(); i++) {
            if (getEntryForIndex(i).equals(e))
                return true;
        }

        return false;
    }
}
