package com.github.mikephil.charting.interfaces.datainterfaces.datasets;

import android.graphics.Typeface;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.List;

/**
 * Created by Philipp Jahoda on 21/10/15.
 */
public interface IDataSet<T extends Entry> {

    /**
     * Returns the label string that describes the DataSet.
     *
     * @return
     */
    String getLabel();

    /**
     * returns the DataSets Entry array
     *
     * @return
     */
    List<T> getYVals();

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
     * returns the number of y-values this DataSet represents -> yvals.size()
     *
     * @return
     */
    int getEntryCount();

    /**
     * Returns the axis this DataSet should be plotted against.
     *
     * @return
     */
    YAxis.AxisDependency getAxisDependency();

    /**
     * returns all the colors that are set for this DataSet
     *
     * @return
     */
    List<Integer> getColors();

    /**
     * calc minimum and maximum y value
     */
    void calcMinMax(List<T> values, int start, int end);

    /**
     * Returns the first Entry object found at the given xIndex with binary
     * search. If the no Entry at the specified x-index is found, this method
     * returns the index at the closest x-index. Returns null if no Entry object
     * at that index. INFORMATION: This method does calculations at runtime. Do
     * not over-use in performance critical situations.
     *
     * @param x
     * @return
     */
    T getEntryForXIndex(int x);

    /**
     * Checks if this DataSet contains the specified Entry. Returns true if so,
     * false if not. NOTE: Performance is pretty bad on this one, do not
     * over-use in performance critical situations.
     *
     * @param e
     * @return
     */
    boolean contains(Entry e);

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
     * Adds an Entry to the DataSet dynamically.
     * Entries are added to the end of the list.
     * This will also recalculate the current minimum and maximum
     * values of the DataSet and the value-sum.
     *
     * @param e
     */
    void addEntry(T e);

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
     * If this component has no ValueFormatter or is only equipped with the
     * default one (no custom set), return true.
     *
     * @return
     */
    boolean needsDefaultFormatter();

    /**
     * Sets the formatter to be used for drawing the values inside the chart. If
     * no formatter is set, the chart will automatically determine a reasonable
     * formatting (concerning decimals) for all the values that are drawn inside
     * the chart. Use chart.getDefaultValueFormatter() to use the formatter
     * calculated by the chart.
     *
     * @param f
     */
    void setValueFormatter(ValueFormatter f);

    /**
     * Sets the color the value-labels of this DataSet should have.
     *
     * @param color
     */
    void setValueTextColor(int color);

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
     * set this to true to draw y-values on the chart NOTE (for bar and
     * linechart): if "maxvisiblecount" is reached, no values will be drawn even
     * if this is enabled
     *
     * @param enabled
     */
    void setDrawValues(boolean enabled);
}
