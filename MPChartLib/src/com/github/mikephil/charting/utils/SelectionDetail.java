package com.github.mikephil.charting.utils;

import com.github.mikephil.charting.interfaces.datasets.IDataSet;

/**
 * Class that encapsulates information of a value that has been
 * selected/highlighted and its DataSet index. The SelectionDetail objects give
 * information about the value at the selected index and the DataSet it belongs
 * to. Needed only for highlighting onTouch().
 *
 * @author Philipp Jahoda
 */
public class SelectionDetail {

    public float y;
    public float value;
    public int dataIndex;
    public int dataSetIndex;
    public IDataSet dataSet;

    public SelectionDetail(float y, float value, int dataIndex, int dataSetIndex, IDataSet set) {
        this.y = y;
        this.value = value;
        this.dataIndex = dataIndex;
        this.dataSetIndex = dataSetIndex;
        this.dataSet = set;
    }

    public SelectionDetail(float y, float value, int dataSetIndex, IDataSet set) {
        this(y, value, 0, dataSetIndex, set);
    }

    public SelectionDetail(float value, int dataSetIndex, IDataSet set) {
        this(Float.NaN, value, 0, dataSetIndex, set);
    }
}