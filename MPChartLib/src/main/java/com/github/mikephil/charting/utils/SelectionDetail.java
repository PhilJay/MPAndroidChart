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

    public float yPx;
    public float xPx;
    public float yValue;
    public float xValue;
    public int dataIndex;
    public int dataSetIndex;
    public IDataSet dataSet;

    public SelectionDetail(float xPx, float yPx, float xValue, float yValue, int dataIndex, int dataSetIndex, IDataSet set) {
        this.xPx = xPx;
        this.yPx = yPx;
        this.xValue = xValue;
        this.yValue = yValue;
        this.dataIndex = dataIndex;
        this.dataSetIndex = dataSetIndex;
        this.dataSet = set;
    }

    public SelectionDetail(float xPx, float yPx, float xValue, float yValue, int dataSetIndex, IDataSet set) {
        this(xPx, yPx, xValue, yValue, 0, dataSetIndex, set);
    }

    public SelectionDetail(float xValue, float yValue, int dataSetIndex, IDataSet set) {
        this(Float.NaN, Float.NaN, xValue, yValue, 0, dataSetIndex, set);
    }
}