
package com.github.mikephil.charting.data;

import java.util.ArrayList;

/**
 * A PieData object can only represent one DataSet. Unlike all other charts, the
 * legend labels of the PieChart are created from the x-values array, and not
 * from the DataSet labels.
 * 
 * @author Philipp Jahoda
 */
public class PieData extends ChartData {

    public PieData(ArrayList<String> xVals, PieDataSet dataSet) {
        super(xVals, toArrayList(dataSet));
    }

    public PieData(String[] xVals, PieDataSet dataSet) {
        super(xVals, toArrayList(dataSet));
    }

    /**
     * Returns the DataSet this PieData object represents.
     * 
     * @return
     */
    public PieDataSet getDataSet() {
        return (PieDataSet) mDataSets.get(0);
    }
}
