package com.github.mikephil.charting.interfaces.datainterfaces.datasets;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;

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
     * gets the sum of all y-values
     *
     * @return
     */
    float getYValueSum();

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
}
