package com.github.mikephil.charting.interfaces.datainterfaces.datasets;

import com.github.mikephil.charting.data.Entry;

/**
 * Created by philipp on 21/10/15.
 */
public interface IBaseDataSet<T> {

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
}
