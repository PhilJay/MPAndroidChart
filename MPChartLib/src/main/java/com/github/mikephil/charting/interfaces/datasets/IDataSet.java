package com.github.mikephil.charting.interfaces.datasets;

import android.graphics.DashPathEffect;
import android.graphics.Typeface;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;

import java.util.List;

/**
 * Created by Philipp Jahoda on 21/10/15.
 */
public interface IDataSet<T extends Entry> {

    /** ###### ###### DATA RELATED METHODS ###### ###### */

    /**
     * returns the minimum y-value this DataSet holds
     *
     * @return
     */
    float getYMin();

    /**
     * returns the maximum y-value this DataSet holds
     *
     * @return
     */
    float getYMax();

    /**
     * returns the minimum x-value this DataSet holds
     *
     * @return
     */
    float getXMin();

    /**
     * returns the maximum x-value this DataSet holds
     *
     * @return
     */
    float getXMax();

    /**
     * Returns the number of y-values this DataSet represents -> the size of the y-values array
     * -> yvals.size()
     *
     * @return
     */
    int getEntryCount();

    /**
     * Calculates the minimum and maximum x and y values (mXMin, mXMax, mYMin, mYMax).
     */
    void calcMinMax();

    /**
     * Calculates the min and max y-values from the Entry closest to the given fromX to the Entry closest to the given toX value.
     * This is only needed for the autoScaleMinMax feature.
     *
     * @param fromX
     * @param toX
     */
    void calcMinMaxY(float fromX, float toX);

    /**
     * Returns the first Entry object found at the given x-value with binary
     * search. If the no Entry at the specified x-value is found, this method
     * returns the Entry at the x-value according to the rounding.
     * INFORMATION: This method does calculations at runtime. Do
     * not over-use in performance critical situations.
     *
     * @param xValue
     * @param rounding determine to round up/down/closest if there is no Entry matching the provided x-index
     * @return
     */
    T getEntryForXValue(float xValue, DataSet.Rounding rounding);

    /**
     * Returns the first Entry object found at the given x-value with binary
     * search. If the no Entry at the specified x-value is found, this method
     * returns the index at the closest x-value.
     * INFORMATION: This method does calculations at runtime. Do
     * not over-use in performance critical situations.
     *
     * @param xValue
     * @return
     */
    T getEntryForXValue(float xValue);

    /**
     * Returns all Entry objects found at the given x-value with binary
     * search. An empty array if no Entry object at that x-value.
     * INFORMATION: This method does calculations at runtime. Do
     * not over-use in performance critical situations.
     *
     * @param xValue
     * @return
     */
    List<T> getEntriesForXValue(float xValue);

    /**
     * Returns the Entry object found at the given index (NOT xIndex) in the values array.
     *
     * @param index
     * @return
     */
    T getEntryForIndex(int index);

    /**
     * Returns the first Entry index found at the given x-value with binary
     * search. If the no Entry at the specified x-value is found, this method
     * returns the Entry at the closest x-value.
     * INFORMATION: This method does calculations at runtime. Do
     * not over-use in performance critical situations.
     *
     * @param xValue
     * @param rounding determine to round up/down/closest if there is no Entry matching the provided x-index
     * @return
     */
    int getEntryIndex(float xValue, DataSet.Rounding rounding);

    /**
     * Returns the position of the provided entry in the DataSets Entry array.
     * Returns -1 if doesn't exist.
     *
     * @param e
     * @return
     */
    int getEntryIndex(T e);


    /**
     * This method returns the actual
     * index in the Entry array of the DataSet for a given xIndex. IMPORTANT: This method does
     * calculations at runtime, do not over-use in performance critical
     * situations.
     *
     * @param xIndex
     * @return
     */
    int getIndexInEntries(int xIndex);

    /**
     * Adds an Entry to the DataSet dynamically.
     * Entries are added to the end of the list.
     * This will also recalculate the current minimum and maximum
     * values of the DataSet and the value-sum.
     *
     * @param e
     */
    boolean addEntry(T e);


    /**
     * Adds an Entry to the DataSet dynamically.
     * Entries are added to their appropriate index in the values array respective to their x-position.
     * This will also recalculate the current minimum and maximum
     * values of the DataSet and the value-sum.
     *
     * @param e
     */
    void addEntryOrdered(T e);

    /**
     * Removes the first Entry (at index 0) of this DataSet from the entries array.
     * Returns true if successful, false if not.
     *
     * @return
     */
    boolean removeFirst();

    /**
     * Removes the last Entry (at index size-1) of this DataSet from the entries array.
     * Returns true if successful, false if not.
     *
     * @return
     */
    boolean removeLast();

    /**
     * Removes an Entry from the DataSets entries array. This will also
     * recalculate the current minimum and maximum values of the DataSet and the
     * value-sum. Returns true if an Entry was removed, false if no Entry could
     * be removed.
     *
     * @param e
     */
    boolean removeEntry(T e);

    /**
     * Removes the Entry object closest to the given x-value from the DataSet.
     * Returns true if an Entry was removed, false if no Entry could be removed.
     *
     * @param xValue
     */
    boolean removeEntryByXValue(float xValue);

