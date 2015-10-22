package com.github.mikephil.charting.utils;

import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.interfaces.datainterfaces.datasets.IDataSet;

/**
 * Class that encapsulates information of a value that has been
 * selected/highlighted and its DataSet index. The SelectionDetail objects give
 * information about the value at the selected index and the DataSet it belongs
 * to. Needed only for highlighting onTouch().
 *
 * @author Philipp Jahoda
 */
public class SelectionDetail {

    public float val;
    public int dataSetIndex;
    public IDataSet dataSet;

    public SelectionDetail(float val, int dataSetIndex, IDataSet set) {
        this.val = val;
        this.dataSetIndex = dataSetIndex;
        this.dataSet = set;
    }
}