package com.github.mikephil.charting.interfaces.datainterfaces.data;

import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;

import java.util.List;

/**
 * Created by Philipp Jahoda on 21/10/15.
 */
public interface IChartData<T extends DataSet<? extends Entry>> {

    /**
     * Returns an the array of DataSets this object holds.
     *
     * @return
     */
    List<T> getDataSets();

    /**
     * Returns the DataSet object at the given index.
     *
     * @param index
     * @return
     */
    T getDataSetByIndex(int index);

    /**
     * Returns the index of the provided DataSet inside the DataSets array of
     * this data object. Returns -1 if the DataSet was not found.
     *
     * @param dataSet
     * @return
     */
    int getIndexOfDataSet(T dataSet);

    /**
     * Returns the total number of y-values across all DataSet objects the this
     * object represents.
     *
     * @return
     */
    int getYValCount();
}