    /**
     * Removes the Entry object at the given index in the values array from the DataSet.
     * Returns true if an Entry was removed, false if no Entry could be removed.
     *
     * @param index
     * @return
     */
    boolean removeEntry(int index);

    /**
     * Checks if this DataSet contains the specified Entry. Returns true if so,
     * false if not. NOTE: Performance is pretty bad on this one, do not
     * over-use in performance critical situations.
     *
     * @param entry
     * @return
     */
    boolean contains(T entry);

    /**
     * Removes all values from this DataSet and does all necessary recalculations.
     */
    void clear();


    /** ###### ###### STYLING RELATED (& OTHER) METHODS ###### ###### */

    /**
     * Returns the label string that describes the DataSet.
     *
     * @return
     */
    String getLabel();

    /**
     * Sets the label string that describes the DataSet.
     *
     * @param label
     */
    void setLabel(String label);

    /**
     * Returns the axis this DataSet should be plotted against.
     *
     * @return
     */
    YAxis.AxisDependency getAxisDependency();

    /**
     * Set the y-axis this DataSet should be plotted against (either LEFT or
     * RIGHT). Default: LEFT
     *
     * @param dependency
     */
    void setAxisDependency(YAxis.AxisDependency dependency);

    /**
     * returns all the colors that are set for this DataSet
     *
     * @return
     */
    List<Integer> getColors();

    /**
     * Returns the first color (index 0) of the colors-array this DataSet
     * contains. This is only used for performance reasons when only one color is in the colors array (size == 1)
     *
     * @return
     */
    int getColor();

    /**
     * Returns the color at the given index of the DataSet's color array.
     * Performs a IndexOutOfBounds check by modulus.
     *
     * @param index
     * @return
     */
    int getColor(int index);

    /**
     * returns true if highlighting of values is enabled, false if not
     *
     * @return
     */
    boolean isHighlightEnabled();

    /**
     * If set to true, value highlighting is enabled which means that values can
     * be highlighted programmatically or by touch gesture.
     *
     * @param enabled
     */
    void setHighlightEnabled(boolean enabled);

    /**
     * Sets the formatter to be used for drawing the values inside the chart. If
     * no formatter is set, the chart will automatically determine a reasonable
     * formatting (concerning decimals) for all the values that are drawn inside
     * the chart. Use chart.getDefaultValueFormatter() to use the formatter
     * calculated by the chart.
     *
     * @param f
     */
    void setValueFormatter(IValueFormatter f);

    /**
     * Returns the formatter used for drawing the values inside the chart.
     *
     * @return
     */
    IValueFormatter getValueFormatter();

    /**
     * Returns true if the valueFormatter object of this DataSet is null.
     *
     * @return
     */
    boolean needsFormatter();

    /**
     * Sets the color the value-labels of this DataSet should have.
     *
     * @param color
     */
    void setValueTextColor(int color);

    /**
     * Sets a list of colors to be used as the colors for the drawn values.
     *
     * @param colors
     */
    void setValueTextColors(List<Integer> colors);

    /**
     * Sets a Typeface for the value-labels of this DataSet.
     *
     * @param tf
     */
    void setValueTypeface(Typeface tf);

    /**
     * Sets the text-size of the value-labels of this DataSet in dp.
     *
     * @param size
     */
    void setValueTextSize(float size);

    /**
     * Returns only the first color of all colors that are set to be used for the values.
     *
     * @return
     */
    int getValueTextColor();

    /**
     * Returns the color at the specified index that is used for drawing the values inside the chart.
     * Uses modulus internally.
     *
     * @param index
     * @return
     */
    int getValueTextColor(int index);

    /**
     * Returns the typeface that is used for drawing the values inside the chart
     *
     * @return
     */
    Typeface getValueTypeface();

    /**
     * Returns the text size that is used for drawing the values inside the chart
     *
     * @return
     */
    float getValueTextSize();

    /**
     * The form to draw for this dataset in the legend.
     * <p/>
     * Return `DEFAULT` to use the default legend form.
     */
    Legend.LegendForm getForm();

    /**
     * The form size to draw for this dataset in the legend.
     * <p/>
     * Return `Float.NaN` to use the default legend form size.
     */
    float getFormSize();

    /**
     * The line width for drawing the form of this dataset in the legend
     * <p/>
     * Return `Float.NaN` to use the default legend form line width.
     */
    float getFormLineWidth();

    /**
     * The line dash path effect used for shapes that consist of lines.
     * <p/>
     * Return `null` to use the default legend form line dash effect.
     */
    DashPathEffect getFormLineDashEffect();

    /**
     * set this to true to draw y-values on the chart NOTE (for bar and
     * linechart): if "maxvisiblecount" is reached, no values will be drawn even
     * if this is enabled
     *
     * @param enabled
     */
    void setDrawValues(boolean enabled);

    /**
     * Returns true if y-value drawing is enabled, false if not
     *
     * @return
     */
    boolean isDrawValuesEnabled();

    /**
     * Set the visibility of this DataSet. If not visible, the DataSet will not
     * be drawn to the chart upon refreshing it.
     *
     * @param visible
     */
    void setVisible(boolean visible);

    /**
     * Returns true if this DataSet is visible inside the chart, or false if it
     * is currently hidden.
     *
     * @return
     */
    boolean isVisible();
}
